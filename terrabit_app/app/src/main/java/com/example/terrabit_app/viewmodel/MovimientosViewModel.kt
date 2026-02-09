package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.IdenMovimiento
import com.example.terrabit_app.data.network.moviminetos.modelos.Movimientos
import com.example.terrabit_app.data.network.moviminetos.modelos.PetConfirmacionMovi
import com.example.terrabit_app.data.network.respuestas.ResConfirmacionMovi
import com.example.terrabit_app.utils.DateUtils.convertirFechaAFormatoAPI
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


    // Codigos de error para la api
    private val _codiTransport = MutableLiveData("")
    val codiTransport = _codiTransport

    private val _codiEstats = MutableLiveData("")
    val codiEstats = _codiEstats

    // Codigo de error para el control de errores

    private  val _codiError = MutableLiveData<Int?>()
    val codiError = _codiError

    // Estados del conductor


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

    fun seleccionarMitjaTransport(medio: String, codigo: String) {
        _mitjaTransport.value = medio
        _codiTransport.value = codigo
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

    fun seleccionarEstatArribada(estat: String, codigo: String) {
        _estatArribada.value = estat
        _codiEstats.value = codigo
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
    // SECCIÓN: LISTA DE ANIMALES
    // ============================================

    private val limitePesoCanal = 5
    private val limiteClassCanel = 5


    // Usar directamente IdenMovimiento con valores por defecto
    private val _listaAnimales = MutableLiveData<List<IdenMovimiento>>(
        listOf(
            IdenMovimiento(
                identificador = "",
                estatArribada = "",
                classCanal = null,
                dataSacrMort = null,
                pesCanal = null,
                tipusPresentacio = null
            )
        )
    )
    val listaAnimales = _listaAnimales




    // Map para rastrear qué animal está siendo editado (por índice)
    private val _estatArribadaExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val estatArribadaExpandidoPorIndice = _estatArribadaExpandidoPorIndice

    private val _classCanalExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val classCanalExpandidoPorIndice = _classCanalExpandidoPorIndice

    private val _tipusPresentacioExpandidoPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val tipusPresentacioExpandidoPorIndice = _tipusPresentacioExpandidoPorIndice

    private val _mostrarDatePickerPorIndice = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val mostrarDatePickerPorIndice = _mostrarDatePickerPorIndice

    // Temporal para mostrar el texto del estado en la UI
    private val _textoEstatArribadaPorIndice = MutableLiveData<Map<Int, String>>(emptyMap())
    val textoEstatArribadaPorIndice = _textoEstatArribadaPorIndice

    // Listas de opciones para estado "80 - Sacrificat"

// ============================================
// FUNCIONES PARA GESTIONAR ANIMALES
// ============================================

    fun agregarAnimal() {
        val listaActual = _listaAnimales.value ?: emptyList()
        val nuevoAnimal = IdenMovimiento(
            identificador = "",
            estatArribada = "",
            classCanal = null,
            dataSacrMort = null,
            pesCanal = null,
            tipusPresentacio = null
        )
        _listaAnimales.value = listaActual + nuevoAnimal
    }

    fun eliminarAnimal(indice: Int) {
        val listaActual = _listaAnimales.value ?: emptyList()
        if (listaActual.size > 1) { // Mantener al menos un animal
            _listaAnimales.value = listaActual.filterIndexed { index, _ -> index != indice }

            // Limpiar estados de expansión del animal eliminado
            _estatArribadaExpandidoPorIndice.value = _estatArribadaExpandidoPorIndice.value?.minus(indice)
            _classCanalExpandidoPorIndice.value = _classCanalExpandidoPorIndice.value?.minus(indice)
            _tipusPresentacioExpandidoPorIndice.value = _tipusPresentacioExpandidoPorIndice.value?.minus(indice)
            _mostrarDatePickerPorIndice.value = _mostrarDatePickerPorIndice.value?.minus(indice)
            _textoEstatArribadaPorIndice.value = _textoEstatArribadaPorIndice.value?.minus(indice)
        }
    }

    fun actualizarIdentificadorAnimal(indice: Int, identificador: String) {
        val listaActual = _listaAnimales.value ?: emptyList()
        _listaAnimales.value = listaActual.mapIndexed { index, animal ->
            if (index == indice) animal.copy(identificador = identificador)
            else animal
        }
    }
    fun actualizarClassCanal(indice: Int, clase: String) {
        val listaActual = _listaAnimales.value ?: emptyList()
        if (clase.length <= limiteClassCanel){
            _listaAnimales.value = listaActual.mapIndexed { index, animal ->
                if (index == indice) animal.copy(classCanal = clase)
                else animal
            }
        }
    }

    fun seleccionarEstatArribadaAnimal(indice: Int, estatTexto: String, estatCodigo: String) {
        val listaActual = _listaAnimales.value ?: emptyList()
        _listaAnimales.value = listaActual.mapIndexed { index, animal ->
            if (index == indice) {
                // Si cambia a un estado diferente de "80", limpiar campos de sacrificio
                if (estatCodigo != "80") {
                    animal.copy(
                        estatArribada = estatCodigo,
                        classCanal = null,
                        dataSacrMort = null,
                        pesCanal = null,
                        tipusPresentacio = null
                    )
                } else {
                    animal.copy(estatArribada = estatCodigo)
                }
            } else animal
        }

        // Guardar texto para mostrar en UI
        val mapaTexto = _textoEstatArribadaPorIndice.value ?: emptyMap()
        _textoEstatArribadaPorIndice.value = mapaTexto + (indice to estatCodigo)

        // Cerrar menú
        val mapaActual = _estatArribadaExpandidoPorIndice.value ?: emptyMap()
        _estatArribadaExpandidoPorIndice.value = mapaActual + (indice to false)
    }

    fun actualizarDataSacrMort(indice: Int, fecha: String) {
        val listaActual = _listaAnimales.value ?: emptyList()
        _listaAnimales.value = listaActual.mapIndexed { index, animal ->
            if (index == indice) animal.copy(dataSacrMort = fecha)
            else animal
        }
    }

    fun actualizarPesCanal(indice: Int, peso: String) {
        val listaActual = _listaAnimales.value ?: emptyList()
        if (peso.length <= 5) {
            _listaAnimales.value = listaActual.mapIndexed { index, animal ->
                if (index == indice) animal.copy(pesCanal = peso)
                else animal
            }
        }

    }


    fun seleccionarTipusPresentacio(indice: Int, codigo: String) {
        val listaActual = _listaAnimales.value ?: emptyList()
        _listaAnimales.value = listaActual.mapIndexed { index, animal ->
            if (index == indice) animal.copy(tipusPresentacio = codigo)
            else animal
        }

        // Cerrar menú
        val mapaActual = _tipusPresentacioExpandidoPorIndice.value ?: emptyMap()
        _tipusPresentacioExpandidoPorIndice.value = mapaActual + (indice to false)
    }

    // Funciones para controlar la expansión de menús por índice
    fun toggleEstatArribadaExpandido(indice: Int) {
        val mapaActual = _estatArribadaExpandidoPorIndice.value ?: emptyMap()
        val valorActual = mapaActual[indice] ?: false
        _estatArribadaExpandidoPorIndice.value = mapaActual + (indice to !valorActual)
    }


    fun toggleTipusPresentacioExpandido(indice: Int) {
        val mapaActual = _tipusPresentacioExpandidoPorIndice.value ?: emptyMap()
        val valorActual = mapaActual[indice] ?: false
        _tipusPresentacioExpandidoPorIndice.value = mapaActual + (indice to !valorActual)
    }

    fun cerrarEstatArribadaMenu(indice: Int) {
        val mapaActual = _estatArribadaExpandidoPorIndice.value ?: emptyMap()
        _estatArribadaExpandidoPorIndice.value = mapaActual + (indice to false)
    }


    fun cerrarTipusPresentacioMenu(indice: Int) {
        val mapaActual = _tipusPresentacioExpandidoPorIndice.value ?: emptyMap()
        _tipusPresentacioExpandidoPorIndice.value = mapaActual + (indice to false)
    }

    // DatePicker para fecha de sacrificio/muerte
    fun mostrarDatePickerSacrMort(indice: Int) {
        val mapaActual = _mostrarDatePickerPorIndice.value ?: emptyMap()
        _mostrarDatePickerPorIndice.value = mapaActual + (indice to true)
    }

    fun ocultarDatePickerSacrMort(indice: Int) {
        val mapaActual = _mostrarDatePickerPorIndice.value ?: emptyMap()
        _mostrarDatePickerPorIndice.value = mapaActual + (indice to false)
    }

    fun seleccionarFechaSacrMort(indice: Int, fechaMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1
        val anio = calendar.get(Calendar.YEAR)

        val fechaFormateada = String.format("%02d/%02d/%04d", dia, mes, anio)
        actualizarDataSacrMort(indice, fechaFormateada)
        ocultarDatePickerSacrMort(indice)
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

        // Validar todos los animales
        val listaAnimales = _listaAnimales.value ?: emptyList()
        val animalesValidos = listaAnimales.all { animal ->
            val identificadorValido = animal.identificador.isNotEmpty()
            val estatValido = animal.estatArribada.isNotEmpty()

            // Si el estado es "80" (Sacrificat), validar campos adicionales
            val camposAdicionales = if (animal.estatArribada == "80") {
                !animal.dataSacrMort.isNullOrEmpty() &&
                        !animal.pesCanal.isNullOrEmpty() &&
                        !animal.classCanal.isNullOrEmpty() &&
                        !animal.tipusPresentacio.isNullOrEmpty()
            } else true

            identificadorValido && estatValido && camposAdicionales
        }

        return codiRemoValido && dataArribadaValida && horaArribadaValida &&
                codiAtesValido && explotacioDestinacioValida && animalesValidos
    }

    // Función para confirmar movimiento con gestión mejorada de errores
    fun confirmarMovimiento() {
        // Validar que todos los campos requeridos estén completos
        if (!esFormularioValido()) {
            val mensajeError = when {
                _codiRemo.value.isNullOrEmpty() ->
                    14
                _dataArribada.value.isNullOrEmpty() ->
                   15
                _horaArribada.value.isNullOrEmpty() ->
                   16
                _codiAtes.value.isNullOrEmpty() ->
                    17
                _explotacioDestinacio.value.isNullOrEmpty() ->
                   18
                _identificadorAnimal.value.isNullOrEmpty() ->
                   12
                _estatArribada.value.isNullOrEmpty() ->
                   19
                else ->
                    0
            }
            _codiError.value = mensajeError
            Log.e("Validación Movimiento", "Error: $mensajeError")
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
                // Extraer código del estado de arribada (primeros 2 dígitos)
                val codigoEstat = _estatArribada.value?.take(2) ?: ""

                // Crear lista de identificadores directamente
                val listaIdentificadores = _listaAnimales.value ?: emptyList()

                // Convertir fechas de formato dd/MM/yyyy a yyyyMMdd para los que tienen estado "80"
                val listaIdentificadoresAPI = listaIdentificadores.map { animal ->
                    animal.copy(
                        dataSacrMort = if (animal.estatArribada == "80" && animal.dataSacrMort != null) {
                            convertirFechaAFormatoAPI(animal.dataSacrMort)
                        } else null
                    )
                }

                // Crear objeto de petición
                val request = PetConfirmacionMovi(
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    especie = "01", // Bovino
                    codiRemo = _codiRemo.value ?: "",
                    dataArribada = fechaHoraArribadaAPI,
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

        // Resetear lista de animales a un solo animal vacío
        _listaAnimales.value = listOf(
            IdenMovimiento(
                identificador = "",
                estatArribada = "",
                classCanal = null,
                dataSacrMort = null,
                pesCanal = null,
                tipusPresentacio = null
            )
        )
        _estatArribadaExpandidoPorIndice.value = emptyMap()
        _classCanalExpandidoPorIndice.value = emptyMap()
        _tipusPresentacioExpandidoPorIndice.value = emptyMap()
        _mostrarDatePickerPorIndice.value = emptyMap()
        _textoEstatArribadaPorIndice.value = emptyMap()
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