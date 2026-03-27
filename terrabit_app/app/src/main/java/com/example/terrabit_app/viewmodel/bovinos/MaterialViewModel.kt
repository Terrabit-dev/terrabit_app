package com.example.terrabit_app.viewmodel.bovinos

import android.util.Log
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
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
    private val borradorDao: BorradorDao,
    private val historialDao: HistorialDao,
    val historialCamposManager: HistorialCamposManager
) : ViewModel() {

    private var borradorSesionId: String = "material_auto_${System.currentTimeMillis()}"

    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""

    private val _empresaSubministradora = MutableLiveData("")
    val empresaSubministradora = _empresaSubministradora
    private val _codigoEmpresa = MutableLiveData("")

    private val _tipoEnviamiento = MutableLiveData("")
    val tipoEnviamiento = _tipoEnviamiento
    private var codigoTipoEnvio = ""

    private val _destinoLliurament = MutableLiveData("")
    val destinoLliurament = _destinoLliurament
    private var codiDestinoEnvio = ""

    private val _oficinaComarcal = MutableLiveData("")
    val oficinaComarcal = _oficinaComarcal
    private var codigoOC = ""

    private val _direccion = MutableLiveData("")
    val direccion = _direccion

    private val _poblacion = MutableLiveData("")
    val poblacion = _poblacion

    private val _codigoPostal = MutableLiveData("")
    val codigoPostal = _codigoPostal

    private val _municipio = MutableLiveData("")
    val municipio = _municipio

    private val _telefonoContacto = MutableLiveData("")
    val telefonoContacto = _telefonoContacto

    private val _identificadorMaterial = MutableLiveData("")
    val identificadorMaterial = _identificadorMaterial

    private val _tipoMaterial = MutableLiveData("")
    val tipoMaterial = _tipoMaterial
    private val _codigoTipoMaterial = MutableLiveData("")

    private val tiposMaterialConCodiMoObligatorio = setOf("21", "22", "25", "26")
    fun codiMoEsObligatorio(): Boolean = _codigoTipoMaterial.value in tiposMaterialConCodiMoObligatorio

    private val _empresaExpandida = MutableLiveData(false)
    val empresaExpandida = _empresaExpandida

    private val _tipoEnviamientoExpandido = MutableLiveData(false)
    val tipoEnviamientoExpandido = _tipoEnviamientoExpandido

    private val _destinoExpandido = MutableLiveData(false)
    val destinoExpandido = _destinoExpandido

    private val _oficinaComarcalExpandida = MutableLiveData(false)
    val oficinaComarcalExpandida = _oficinaComarcalExpandida

    private val _tipoMaterialExpandido = MutableLiveData(false)
    val tipoMaterialExpandido = _tipoMaterialExpandido

    private val _registroMaterialExitoso = MutableLiveData<Boolean>()
    val registroMaterialExitoso = _registroMaterialExitoso

    private val _mensajeErrorMaterial = MutableLiveData<String>()
    val mensajeErrorMaterial = _mensajeErrorMaterial

    private val _cargandoMaterial = MutableLiveData(false)
    val cargandoMaterial = _cargandoMaterial

    private val _listaUnidades = MutableLiveData<List<Unitat>>(
        listOf(Unitat(codiExplotacio = "", nombreUnitats = ""))
    )
    val listaUnidades = _listaUnidades

    // ── Borrador ─────────────────────────────────────────────────────────────

    fun tieneContenido(): Boolean {
        return !_empresaSubministradora.value.isNullOrEmpty() ||
                !_tipoEnviamiento.value.isNullOrEmpty() ||
                !_destinoLliurament.value.isNullOrEmpty() ||
                !_tipoMaterial.value.isNullOrEmpty() ||
                (_listaUnidades.value?.any { !it.nombreUnitats.isNullOrEmpty() } == true)
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) return
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "empresaSubministradora" to _empresaSubministradora.value,
                    "codigoEmpresa" to _codigoEmpresa.value,
                    "tipoEnviamiento" to _tipoEnviamiento.value,
                    "codigoTipoEnvio" to codigoTipoEnvio,
                    "destinoLliurament" to _destinoLliurament.value,
                    "codiDestinoEnvio" to codiDestinoEnvio,
                    "oficinaComarcal" to _oficinaComarcal.value,
                    "codigoOC" to codigoOC,
                    "direccion" to _direccion.value,
                    "poblacion" to _poblacion.value,
                    "codigoPostal" to _codigoPostal.value,
                    "municipio" to _municipio.value,
                    "telefonoContacto" to _telefonoContacto.value,
                    "tipoMaterial" to _tipoMaterial.value,
                    "codigoTipoMaterial" to _codigoTipoMaterial.value,
                    "listaUnidades" to _listaUnidades.value
                )
                val existente = borradorDao.getAll().find { it.id == borradorSesionId }
                val entity = existente?.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos)
                ) ?: BorradorEntity(
                    id = borradorSesionId, tipo = "MATERIAL",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos), estado = "BORRADOR_AUTO"
                )
                borradorDao.upsert(entity)
                Log.d("Autoguardado Material", "Borrador guardado: $borradorSesionId")
            } catch (e: Exception) {
                Log.e("Error Autoguardado Material", "Error al guardar: ${e.message}", e)
            }
        }
    }

    fun cargarBorradorPorId(id: String) {
        viewModelScope.launch {
            try {
                val borrador = borradorDao.getAll().find { it.id == id } ?: return@launch
                borradorSesionId = borrador.id
                val datos: Map<String, Any?> = Gson().fromJson(
                    borrador.datos,
                    object : TypeToken<Map<String, Any?>>() {}.type
                )
                _empresaSubministradora.value = datos["empresaSubministradora"] as? String ?: ""
                _codigoEmpresa.value = datos["codigoEmpresa"] as? String ?: ""
                _tipoEnviamiento.value = datos["tipoEnviamiento"] as? String ?: ""
                codigoTipoEnvio = datos["codigoTipoEnvio"] as? String ?: ""
                _destinoLliurament.value = datos["destinoLliurament"] as? String ?: ""
                codiDestinoEnvio = datos["codiDestinoEnvio"] as? String ?: ""
                _oficinaComarcal.value = datos["oficinaComarcal"] as? String ?: ""
                codigoOC = datos["codigoOC"] as? String ?: ""
                _direccion.value = datos["direccion"] as? String ?: ""
                _poblacion.value = datos["poblacion"] as? String ?: ""
                _codigoPostal.value = datos["codigoPostal"] as? String ?: ""
                _municipio.value = datos["municipio"] as? String ?: ""
                _telefonoContacto.value = datos["telefonoContacto"] as? String ?: ""
                _tipoMaterial.value = datos["tipoMaterial"] as? String ?: ""
                _codigoTipoMaterial.value = datos["codigoTipoMaterial"] as? String ?: ""
                val listaJson = datos["listaUnidades"] as? List<*>
                if (listaJson != null) {
                    val listaRestaurada = listaJson.mapNotNull { item ->
                        val m = item as? Map<*, *>
                        Unitat(
                            codiExplotacio = m?.get("codiExplotacio") as? String ?: "",
                            nombreUnitats = m?.get("nombreUnitats") as? String ?: ""
                        )
                    }
                    _listaUnidades.value = listaRestaurada.ifEmpty { listOf(Unitat(codiExplotacio = "", nombreUnitats = "")) }
                }
            } catch (e: Exception) {
                Log.e("MaterialVM", "Error al cargar borrador por ID: ${e.message}", e)
            }
        }
    }

    fun eliminarBorradorAutomatico() {
        viewModelScope.launch {
            try {
                if (borradorSesionId.isNotEmpty()) {
                    borradorDao.deleteById(borradorSesionId)
                    borradorSesionId = ""
                }
            } catch (e: Exception) {
                Log.e("Error Eliminar Borrador", "Error: ${e.message}", e)
            }
        }
    }

    // ── Formulario ───────────────────────────────────────────────────────────

    fun agregarUnidades() {
        _listaUnidades.value = (_listaUnidades.value?.toMutableList() ?: mutableListOf()).also { it.add(Unitat(codiExplotacio = "", nombreUnitats = "")) }
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

    fun seleccionarEmpresa(nombre: String, nif: String) { _empresaSubministradora.value = nombre; _codigoEmpresa.value = nif; _empresaExpandida.value = false }
    fun seleccionarTipoEnviamiento(tipo: String, codigo: String) { _tipoEnviamiento.value = tipo; codigoTipoEnvio = codigo; _tipoEnviamientoExpandido.value = false }

    fun seleccionarDestino(destino: String, codigo: String) {
        _destinoLliurament.value = destino
        codiDestinoEnvio = codigo
        when (codigo) {
            "01" -> { _direccion.value = ""; _poblacion.value = ""; _codigoPostal.value = ""; _municipio.value = ""; _telefonoContacto.value = "" }
            "02", "03" -> { _oficinaComarcal.value = ""; codigoOC = "" }
        }
        _destinoExpandido.value = false
    }

    fun seleccionarOficinaComarcal(nombre: String, codigo: String) { _oficinaComarcal.value = nombre; codigoOC = codigo; _oficinaComarcalExpandida.value = false }
    fun seleccionarTipoMaterial(nombre: String, codigo: String) { _tipoMaterial.value = nombre; _codigoTipoMaterial.value = codigo; _tipoMaterialExpandido.value = false }

    fun actualizarDireccion(valor: String) { _direccion.value = valor }
    fun actualizarPoblacion(valor: String) { _poblacion.value = valor }
    fun actualizarCodigoPostal(valor: String) {
        if (valor.length <= 5 && (valor.isEmpty() || valor.all { it.isDigit() })) _codigoPostal.value = valor
    }
    fun actualizarMunicipio(valor: String) { _municipio.value = valor }
    fun actualizarTelefonoContacto(valor: String) {
        if (valor.all { it.isDigit() || it.isWhitespace() }) _telefonoContacto.value = valor
    }
    fun actualizarIdentificadorMaterial(valor: String) { _identificadorMaterial.value = valor }

    fun toggleEmpresaExpandida() { _empresaExpandida.value = !(_empresaExpandida.value ?: false) }
    fun toggleTipoEnviamientoExpandido() { _tipoEnviamientoExpandido.value = !(_tipoEnviamientoExpandido.value ?: false) }
    fun toggleDestinoExpandido() { _destinoExpandido.value = !(_destinoExpandido.value ?: false) }
    fun toggleOficinaComarcalExpandida() { _oficinaComarcalExpandida.value = !(_oficinaComarcalExpandida.value ?: false) }
    fun toggleTipoMaterialExpandido() { _tipoMaterialExpandido.value = !(_tipoMaterialExpandido.value ?: false) }
    fun cerrarEmpresaMenu() { _empresaExpandida.value = false }
    fun cerrarTipoEnviamientoMenu() { _tipoEnviamientoExpandido.value = false }
    fun cerrarDestinoMenu() { _destinoExpandido.value = false }
    fun cerrarOficinaComarcalMenu() { _oficinaComarcalExpandida.value = false }
    fun cerrarTipoMaterialMenu() { _tipoMaterialExpandido.value = false }

    fun getCodigoTipoEnvio(): String = codigoTipoEnvio
    fun getCodiDestinoEnvio(): String = codiDestinoEnvio
    fun getCodigoTipoMaterial(): String = _codigoTipoMaterial.value ?: ""

    fun esFormularioMaterialValido(): Boolean {
        if (_empresaSubministradora.value.isNullOrEmpty()) return false
        if (_tipoEnviamiento.value.isNullOrEmpty()) return false
        if (_destinoLliurament.value.isNullOrEmpty()) return false
        if (_tipoMaterial.value.isNullOrEmpty()) return false
        when (codiDestinoEnvio) {
            "01" -> if (codigoOC.isEmpty()) return false
            "03" -> {
                if (_direccion.value.isNullOrEmpty()) return false
                if (_poblacion.value.isNullOrEmpty()) return false
                if (_codigoPostal.value.isNullOrEmpty()) return false
                if (_municipio.value.isNullOrEmpty()) return false
                if (_telefonoContacto.value.isNullOrEmpty()) return false
            }
        }
        val unidades = _listaUnidades.value ?: return false
        if (unidades.isEmpty()) return false
        if (unidades.any { it.nombreUnitats.isNullOrEmpty() }) return false
        if (codiMoEsObligatorio() && unidades.any { it.codiExplotacio.isNullOrEmpty() }) return false
        return true
    }

    fun solicitarMaterial() {
        if (!esFormularioMaterialValido()) {
            _mensajeErrorMaterial.value = when {
                _empresaSubministradora.value.isNullOrEmpty() -> "Por favor, seleccione la empresa subministradora"
                _tipoEnviamiento.value.isNullOrEmpty() -> "Por favor, seleccione el tipo de envío"
                _destinoLliurament.value.isNullOrEmpty() -> "Por favor, seleccione el destino de entrega"
                codiDestinoEnvio == "01" && codigoOC.isEmpty() -> "Por favor, seleccione la oficina comarcal"
                codiDestinoEnvio == "03" && _direccion.value.isNullOrEmpty() -> "Por favor, introduzca la dirección"
                codiDestinoEnvio == "03" && _poblacion.value.isNullOrEmpty() -> "Por favor, introduzca la población"
                codiDestinoEnvio == "03" && _codigoPostal.value.isNullOrEmpty() -> "Por favor, introduzca el código postal"
                codiDestinoEnvio == "03" && _municipio.value.isNullOrEmpty() -> "Por favor, introduzca el municipio"
                codiDestinoEnvio == "03" && _telefonoContacto.value.isNullOrEmpty() -> "Por favor, introduzca el teléfono de contacto"
                _tipoMaterial.value.isNullOrEmpty() -> "Por favor, seleccione el tipo de material"
                _listaUnidades.value?.any { it.nombreUnitats.isNullOrEmpty() } == true -> "Por favor, introduzca el número de unidades en cada fila"
                codiMoEsObligatorio() && _listaUnidades.value?.any { it.codiExplotacio.isNullOrEmpty() } == true -> "El Codi MO es obligatorio para el tipo de material seleccionado"
                else -> "Por favor, complete todos los campos obligatorios"
            }
            Log.e("Validación Material", _mensajeErrorMaterial.value ?: "")
            return
        }
        viewModelScope.launch {
            _cargandoMaterial.postValue(true)
            try {
                val adrecaFinal = if (codiDestinoEnvio == "03") _direccion.value else null
                val poblacionFinal = if (codiDestinoEnvio == "03") _poblacion.value else null
                val cpFinal = if (codiDestinoEnvio == "03") _codigoPostal.value else null
                val municipioFinal = if (codiDestinoEnvio == "03") _municipio.value else null
                val telefonoFinal = if (codiDestinoEnvio == "03") _telefonoContacto.value else null
                val ocFinal = if (codiDestinoEnvio == "01") codigoOC else null
                val request = PetSolicitudMaterial(
                    nif = nif, passwordMobilitat = password, especie = "01",
                    empresaSubministradora = _codigoEmpresa.value ?: "",
                    tipusEnviament = codigoTipoEnvio,
                    adrecaLliurament = codiDestinoEnvio,
                    oc = ocFinal,
                    adreca = if (codiDestinoEnvio == "02") _direccion.value?.takeIf { it.isNotEmpty() } else adrecaFinal,
                    poblacio = if (codiDestinoEnvio == "02") _poblacion.value?.takeIf { it.isNotEmpty() } else poblacionFinal,
                    cp = if (codiDestinoEnvio == "02") _codigoPostal.value?.takeIf { it.isNotEmpty() } else cpFinal,
                    municipi = if (codiDestinoEnvio == "02") _municipio.value?.takeIf { it.isNotEmpty() } else municipioFinal,
                    telefonContacte = if (codiDestinoEnvio == "02") _telefonoContacto.value?.takeIf { it.isNotEmpty() } else telefonoFinal,
                    tipusMaterial = _codigoTipoMaterial.value ?: "",
                    unitats = _listaUnidades.value ?: listOf(Unitat(codiExplotacio = null, nombreUnitats = "1"))
                )
                Log.d("Solicitud Material", "Request: $request")
                val response = repositorio.putSolicitudMaterial(request)
                withContext(Dispatchers.Main) {
                    _cargandoMaterial.value = false
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroMaterialExitoso.value = true
                                _mensajeErrorMaterial.value = ""
                                guardarEnHistorial("Solicitud de material enviada")
                                guardarHistorialCampos()
                                eliminarBorradorAutomatico()
                                limpiarFormularioMaterial()
                            } else {
                                _registroMaterialExitoso.value = false
                                _mensajeErrorMaterial.value = "Error: [${body.codi}] ${body.descripcio}"
                            }
                        }
                        !response.isSuccessful -> {
                            _registroMaterialExitoso.value = false
                            _mensajeErrorMaterial.value = "Error HTTP ${response.code()}: ${response.message()}"
                        }
                        else -> {
                            _registroMaterialExitoso.value = false
                            _mensajeErrorMaterial.value = "Error: Respuesta vacía del servidor"
                        }
                    }
                }
            } catch (e: SocketTimeoutException) {
                withContext(Dispatchers.Main) { _cargandoMaterial.value = false; _registroMaterialExitoso.value = false; _mensajeErrorMaterial.value = "Tiempo de espera agotado." }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) { _cargandoMaterial.value = false; _registroMaterialExitoso.value = false; _mensajeErrorMaterial.value = "Error de conexión." }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { _cargandoMaterial.value = false; _registroMaterialExitoso.value = false; _mensajeErrorMaterial.value = "Error inesperado: ${e.message ?: "Error desconocido"}"; e.printStackTrace() }
            }
        }
    }

    fun limpiarFormularioMaterial() {
        _empresaSubministradora.value = ""; _codigoEmpresa.value = ""
        _tipoEnviamiento.value = ""; codigoTipoEnvio = ""
        _destinoLliurament.value = ""; codiDestinoEnvio = ""
        _oficinaComarcal.value = ""; codigoOC = ""
        _direccion.value = ""; _poblacion.value = ""; _codigoPostal.value = ""
        _municipio.value = ""; _telefonoContacto.value = ""
        _tipoMaterial.value = ""; _codigoTipoMaterial.value = ""
        _listaUnidades.value = listOf(Unitat(codiExplotacio = "", nombreUnitats = ""))
        borradorSesionId = ""
    }

    fun resetearEstadoRegistroMaterial() { _registroMaterialExitoso.value = false; _mensajeErrorMaterial.value = "" }

    private fun guardarHistorialCampos() {
        viewModelScope.launch {
            _listaUnidades.value?.forEach { unitat ->
                if (!unitat.codiExplotacio.isNullOrBlank()) {
                    historialCamposManager.guardarValor("codi_mo", unitat.codiExplotacio)
                }
            }
        }
    }

    private fun guardarEnHistorial(resumen: String = "") {
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "empresaSubministradora" to _empresaSubministradora.value,
                    "codigoEmpresa" to _codigoEmpresa.value,
                    "tipoEnviamiento" to _tipoEnviamiento.value,
                    "codigoTipoEnvio" to codigoTipoEnvio,
                    "destinoLliurament" to _destinoLliurament.value,
                    "codiDestinoEnvio" to codiDestinoEnvio,
                    "oficinaComarcal" to _oficinaComarcal.value,
                    "codigoOC" to codigoOC,
                    "direccion" to _direccion.value,
                    "poblacion" to _poblacion.value,
                    "codigoPostal" to _codigoPostal.value,
                    "municipio" to _municipio.value,
                    "telefonoContacto" to _telefonoContacto.value,
                    "tipoMaterial" to _tipoMaterial.value,
                    "codigoTipoMaterial" to _codigoTipoMaterial.value,
                    "listaUnidades" to _listaUnidades.value
                )
                historialDao.insert(HistorialEntity(
                    id = UUID.randomUUID().toString(),
                    tipo = "MATERIAL",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos),
                    resumen = resumen
                ))
            } catch (e: Exception) {
                Log.e("Historial", "Error: ${e.message}", e)
            }
        }
    }

    fun cargarDesdeHistorial(id: String) {
        viewModelScope.launch {
            try {
                val registro = historialDao.getAll().find { it.id == id } ?: return@launch
                val datos: Map<String, Any?> = Gson().fromJson(
                    registro.datos,
                    object : TypeToken<Map<String, Any?>>() {}.type
                )
                _empresaSubministradora.value = datos["empresaSubministradora"] as? String ?: ""
                _codigoEmpresa.value = datos["codigoEmpresa"] as? String ?: ""
                _tipoEnviamiento.value = datos["tipoEnviamiento"] as? String ?: ""
                codigoTipoEnvio = datos["codigoTipoEnvio"] as? String ?: ""
                _destinoLliurament.value = datos["destinoLliurament"] as? String ?: ""
                codiDestinoEnvio = datos["codiDestinoEnvio"] as? String ?: ""
                _oficinaComarcal.value = datos["oficinaComarcal"] as? String ?: ""
                codigoOC = datos["codigoOC"] as? String ?: ""
                _direccion.value = datos["direccion"] as? String ?: ""
                _poblacion.value = datos["poblacion"] as? String ?: ""
                _codigoPostal.value = datos["codigoPostal"] as? String ?: ""
                _municipio.value = datos["municipio"] as? String ?: ""
                _telefonoContacto.value = datos["telefonoContacto"] as? String ?: ""
                _tipoMaterial.value = datos["tipoMaterial"] as? String ?: ""
                _codigoTipoMaterial.value = datos["codigoTipoMaterial"] as? String ?: ""
                val listaJson = datos["listaUnidades"] as? List<*>
                if (listaJson != null) {
                    val listaRestaurada = listaJson.mapNotNull { item ->
                        val m = item as? Map<*, *>
                        Unitat(
                            codiExplotacio = m?.get("codiExplotacio") as? String ?: "",
                            nombreUnitats = m?.get("nombreUnitats") as? String ?: ""
                        )
                    }
                    _listaUnidades.value = listaRestaurada.ifEmpty { listOf(Unitat(codiExplotacio = "", nombreUnitats = "")) }
                }
            } catch (e: Exception) {
                Log.e("MaterialVM", "Error al cargar desde historial: ${e.message}", e)
            }
        }
    }
}