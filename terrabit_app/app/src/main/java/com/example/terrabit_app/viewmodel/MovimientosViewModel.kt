package com.example.terrabit_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.Borrador
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.IdenMovimiento
import com.example.terrabit_app.data.network.moviminetos.modelos.Movimientos
import com.example.terrabit_app.data.network.moviminetos.modelos.PetConfirmacionMovi
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.utils.DateUtils.convertirFechaAFormatoAPI
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MovimientosViewModel : ViewModel() {

    private val repositorio = Repositorio()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    // ID único para la sesión actual del formulario
    private var borradorSesionId: String = ""

    // ============================================
    // SECCIÓN: AUTOGUARDADO
    // ============================================

    fun inicializarSharedPreferences(context: Context) {
        sharedPreferencesManager = SharedPreferencesManager(context)

        // Generar nuevo ID de sesión si no existe
        if (borradorSesionId.isEmpty()) {
            borradorSesionId = "movimiento_auto_${System.currentTimeMillis()}"
        }
    }

    fun tieneContenido(): Boolean {
        return !_codiRemo.value.isNullOrEmpty() ||
                !_dataArribada.value.isNullOrEmpty() ||
                !_horaArribada.value.isNullOrEmpty() ||
                !_codiAtes.value.isNullOrEmpty() ||
                !_nomTransportista.value.isNullOrEmpty() ||
                !_matricula.value.isNullOrEmpty() ||
                !_mitjaTransport.value.isNullOrEmpty() ||
                !_nifConductor.value.isNullOrEmpty() ||
                !_nomConductor.value.isNullOrEmpty() ||
                !_explotacioDestinacio.value.isNullOrEmpty() ||
                (_listaAnimales.value?.any { it.identificador.isNotEmpty() } == true)
    }

    fun guardarBorradorAutomatico() {
        if (!tieneContenido()) {
            Log.d("Autoguardado Movimiento", "No hay contenido para guardar")
            return
        }

        try {
            val datosMovimiento = mapOf(
                "codiRemo" to _codiRemo.value,
                "dataArribada" to _dataArribada.value,
                "horaArribada" to _horaArribada.value,
                "codiAtes" to _codiAtes.value,
                "nomTransportista" to _nomTransportista.value,
                "matricula" to _matricula.value,
                "mitjaTransport" to _mitjaTransport.value,
                "nifConductor" to _nifConductor.value,
                "nomConductor" to _nomConductor.value,
                "explotacioDestinacio" to _explotacioDestinacio.value,
                "listaAnimales" to _listaAnimales.value,
                "codiTransport" to _codiTransport.value
            )

            // Buscar si ya existe este borrador específico de la sesión actual
            val borradorExistente = sharedPreferencesManager.obtenerBorradores()
                .find { it.id == borradorSesionId }

            val borrador = if (borradorExistente != null) {
                // Actualizar borrador de esta sesión
                borradorExistente.copy(
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosMovimiento)
                )
            } else {
                // Crear nuevo borrador con ID de sesión
                Borrador(
                    id = borradorSesionId,
                    tipo = "MOVIMIENTO",
                    fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    datos = Gson().toJson(datosMovimiento),
                    estado = "BORRADOR_AUTO"
                )
            }

            sharedPreferencesManager.guardarBorrador(borrador)
            Log.d("Autoguardado Movimiento", "Borrador guardado: $borradorSesionId")
        } catch (e: Exception) {
            Log.e("Error Autoguardado Movimiento", "Error al guardar: ${e.message}", e)
        }
    }

    fun cargarBorradorExistente() {
        try {
            val borradores = sharedPreferencesManager.obtenerBorradores()

            // Buscar cualquier borrador de tipo MOVIMIENTO con estado BORRADOR_AUTO
            val borradoresMovimiento = borradores.filter {
                it.tipo == "MOVIMIENTO" && it.estado == "BORRADOR_AUTO"
            }

            if (borradoresMovimiento.isNotEmpty()) {
                // Tomar el más reciente (último guardado)
                val borradorMovimiento = borradoresMovimiento.maxByOrNull {
                    it.id.substringAfter("movimiento_auto_").toLongOrNull() ?: 0L
                }

                if (borradorMovimiento != null) {
                    // Asignar este ID a la sesión actual
                    borradorSesionId = borradorMovimiento.id

                    val gson = Gson()
                    val datos: Map<String, Any?> = gson.fromJson(
                        borradorMovimiento.datos,
                        object : TypeToken<Map<String, Any?>>() {}.type
                    )

                    // Restaurar datos
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

                    // Restaurar lista de animales
                    val listaAnimalesJson = datos["listaAnimales"] as? List<*>
                    if (listaAnimalesJson != null) {
                        val listaAnimalesRestaurada = listaAnimalesJson.mapNotNull { item ->
                            try {
                                val itemMap = item as? Map<*, *>
                                IdenMovimiento(
                                    identificador = itemMap?.get("identificador") as? String ?: "",
                                    estatArribada = itemMap?.get("estatArribada") as? String ?: "",
                                    classCanal = itemMap?.get("classCanal") as? String,
                                    dataSacrMort = itemMap?.get("dataSacrMort") as? String,
                                    pesCanal = itemMap?.get("pesCanal") as? String,
                                    tipusPresentacio = itemMap?.get("tipusPresentacio") as? String
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        _listaAnimales.value = listaAnimalesRestaurada.ifEmpty {
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
                        }
                    }

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

    fun obtenerBorradoresMovimiento(): List<Borrador> {
        return try {
            sharedPreferencesManager.obtenerBorradores()
                .filter { it.tipo == "MOVIMIENTO" && it.estado == "BORRADOR_AUTO" }
        } catch (e: Exception) {
            Log.e("Error", "Error al obtener borradores: ${e.message}", e)
            emptyList()
        }
    }

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

    // ============================================
    // LISTAS DE OPCIONES
    // ============================================

    data class CodigoAtes(val codigo: String, val nombre: String)

    val listaCodigosAtes = listOf(
        CodigoAtes("D", "D - Transportista")
    )

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

        val mapaTexto = _textoEstatArribadaPorIndice.value ?: emptyMap()
        _textoEstatArribadaPorIndice.value = mapaTexto + (indice to estatCodigo)

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

        val mapaActual = _tipusPresentacioExpandidoPorIndice.value ?: emptyMap()
        _tipusPresentacioExpandidoPorIndice.value = mapaActual + (indice to false)
    }

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

    fun esFormularioValido(): Boolean {
        val codiRemoValido = !_codiRemo.value.isNullOrEmpty()
        val dataArribadaValida = !_dataArribada.value.isNullOrEmpty()
        val horaArribadaValida = !_horaArribada.value.isNullOrEmpty()
        val codiAtesValido = !_codiAtes.value.isNullOrEmpty()
        val explotacioDestinacioValida = !_explotacioDestinacio.value.isNullOrEmpty()

        val listaAnimales = _listaAnimales.value ?: emptyList()
        val animalesValidos = listaAnimales.all { animal ->
            val identificadorValido = animal.identificador.isNotEmpty()
            val estatValido = animal.estatArribada.isNotEmpty()

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

    fun confirmarMovimiento() {
        _codiError.value = null

        if (!esFormularioValido()) {
            val mensajeError = when {
                _codiRemo.value.isNullOrEmpty() -> 14
                _dataArribada.value.isNullOrEmpty() -> 15
                _horaArribada.value.isNullOrEmpty() -> 16
                _codiAtes.value.isNullOrEmpty() -> 17
                _explotacioDestinacio.value.isNullOrEmpty() -> 18
                _identificadorAnimal.value.isNullOrEmpty() -> 12
                _estatArribada.value.isNullOrEmpty() -> 19
                else -> 0
            }
            _codiError.value = mensajeError
            Log.e("Validación Movimiento", "Error: $mensajeError")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                _cargandoMovimiento.value = true
            }

            try {
                val fechaHoraArribadaAPI = convertirFechaHoraAFormatoAPI(
                    _dataArribada.value ?: "",
                    _horaArribada.value ?: ""
                )

                val listaIdentificadores = _listaAnimales.value ?: emptyList()

                val listaIdentificadoresAPI = listaIdentificadores.map { animal ->
                    animal.copy(
                        dataSacrMort = if (animal.estatArribada == "80" && animal.dataSacrMort != null) {
                            convertirFechaAFormatoAPI(animal.dataSacrMort)
                        } else null
                    )
                }

                val request = PetConfirmacionMovi(
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    especie = "01",
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

                val response = repositorio.putConfirmarMovi(request)

                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false

                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            if (body.codiRemo == "0" || body.descripcio?.contains("correcte", ignoreCase = true) == true) {
                                _registroExitoso.value = true
                                _mensajeError.value = ""

                                Log.d("Confirmar Movimiento", "Movimiento confirmado exitosamente")

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
                                    _mensajeError.value = errorObj.errors?.firstOrNull()?.descripcio
                                        ?: "Error desconocido"
                                } catch (e: Exception) {
                                    _mensajeError.value = "Error al procesar respuesta"
                                }
                            }
                            _registroExitoso.value = false
                            Log.e("Error Movimiento", "HTTP ${response.code()}")
                            if (errorBody != null) {
                                Log.e("Error Movimiento", "Body: $errorBody")
                            }
                        }
                        else -> {
                            _registroExitoso.value = false
                            _mensajeError.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Movimiento", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Movimiento", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargandoMovimiento.value = false
                    _registroExitoso.value = false
                    _mensajeError.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Movimiento", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
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
        _codiTransport.value = ""

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