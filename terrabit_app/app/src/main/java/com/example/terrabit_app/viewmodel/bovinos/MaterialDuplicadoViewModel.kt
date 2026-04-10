package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.network.Identificadores.IdenSolicitudDupli
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.material.PetSolicitudDuplicado
import com.example.terrabit_app.utils.CodigoPaisUtils
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MaterialDuplicadoViewModel @Inject constructor(
    override val repositorio: Repositorio,
    override val userPreferences: UserPreferences,
    override val borradorDao: BorradorDao,
    override val historialDao: HistorialDao
) : BaseMaterialViewModel() {

    // ─── Estado específico ────────────────────────────────────────────────────
    private val _listaAnimales = MutableLiveData<List<IdenSolicitudDupli>>(
        listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = ""))
    )
    val listaAnimales: LiveData<List<IdenSolicitudDupli>> = _listaAnimales

    private val _tipoMaterialExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val tipoMaterialExpandidoPorIndice: LiveData<Map<Int, Boolean>> = _tipoMaterialExpandidoPorIndice

    init {
        borradorSesionId = "material_duplicado_auto_${System.currentTimeMillis()}"
        cargarBovinosEnCache()   // ← este sí usa bovinos
    }

    private val _activeFieldIndex = MutableLiveData(-1)
    val activeFieldIndex: LiveData<Int> = _activeFieldIndex

    // ─── Contrato con la base ─────────────────────────────────────────────────
    override fun getTipoRegistro() = "MATERIAL_DUPLICADO"

    override fun getDatosFormulario(): Map<String, Any?> = getDatosEnvio() + mapOf(
        "listaAnimales" to _listaAnimales.value
    )

    override fun restaurarDatos(datos: Map<String, Any?>) {
        restaurarDatosEnvio(datos)
        val listaJson = datos["listaAnimales"] as? List<*>
        if (listaJson != null) {
            _listaAnimales.value = listaJson.mapNotNull { item ->
                val m = item as? Map<*, *>
                IdenSolicitudDupli(
                    identificador = m?.get("identificador") as? String ?: "",
                    tipusMaterial = m?.get("tipusMaterial") as? String ?: ""
                )
            }.ifEmpty { listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = "")) }
        }
    }

    override fun limpiarFormulario() {
        limpiarBloquEnvio()
        _listaAnimales.value = listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = ""))
        _tipoMaterialExpandidoPorIndice.value = emptyMap()
        borradorSesionId = ""
    }

    override fun tieneContenido() =
        !_empresaSubministradora.value.isNullOrEmpty() ||
                (_tipoEnviamiento.value ?: 0) != 0 ||
                (_destinoEnvio.value ?: 0) != 0 ||
                (_listaAnimales.value?.any { it.identificador.isNotEmpty() } == true)

    // ─── Bovinos ──────────────────────────────────────────────────────────────
    fun precargarAnimal(animalId: String) {
        val lista = _listaAnimales.value?.toMutableList() ?: mutableListOf()
        if (lista.isEmpty()) lista.add(IdenSolicitudDupli(identificador = animalId, tipusMaterial = ""))
        else lista[0] = lista[0].copy(identificador = animalId)
        _listaAnimales.value = lista
    }

    fun onBovinoSelected(index: Int, animal: Animal) {
        actualizarIdentificador(index, animal.identificador)
        limpiarSugerencias()
    }

    // ─── Lista de animales ────────────────────────────────────────────────────
    fun actualizarIdentificador(indice: Int, identificador: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(identificador = identificador) else animal
        }
    }

    fun actualizarIdentificadorDesdeHardware(indice: Int, identificador: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(identificador = CodigoPaisUtils.traducirCodigoPais(identificador.trim()))
            else animal
        }
    }
    fun seleccionarTipoMaterialIdentificador(indice: Int, codigoTipo: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(tipusMaterial = codigoTipo) else animal
        }
        _tipoMaterialExpandidoPorIndice.value =
            (_tipoMaterialExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun agregarAnimal() {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()) +
                IdenSolicitudDupli(identificador = "", tipusMaterial = "")
    }

    fun eliminarAnimal(indice: Int) {
        val lista = _listaAnimales.value ?: emptyList()
        if (lista.size > 1) {
            _listaAnimales.value = lista.filterIndexed { index, _ -> index != indice }
            _tipoMaterialExpandidoPorIndice.value =
                _tipoMaterialExpandidoPorIndice.value?.minus(indice)
        }
    }

    fun toggleTipoMaterialExpandido(indice: Int) {
        val mapa = _tipoMaterialExpandidoPorIndice.value ?: emptyMap()
        _tipoMaterialExpandidoPorIndice.value = mapa + (indice to !(mapa[indice] ?: false))
    }

    fun cerrarTipoMaterialMenu(indice: Int) {
        _tipoMaterialExpandidoPorIndice.value =
            (_tipoMaterialExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun searchBovinosConCampo(fieldIndex: Int, query: String) {
        _activeFieldIndex.value = fieldIndex
        searchBovinos(query)
    }

    // ─── Validación y envío ───────────────────────────────────────────────────
    fun esFormularioValido(): Boolean {
        if (codigoEmpresa.isEmpty() || codigoTipoEnvio.isEmpty() || codigoDestino.isEmpty()) return false
        when (codigoDestino) {
            "01" -> if (codigoOC.isEmpty()) return false
            "03" -> if (_direccion.value.isNullOrEmpty() || _poblacion.value.isNullOrEmpty() ||
                _codigoPostal.value.isNullOrEmpty() || _municipio.value.isNullOrEmpty() ||
                _telefonoContacto.value.isNullOrEmpty()) return false
        }
        val ids = _listaAnimales.value ?: return false
        return ids.isNotEmpty() && ids.none { it.identificador.isEmpty() || it.tipusMaterial.isEmpty() }
    }

    fun solicitarDuplicado() {
        if (!esFormularioValido()) {
            _mensajeError.value = when {
                codigoEmpresa.isEmpty()                                                                   -> "Por favor, seleccione la empresa subministradora"
                codigoTipoEnvio.isEmpty()                                                                 -> "Por favor, seleccione el tipo de envío"
                codigoDestino.isEmpty()                                                                   -> "Por favor, seleccione la dirección de envío"
                codigoDestino == "01" && codigoOC.isEmpty()                                               -> "Por favor, seleccione la oficina comarcal"
                codigoDestino == "03" && _direccion.value.isNullOrEmpty()                                 -> "Por favor, introduzca la dirección"
                codigoDestino == "03" && _poblacion.value.isNullOrEmpty()                                 -> "Por favor, introduzca la población"
                codigoDestino == "03" && _codigoPostal.value.isNullOrEmpty()                              -> "Por favor, introduzca el código postal"
                codigoDestino == "03" && _municipio.value.isNullOrEmpty()                                 -> "Por favor, introduzca el municipio"
                codigoDestino == "03" && _telefonoContacto.value.isNullOrEmpty()                          -> "Por favor, introduzca el teléfono de contacto"
                _listaAnimales.value?.any { it.identificador.isEmpty() } == true                          -> "Por favor, complete todos los identificadores"
                _listaAnimales.value?.any { it.tipusMaterial.isEmpty() } == true                          -> "Por favor, seleccione el tipo de material para cada identificador"
                else                                                                                      -> "Por favor, complete todos los campos obligatorios"
            }
            return
        }
        launchApiCall {
            val request = PetSolicitudDuplicado(
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
                identificadors         = _listaAnimales.value ?: emptyList()
            )
            val response = repositorio.putSolicitudDuplicado(request)
            withContext(Dispatchers.Main) {
                _estadoCarga.value = false
                when {
                    response.isSuccessful && response.body()
                        ?.let { it.codi == "0" || it.descripcio == "OK" } == true -> {
                        _operacionExitosa.value = true; _mensajeError.value = ""
                        guardarEnHistorial("Solicitud de duplicado enviada")
                        eliminarBorradorAutomatico()
                        limpiarFormulario()
                    }
                    !response.isSuccessful -> {
                        _mensajeError.value = parsearMensajeError(response)
                        _operacionExitosa.value = false
                    }
                    else -> { _operacionExitosa.value = false; _mensajeError.value = "Error: Respuesta vacía del servidor" }
                }
            }
        }
    }
}