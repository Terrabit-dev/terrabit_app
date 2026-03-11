package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.IdenMovimiento
import com.example.terrabit_app.data.network.lista_bovinos.Animal
import com.example.terrabit_app.data.network.moviminetos.modelos.Movimientos
import com.example.terrabit_app.data.network.moviminetos.modelos.PetConfirmacionMovi
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils.convertirFechaAFormatoAPI
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.local.database.BorradorEntity
import com.example.terrabit_app.data.local.database.HistorialEntity
import com.example.terrabit_app.data.local.database.toBorrador
import java.util.UUID

@HiltViewModel
class MovimientosViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences,
    private val borradorDao: BorradorDao,
    private val historialDao: HistorialDao
) : ViewModel() {

    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""
    val codiMo = userPreferences.getCodiMO() ?: ""
    val listaCodigosAtes = listOf(CodigoAtes("D", "D - Transportista"))

    private var borradorSesionId: String = ""

    private val _suggestionsBovinos = MutableLiveData<List<Animal>>(emptyList())
    val suggestionsBovinos = _suggestionsBovinos

    private val _isLoadingBovinos = MutableLiveData(false)
    val isLoadingBovinos = _isLoadingBovinos

    private val _bovinosCargados = MutableLiveData(false)
    val bovinosCargados = _bovinosCargados

    private val _activeFieldIndex = MutableLiveData<Int>(-1)
    val activeFieldIndex = _activeFieldIndex

    private val _movimientosPendientes = MutableLiveData<Movimientos?>()
    val movimientosPendientes = _movimientosPendientes

    private val _codiRemo = MutableLiveData("")
    val codiRemo = _codiRemo

    private val _dataArribada = MutableLiveData("")
    val dataArribada = _dataArribada

    private val _horaArribada = MutableLiveData("")
    val horaArribada = _horaArribada

    private val _codiAtes = MutableLiveData("")
    val codiAtes = _codiAtes

    private val _nomTransportista = MutableLiveData("")
    val nomTransportista = _nomTransportista

    private val _matricula = MutableLiveData("")
    val matricula = _matricula

    private val _mitjaTransport = MutableLiveData("")
    val mitjaTransport = _mitjaTransport

    private val _codiTransport = MutableLiveData("")
    val codiTransport = _codiTransport

    private val _codiEstats = MutableLiveData("")
    val codiEstats = _codiEstats

    private val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    private val _nifConductor = MutableLiveData("")
    val nifConductor = _nifConductor

    private val _nomConductor = MutableLiveData("")
    val nomConductor = _nomConductor

    private val _explotacioDestinacio = MutableLiveData("")
    val explotacioDestinacio = _explotacioDestinacio

    private val _identificadorAnimal = MutableLiveData("")
    val identificadorAnimal = _identificadorAnimal

    private val _estatArribada = MutableLiveData("")
    val estatArribada = _estatArribada

    private val _codiAtesExpandido = MutableLiveData(false)
    val codiAtesExpandido = _codiAtesExpandido

    private val _mitjaTransportExpandido = MutableLiveData(false)
    val mitjaTransportExpandido = _mitjaTransportExpandido

    private val _estatArribadaExpandido = MutableLiveData(false)
    val estatArribadaExpandido = _estatArribadaExpandido

    private val _mostrarDatePickerArribada = MutableLiveData(false)
    val mostrarDatePickerArribada = _mostrarDatePickerArribada

    private val _mostrarTimePickerArribada = MutableLiveData(false)
    val mostrarTimePickerArribada = _mostrarTimePickerArribada

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    private val _cargandoMovimiento = MutableLiveData(false)
    val cargandoMovimiento = _cargandoMovimiento

    private val limiteClassCanel = 5

    private val _listaAnimales = MutableLiveData<List<IdenMovimiento>>(
        listOf(IdenMovimiento(identificador = "", estatArribada = "", classCanal = null, dataSacrMort = null, pesCanal = null, tipusPresentacio = null))
    )
    val listaAnimales = _listaAnimales

    private val _estatArribadaExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val estatArribadaExpandidoPorIndice = _estatArribadaExpandidoPorIndice

    private val _classCanalExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val classCanalExpandidoPorIndice = _classCanalExpandidoPorIndice

    private val _tipusPresentacioExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val tipusPresentacioExpandidoPorIndice = _tipusPresentacioExpandidoPorIndice

    private val _mostrarDatePickerPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val mostrarDatePickerPorIndice = _mostrarDatePickerPorIndice

    private val _textoEstatArribadaPorIndice = MutableLiveData<Map<Int, String>>(emptyMap())
    val textoEstatArribadaPorIndice = _textoEstatArribadaPorIndice

    init {
        borradorSesionId = "movimiento_auto_${System.currentTimeMillis()}"
        cargarBovinosEnCache()
    }


    suspend fun obtenerCantidadBorradoresMovimiento(): Int {
        return borradorDao.getAll().count { it.tipo == "MOVIMIENTO" && it.estado == "BORRADOR_AUTO" }
    }
    private fun cargarBovinosEnCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoadingBovinos.postValue(true)
                repositorio.getBovinosWithCache(
                    nif = nif, password = password,
                    tipusVinculacio = "1", explotacio = codiMo, forceRefresh = false
                )
                _bovinosCargados.postValue(true)
                _isLoadingBovinos.postValue(false)
                Log.d("MovimientosVM", "Bovinos cargados en caché")
            } catch (e: Exception) {
                _isLoadingBovinos.postValue(false)
                _bovinosCargados.postValue(false)
                Log.e("MovimientosVM", "Error al cargar bovinos: ${e.message}", e)
            }
        }
    }

    fun searchBovinos(index: Int, query: String) {
        _activeFieldIndex.value = index
        if (query.isBlank()) { _suggestionsBovinos.value = emptyList(); return }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultados = repositorio.searchBovinosLocal(query)
                _suggestionsBovinos.postValue(resultados)
                Log.d("MovimientosVM", "Búsqueda en índice $index: '$query' - ${resultados.size} resultados")
            } catch (e: Exception) {
                _suggestionsBovinos.postValue(emptyList())
                Log.e("MovimientosVM", "Error en búsqueda: ${e.message}", e)
            }
        }
    }

    fun onBovinoSelected(index: Int, animal: Animal) {
        actualizarIdentificadorAnimal(index, animal.identificador)
        _suggestionsBovinos.value = emptyList()
        _activeFieldIndex.value = -1
        Log.d("MovimientosVM", "Bovino seleccionado en índice $index: ${animal.identificador}")
    }

    fun tieneContenido(): Boolean {
        return !_codiRemo.value.isNullOrEmpty() || !_dataArribada.value.isNullOrEmpty() ||
                !_horaArribada.value.isNullOrEmpty() || !_codiAtes.value.isNullOrEmpty() ||
                !_nomTransportista.value.isNullOrEmpty() || !_matricula.value.isNullOrEmpty() ||
                !_mitjaTransport.value.isNullOrEmpty() || !_nifConductor.value.isNullOrEmpty() ||
                !_nomConductor.value.isNullOrEmpty() || !_explotacioDestinacio.value.isNullOrEmpty() ||
                (_listaAnimales.value?.any { it.identificador.isNotEmpty() } == true)
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) return
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "codiRemo" to _codiRemo.value, "dataArribada" to _dataArribada.value,
                    "horaArribada" to _horaArribada.value, "codiAtes" to _codiAtes.value,
                    "nomTransportista" to _nomTransportista.value, "matricula" to _matricula.value,
                    "mitjaTransport" to _mitjaTransport.value, "nifConductor" to _nifConductor.value,
                    "nomConductor" to _nomConductor.value, "explotacioDestinacio" to _explotacioDestinacio.value,
                    "listaAnimales" to _listaAnimales.value, "codiTransport" to _codiTransport.value
                )
                val existente = borradorDao.getAll().find { it.id == borradorSesionId }
                val entity = existente?.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos)
                ) ?: BorradorEntity(
                    id = borradorSesionId, tipo = "MOVIMIENTO",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datos), estado = "BORRADOR_AUTO"
                )
                borradorDao.upsert(entity)
            } catch (e: Exception) {
                Log.e("Error Autoguardado Movimiento", "Error al guardar: ${e.message}", e)
            }
        }
    }

    fun cargarBorradorPorId(id: String) {
        viewModelScope.launch {
            try {
                val borrador = borradorDao.getAll().find { it.id == id } ?: return@launch
                borradorSesionId = borrador.id
                val datos: Map<String, Any?> = Gson().fromJson(borrador.datos, object : TypeToken<Map<String, Any?>>() {}.type)
                _codiRemo.value = datos["codiRemo"] as? String ?: ""
                _dataArribada.value = datos["dataArribada"] as? String ?: ""
                _horaArribada.value = datos["horaArribada"] as? String ?: ""
                _codiAtes.value = datos["codiAtes"] as? String ?: ""
                _nomTransportista.value = datos["nomTransportista"] as? String ?: ""
                _matricula.value = datos["matricula"] as? String ?: ""
                _mitjaTransport.value = datos["mitjaTransport"] as? String ?: ""
                _nifConductor.value = datos["nifConductor"] as? String ?: ""
                _nomConductor.value = datos["nomConductor"] as? String ?: ""
                _explotacioDestinacio.value = datos["explotacioDestinacio"] as? String ?: ""
                _codiTransport.value = datos["codiTransport"] as? String ?: ""
                val listaAnimalesJson = datos["listaAnimales"] as? List<*>
                if (listaAnimalesJson != null) {
                    val listaRestaurada = listaAnimalesJson.mapNotNull { item ->
                        try {
                            val itemMap = item as? Map<*, *>
                            IdenMovimiento(
                                identificador = itemMap?.get("identificador") as? String ?: "",
                                estatArribada = itemMap?.get("estatArribada") as? String,
                                classCanal = itemMap?.get("classCanal") as? String,
                                dataSacrMort = itemMap?.get("dataSacrMort") as? String,
                                pesCanal = itemMap?.get("pesCanal") as? String,
                                tipusPresentacio = itemMap?.get("tipusPresentacio") as? String
                            )
                        } catch (e: Exception) { null }
                    }
                    _listaAnimales.value = listaRestaurada.ifEmpty {
                        listOf(IdenMovimiento(identificador = "", estatArribada = null, classCanal = null, dataSacrMort = null, pesCanal = null, tipusPresentacio = null))
                    }
                }
            } catch (e: Exception) {
                Log.e("MovimientosVM", "Error al cargar borrador por ID: ${e.message}", e)
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

    private val _cargandoLista = MutableLiveData(false)
    val cargandoLista = _cargandoLista

    fun obtenerMovimientosPendientes(nif: String, password: String, explotacioDestinacio: String, dataSortida: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repositorio.getConfirmacionMovimientos(nif, password, explotacioDestinacio, dataSortida)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _movimientosPendientes.value = response.body()
                    Log.d("Movimientos", "Movimientos obtenidos: ${response.body()?.moviments?.size ?: 0}")
                } else {
                    Log.e("Error Movimientos", "HTTP ${response.code()}: ${response.message()}")
                    _mensajeError.value = "Error al obtener movimientos pendientes"
                }
            }
        }
    }



    data class CodigoAtes(val codigo: String, val nombre: String)


    fun actualizarCodiRemo(nuevoValor: String) { _codiRemo.value = nuevoValor }
    fun actualizarDataArribada(nuevaFecha: String) { _dataArribada.value = nuevaFecha }
    fun actualizarHoraArribada(hora: String, minutos: String) { _horaArribada.value = String.format("%02d:%02d", hora.toInt(), minutos.toInt()) }
    fun seleccionarCodiAtes(codigo: String) { _codiAtes.value = codigo; _codiAtesExpandido.value = false }
    fun actualizarNomTransportista(nombre: String) { _nomTransportista.value = nombre }
    fun actualizarMatricula(matricula: String) { _matricula.value = matricula }
    fun seleccionarMitjaTransport(medio: String, codigo: String) { _mitjaTransport.value = medio; _codiTransport.value = codigo; _mitjaTransportExpandido.value = false }
    fun actualizarNifConductor(nif: String) { _nifConductor.value = nif }
    fun actualizarNomConductor(nombre: String) { _nomConductor.value = nombre }
    fun actualizarExplotacioDestinacio(explotacion: String) { _explotacioDestinacio.value = explotacion }
    fun actualizarIdentificadorAnimal(identificador: String) { _identificadorAnimal.value = identificador }
    fun seleccionarEstatArribada(estat: String, codigo: String) { _estatArribada.value = estat; _codiEstats.value = codigo; _estatArribadaExpandido.value = false }
    fun toggleCodiAtesExpandido() { _codiAtesExpandido.value = !(_codiAtesExpandido.value ?: false) }
    fun toggleMitjaTransportExpandido() { _mitjaTransportExpandido.value = !(_mitjaTransportExpandido.value ?: false) }
    fun toggleEstatArribadaExpandido() { _estatArribadaExpandido.value = !(_estatArribadaExpandido.value ?: false) }
    fun cerrarCodiAtesMenu() { _codiAtesExpandido.value = false }
    fun cerrarMitjaTransportMenu() { _mitjaTransportExpandido.value = false }
    fun cerrarEstatArribadaMenu() { _estatArribadaExpandido.value = false }
    fun mostrarDatePickerArribada() { _mostrarDatePickerArribada.value = true }
    fun ocultarDatePickerArribada() { _mostrarDatePickerArribada.value = false }
    fun mostrarTimePickerArribada() { _mostrarTimePickerArribada.value = true }
    fun ocultarTimePickerArribada() { _mostrarTimePickerArribada.value = false }

    fun seleccionarFechaArribada(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        _dataArribada.value = String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
        _mostrarDatePickerArribada.value = false
    }

    fun agregarAnimal() {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()) +
                IdenMovimiento(identificador = "", estatArribada = "", classCanal = null, dataSacrMort = null, pesCanal = null, tipusPresentacio = null)
    }

    fun eliminarAnimal(indice: Int) {
        val listaActual = _listaAnimales.value ?: emptyList()
        if (listaActual.size > 1) {
            _listaAnimales.value = listaActual.filterIndexed { index, _ -> index != indice }
            _estatArribadaExpandidoPorIndice.value = _estatArribadaExpandidoPorIndice.value?.minus(indice)
            _classCanalExpandidoPorIndice.value = _classCanalExpandidoPorIndice.value?.minus(indice)
            _tipusPresentacioExpandidoPorIndice.value = _tipusPresentacioExpandidoPorIndice.value?.minus(indice)
            _mostrarDatePickerPorIndice.value = _mostrarDatePickerPorIndice.value?.minus(indice)
            _textoEstatArribadaPorIndice.value = _textoEstatArribadaPorIndice.value?.minus(indice)
        }
    }

    fun actualizarIdentificadorAnimal(indice: Int, identificador: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(identificador = identificador) else animal
        }
    }

    fun actualizarClassCanal(indice: Int, clase: String) {
        if (clase.length <= limiteClassCanel) {
            _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
                if (index == indice) animal.copy(classCanal = clase) else animal
            }
        }
    }

    fun seleccionarEstatArribadaAnimal(indice: Int, estatTexto: String, estatCodigo: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) {
                if (estatCodigo != "80") animal.copy(estatArribada = estatCodigo, classCanal = null, dataSacrMort = null, pesCanal = null, tipusPresentacio = null)
                else animal.copy(estatArribada = estatCodigo)
            } else animal
        }
        _textoEstatArribadaPorIndice.value = (_textoEstatArribadaPorIndice.value ?: emptyMap()) + (indice to estatCodigo)
        _estatArribadaExpandidoPorIndice.value = (_estatArribadaExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun actualizarDataSacrMort(indice: Int, fecha: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(dataSacrMort = fecha) else animal
        }
    }

    fun actualizarPesCanal(indice: Int, peso: String) {
        if (peso.length <= 5) {
            _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
                if (index == indice) animal.copy(pesCanal = peso) else animal
            }
        }
    }

    fun seleccionarTipusPresentacio(indice: Int, codigo: String) {
        _listaAnimales.value = (_listaAnimales.value ?: emptyList()).mapIndexed { index, animal ->
            if (index == indice) animal.copy(tipusPresentacio = codigo) else animal
        }
        _tipusPresentacioExpandidoPorIndice.value = (_tipusPresentacioExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun toggleEstatArribadaExpandido(indice: Int) {
        val mapa = _estatArribadaExpandidoPorIndice.value ?: emptyMap()
        _estatArribadaExpandidoPorIndice.value = mapa + (indice to !(mapa[indice] ?: false))
    }

    fun toggleTipusPresentacioExpandido(indice: Int) {
        val mapa = _tipusPresentacioExpandidoPorIndice.value ?: emptyMap()
        _tipusPresentacioExpandidoPorIndice.value = mapa + (indice to !(mapa[indice] ?: false))
    }

    fun cerrarEstatArribadaMenu(indice: Int) {
        _estatArribadaExpandidoPorIndice.value = (_estatArribadaExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun cerrarTipusPresentacioMenu(indice: Int) {
        _tipusPresentacioExpandidoPorIndice.value = (_tipusPresentacioExpandidoPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun mostrarDatePickerSacrMort(indice: Int) {
        _mostrarDatePickerPorIndice.value = (_mostrarDatePickerPorIndice.value ?: emptyMap()) + (indice to true)
    }

    fun ocultarDatePickerSacrMort(indice: Int) {
        _mostrarDatePickerPorIndice.value = (_mostrarDatePickerPorIndice.value ?: emptyMap()) + (indice to false)
    }

    fun seleccionarFechaSacrMort(indice: Int, fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        actualizarDataSacrMort(indice, String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)))
        ocultarDatePickerSacrMort(indice)
    }

    fun esFormularioValido(): Boolean {
        val animalesValidos = (_listaAnimales.value ?: emptyList()).all { animal ->
            val camposAdicionales = if (animal.estatArribada == "80") {
                !animal.dataSacrMort.isNullOrEmpty() && !animal.pesCanal.isNullOrEmpty() &&
                        !animal.classCanal.isNullOrEmpty() && !animal.tipusPresentacio.isNullOrEmpty()
            } else true
            animal.identificador.isNotEmpty() && animal.estatArribada?.isNotEmpty() == true && camposAdicionales
        }
        return !_codiRemo.value.isNullOrEmpty() && !_dataArribada.value.isNullOrEmpty() &&
                !_horaArribada.value.isNullOrEmpty() && !_codiAtes.value.isNullOrEmpty() &&
                !_explotacioDestinacio.value.isNullOrEmpty() && animalesValidos
    }

    fun confirmarMovimiento() {
        _codiError.value = null
        if (!esFormularioValido()) {
            _codiError.value = when {
                _codiRemo.value.isNullOrEmpty() -> 14
                _dataArribada.value.isNullOrEmpty() -> 15
                _horaArribada.value.isNullOrEmpty() -> 16
                _codiAtes.value.isNullOrEmpty() -> 17
                _explotacioDestinacio.value.isNullOrEmpty() -> 18
                _identificadorAnimal.value.isNullOrEmpty() -> 12
                _estatArribada.value.isNullOrEmpty() -> 19
                else -> 0
            }
            Log.e("Validación Movimiento", "Error: ${_codiError.value}")
            return
        }
        viewModelScope.launch {
            _cargandoMovimiento.postValue(true)
            try {
                val listaIdentificadoresAPI = (_listaAnimales.value ?: emptyList()).map { animal ->
                    animal.copy(
                        dataSacrMort = if (animal.estatArribada == "80" && animal.dataSacrMort != null)
                            convertirFechaAFormatoAPI(animal.dataSacrMort) else null
                    )
                }
                val request = PetConfirmacionMovi(
                    nif = nif, passwordMobilitat = password, especie = "01",
                    codiRemo = _codiRemo.value ?: "",
                    dataArribada = convertirFechaHoraAFormatoAPI(_dataArribada.value ?: "", _horaArribada.value ?: ""),
                    codiAtes = _codiAtes.value ?: "",
                    nomTransportista = _nomTransportista.value ?: "",
                    mitjaTransport = _codiTransport.value ?: "",
                    matricula = _matricula.value ?: "",
                    nifConductor = _nifConductor.value ?: "",
                    nomConductor = _nomConductor.value ?: "",
                    explotacioDestinacio = _explotacioDestinacio.value ?: "",
                    identificadors = listaIdentificadoresAPI
                )
                Log.d("Confirmar Movimiento", "Request: $request")
                val response = repositorio.putConfirmarMovi(request)
                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!
                            if (body.codiRemo == "0" || body.descripcio?.contains("correcte", ignoreCase = true) == true) {
                                _registroExitoso.value = true; _mensajeError.value = ""
                                Log.d("Confirmar Movimiento", "Movimiento confirmado exitosamente")
                                guardarEnHistorial("Movimiento Registrado")
                                eliminarBorradorAutomatico()
                                limpiarFormulario()
                            } else {
                                _registroExitoso.value = false
                                _mensajeError.value = "Respuesta inesperada del servidor: ${body.descripcio ?: "Sin descripción"}"
                                Log.w("Confirmar Movimiento", "Respuesta inesperada: [${body.codiRemo}] ${body.descripcio}")
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, RespuestaUnificada::class.java)
                                    _mensajeError.value = errorObj.errors?.firstOrNull()?.descripcio ?: "Error desconocido"
                                } catch (e: Exception) { _mensajeError.value = "Error al procesar respuesta" }
                            }
                            _registroExitoso.value = false
                            Log.e("Error Movimiento", "HTTP ${response.code()}")
                        }
                        else -> {
                            _registroExitoso.value = false; _mensajeError.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Movimiento", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false; _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Movimiento", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false; _registroExitoso.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Movimiento", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false; _registroExitoso.value = false
                    _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Movimiento", "Error general: ${e.message}", e); e.printStackTrace()
                }
            }
        }
    }

    fun limpiarFormulario() {
        _codiRemo.value = ""; _dataArribada.value = ""; _horaArribada.value = ""
        _codiAtes.value = ""; _nomTransportista.value = ""; _matricula.value = ""
        _mitjaTransport.value = ""; _nifConductor.value = ""; _nomConductor.value = ""
        _explotacioDestinacio.value = ""; _codiTransport.value = ""
        _listaAnimales.value = listOf(IdenMovimiento(identificador = "", estatArribada = null, classCanal = null, dataSacrMort = null, pesCanal = null, tipusPresentacio = null))
        _estatArribadaExpandidoPorIndice.value = emptyMap()
        _classCanalExpandidoPorIndice.value = emptyMap()
        _tipusPresentacioExpandidoPorIndice.value = emptyMap()
        _mostrarDatePickerPorIndice.value = emptyMap()
        _textoEstatArribadaPorIndice.value = emptyMap()
        borradorSesionId = ""
    }

    fun resetearEstadoRegistro() { _registroExitoso.value = false; _mensajeError.value = ""; _codiError.value = null }

    private fun convertirFechaHoraAFormatoAPI(fecha: String, hora: String): String {
        return try {
            if (fecha.length == 10 && hora.length == 5) {
                val (dia, mes, anio) = fecha.split("/")
                val (horas, minutos) = hora.split(":")
                "$anio$mes$dia$horas$minutos"
            } else ""
        } catch (e: Exception) {
            Log.e("Error conversión fecha/hora", e.message ?: "Error desconocido"); ""
        }
    }

    private fun guardarEnHistorial(resumen: String = "") {
        viewModelScope.launch {
            try {
                historialDao.insert(
                    HistorialEntity(
                        id = UUID.randomUUID().toString(),
                        tipo = "MOVIMIENTO",
                        fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                        datos = "",
                        resumen = resumen
                    )
                )
            } catch (e: Exception) {
                Log.e("Historial", "Error al guardar en historial: ${e.message}", e)
            }
        }
    }
}