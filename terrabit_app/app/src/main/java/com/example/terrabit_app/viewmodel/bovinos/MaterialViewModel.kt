package com.example.terrabit_app.viewmodel.bovinos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.HistorialCamposManager
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.local.database.BorradorEntity
import com.example.terrabit_app.data.local.database.HistorialEntity
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.material.PetSolicitudMaterial
import com.example.terrabit_app.data.network.material.Unitat
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.UUID

@HiltViewModel
class MaterialViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val borradorDao: BorradorDao,
    override val historialDao: HistorialDao,
    val historialCamposManager: HistorialCamposManager
) : BaseMaterialViewModel() {

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _tipoMaterial = MutableLiveData(0)
    val tipoMaterial: LiveData<Int> = _tipoMaterial
    private val _codigoTipoMaterial = MutableLiveData("")

    private val _listaUnidades = MutableLiveData<List<Unitat>>(
        listOf(Unitat(codiExplotacio = "", nombreUnitats = ""))
    )
    val listaUnidades: LiveData<List<Unitat>> = _listaUnidades

    private val _tipoMaterialExpandido = MutableLiveData(false)
    val tipoMaterialExpandido: LiveData<Boolean> = _tipoMaterialExpandido

    private val _codigoMoObligatorio = MutableLiveData(false)
    val codigoMoObligatorio: LiveData<Boolean> = _codigoMoObligatorio


    private val tiposMaterialConCodiMoObligatorio = setOf("21", "22", "25", "26")

    init {
        borradorSesionId = "material_auto_${System.currentTimeMillis()}"
        // MaterialViewModel no carga bovinos — no llamamos cargarBovinosEnCache()
    }

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun getTipoRegistro() = "MATERIAL"

    override fun getDatosFormulario(): Map<String, Any?> = getDatosEnvio() + mapOf(
        "tipoMaterial"       to _tipoMaterial.value,
        "codigoTipoMaterial" to _codigoTipoMaterial.value,
        "listaUnidades"      to _listaUnidades.value
    )

    override fun restaurarDatos(datos: Map<String, Any?>) {
        restaurarDatosEnvio(datos)
        _tipoMaterial.value        = (datos["tipoMaterial"] as? Double)?.toInt() ?: 0
        _codigoTipoMaterial.value  = datos["codigoTipoMaterial"] as? String ?: ""
        val listaJson = datos["listaUnidades"] as? List<*>
        if (listaJson != null) {
            _listaUnidades.value = listaJson.mapNotNull { item ->
                val m = item as? Map<*, *>
                Unitat(
                    codiExplotacio = m?.get("codiExplotacio") as? String ?: "",
                    nombreUnitats  = m?.get("nombreUnitats") as? String ?: ""
                )
            }.ifEmpty { listOf(Unitat(codiExplotacio = "", nombreUnitats = "")) }
        }
    }

    override fun limpiarFormulario() {
        limpiarBloquEnvio()
        _tipoMaterial.value = 0; _codigoTipoMaterial.value = ""
        _listaUnidades.value = listOf(Unitat(codiExplotacio = "", nombreUnitats = ""))
        borradorSesionId = ""
    }

    override fun tieneContenido() =
        !_empresaSubministradora.value.isNullOrEmpty() ||
                (_tipoEnviamiento.value ?: 0) != 0 ||
                (_destinoEnvio.value ?: 0) != 0 ||
                (_tipoMaterial.value ?: 0) != 0 ||
                (_listaUnidades.value?.any { it.nombreUnitats.isNotEmpty() } == true)

    // ─── Helpers de tipo material ─────────────────────────────────────────────
    private fun comprobarMaterial(){
        if (_codigoTipoMaterial.value in tiposMaterialConCodiMoObligatorio) {
            _codigoMoObligatorio.value = true
        } else {
            _codigoMoObligatorio.value = false
        }
    }
    fun codiMoEsObligatorio() = _codigoTipoMaterial.value in tiposMaterialConCodiMoObligatorio
    fun getCodigoTipoMaterial() = _codigoTipoMaterial.value ?: ""

    fun seleccionarTipoMaterial(nombre: Int, codigo: String) {
        _tipoMaterial.value = nombre; _codigoTipoMaterial.value = codigo
        _tipoMaterialExpandido.value = false
        comprobarMaterial()
    }
    fun toggleTipoMaterialExpandido() { _tipoMaterialExpandido.value = !(_tipoMaterialExpandido.value ?: false) }
    fun cerrarTipoMaterialMenu()      { _tipoMaterialExpandido.value = false }

    // ─── Lista de unidades ────────────────────────────────────────────────────
    fun agregarUnidades() {
        _listaUnidades.value = (_listaUnidades.value?.toMutableList() ?: mutableListOf())
            .also { it.add(Unitat(codiExplotacio = "", nombreUnitats = "")) }
    }
    fun eliminarUnidades(indice: Int) {
        val lista = _listaUnidades.value?.toMutableList() ?: return
        if (lista.size > 1) { lista.removeAt(indice); _listaUnidades.value = lista }
    }
    fun actualizarCodiExplotacio(indice: Int, valor: String) {
        val lista = _listaUnidades.value?.toMutableList() ?: return
        if (indice < lista.size) { lista[indice] = lista[indice].copy(codiExplotacio = valor); _listaUnidades.value = lista }
    }
    fun actualizarUnidades(indice: Int, valor: String) {
        val lista = _listaUnidades.value?.toMutableList() ?: return
        if (indice < lista.size) { lista[indice] = lista[indice].copy(nombreUnitats = valor); _listaUnidades.value = lista }
    }

    // ─── Validación y envío ───────────────────────────────────────────────────
    fun esFormularioValido(): Boolean {
        if (_empresaSubministradora.value.isNullOrEmpty()) return false
        if ((_tipoEnviamiento.value ?: 0) == 0) return false
        if ((_destinoEnvio.value ?: 0) == 0) return false
        if ((_tipoMaterial.value ?: 0) == 0) return false
        when (codigoDestino) {
            "01" -> if (codigoOC.isEmpty()) return false
            "03" -> if (_direccion.value.isNullOrEmpty() || _poblacion.value.isNullOrEmpty() ||
                _codigoPostal.value.isNullOrEmpty() || _municipio.value.isNullOrEmpty() ||
                _telefonoContacto.value.isNullOrEmpty()) return false
        }
        val unidades = _listaUnidades.value ?: return false
        if (unidades.isEmpty() || unidades.any { it.nombreUnitats.isEmpty() }) return false
        if (codiMoEsObligatorio() && unidades.any { it.codiExplotacio.isNullOrEmpty() }) return false
        return true
    }

    fun solicitarMaterial() {
        if (!esFormularioValido()) {
            _mensajeError.value = when {
                _empresaSubministradora.value.isNullOrEmpty()                                          -> "Por favor, seleccione la empresa subministradora"
                (_tipoEnviamiento.value ?: 0) == 0                                                     -> "Por favor, seleccione el tipo de envío"
                (_destinoEnvio.value ?: 0) == 0                                                        -> "Por favor, seleccione el destino de entrega"
                codigoDestino == "01" && codigoOC.isEmpty()                                            -> "Por favor, seleccione la oficina comarcal"
                codigoDestino == "03" && _direccion.value.isNullOrEmpty()                              -> "Por favor, introduzca la dirección"
                codigoDestino == "03" && _poblacion.value.isNullOrEmpty()                              -> "Por favor, introduzca la población"
                codigoDestino == "03" && _codigoPostal.value.isNullOrEmpty()                           -> "Por favor, introduzca el código postal"
                codigoDestino == "03" && _municipio.value.isNullOrEmpty()                              -> "Por favor, introduzca el municipio"
                codigoDestino == "03" && _telefonoContacto.value.isNullOrEmpty()                       -> "Por favor, introduzca el teléfono de contacto"
                (_tipoMaterial.value ?: 0) == 0                                                        -> "Por favor, seleccione el tipo de material"
                _listaUnidades.value?.any { it.nombreUnitats.isEmpty() } == true                 -> "Por favor, introduzca el número de unidades en cada fila"
                codiMoEsObligatorio() && _listaUnidades.value?.any { it.codiExplotacio.isNullOrEmpty() } == true -> "El Codi MO es obligatorio para el tipo de material seleccionado"
                else                                                                                   -> "Por favor, complete todos los campos obligatorios"
            }
            return
        }
        launchApiCall {
            val request = PetSolicitudMaterial(
                nif                    = nif,
                passwordMobilitat      = password,
                especie                = "01",
                empresaSubministradora = codigoEmpresa,
                tipusEnviament         = codigoTipoEnvio,
                adrecaLliurament       = codigoDestino,
                oc                     = if (codigoDestino == "01") codigoOC else null,
                adreca                 = if (codigoDestino == "03") _direccion.value else null,
                poblacio               = if (codigoDestino == "03") _poblacion.value else null,
                cp                     = if (codigoDestino == "03") _codigoPostal.value else null,
                municipi               = if (codigoDestino == "03") _municipio.value else null,
                telefonContacte        = if (codigoDestino == "03") _telefonoContacto.value else null,
                tipusMaterial          = _codigoTipoMaterial.value ?: "",
                unitats                = _listaUnidades.value ?: listOf(Unitat(codiExplotacio = null, nombreUnitats = "1"))
            )
            Log.d("Solicitud Material", "Request: $request")
            val response = repositorio.putSolicitudMaterial(request)
            val body = if (response.isSuccessful) response.body() else null  // ← una sola lectura

            withContext(Dispatchers.Main) {
                _estadoCarga.value = false
                when {
                    response.isSuccessful && body?.descripcio == "OK" -> {
                        val codi = body.codi
                        _operacionExitosa.value = true
                        _mensajeError.value = ""
                        _codiSeguimiento.value = codi
                        guardarEnHistorial("Solicitud de material enviada - $codi")
                        guardarHistorialCampos()
                        eliminarBorradorAutomatico()
                        limpiarFormulario()
                    }
                    !response.isSuccessful -> {
                        _mensajeError.value = parsearMensajeError(response)
                        _operacionExitosa.value = false
                    }
                    else -> {
                        _operacionExitosa.value = false
                        _mensajeError.value = "Error: Respuesta vacía del servidor"
                    }
                }
            }
        }
    }

    private fun guardarHistorialCampos() {
        viewModelScope.launch {
            _listaUnidades.value?.forEach { unitat ->
                if (!unitat.codiExplotacio.isNullOrBlank())
                    historialCamposManager.guardarValor("codi_mo", unitat.codiExplotacio)
            }
        }
    }
}