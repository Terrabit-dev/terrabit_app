package com.example.terrabit_app.viewmodel.bovinos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.local.database.BorradorEntity
import com.example.terrabit_app.data.local.database.HistorialEntity
import com.example.terrabit_app.data.network.Identificadores.IdenSolicitudDupli
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.material.PetSolicitudDuplicado
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MaterialDuplicadoViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
    private val borradorDao: BorradorDao,
    private val historialDao: HistorialDao
) : ViewModel() {

    private var borradorSesionId: String = "material_duplicado_auto_${System.currentTimeMillis()}"

    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""
    var codiMo = userPreferences.getCodiMO() ?: ""

    private val _suggestionsBovinos = MutableLiveData<List<Animal>>(emptyList())
    val suggestionsBovinos = _suggestionsBovinos

    private val _isLoadingBovinos = MutableLiveData(false)
    val isLoadingBovinos = _isLoadingBovinos

    private val _bovinosCargados = MutableLiveData(false)
    val bovinosCargados = _bovinosCargados

    private val _activeFieldIndex = MutableLiveData<Int>(-1)
    val activeFieldIndex = _activeFieldIndex

    private val _listaAnimales = MutableLiveData<List<IdenSolicitudDupli>>(
        listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = ""))
    )
    val listaAnimales = _listaAnimales

    private val _empresaSubministradora = MutableLiveData("")
    val empresaSubministradora = _empresaSubministradora
    private var codigoEmpresaSubministradora = ""

    private val _tipoEnviamiento = MutableLiveData("")
    val tipoEnviamiento = _tipoEnviamiento
    private var codigoTipoEnviamiento = ""

    private val _direccionEnvio = MutableLiveData("")
    val direccionEnvio = _direccionEnvio
    private var codigoDireccionEnvio = ""

    private val _oficinaComarcal = MutableLiveData("")
    val oficinaComarcal = _oficinaComarcal
    private var codigoOficinaComarcal = ""

    private val _dirrecionEnvio = MutableLiveData("")
    val dirrecionEnvio = _dirrecionEnvio

    private val _poblacion = MutableLiveData("")
    val poblacion = _poblacion

    private val _codigoPostal = MutableLiveData("")
    val codigoPostal = _codigoPostal

    private val _municipio = MutableLiveData("")
    val municipio = _municipio

    private val _telefonoContacto = MutableLiveData("")
    val telefonoContacto = _telefonoContacto

    private val _tipoMaterialExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val tipoMaterialExpandidoPorIndice = _tipoMaterialExpandidoPorIndice

    private val _empresaExpandida = MutableLiveData(false)
    val empresaExpandida = _empresaExpandida

    private val _tipoEnviamientoExpandido = MutableLiveData(false)
    val tipoEnviamientoExpandido = _tipoEnviamientoExpandido

    private val _direccionEnvioExpandido = MutableLiveData(false)
    val direccionEnvioExpandido = _direccionEnvioExpandido

    private val _oficinaComarcalExpandido = MutableLiveData(false)
    val oficinaComarcalExpandido = _oficinaComarcalExpandido

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    private val _cargando = MutableLiveData(false)
    val cargando = _cargando

    init {
        cargarBovinosEnCache()
    }

    private fun cargarBovinosEnCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoadingBovinos.postValue(true)
                codiMo = userPreferences.getCodiMO() ?: ""
                repositorio.getBovinosWithCache(
                    nif = nif,
                    password = password,
                    tipusVinculacio = "1",
                    explotacio = codiMo,
                    forceRefresh = true
                )
                _bovinosCargados.postValue(true)
                _isLoadingBovinos.postValue(false)
            } catch (e: Exception) {
                _isLoadingBovinos.postValue(false)
                _bovinosCargados.postValue(false)
                Log.e("MaterialDuplicadoVM", "Error al cargar bovinos: ${e.message}", e)
            }
        }
    }

    // ── Borrador ─────────────────────────────────────────────────────────────

    fun tieneContenido(): Boolean {
        return !_empresaSubministradora.value.isNullOrEmpty() ||
                !_tipoEnviamiento.value.isNullOrEmpty() ||
                !_direccionEnvio.value.isNullOrEmpty() ||
                (_listaAnimales.value?.any { it.identificador.isNotEmpty() } == true)
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) return
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "empresaSubministradora" to _empresaSubministradora.value,
                    "codigoEmpresaSubministradora" to codigoEmpresaSubministradora,
                    "tipoEnviamiento" to _tipoEnviamiento.value,
                    "codigoTipoEnviamiento" to codigoTipoEnviamiento,
                    "direccionEnvio" to _direccionEnvio.value,
                    "codigoDireccionEnvio" to codigoDireccionEnvio,
                    "oficinaComarcal" to _oficinaComarcal.value,
                    "codigoOficinaComarcal" to codigoOficinaComarcal,
                    "dirrecionEnvio" to _dirrecionEnvio.value,
                    "poblacion" to _poblacion.value,
                    "codigoPostal" to _codigoPostal.value,
                    "municipio" to _municipio.value,
                    "telefonoContacto" to _telefonoContacto.value,
                    "listaAnimales" to _listaAnimales.value
                )
                val existente = borradorDao.getAll().find { it.id == borradorSesionId }
                val entity = existente?.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos)
                ) ?: BorradorEntity(
                    id = borradorSesionId, tipo = "MATERIAL_DUPLICADO",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos), estado = "BORRADOR_AUTO"
                )
                borradorDao.upsert(entity)
                Log.d("Autoguardado MaterialDuplicado", "Borrador guardado: $borradorSesionId")
            } catch (e: Exception) {
                Log.e("Error Autoguardado MaterialDuplicado", "Error al guardar: ${e.message}", e)
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
                codigoEmpresaSubministradora = datos["codigoEmpresaSubministradora"] as? String ?: ""
                _tipoEnviamiento.value = datos["tipoEnviamiento"] as? String ?: ""
                codigoTipoEnviamiento = datos["codigoTipoEnviamiento"] as? String ?: ""
                _direccionEnvio.value = datos["direccionEnvio"] as? String ?: ""
                codigoDireccionEnvio = datos["codigoDireccionEnvio"] as? String ?: ""
                _oficinaComarcal.value = datos["oficinaComarcal"] as? String ?: ""
                codigoOficinaComarcal = datos["codigoOficinaComarcal"] as? String ?: ""
                _dirrecionEnvio.value = datos["dirrecionEnvio"] as? String ?: ""
                _poblacion.value = datos["poblacion"] as? String ?: ""
                _codigoPostal.value = datos["codigoPostal"] as? String ?: ""
                _municipio.value = datos["municipio"] as? String ?: ""
                _telefonoContacto.value = datos["telefonoContacto"] as? String ?: ""
                val listaJson = datos["listaAnimales"] as? List<*>
                if (listaJson != null) {
                    val listaRestaurada = listaJson.mapNotNull { item ->
                        val m = item as? Map<*, *>
                        IdenSolicitudDupli(
                            identificador = m?.get("identificador") as? String ?: "",
                            tipusMaterial = m?.get("tipusMaterial") as? String ?: ""
                        )
                    }
                    _listaAnimales.value = listaRestaurada.ifEmpty { listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = "")) }
                }
            } catch (e: Exception) {
                Log.e("MaterialDuplicadoVM", "Error al cargar borrador por ID: ${e.message}", e)
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

    fun searchBovinos(index: Int, query: String) {
        _activeFieldIndex.value = index
        if (query.isBlank()) { _suggestionsBovinos.value = emptyList(); return }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultados = repositorio.searchBovinosLocal(query)
                _suggestionsBovinos.postValue(resultados)
            } catch (e: Exception) {
                _suggestionsBovinos.postValue(emptyList())
            }
        }
    }

    fun onBovinoSelected(index: Int, animal: Animal) {
        actualizarIdentificador(index, animal.identificador)
        _suggestionsBovinos.value = emptyList()
        _activeFieldIndex.value = -1
    }

    fun actualizarIdentificador(indice: Int, identificador: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(identificador = identificador) else animal
        }
    }

    fun seleccionarTipoMaterialIdentificador(indice: Int, codigoTipo: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(tipusMaterial = codigoTipo) else animal
        }
        _tipoMaterialExpandidoPorIndice.value = (_tipoMaterialExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun toggleTipoMaterialExpandido(indice: Int) {
        val mapa = _tipoMaterialExpandidoPorIndice.value ?: emptyMap()
        _tipoMaterialExpandidoPorIndice.value = mapa + (indice to !(mapa[indice] ?: false))
    }

    fun cerrarTipoMaterialMenu(indice: Int) {
        _tipoMaterialExpandidoPorIndice.value = (_tipoMaterialExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun agregarAnimal() {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()) + IdenSolicitudDupli(identificador = "", tipusMaterial = "")
    }

    fun eliminarAnimal(indice: Int) {
        val lista = _listaAnimales.value ?: emptyList()
        if (lista.size > 1) {
            _listaAnimales.value = lista.filterIndexed { index, _ -> index != indice }
            _tipoMaterialExpandidoPorIndice.value = _tipoMaterialExpandidoPorIndice.value?.minus(indice)
        }
    }

    fun toggleEmpresaExpandida() { _empresaExpandida.value = !(_empresaExpandida.value ?: false) }
    fun toggleTipoEnviamientoExpandido() { _tipoEnviamientoExpandido.value = !(_tipoEnviamientoExpandido.value ?: false) }
    fun toggleDireccionEnvioExpandido() { _direccionEnvioExpandido.value = !(_direccionEnvioExpandido.value ?: false) }
    fun toggleOficinaComarcalExpandido() { _oficinaComarcalExpandido.value = !(_oficinaComarcalExpandido.value ?: false) }
    fun cerrarEmpresaMenu() { _empresaExpandida.value = false }
    fun cerrarTipoEnviamientoMenu() { _tipoEnviamientoExpandido.value = false }
    fun cerrarDireccionEnvioMenu() { _direccionEnvioExpandido.value = false }
    fun cerrarOficinaComarcalMenu() { _oficinaComarcalExpandido.value = false }

    fun seleccionarEmpresa(codigo: String, nombre: String) { _empresaSubministradora.value = nombre; codigoEmpresaSubministradora = codigo; cerrarEmpresaMenu() }
    fun seleccionarTipoEnviamiento(codigo: String, nombre: String) { _tipoEnviamiento.value = nombre; codigoTipoEnviamiento = codigo; cerrarTipoEnviamientoMenu() }

    fun seleccionarDireccionEnvio(codigo: String, nombre: String) {
        _direccionEnvio.value = nombre; codigoDireccionEnvio = codigo; cerrarDireccionEnvioMenu()
        when (codigo) {
            "01" -> { _dirrecionEnvio.value = ""; _poblacion.value = ""; _codigoPostal.value = ""; _municipio.value = ""; _telefonoContacto.value = "" }
            "02", "03" -> { _oficinaComarcal.value = ""; codigoOficinaComarcal = "" }
        }
    }

    fun seleccionarOficinaComarcal(codigo: String, nombre: String) { _oficinaComarcal.value = nombre; codigoOficinaComarcal = codigo; cerrarOficinaComarcalMenu() }
    fun actualizarDireccionEnvio(valor: String) { _dirrecionEnvio.value = valor }
    fun actualizarPoblacion(valor: String) { _poblacion.value = valor }
    fun actualizarCodigoPostal(valor: String) {
        if (valor.length <= 5 && (valor.isEmpty() || valor.all { it.isDigit() })) _codigoPostal.value = valor
    }
    fun actualizarMunicipio(valor: String) { _municipio.value = valor }
    fun actualizarTelefonoContacto(valor: String) {
        if (valor.all { it.isDigit() || it.isWhitespace() }) _telefonoContacto.value = valor
    }
    fun getCodigoDirecioEnvio(): String = codigoDireccionEnvio

    fun esFormularioValido(): Boolean {
        if (codigoEmpresaSubministradora.isEmpty()) return false
        if (codigoTipoEnviamiento.isEmpty()) return false
        if (codigoDireccionEnvio.isEmpty()) return false
        when (codigoDireccionEnvio) {
            "01" -> if (codigoOficinaComarcal.isEmpty()) return false
            "03" -> {
                if (_dirrecionEnvio.value.isNullOrEmpty()) return false
                if (_poblacion.value.isNullOrEmpty()) return false
                if (_codigoPostal.value.isNullOrEmpty()) return false
                if (_municipio.value.isNullOrEmpty()) return false
                if (_telefonoContacto.value.isNullOrEmpty()) return false
            }
        }
        val identificadores = _listaAnimales.value ?: return false
        if (identificadores.isEmpty()) return false
        if (identificadores.any { it.identificador.isEmpty() || it.tipusMaterial.isEmpty() }) return false
        return true
    }

    fun solicitarDuplicado() {
        if (!esFormularioValido()) {
            _mensajeError.value = when {
                codigoEmpresaSubministradora.isEmpty() -> "Por favor, seleccione la empresa subministradora"
                codigoTipoEnviamiento.isEmpty() -> "Por favor, seleccione el tipo de envío"
                codigoDireccionEnvio.isEmpty() -> "Por favor, seleccione la dirección de envío"
                codigoDireccionEnvio == "01" && codigoOficinaComarcal.isEmpty() -> "Por favor, seleccione la oficina comarcal"
                codigoDireccionEnvio == "03" && _dirrecionEnvio.value.isNullOrEmpty() -> "Por favor, introduzca la dirección"
                codigoDireccionEnvio == "03" && _poblacion.value.isNullOrEmpty() -> "Por favor, introduzca la población"
                codigoDireccionEnvio == "03" && _codigoPostal.value.isNullOrEmpty() -> "Por favor, introduzca el código postal"
                codigoDireccionEnvio == "03" && _municipio.value.isNullOrEmpty() -> "Por favor, introduzca el municipio"
                codigoDireccionEnvio == "03" && _telefonoContacto.value.isNullOrEmpty() -> "Por favor, introduzca el teléfono de contacto"
                (_listaAnimales.value?.any { it.identificador.isEmpty() } == true) -> "Por favor, complete todos los identificadores"
                (_listaAnimales.value?.any { it.tipusMaterial.isEmpty() } == true) -> "Por favor, seleccione el tipo de material para cada identificador"
                else -> "Por favor, complete todos los campos obligatorios"
            }
            return
        }
        viewModelScope.launch {
            _cargando.postValue(true)
            try {
                val request = PetSolicitudDuplicado(
                    nif = nif, passwordMobilitat = password, especie = "01",
                    empresaSubministradora = codigoEmpresaSubministradora,
                    tipusEnviament = codigoTipoEnviamiento,
                    adrecaLliurament = codigoDireccionEnvio,
                    oc = if (codigoDireccionEnvio == "01") codigoOficinaComarcal else null,
                    adreca = if (codigoDireccionEnvio == "03") _dirrecionEnvio.value else null,
                    poblacio = if (codigoDireccionEnvio == "03") _poblacion.value else null,
                    cp = if (codigoDireccionEnvio == "03") _codigoPostal.value else null,
                    municipi = if (codigoDireccionEnvio == "03") _municipio.value else null,
                    telefonContacte = if (codigoDireccionEnvio == "03") _telefonoContacto.value else null,
                    identificadors = _listaAnimales.value ?: emptyList()
                )
                val response = repositorio.putSolicitudDuplicado(request)
                withContext(Dispatchers.Main) {
                    _cargando.value = false
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroExitoso.value = true; _mensajeError.value = ""
                                guardarEnHistorial("Solicitud de duplicado enviada")
                                eliminarBorradorAutomatico()
                                limpiarFormulario()
                            } else {
                                _registroExitoso.value = false
                                _mensajeError.value = "Error: [${body.codi}] ${body.descripcio}"
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    _mensajeError.value = errorObj.errors?.firstOrNull()?.descripcio ?: "Error desconocido del servidor"
                                } catch (e: Exception) { _mensajeError.value = "Error al procesar respuesta" }
                            }
                            _registroExitoso.value = false
                        }
                        else -> { _registroExitoso.value = false; _mensajeError.value = "Error: Respuesta vacía del servidor" }
                    }
                }
            } catch (e: SocketTimeoutException) {
                withContext(Dispatchers.Main) { _cargando.value = false; _registroExitoso.value = false; _mensajeError.value = "Tiempo de espera agotado." }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) { _cargando.value = false; _registroExitoso.value = false; _mensajeError.value = "Error de conexión." }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { _cargando.value = false; _registroExitoso.value = false; _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}" }
            }
        }
    }

    fun limpiarFormulario() {
        _empresaSubministradora.value = ""; codigoEmpresaSubministradora = ""
        _tipoEnviamiento.value = ""; codigoTipoEnviamiento = ""
        _direccionEnvio.value = ""; codigoDireccionEnvio = ""
        _oficinaComarcal.value = ""; codigoOficinaComarcal = ""
        _dirrecionEnvio.value = ""; _poblacion.value = ""; _codigoPostal.value = ""
        _municipio.value = ""; _telefonoContacto.value = ""
        _listaAnimales.value = listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = ""))
        _tipoMaterialExpandidoPorIndice.value = emptyMap()
        borradorSesionId = ""
    }

    fun resetearEstado() { _registroExitoso.value = false; _mensajeError.value = "" }

    private fun guardarEnHistorial(resumen: String = "") {
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "empresaSubministradora" to _empresaSubministradora.value,
                    "codigoEmpresaSubministradora" to codigoEmpresaSubministradora,
                    "tipoEnviamiento" to _tipoEnviamiento.value,
                    "codigoTipoEnviamiento" to codigoTipoEnviamiento,
                    "direccionEnvio" to _direccionEnvio.value,
                    "codigoDireccionEnvio" to codigoDireccionEnvio,
                    "oficinaComarcal" to _oficinaComarcal.value,
                    "codigoOficinaComarcal" to codigoOficinaComarcal,
                    "dirrecionEnvio" to _dirrecionEnvio.value,
                    "poblacion" to _poblacion.value,
                    "codigoPostal" to _codigoPostal.value,
                    "municipio" to _municipio.value,
                    "telefonoContacto" to _telefonoContacto.value,
                    "listaAnimales" to _listaAnimales.value
                )
                historialDao.insert(HistorialEntity(
                    id = UUID.randomUUID().toString(),
                    tipo = "MATERIAL_DUPLICADO",
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
                codigoEmpresaSubministradora = datos["codigoEmpresaSubministradora"] as? String ?: ""
                _tipoEnviamiento.value = datos["tipoEnviamiento"] as? String ?: ""
                codigoTipoEnviamiento = datos["codigoTipoEnviamiento"] as? String ?: ""
                _direccionEnvio.value = datos["direccionEnvio"] as? String ?: ""
                codigoDireccionEnvio = datos["codigoDireccionEnvio"] as? String ?: ""
                _oficinaComarcal.value = datos["oficinaComarcal"] as? String ?: ""
                codigoOficinaComarcal = datos["codigoOficinaComarcal"] as? String ?: ""
                _dirrecionEnvio.value = datos["dirrecionEnvio"] as? String ?: ""
                _poblacion.value = datos["poblacion"] as? String ?: ""
                _codigoPostal.value = datos["codigoPostal"] as? String ?: ""
                _municipio.value = datos["municipio"] as? String ?: ""
                _telefonoContacto.value = datos["telefonoContacto"] as? String ?: ""
                val listaJson = datos["listaAnimales"] as? List<*>
                if (listaJson != null) {
                    val listaRestaurada = listaJson.mapNotNull { item ->
                        val m = item as? Map<*, *>
                        IdenSolicitudDupli(
                            identificador = m?.get("identificador") as? String ?: "",
                            tipusMaterial = m?.get("tipusMaterial") as? String ?: ""
                        )
                    }
                    _listaAnimales.value = listaRestaurada.ifEmpty { listOf(IdenSolicitudDupli(identificador = "", tipusMaterial = "")) }
                }
            } catch (e: Exception) {
                Log.e("MaterialDuplicadoVM", "Error al cargar desde historial: ${e.message}", e)
            }
        }
    }
}