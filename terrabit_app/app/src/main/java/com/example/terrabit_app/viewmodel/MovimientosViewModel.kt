package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.IdenMovimiento
import com.example.terrabit_app.data.network.moviminetos.modelos.Movimientos
import com.example.terrabit_app.data.network.moviminetos.modelos.PetConfirmacionMovi
import com.example.terrabit_app.data.network.respuestas.ResConfirmacionMovi
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MovimientosViewModel : ViewModel() {

    // Instancia del repositorio
    private val repositorio = Repositorio()

    // ============================================
    // SECCIÓN: LISTA DE MOVIMIENTOS PENDIENTES
    // ============================================

    private val _movimientosPendientes = MutableLiveData<Movimientos?>()
    val movimientosPendientes = _movimientosPendientes

    private val _cargandoLista = MutableLiveData(false)
    val cargandoLista = _cargandoLista

    fun obtenerMovimientosPendientes(
        nif: String,
        password: String,
        explotacioDestinacio: String,
        dataSortida: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repositorio.getConfirmacionMovimientos(
                nif,
                password,
                explotacioDestinacio,
                dataSortida
            )
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

    // ============================================
    // SECCIÓN: FORMULARIO DE CONFIRMACIÓN
    // ============================================

    // Estados del formulario
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

    // Estados de expansión de menús desplegables
    private val _codiAtesExpandido = MutableLiveData(false)
    val codiAtesExpandido = _codiAtesExpandido

    private val _mitjaTransportExpandido = MutableLiveData(false)
    val mitjaTransportExpandido = _mitjaTransportExpandido

    private val _estatArribadaExpandido = MutableLiveData(false)
    val estatArribadaExpandido = _estatArribadaExpandido

    // Estado para mostrar el DatePicker y TimePicker
    private val _mostrarDatePickerArribada = MutableLiveData(false)
    val mostrarDatePickerArribada = _mostrarDatePickerArribada

    private val _mostrarTimePickerArribada = MutableLiveData(false)
    val mostrarTimePickerArribada = _mostrarTimePickerArribada

    // Estados para feedback del registro
    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    // Estado de carga
    private val _cargandoMovimiento = MutableLiveData(false)
    val cargandoMovimiento = _cargandoMovimiento

    // ============================================
    // LISTAS DE OPCIONES
    // ============================================

    // Data class para códigos ATES
    data class CodigoAtes(val codigo: String, val nombre: String)

    val listaCodigosAtes = listOf(
        CodigoAtes("D", "D - Transportista")
    )

    // Medios de transporte
    val listaMitjaTransport = listOf(
        "01 - Camió",
        "05 - Vaixell",
        "06 - Avió",
        "07 - Tren",
        "08 - Conducció a peu",
        "99 - Altres"
    )

    // Estados de arribada
    val listaEstatArribada = listOf(
        "80 - Sacrificat",
        "93 - Mort durant transport",
        "92 - Arribat"
    )

    // ============================================
    // FUNCIONES PARA ACTUALIZAR CAMPOS
    // ============================================

    fun actualizarCodiRemo(nuevoValor: String) {
        _codiRemo.value = nuevoValor
    }

    fun actualizarDataArribada(nuevaFecha: String) {
        _dataArribada.value = nuevaFecha
    }

    fun actualizarHoraArribada(hora: String, minutos: String) {
        _horaArribada.value = String.format("%02d:%02d", hora.toInt(), minutos.toInt())
    }

    fun seleccionarCodiAtes(codigo: String) {
        _codiAtes.value = codigo
        _codiAtesExpandido.value = false
    }

    fun actualizarNomTransportista(nombre: String) {
        _nomTransportista.value = nombre
    }

    fun actualizarMatricula(matricula: String) {
        _matricula.value = matricula
    }

    fun seleccionarMitjaTransport(medio: String) {
        _mitjaTransport.value = medio
        _mitjaTransportExpandido.value = false
    }

    fun actualizarNifConductor(nif: String) {
        _nifConductor.value = nif
    }

    fun actualizarNomConductor(nombre: String) {
        _nomConductor.value = nombre
    }

    fun actualizarExplotacioDestinacio(explotacion: String) {
        _explotacioDestinacio.value = explotacion
    }

    fun actualizarIdentificadorAnimal(identificador: String) {
        _identificadorAnimal.value = identificador
    }

    fun seleccionarEstatArribada(estat: String) {
        _estatArribada.value = estat
        _estatArribadaExpandido.value = false
    }

    // ============================================
    // FUNCIONES PARA CONTROLAR EXPANSIÓN DE MENÚS
    // ============================================

    fun toggleCodiAtesExpandido() {
        _codiAtesExpandido.value = !(_codiAtesExpandido.value ?: false)
    }

    fun toggleMitjaTransportExpandido() {
        _mitjaTransportExpandido.value = !(_mitjaTransportExpandido.value ?: false)
    }

    fun toggleEstatArribadaExpandido() {
        _estatArribadaExpandido.value = !(_estatArribadaExpandido.value ?: false)
    }

    fun cerrarCodiAtesMenu() {
        _codiAtesExpandido.value = false
    }

    fun cerrarMitjaTransportMenu() {
        _mitjaTransportExpandido.value = false
    }

    fun cerrarEstatArribadaMenu() {
        _estatArribadaExpandido.value = false
    }

    // ============================================
    // FUNCIONES PARA CONTROLAR DATE/TIME PICKER
    // ============================================

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

    // Función seleccionar fecha
    fun seleccionarFechaArribada(fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        _dataArribada.value = String.format("%02d/%02d/%04d", dia, mes, anio)
        _mostrarDatePickerArribada.value = false
    }

    // ============================================
    // VALIDACIÓN Y CONFIRMACIÓN
    // ============================================

    // Función para validar el formulario
    fun esFormularioValido(): Boolean {
        val codiRemoValido = !_codiRemo.value.isNullOrEmpty()
        val dataArribadaValida = !_dataArribada.value.isNullOrEmpty()
        val horaArribadaValida = !_horaArribada.value.isNullOrEmpty()
        val codiAtesValido = !_codiAtes.value.isNullOrEmpty()
        val explotacioDestinacioValida = !_explotacioDestinacio.value.isNullOrEmpty()
        val identificadorValido = !_identificadorAnimal.value.isNullOrEmpty()
        val estatArribadaValido = !_estatArribada.value.isNullOrEmpty()

        return codiRemoValido && dataArribadaValida && horaArribadaValida &&
                codiAtesValido && explotacioDestinacioValida &&
                identificadorValido && estatArribadaValido
    }

    // Función para confirmar movimiento con gestión mejorada de errores
    fun confirmarMovimiento() {
        // Validar que todos los campos requeridos estén completos
        if (!esFormularioValido()) {
            val mensajeError = when {
                _codiRemo.value.isNullOrEmpty() ->
                    "Por favor, introduzca el código REMO de la guía"
                _dataArribada.value.isNullOrEmpty() ->
                    "Por favor, seleccione la fecha de arribada"
                _horaArribada.value.isNullOrEmpty() ->
                    "Por favor, seleccione la hora de arribada"
                _codiAtes.value.isNullOrEmpty() ->
                    "Por favor, seleccione el código ATES"
                _explotacioDestinacio.value.isNullOrEmpty() ->
                    "Por favor, introduzca la explotación de destino"
                _identificadorAnimal.value.isNullOrEmpty() ->
                    "Por favor, introduzca el identificador del animal"
                _estatArribada.value.isNullOrEmpty() ->
                    "Por favor, seleccione el estado de arribada"
                else ->
                    "Por favor, complete todos los campos obligatorios marcados con *"
            }
            _mensajeError.value = mensajeError
            Log.e("Validación Movimiento", mensajeError)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            // Activar indicador de carga
            withContext(Dispatchers.Main) {
                _cargandoMovimiento.value = true
            }

            try {
                // Convertir fecha y hora al formato API (yyyyMMddHHmm)
                val fechaHoraArribadaAPI = convertirFechaHoraAFormatoAPI(
                    _dataArribada.value ?: "",
                    _horaArribada.value ?: ""
                )

                // Extraer código del medio de transporte (primeros 2 dígitos)
                val codigoMedio = _mitjaTransport.value?.take(2) ?: ""

                // Extraer código del estado de arribada (primeros 2 dígitos)
                val codigoEstat = _estatArribada.value?.take(2) ?: ""

                // Crear lista de identificadores usando IdenMovimiento
                val listaIdentificadores = listOf(
                    IdenMovimiento(
                        identificador = _identificadorAnimal.value ?: "",
                        estatArribada = codigoEstat,
                        classCanal = null,
                        dataSacrMort = null,
                        pesCanal = null,
                        tipusPresentacio = null
                    )
                )

                // Crear objeto de petición
                val request = PetConfirmacionMovi(
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    especie = "01", // Bovino
                    codiRemo = _codiRemo.value ?: "",
                    dataArribada = fechaHoraArribadaAPI,
                    codiAtes = _codiAtes.value ?: "",
                    nomTransportista = _nomTransportista.value ?: "",
                    mitjaTransport = codigoMedio,
                    matricula = _matricula.value ?: "",
                    nifConductor = _nifConductor.value ?: "",
                    nomConductor = _nomConductor.value ?: "",
                    explotacioDestinacio = _explotacioDestinacio.value ?: "",
                    identificadors = listaIdentificadores
                )
                Log.d("Confirmar Movimiento", "Request: $request")

                // Llamar a la API
                val response = repositorio.putConfirmarMovi(request)

                // Procesar respuesta
                withContext(Dispatchers.Main) {
                    // Desactivar indicador de carga
                    _cargandoMovimiento.value = false

                    when {
                        // Caso: HTTP 200 OK
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            // Verificar si es respuesta exitosa (codi = "0")
                            if (body.codiRemo == "0" || body.descripcio?.contains("correcte", ignoreCase = true) == true) {
                                _registroExitoso.value = true
                                _mensajeError.value = ""

                                Log.d("Confirmar Movimiento", "Movimiento confirmado exitosamente")
                                Log.d("Confirmar Movimiento", "Respuesta: [${body.codiRemo}] ${body.descripcio}")

                                // Limpiar formulario después de confirmar exitosamente
                                limpiarFormulario()
                            }
                            // Caso inesperado: respuesta exitosa pero sin código 0
                            else {
                                _registroExitoso.value = false
                                _mensajeError.value = "Respuesta inesperada del servidor: ${body.descripcio ?: "Sin descripción"}"
                                Log.w("Confirmar Movimiento", "Respuesta inesperada: [${body.codiRemo}] ${body.descripcio}")
                            }
                        }

                        // Caso 2: HTTP Error (4xx, 5xx)
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                try {
                                    val errorObj = Gson().fromJson(errorBody, ResConfirmacionMovi::class.java)
                                    _mensajeError.value = errorObj.descripcio
                                        ?: "Error desconocido del servidor"
                                } catch (e: Exception) {
                                    _mensajeError.value = "Error al procesar respuesta"
                                }
                            }
                            _registroExitoso.value = false
                            Log.e("Error Movimiento", "HTTP ${response.code()}")
                            Log.e("Error Movimiento", "Mensaje: ${response.message()}")
                            if (errorBody != null) {
                                Log.e("Error Movimiento", "Body: $errorBody")
                            }
                        }

                        // Caso 3: Respuesta exitosa pero sin body
                        else -> {
                            _registroExitoso.value = false
                            _mensajeError.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Movimiento", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                // Manejo específico de timeout
                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Movimiento", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                // Error de red
                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Movimiento", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                // Otros errores
                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Movimiento", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    // ============================================
    // FUNCIONES AUXILIARES
    // ============================================

    // Función para limpiar el formulario
    fun limpiarFormulario() {
        _codiRemo.value = ""
        _dataArribada.value = ""
        _horaArribada.value = ""
        _codiAtes.value = ""
        _nomTransportista.value = ""
        _matricula.value = ""
        _mitjaTransport.value = ""
        _nifConductor.value = ""
        _nomConductor.value = ""
        _explotacioDestinacio.value = ""
        _identificadorAnimal.value = ""
        _estatArribada.value = ""
    }

    // Función para resetear el estado de registro
    fun resetearEstadoRegistro() {
        _registroExitoso.value = false
        _mensajeError.value = ""
    }

    // Convertir fecha y hora al formato de la API (yyyyMMddHHmm)
    private fun convertirFechaHoraAFormatoAPI(fecha: String, hora: String): String {
        return try {
            // fecha viene en formato dd/MM/yyyy
            // hora viene en formato HH:mm
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