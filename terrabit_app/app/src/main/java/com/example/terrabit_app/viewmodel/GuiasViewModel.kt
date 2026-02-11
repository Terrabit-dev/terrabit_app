package com.example.terrabit_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.guias.PeticionAltaGuia
import com.example.terrabit_app.data.network.respuestas.ResAltaGuia
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GuiasViewModel : ViewModel() {

    private val repositorio = Repositorio()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    // ID único para la sesión actual del formulario
    private var borradorSesionId: String = ""

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)

        // Generar nuevo ID de sesión si no existe
        if (borradorSesionId.isEmpty()) {
            borradorSesionId = "guia_auto_${System.currentTimeMillis()}"
        }
    }

    fun tieneContenido(): Boolean {
        return !_explotacioOrigen.value.isNullOrEmpty() ||
                !_explotacioDestinacio.value.isNullOrEmpty() ||
                !_temporal.value.isNullOrEmpty() ||
                !_dataSortida.value.isNullOrEmpty() ||
                !_horaSortida.value.isNullOrEmpty() ||
                !_dataArribada.value.isNullOrEmpty() ||
                !_horaArribada.value.isNullOrEmpty() ||
                !_mobilitat.value.isNullOrEmpty() ||
                !_pais.value.isNullOrEmpty() ||
                !_codiExplotacio.value.isNullOrEmpty() ||
                !_codiAtes.value.isNullOrEmpty() ||
                !_nomTransportista.value.isNullOrEmpty() ||
                !_mitjaTransport.value.isNullOrEmpty() ||
                !_matricula.value.isNullOrEmpty() ||
                !_nifConductor.value.isNullOrEmpty() ||
                !_nomConductor.value.isNullOrEmpty() ||
                (_identificadors.value?.any { it.isNotEmpty() } == true)
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) {
            Log.d("Autoguardado Guía", "No hay contenido para guardar")
            return
        }

        try {
            val datosGuia = mapOf(
                "explotacioOrigen" to _explotacioOrigen.value,
                "explotacioDestinacio" to _explotacioDestinacio.value,
                "temporal" to _temporal.value,
                "dataSortida" to _dataSortida.value,
                "horaSortida" to _horaSortida.value,
                "dataArribada" to _dataArribada.value,
                "horaArribada" to _horaArribada.value,
                "mobilitat" to _mobilitat.value,
                "pais" to _pais.value,
                "codiExplotacio" to _codiExplotacio.value,
                "codiAtes" to _codiAtes.value,
                "nomTransportista" to _nomTransportista.value,
                "mitjaTransport" to _mitjaTransport.value,
                "matricula" to _matricula.value,
                "nifConductor" to _nifConductor.value,
                "nomConductor" to _nomConductor.value,
                "identificadors" to _identificadors.value,
                "codiTemporal" to codiTemporal,
                "codiGuiaMobilidad" to codiGuiaMobilidad,
                "codiTransport" to codiTransport
            )

            // Buscar si ya existe este borrador específico de la sesión actual
            val borradorExistente = sharedPreferencesManager.obtenerBorradores()
                .find { it.id == borradorSesionId }

            val borrador = if (borradorExistente != null) {
                // Actualizar borrador de esta sesión
                borradorExistente.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosGuia)
                )
            } else {
                // Crear nuevo borrador con ID de sesión
                Borrador(
                    id = borradorSesionId,
                    tipo = "GUIA",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosGuia),
                    estado = "BORRADOR_AUTO"
                )
            }

            sharedPreferencesManager.guardarBorrador(borrador)
            Log.d("Autoguardado Guía", "Borrador guardado: $borradorSesionId")
        } catch (e: Exception) {
            Log.e("Error Autoguardado Guía", "Error al guardar: ${e.message}", e)
        }
    }

    fun cargarBorradorExistente() {
        try {
            val borradores = sharedPreferencesManager.obtenerBorradores()

            // Buscar cualquier borrador de tipo GUIA con estado BORRADOR_AUTO
            val borradoresGuia = borradores.filter {
                it.tipo == "GUIA" && it.estado == "BORRADOR_AUTO"
            }

            if (borradoresGuia.isNotEmpty()) {
                // Tomar el más reciente (último guardado)
                val borradorGuia = borradoresGuia.maxByOrNull {
                    it.id.substringAfter("guia_auto_").toLongOrNull() ?: 0L
                }

                if (borradorGuia != null) {
                    // Asignar este ID a la sesión actual
                    borradorSesionId = borradorGuia.id

                    val gson = Gson()
                    val datos: Map<String, Any?> = gson.fromJson(
                        borradorGuia.datos,
                        object : TypeToken<Map<String, Any?>>() {}.type
                    )

                    // Restaurar datos
                    _explotacioOrigen.value = datos["explotacioOrigen"] as? String ?: ""
                    _explotacioDestinacio.value = datos["explotacioDestinacio"] as? String ?: ""
                    _temporal.value = datos["temporal"] as? String ?: ""
                    _dataSortida.value = datos["dataSortida"] as? String ?: ""
                    _horaSortida.value = datos["horaSortida"] as? String ?: ""
                    _dataArribada.value = datos["dataArribada"] as? String ?: ""
                    _horaArribada.value = datos["horaArribada"] as? String ?: ""
                    _mobilitat.value = datos["mobilitat"] as? String ?: ""
                    _pais.value = datos["pais"] as? String ?: ""
                    _codiExplotacio.value = datos["codiExplotacio"] as? String ?: ""
                    _codiAtes.value = datos["codiAtes"] as? String ?: ""
                    _nomTransportista.value = datos["nomTransportista"] as? String ?: ""
                    _mitjaTransport.value = datos["mitjaTransport"] as? String ?: ""
                    _matricula.value = datos["matricula"] as? String ?: ""
                    _nifConductor.value = datos["nifConductor"] as? String ?: ""
                    _nomConductor.value = datos["nomConductor"] as? String ?: ""

                    // Restaurar códigos
                    codiTemporal = datos["codiTemporal"] as? String ?: ""
                    codiGuiaMobilidad = datos["codiGuiaMobilidad"] as? String ?: ""
                    codiTransport = datos["codiTransport"] as? String ?: ""

                    // Restaurar lista de identificadores
                    @Suppress("UNCHECKED_CAST")
                    val identificadoresList = datos["identificadors"] as? List<String>
                    _identificadors.value = identificadoresList ?: listOf("")

                    Log.d("Cargar Borrador", "Borrador cargado: $borradorSesionId")
                }
            }
        } catch (e: Exception) {
            Log.e("Error Cargar Borrador", "Error al cargar: ${e.message}", e)
        }
    }

    fun eliminarBorradorAutomatico() {
        try {
            if (borradorSesionId.isNotEmpty()) {
                sharedPreferencesManager.eliminarBorrador(borradorSesionId)
                Log.d("Eliminar Borrador", "Borrador eliminado: $borradorSesionId")
                borradorSesionId = "" // Resetear el ID de sesión
            }
        } catch (e: Exception) {
            Log.e("Error Eliminar Borrador", "Error: ${e.message}", e)
        }
    }

    fun obtenerBorradoresGuia(): List<Borrador> {
        return try {
            sharedPreferencesManager.obtenerBorradores()
                .filter { it.tipo == "GUIA" && it.estado == "BORRADOR_AUTO" }
        } catch (e: Exception) {
            Log.e("Error", "Error al obtener borradores: ${e.message}", e)
            emptyList()
        }
    }

    private val _explotacioOrigen = MutableLiveData("")
    val explotacioOrigen = _explotacioOrigen

    private val _explotacioDestinacio = MutableLiveData("")
    val explotacioDestinacio = _explotacioDestinacio

    private val _temporal = MutableLiveData("")
    val temporal = _temporal

    private val _dataSortida = MutableLiveData("")
    val dataSortida = _dataSortida

    private val _horaSortida = MutableLiveData("")
    val horaSortida = _horaSortida

    private val _dataArribada = MutableLiveData("")
    val dataArribada = _dataArribada

    private val _horaArribada = MutableLiveData("")
    val horaArribada = _horaArribada

    private val _mobilitat = MutableLiveData("")
    val mobilitat = _mobilitat

    private val _pais = MutableLiveData("")
    val pais = _pais

    private val _codiExplotacio = MutableLiveData("")
    val codiExplotacio = _codiExplotacio

    private val _codiAtes = MutableLiveData("")
    val codiAtes = _codiAtes

    private val _nomTransportista = MutableLiveData("")
    val nomTransportista = _nomTransportista

    private val _mitjaTransport = MutableLiveData("")
    val mitjaTransport = _mitjaTransport

    private val _matricula = MutableLiveData("")
    val matricula = _matricula

    private val _nifConductor = MutableLiveData("")
    val nifConductor = _nifConductor

    private val _nomConductor = MutableLiveData("")
    val nomConductor = _nomConductor

    private val _identificadors = MutableLiveData<List<String>>(listOf(""))
    val identificadors = _identificadors

    private val _temporalExpandido = MutableLiveData(false)
    val temporalExpandido = _temporalExpandido

    private val _mobilitatExpandido = MutableLiveData(false)
    val mobilitatExpandido = _mobilitatExpandido

    private val _mitjaTransportExpandido = MutableLiveData(false)
    val mitjaTransportExpandido = _mitjaTransportExpandido

    private val _mostrarDatePickerSortida = MutableLiveData(false)
    val mostrarDatePickerSortida = _mostrarDatePickerSortida

    private val _mostrarTimePickerSortida = MutableLiveData(false)
    val mostrarTimePickerSortida = _mostrarTimePickerSortida

    private val _mostrarDatePickerArribada = MutableLiveData(false)
    val mostrarDatePickerArribada = _mostrarDatePickerArribada

    private val _mostrarTimePickerArribada = MutableLiveData(false)
    val mostrarTimePickerArribada = _mostrarTimePickerArribada

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    private val _cargandoGuia = MutableLiveData(false)
    val cargandoGuia = _cargandoGuia

    private var codiTemporal = ""
    private var codiGuiaMobilidad = ""
    private var codiTransport = ""

    private val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    fun actualizarExplotacioOrigen(valor: String) {
        _explotacioOrigen.value = valor
    }

    fun actualizarExplotacioDestinacio(valor: String) {
        _explotacioDestinacio.value = valor
    }

    fun seleccionarTemporal(valor: String, codigo: String) {
        _temporal.value = valor
        codiTemporal = codigo
        _temporalExpandido.value = false
    }

    fun actualizarDataSortida(fecha: String) {
        _dataSortida.value = fecha
    }

    fun actualizarHoraSortida(hora: String, minutos: String) {
        _horaSortida.value = String.format("%02d:%02d", hora.toInt(), minutos.toInt())
    }

    fun actualizarDataArribada(fecha: String) {
        _dataArribada.value = fecha
    }

    fun actualizarHoraArribada(hora: String, minutos: String) {
        _horaArribada.value = String.format("%02d:%02d", hora.toInt(), minutos.toInt())
    }

    fun seleccionarMobilitat(valor: String, codigo: String) {
        _mobilitat.value = valor
        codiGuiaMobilidad = codigo
        _mobilitatExpandido.value = false
    }

    fun actualizarPais(valor: String) {
        _pais.value = valor
    }

    fun actualizarCodiExplotacio(valor: String) {
        _codiExplotacio.value = valor
    }

    fun campoCodiAtes(codigo: String) {
        if (codigo.length <= 15) {
            _codiAtes.value = codigo
        }
    }

    fun actualizarNomTransportista(nombre: String) {
        _nomTransportista.value = nombre
    }

    fun seleccionarMitjaTransport(medio: String, codigo: String) {
        _mitjaTransport.value = medio
        codiTransport = codigo
        _mitjaTransportExpandido.value = false
    }

    fun actualizarMatricula(matricula: String) {
        _matricula.value = matricula
    }

    fun actualizarNifConductor(nif: String) {
        if (nif.length <= 9) {
            _nifConductor.value = nif
        }
    }

    fun actualizarNomConductor(nombre: String) {
        _nomConductor.value = nombre
    }

    fun actualizarIdentificador(index: Int, valor: String) {
        val listaActual = _identificadors.value?.toMutableList() ?: mutableListOf()
        if (index < listaActual.size) {
            listaActual[index] = valor
            _identificadors.value = listaActual
        }
    }

    fun agregarIdentificador() {
        val listaActual = _identificadors.value?.toMutableList() ?: mutableListOf()
        listaActual.add("")
        _identificadors.value = listaActual
    }

    fun eliminarIdentificador(index: Int) {
        val listaActual = _identificadors.value?.toMutableList() ?: mutableListOf()
        if (listaActual.size > 1 && index < listaActual.size) {
            listaActual.removeAt(index)
            _identificadors.value = listaActual
        }
    }

    fun toggleTemporalExpandido() {
        _temporalExpandido.value = !(_temporalExpandido.value ?: false)
    }

    fun toggleMobilitatExpandido() {
        _mobilitatExpandido.value = !(_mobilitatExpandido.value ?: false)
    }

    fun toggleMitjaTransportExpandido() {
        _mitjaTransportExpandido.value = !(_mitjaTransportExpandido.value ?: false)
    }

    fun cerrarTemporalMenu() {
        _temporalExpandido.value = false
    }

    fun cerrarMobilitatMenu() {
        _mobilitatExpandido.value = false
    }

    fun cerrarMitjaTransportMenu() {
        _mitjaTransportExpandido.value = false
    }

    fun mostrarDatePickerSortida() {
        _mostrarDatePickerSortida.value = true
    }

    fun ocultarDatePickerSortida() {
        _mostrarDatePickerSortida.value = false
    }

    fun mostrarTimePickerSortida() {
        _mostrarTimePickerSortida.value = true
    }

    fun ocultarTimePickerSortida() {
        _mostrarTimePickerSortida.value = false
    }

    fun mostrarDatePickerArribada() {
        _mostrarDatePickerArribada.value = true
    }

    fun ocultarDatePickerArribada() {
        _mostrarDatePickerArribada.value = false
    }

    fun mostrarTimePickerArribada() {
        _mostrarTimePickerArribada.value = true
    }

    fun ocultarTimePickerArribada() {
        _mostrarTimePickerArribada.value = false
    }

    fun seleccionarFechaSortida(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _dataSortida.value = String.format("%02d/%02d/%04d", dia, mes, anio)
        _mostrarDatePickerSortida.value = false
    }

    fun seleccionarFechaArribada(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _dataArribada.value = String.format("%02d/%02d/%04d", dia, mes, anio)
        _mostrarDatePickerArribada.value = false
    }

    fun esFormularioValido(): Boolean {
        val explotacioOrigenValida = !_explotacioOrigen.value.isNullOrEmpty()
        val explotacioDestinacioValida = !_explotacioDestinacio.value.isNullOrEmpty()
        val temporalValido = !_temporal.value.isNullOrEmpty()
        val dataSortidaValida = !_dataSortida.value.isNullOrEmpty()
        val horaSortidaValida = !_horaSortida.value.isNullOrEmpty()
        val dataArribadaValida = !_dataArribada.value.isNullOrEmpty()
        val horaArribadaValida = !_horaArribada.value.isNullOrEmpty()
        val mobilitatValida = !_mobilitat.value.isNullOrEmpty()

        return explotacioOrigenValida && explotacioDestinacioValida &&
                temporalValido && dataSortidaValida && horaSortidaValida &&
                dataArribadaValida && horaArribadaValida && mobilitatValida
    }

    fun confirmarAltaGuia() {
        _codiError.value = null

        if (!esFormularioValido()) {
            val mensajeError = when {
                _explotacioOrigen.value.isNullOrEmpty() -> 20
                _explotacioDestinacio.value.isNullOrEmpty() -> 18
                _temporal.value.isNullOrEmpty() -> 21
                _dataSortida.value.isNullOrEmpty() -> 22
                _horaSortida.value.isNullOrEmpty() -> 23
                _dataArribada.value.isNullOrEmpty() -> 15
                _horaArribada.value.isNullOrEmpty() -> 16
                _mobilitat.value.isNullOrEmpty() -> 24
                else -> 0
            }
            _codiError.value = mensajeError
            Log.e("Validación Guía", "$mensajeError")
            return
        }

        viewModelScope.launch {
            _cargandoGuia.postValue(true)

            try {
                val fechaHoraSortidaAPI = convertirFechaHoraAFormatoAPI(
                    _dataSortida.value ?: "",
                    _horaSortida.value ?: ""
                )

                val fechaHoraArribadaAPI = convertirFechaHoraAFormatoAPI(
                    _dataArribada.value ?: "",
                    _horaArribada.value ?: ""
                )

                val temporalAPI = _temporal.value ?: ""
                val mobilitatAPI = _mobilitat.value ?: ""

                val identificadoresList = _identificadors.value
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?.takeIf { it.isNotEmpty() }

                val request = PeticionAltaGuia(
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    especie = "01",
                    explotacioOrigen = _explotacioOrigen.value ?: "",
                    explotacioDestinacio = _explotacioDestinacio.value ?: "",
                    temporal = temporalAPI,
                    dataSortida = fechaHoraSortidaAPI,
                    dataArribada = fechaHoraArribadaAPI,
                    mobilitat = mobilitatAPI,
                    pais = _pais.value?.ifEmpty { null },
                    codiExplotacio = _codiExplotacio.value?.ifEmpty { null },
                    codiAtes = _codiAtes.value?.ifEmpty { null },
                    nomTransportista = _nomTransportista.value?.ifEmpty { null },
                    mitjaTransport = codiTransport.ifEmpty { null },
                    matricula = _matricula.value?.ifEmpty { null },
                    nifConductor = _nifConductor.value?.ifEmpty { null },
                    nomConductor = _nomConductor.value?.ifEmpty { null },
                    identificadors = identificadoresList
                )

                Log.d("Alta Guía", "Request: $request")

                val response = repositorio.putAltaGuia(request)

                withContext(Dispatchers.Main) {
                    _cargandoGuia.value = false

                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            if (body.codiRemo == "0" || body.descripcio?.contains("correcte", ignoreCase = true) == true) {
                                _registroExitoso.value = true
                                _mensajeError.value = ""

                                Log.d("Alta Guía", "Guía creada exitosamente")

                                eliminarBorradorAutomatico()
                                limpiarFormulario()
                            } else {
                                _registroExitoso.value = false
                                _mensajeError.value = body.descripcio ?: "Error desconocido"
                                Log.w("Alta Guía", "Respuesta inesperada: [${body.codiRemo}] ${body.descripcio}")
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, ResAltaGuia::class.java)
                                    _mensajeError.value = errorObj.errors?.firstOrNull()?.descripcio
                                        ?: errorObj.descripcio
                                                ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeError.value = "Error al procesar respuesta"
                                    Log.e("Error parsing", "Error: ${e.message}", e)
                                }
                            } else {
                                _mensajeError.value = "Error del servidor sin detalles"
                            }
                            _registroExitoso.value = false
                            Log.e("Error Guía", "HTTP ${response.code()}: ${response.message()}")
                            if (errorBody != null) {
                                Log.e("Error Registro Nacimiento", "Body: $errorBody")
                            }
                        }
                        else -> {
                            _registroExitoso.value = false
                            _mensajeError.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Guía", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargandoGuia.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Guía", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargandoGuia.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Guía", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _cargandoGuia.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Guía", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    fun limpiarFormulario() {
        _explotacioOrigen.value = ""
        _explotacioDestinacio.value = ""
        _temporal.value = ""
        _dataSortida.value = ""
        _horaSortida.value = ""
        _dataArribada.value = ""
        _horaArribada.value = ""
        _mobilitat.value = ""
        _pais.value = ""
        _codiExplotacio.value = ""
        _codiAtes.value = ""
        _nomTransportista.value = ""
        _mitjaTransport.value = ""
        _matricula.value = ""
        _nifConductor.value = ""
        _nomConductor.value = ""
        _identificadors.value = listOf("")
        codiTemporal = ""
        codiGuiaMobilidad = ""
        codiTransport = ""

        // Generar nuevo ID de sesión para el próximo formulario
        borradorSesionId = ""
    }

    fun resetearEstadoRegistro() {
        _registroExitoso.value = false
        _mensajeError.value = ""
        _codiError.value = null
    }

    private fun convertirFechaHoraAFormatoAPI(fecha: String, hora: String): String {
        return try {
            if (fecha.length == 10 && hora.length == 5) {
                val partesFecha = fecha.split("/")
                val partesHora = hora.split(":")
                val dia = partesFecha[0]
                val mes = partesFecha[1]
                val anio = partesFecha[2]
                val horas = partesHora[0]
                val minutos = partesHora[1]
                "$anio$mes$dia$horas$minutos"
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e("Error conversión fecha/hora", e.message ?: "Error desconocido")
            ""
        }
    }
}