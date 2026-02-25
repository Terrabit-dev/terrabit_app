package com.example.terrabit_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Identificadores.IdenSolicitudDupli
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.material.PetSolicitudMaterial
import com.example.terrabit_app.data.network.material.Unitat
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaterialViewModel(application: Application) : AndroidViewModel(application) {

    private var repositorio = Repositorio(application)
    private val userPreferences = UserPreferences(application)

    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""

    // ============================================
    // ESTADOS DEL FORMULARIO
    // ============================================

    private val _empresaSubministradora = MutableLiveData("")
    val empresaSubministradora = _empresaSubministradora
    private val _codigoEmpresa = MutableLiveData("")

    private val _tipoEnviamiento = MutableLiveData("")
    val tipoEnviamiento = _tipoEnviamiento
    // Almacena el código real del tipo de envío (ej: "01", "04")
    private var codigoTipoEnvio = ""

    private val _destinoLliurament = MutableLiveData("")
    val destinoLliurament = _destinoLliurament
    // Almacena el código real del destino (ej: "01", "02", "03")
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
    // Almacena el código real del tipo de material (ej: "21", "22", "25", "26")
    private val _codigoTipoMaterial = MutableLiveData("")

    // ============================================
    // TIPOS DE MATERIAL QUE REQUIEREN CODI MO
    // ============================================

    // Estos códigos de material requieren codiMo obligatorio en cada unidad
    private val tiposMaterialConCodiMoObligatorio = setOf("21", "22", "25", "26")

    fun codiMoEsObligatorio(): Boolean {
        return _codigoTipoMaterial.value in tiposMaterialConCodiMoObligatorio
    }

    // ============================================
    // ESTADOS DE EXPANSIÓN DE MENÚS
    // ============================================

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

    // ============================================
    // ESTADOS DE FEEDBACK
    // ============================================

    private val _registroMaterialExitoso = MutableLiveData<Boolean>()
    val registroMaterialExitoso = _registroMaterialExitoso

    private val _mensajeErrorMaterial = MutableLiveData<String>()
    val mensajeErrorMaterial = _mensajeErrorMaterial

    private val _cargandoMaterial = MutableLiveData(false)
    val cargandoMaterial = _cargandoMaterial

    // ============================================
    // SECCIÓN: Lista de unidades
    // ============================================

    private val _listaUnidades = MutableLiveData<List<Unitat>>(
        listOf(Unitat(codiExplotacio = "", nombreUnitats = ""))
    )
    val listaUnidades = _listaUnidades

    fun agregarUnidades() {
        val listaActual = _listaUnidades.value?.toMutableList() ?: mutableListOf()
        listaActual.add(Unitat(codiExplotacio = "", nombreUnitats = ""))
        _listaUnidades.value = listaActual
    }

    fun eliminarUnidades(indice: Int) {
        val listaActual = _listaUnidades.value?.toMutableList() ?: return
        if (listaActual.size > 1) {
            listaActual.removeAt(indice)
            _listaUnidades.value = listaActual
        }
    }

    fun actualizarCodiExplotacio(indice: Int, valor: String) {
        val listaActual = _listaUnidades.value?.toMutableList() ?: return
        if (indice < listaActual.size) {
            listaActual[indice] = listaActual[indice].copy(codiExplotacio = valor)
            _listaUnidades.value = listaActual
        }
    }

    fun actualizarUnidades(indice: Int, valor: String) {
        val listaActual = _listaUnidades.value?.toMutableList() ?: return
        if (indice < listaActual.size) {
            listaActual[indice] = listaActual[indice].copy(nombreUnitats = valor)
            _listaUnidades.value = listaActual
        }
    }

    // ============================================
    // FUNCIONES DE SELECCIÓN
    // ============================================

    fun seleccionarEmpresa(nombre: String, nif: String) {
        _empresaSubministradora.value = nombre
        _codigoEmpresa.value = nif
        _empresaExpandida.value = false
    }

    fun seleccionarTipoEnviamiento(tipo: String, codigo: String) {
        _tipoEnviamiento.value = tipo
        codigoTipoEnvio = codigo             // Guardamos el código real
        _tipoEnviamientoExpandido.value = false
    }

    fun seleccionarDestino(destino: String, codigo: String) {
        _destinoLliurament.value = destino
        codiDestinoEnvio = codigo            // Guardamos el código real

        // Limpieza de campos condicionales según el CÓDIGO (no el nombre)
        when (codigo) {
            "01" -> {                        // OC: limpiar campos de dirección libre
                _direccion.value = ""
                _poblacion.value = ""
                _codigoPostal.value = ""
                _municipio.value = ""
                _telefonoContacto.value = ""
            }
            "02", "03" -> {                 // Explotación / Dirección alternativa: limpiar OC
                _oficinaComarcal.value = ""
                codigoOC = ""
            }
        }

        _destinoExpandido.value = false
    }

    fun seleccionarOficinaComarcal(nombre: String, codigo: String) {
        _oficinaComarcal.value = nombre
        codigoOC = codigo
        _oficinaComarcalExpandida.value = false
    }

    fun seleccionarTipoMaterial(nombre: String, codigo: String) {
        _tipoMaterial.value = nombre
        _codigoTipoMaterial.value = codigo   // Guardamos el código real
        _tipoMaterialExpandido.value = false
    }

    fun actualizarDireccion(valor: String) { _direccion.value = valor }
    fun actualizarPoblacion(valor: String) { _poblacion.value = valor }
    fun actualizarCodigoPostal(valor: String) {
        if (valor.length <= 5 && (valor.isEmpty() || valor.all { it.isDigit() })) {
            _codigoPostal.value = valor
        }
    }
    fun actualizarMunicipio(valor: String) { _municipio.value = valor }
    fun actualizarTelefonoContacto(valor: String) {
        if (valor.all { it.isDigit() || it.isWhitespace() }) {
            _telefonoContacto.value = valor
        }
    }
    fun actualizarIdentificadorMaterial(valor: String) { _identificadorMaterial.value = valor }

    // ============================================
    // FUNCIONES DE EXPANSIÓN DE MENÚS
    // ============================================

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

    // ============================================
    // EXPOSICIÓN DE CÓDIGOS INTERNOS (para la UI)
    // ============================================

    fun getCodigoTipoEnvio(): String = codigoTipoEnvio
    fun getCodiDestinoEnvio(): String = codiDestinoEnvio
    fun getCodigoTipoMaterial(): String = _codigoTipoMaterial.value ?: ""

    // ============================================
    // VALIDACIÓN
    // ============================================

    fun esFormularioMaterialValido(): Boolean {
        // Campos siempre obligatorios
        if (_empresaSubministradora.value.isNullOrEmpty()) return false
        if (_tipoEnviamiento.value.isNullOrEmpty()) return false
        if (_destinoLliurament.value.isNullOrEmpty()) return false
        if (_tipoMaterial.value.isNullOrEmpty()) return false

        // Campos condicionales según el CÓDIGO real del destino
        when (codiDestinoEnvio) {
            "01" -> if (codigoOC.isEmpty()) return false
            "02" -> { /* campos de dirección opcionales */ }
            "03" -> {
                if (_direccion.value.isNullOrEmpty()) return false
                if (_poblacion.value.isNullOrEmpty()) return false
                if (_codigoPostal.value.isNullOrEmpty()) return false
                if (_municipio.value.isNullOrEmpty()) return false
                if (_telefonoContacto.value.isNullOrEmpty()) return false
            }
        }

        // Validar unidades: nombreUnitats siempre obligatorio
        val unidades = _listaUnidades.value ?: return false
        if (unidades.isEmpty()) return false
        if (unidades.any { it.nombreUnitats.isNullOrEmpty() }) return false

        // Si el tipo de material requiere codiMo, validar que todas las unidades lo tengan
        if (codiMoEsObligatorio()) {
            if (unidades.any { it.codiExplotacio.isNullOrEmpty() }) return false
        }

        return true
    }

    // ============================================
    // LLAMADA A LA API
    // ============================================

    fun solicitarMaterial() {
        if (!esFormularioMaterialValido()) {
            val mensajeError = when {
                _empresaSubministradora.value.isNullOrEmpty() ->
                    "Por favor, seleccione la empresa subministradora"
                _tipoEnviamiento.value.isNullOrEmpty() ->
                    "Por favor, seleccione el tipo de envío"
                _destinoLliurament.value.isNullOrEmpty() ->
                    "Por favor, seleccione el destino de entrega"
                codiDestinoEnvio == "01" && codigoOC.isEmpty() ->
                    "Por favor, seleccione la oficina comarcal"
                codiDestinoEnvio == "03" && _direccion.value.isNullOrEmpty() ->
                    "Por favor, introduzca la dirección"
                codiDestinoEnvio == "03" && _poblacion.value.isNullOrEmpty() ->
                    "Por favor, introduzca la población"
                codiDestinoEnvio == "03" && _codigoPostal.value.isNullOrEmpty() ->
                    "Por favor, introduzca el código postal"
                codiDestinoEnvio == "03" && _municipio.value.isNullOrEmpty() ->
                    "Por favor, introduzca el municipio"
                codiDestinoEnvio == "03" && _telefonoContacto.value.isNullOrEmpty() ->
                    "Por favor, introduzca el teléfono de contacto"
                _tipoMaterial.value.isNullOrEmpty() ->
                    "Por favor, seleccione el tipo de material"
                _listaUnidades.value?.any { it.nombreUnitats.isNullOrEmpty() } == true ->
                    "Por favor, introduzca el número de unidades en cada fila"
                codiMoEsObligatorio() && _listaUnidades.value?.any { it.codiExplotacio.isNullOrEmpty() } == true ->
                    "El Codi MO es obligatorio para el tipo de material seleccionado"
                else ->
                    "Por favor, complete todos los campos obligatorios"
            }
            _mensajeErrorMaterial.value = mensajeError
            Log.e("Validación Material", mensajeError)
            return
        }

        viewModelScope.launch {
            _cargandoMaterial.postValue(true)
            try {
                // Usamos los códigos reales almacenados, no intentamos extraerlos del texto mostrado
                val adrecaFinal = if (codiDestinoEnvio == "03") _direccion.value else null
                val poblacionFinal = if (codiDestinoEnvio == "03") _poblacion.value else null
                val cpFinal = if (codiDestinoEnvio == "03") _codigoPostal.value else null
                val municipioFinal = if (codiDestinoEnvio == "03") _municipio.value else null
                val telefonoFinal = if (codiDestinoEnvio == "03") _telefonoContacto.value else null
                val ocFinal = if (codiDestinoEnvio == "01") codigoOC else null

                // Para "02": incluir los campos de dirección si el usuario los rellenó (opcionales)
                val adrecaFinalConOpcional = if (codiDestinoEnvio == "02") _direccion.value?.takeIf { it.isNotEmpty() } else adrecaFinal
                val poblacionFinalConOpcional = if (codiDestinoEnvio == "02") _poblacion.value?.takeIf { it.isNotEmpty() } else poblacionFinal
                val cpFinalConOpcional = if (codiDestinoEnvio == "02") _codigoPostal.value?.takeIf { it.isNotEmpty() } else cpFinal
                val municipioFinalConOpcional = if (codiDestinoEnvio == "02") _municipio.value?.takeIf { it.isNotEmpty() } else municipioFinal
                val telefonoFinalConOpcional = if (codiDestinoEnvio == "02") _telefonoContacto.value?.takeIf { it.isNotEmpty() } else telefonoFinal

                val unidades = _listaUnidades.value ?: listOf(Unitat(codiExplotacio = null, nombreUnitats = "1"))

                val request = PetSolicitudMaterial(
                    nif = nif,
                    passwordMobilitat = password,
                    especie = "01",
                    empresaSubministradora = _codigoEmpresa.value ?: "",
                    tipusEnviament = codigoTipoEnvio,       // Código real, no texto
                    adrecaLliurament = codiDestinoEnvio,    // Código real, no texto
                    oc = ocFinal,
                    adreca = adrecaFinalConOpcional,
                    poblacio = poblacionFinalConOpcional,
                    cp = cpFinalConOpcional,
                    municipi = municipioFinalConOpcional,
                    telefonContacte = telefonoFinalConOpcional,
                    tipusMaterial = _codigoTipoMaterial.value ?: "",  // Código real
                    unitats = unidades
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
                                Log.d("Solicitud Material", "Exitoso: [${body.codi}] ${body.descripcio}")
                                limpiarFormularioMaterial()
                            } else {
                                _registroMaterialExitoso.value = false
                                _mensajeErrorMaterial.value = "Error: [${body.codi}] ${body.descripcio}"
                                Log.e("Error Solicitud Material", "[${body.codi}] ${body.descripcio}")
                            }
                        }
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            _registroMaterialExitoso.value = false
                            _mensajeErrorMaterial.value = "Error HTTP ${response.code()}: ${response.message()}"
                            Log.e("Error Solicitud Material", "HTTP ${response.code()} - $errorBody")
                        }
                        else -> {
                            _registroMaterialExitoso.value = false
                            _mensajeErrorMaterial.value = "Error: Respuesta vacía del servidor"
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _cargandoMaterial.value = false
                    _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Solicitud Material", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargandoMaterial.value = false
                    _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Solicitud Material", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _cargandoMaterial.value = false
                    _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Solicitud Material", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    fun limpiarFormularioMaterial() {
        _empresaSubministradora.value = ""
        _codigoEmpresa.value = ""
        _tipoEnviamiento.value = ""
        codigoTipoEnvio = ""
        _destinoLliurament.value = ""
        codiDestinoEnvio = ""
        _oficinaComarcal.value = ""
        codigoOC = ""
        _direccion.value = ""
        _poblacion.value = ""
        _codigoPostal.value = ""
        _municipio.value = ""
        _telefonoContacto.value = ""
        _tipoMaterial.value = ""
        _codigoTipoMaterial.value = ""
        _listaUnidades.value = listOf(Unitat(codiExplotacio = "", nombreUnitats = ""))
    }

    fun resetearEstadoRegistroMaterial() {
        _registroMaterialExitoso.value = false
        _mensajeErrorMaterial.value = ""
    }
}