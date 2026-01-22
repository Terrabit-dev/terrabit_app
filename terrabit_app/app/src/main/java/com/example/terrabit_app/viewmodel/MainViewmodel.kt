package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.Identificadores.Identificadores
import com.example.terrabit_app.data.network.animales.PetModicarAnimal
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
import com.example.terrabit_app.data.network.animales.RegistroNacimientoBovi
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import com.example.terrabit_app.data.network.material.PetSolicitudMaterial
import com.example.terrabit_app.data.network.material.Unitat
import com.example.terrabit_app.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainViewmodel : ViewModel() {
    private val repositorio = Repositorio()

    // ============================================
    // SECCIÓN: IDENTIFICADORES
    // ============================================
    private val _identificadores = MutableLiveData<Identificadores>()
    val identificadores = _identificadores

    fun getIdentificadores(nif: String, password: String, codiMO: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = repositorio.getIdentificadoresDisponibles(nif, password, codiMO)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _identificadores.value = response.body()
                } else {
                    Log.e("Error identificadores:", response.message())
                }
            }
        }
    }







    // ============================================
// SECCIÓN: SOLICITUD DE MATERIAL
// ============================================

    // Estados del formulario de material
    private val _empresaSubministradora = MutableLiveData("")
    val empresaSubministradora = _empresaSubministradora

    private val _codigoEmpresa = MutableLiveData("")

    private val _tipoEnviamiento = MutableLiveData("")
    val tipoEnviamiento = _tipoEnviamiento

    private val _destinoLliurament = MutableLiveData("")
    val destinoLliurament = _destinoLliurament

    private val _oficinaComarcal = MutableLiveData("")
    val oficinaComarcal = _oficinaComarcal

    private val _codigoOC = MutableLiveData("")

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

    private val _numeroUnidades = MutableLiveData("1")
    val numeroUnidades = _numeroUnidades

    private val _codigoExplotacion = MutableLiveData("")
    val codigoExplotacion = _codigoExplotacion

    // Estados de expansión de menús desplegables - Material
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

    // Estados para feedback del registro - Material
    private val _registroMaterialExitoso = MutableLiveData<Boolean>()
    val registroMaterialExitoso = _registroMaterialExitoso

    private val _mensajeErrorMaterial = MutableLiveData<String>()
    val mensajeErrorMaterial = _mensajeErrorMaterial

    // Data classes para opciones UI
    data class EmpresaSubministradora(val nif: String, val nombre: String)
    data class OficinaComarcal(val codigo: String, val nombre: String)
    data class TipoMaterial(val codigo: String, val nombre: String)

    // Listas de opciones - Material
    val listaEmpresas = listOf(
        EmpresaSubministradora("A60229508", "Tecnología Agrícola S.L."),
        EmpresaSubministradora("B65432109", "Ganadera del Norte S.A."),
        EmpresaSubministradora("C78945612", "Suministros Ganaderos Catalunya")
    )

    val listaTiposEnviamiento = listOf(
        "01 - Correo ordinario",
        "04 - Correo certificado"
    )

    val listaDestinos = listOf(
        "01 - Oficina Comarcal (OC)",
        "02 - Ramader/ER",
        "03 - Dirección alternativa"
    )

    val listaOficinasComarcales = listOf(
        OficinaComarcal("OC001", "Barcelona"),
        OficinaComarcal("OC002", "Girona"),
        OficinaComarcal("OC003", "Lleida"),
        OficinaComarcal("OC004", "Tarragona")
    )

    val listaTiposMaterial = listOf(
        TipoMaterial("07", "Crotal"),
        TipoMaterial("20", "Crotal electrónico"),
        TipoMaterial("21", "Injectable electrónico"),
        TipoMaterial("22", "Bol ruminal")
    )

    // Funciones para actualizar los campos - Material
    fun seleccionarEmpresa(nombre: String, nif: String) {
        _empresaSubministradora.value = nombre
        _codigoEmpresa.value = nif
        _empresaExpandida.value = false
    }

    fun seleccionarTipoEnviamiento(tipo: String) {
        _tipoEnviamiento.value = tipo
        _tipoEnviamientoExpandido.value = false
    }

    fun seleccionarDestino(destino: String) {
        _destinoLliurament.value = destino
        _destinoExpandido.value = false

        when {
            destino.startsWith("01") -> {
                _direccion.value = ""
                _poblacion.value = ""
                _codigoPostal.value = ""
                _municipio.value = ""
                _telefonoContacto.value = ""
            }
            destino.startsWith("02") -> {
                _oficinaComarcal.value = ""
                _codigoOC.value = ""
            }
            destino.startsWith("03") -> {
                _oficinaComarcal.value = ""
                _codigoOC.value = ""
            }
        }
    }

    fun seleccionarOficinaComarcal(nombre: String, codigo: String) {
        _oficinaComarcal.value = nombre
        _codigoOC.value = codigo
        _oficinaComarcalExpandida.value = false
    }

    fun seleccionarTipoMaterial(nombre: String, codigo: String) {
        _tipoMaterial.value = nombre
        _codigoTipoMaterial.value = codigo
        _tipoMaterialExpandido.value = false
    }

    fun actualizarDireccion(valor: String) {
        _direccion.value = valor
    }

    fun actualizarPoblacion(valor: String) {
        _poblacion.value = valor
    }

    fun actualizarCodigoPostal(valor: String) {
        if (valor.length <= 5 && (valor.isEmpty() || valor.all { it.isDigit() })) {
            _codigoPostal.value = valor
        }
    }

    fun actualizarMunicipio(valor: String) {
        _municipio.value = valor
    }

    fun actualizarTelefonoContacto(valor: String) {
        if (valor.all { it.isDigit() || it.isWhitespace() }) {
            _telefonoContacto.value = valor
        }
    }

    fun actualizarIdentificadorMaterial(valor: String) {
        _identificadorMaterial.value = valor
    }

    fun actualizarNumeroUnidades(valor: String) {
        if (valor.isEmpty() || valor.all { it.isDigit() }) {
            _numeroUnidades.value = valor
        }
    }

    fun actualizarCodigoExplotacion(valor: String) {
        _codigoExplotacion.value = valor
    }

    // Funciones para controlar la expansión de menús - Material
    fun toggleEmpresaExpandida() {
        _empresaExpandida.value = !(_empresaExpandida.value ?: false)
    }

    fun toggleTipoEnviamientoExpandido() {
        _tipoEnviamientoExpandido.value = !(_tipoEnviamientoExpandido.value ?: false)
    }

    fun toggleDestinoExpandido() {
        _destinoExpandido.value = !(_destinoExpandido.value ?: false)
    }

    fun toggleOficinaComarcalExpandida() {
        _oficinaComarcalExpandida.value = !(_oficinaComarcalExpandida.value ?: false)
    }

    fun toggleTipoMaterialExpandido() {
        _tipoMaterialExpandido.value = !(_tipoMaterialExpandido.value ?: false)
    }

    fun cerrarEmpresaMenu() {
        _empresaExpandida.value = false
    }

    fun cerrarTipoEnviamientoMenu() {
        _tipoEnviamientoExpandido.value = false
    }

    fun cerrarDestinoMenu() {
        _destinoExpandido.value = false
    }

    fun cerrarOficinaComarcalMenu() {
        _oficinaComarcalExpandida.value = false
    }

    fun cerrarTipoMaterialMenu() {
        _tipoMaterialExpandido.value = false
    }

    // Función para validar el formulario - Material
    fun esFormularioMaterialValido(): Boolean {
        val empresaValida = !_empresaSubministradora.value.isNullOrEmpty()
        val tipoEnviamientoValido = !_tipoEnviamiento.value.isNullOrEmpty()
        val destinoValido = !_destinoLliurament.value.isNullOrEmpty()
        val identificadorValido = !_identificadorMaterial.value.isNullOrEmpty()
        val tipoMaterialValido = !_tipoMaterial.value.isNullOrEmpty()

        val camposDestinoValidos = when {
            _destinoLliurament.value?.startsWith("01") == true -> {
                !_oficinaComarcal.value.isNullOrEmpty()
            }
            _destinoLliurament.value?.startsWith("03") == true -> {
                !_direccion.value.isNullOrEmpty() &&
                        !_poblacion.value.isNullOrEmpty() &&
                        !_codigoPostal.value.isNullOrEmpty() &&
                        !_municipio.value.isNullOrEmpty() &&
                        !_telefonoContacto.value.isNullOrEmpty()
            }
            else -> true
        }

        return empresaValida && tipoEnviamientoValido && destinoValido &&
                camposDestinoValidos && identificadorValido && tipoMaterialValido
    }

    // Función para solicitar material
// Función para solicitar material
    fun solicitarMaterial() {
        if (!esFormularioMaterialValido()) {
            val mensajeError = when {
                _empresaSubministradora.value.isNullOrEmpty() ->
                    "Por favor, seleccione la empresa subministradora"
                _tipoEnviamiento.value.isNullOrEmpty() ->
                    "Por favor, seleccione el tipo de envío"
                _destinoLliurament.value.isNullOrEmpty() ->
                    "Por favor, seleccione el destino de entrega"
                _destinoLliurament.value?.startsWith("01") == true && _oficinaComarcal.value.isNullOrEmpty() ->
                    "Por favor, seleccione la oficina comarcal"
                _destinoLliurament.value?.startsWith("03") == true && _direccion.value.isNullOrEmpty() ->
                    "Por favor, introduzca la dirección"
                _destinoLliurament.value?.startsWith("03") == true && _poblacion.value.isNullOrEmpty() ->
                    "Por favor, introduzca la población"
                _destinoLliurament.value?.startsWith("03") == true && _codigoPostal.value.isNullOrEmpty() ->
                    "Por favor, introduzca el código postal"
                _destinoLliurament.value?.startsWith("03") == true && _municipio.value.isNullOrEmpty() ->
                    "Por favor, introduzca el municipio"
                _destinoLliurament.value?.startsWith("03") == true && _telefonoContacto.value.isNullOrEmpty() ->
                    "Por favor, introduzca el teléfono de contacto"
                _identificadorMaterial.value.isNullOrEmpty() ->
                    "Por favor, introduzca el identificador"
                _tipoMaterial.value.isNullOrEmpty() ->
                    "Por favor, seleccione el tipo de material"
                else ->
                    "Por favor, complete todos los campos obligatorios"
            }
            _mensajeErrorMaterial.value = mensajeError
            Log.e("Validación Material", mensajeError)
            return
        }

        viewModelScope.launch {
            try {
                val codigoTipoEnvio = _tipoEnviamiento.value?.substring(0, 2) ?: ""
                val codigoDestino = _destinoLliurament.value?.substring(0, 2) ?: ""

                val adrecaFinal = if (codigoDestino == "03") _direccion.value else null
                val poblacionFinal = if (codigoDestino == "03") _poblacion.value else null
                val cpFinal = if (codigoDestino == "03") _codigoPostal.value else null
                val municipioFinal = if (codigoDestino == "03") _municipio.value else null
                val telefonoFinal = if (codigoDestino == "03") _telefonoContacto.value else null
                val ocFinal = if (codigoDestino == "01") _codigoOC.value else null

                val unidades = listOf(
                    Unitat(
                        codiExplotacio = _codigoExplotacion.value?.takeIf { it.isNotEmpty() },
                        nombreUnitats = _numeroUnidades.value ?: "1"
                    )
                )

                val request = PetSolicitudMaterial(
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    especie = "01",
                    empresaSubministradora = _codigoEmpresa.value ?: "",
                    tipusEnviament = codigoTipoEnvio,
                    adrecaLliurament = codigoDestino,
                    oc = ocFinal,
                    adreca = adrecaFinal,
                    poblacio = poblacionFinal,
                    cp = cpFinal,
                    municipi = municipioFinal,
                    telefonContacte = telefonoFinal,
                    tipusMaterial = _codigoTipoMaterial.value ?: "",
                    unitats = unidades
                )

                Log.d("Solicitud Material", "Request: $request")

                val response = repositorio.putSolicitudMaterial(request)

                withContext(Dispatchers.Main) {
                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            // ResBasica probablemente tiene: codi, descripcio (sin errors)
                            if (body.codi == "0" || body.descripcio == "OK") {
                                _registroMaterialExitoso.value = true
                                _mensajeErrorMaterial.value = ""

                                Log.d("Solicitud Material", "Material solicitado exitosamente")
                                Log.d("Solicitud Material", "Respuesta: [${body.codi}] ${body.descripcio}")

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

                            Log.e("Error Solicitud Material", "HTTP ${response.code()}")
                            Log.e("Error Solicitud Material", "Mensaje: ${response.message()}")
                            if (errorBody != null) {
                                Log.e("Error Solicitud Material", "Body: $errorBody")
                            }
                        }
                        else -> {
                            _registroMaterialExitoso.value = false
                            _mensajeErrorMaterial.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Solicitud Material", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Tiempo de espera agotado"
                    Log.e("Error Solicitud Material", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Error de conexión"
                    Log.e("Error Solicitud Material", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Error inesperado: ${e.message}"
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
        _destinoLliurament.value = ""
        _oficinaComarcal.value = ""
        _codigoOC.value = ""
        _direccion.value = ""
        _poblacion.value = ""
        _codigoPostal.value = ""
        _municipio.value = ""
        _telefonoContacto.value = ""
        _identificadorMaterial.value = ""
        _tipoMaterial.value = ""
        _codigoTipoMaterial.value = ""
        _numeroUnidades.value = "1"
        _codigoExplotacion.value = ""
    }

    fun resetearEstadoRegistroMaterial() {
        _registroMaterialExitoso.value = false
        _mensajeErrorMaterial.value = ""
    }

    // ============================================
    // FUNCIONES AUXILIARES
    // ============================================

    /**
     * Convierte una fecha de formato "dd/MM/yyyy" a "yyyymmdd"
     */


    /**
     * Convierte una fecha de formato "yyyymmdd" a "dd/MM/yyyy"
     */
    private fun convertirFechaDesdeAPI(fechaAPI: String): String {
        return try {
            if (fechaAPI.length == 8) {
                val anio = fechaAPI.substring(0, 4)
                val mes = fechaAPI.substring(4, 6)
                val dia = fechaAPI.substring(6, 8)
                "$dia/$mes/$anio"
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e("Error conversión fecha", e.message ?: "Error desconocido")
            ""
        }
    }

    // ============================================
    // SECCIÓN: CORRECCIÓN DE SEXO
    // ============================================

    // Estados del formulario de corrección de sexo
    private val _identificadorCorreccionSexo = MutableLiveData("")
    val identificadorCorreccionSexo = _identificadorCorreccionSexo

    private val _sexoCorreccionSeleccionado = MutableLiveData("")
    val sexoCorreccionSeleccionado = _sexoCorreccionSeleccionado

    // Estados de expansión de menús desplegables
    private val _sexoCorreccionExpandido = MutableLiveData(false)
    val sexoCorreccionExpandido = _sexoCorreccionExpandido

    // Estados para feedback del registro
    private val _correccionSexoExitosa = MutableLiveData<Boolean>()
    val correccionSexoExitosa = _correccionSexoExitosa

    private val _mensajeErrorCorreccionSexo = MutableLiveData<String>()
    val mensajeErrorCorreccionSexo = _mensajeErrorCorreccionSexo

    // Lista de opciones de sexo (AGREGADO)
    val listaSexos = listOf("Macho", "Hembra")

    // Funciones para actualizar los campos
    fun actualizarIdentificadorCorreccionSexo(nuevoId: String) {
        _identificadorCorreccionSexo.value = nuevoId
    }

    fun seleccionarSexoCorreccion(sexo: String) {
        _sexoCorreccionSeleccionado.value = sexo
        _sexoCorreccionExpandido.value = false
    }

    // Funciones para controlar la expansión de menús
    fun toggleSexoCorreccionExpandido() {
        _sexoCorreccionExpandido.value = !(_sexoCorreccionExpandido.value ?: false)
    }

    fun cerrarSexoCorreccionMenu() {
        _sexoCorreccionExpandido.value = false
    }

    // Función para validar el formulario
    fun esFormularioCorreccionSexoValido(): Boolean {
        val identificadorValido = !_identificadorCorreccionSexo.value.isNullOrEmpty()
        val sexoValido = !_sexoCorreccionSeleccionado.value.isNullOrEmpty()
        return identificadorValido && sexoValido
    }

    // Función para corregir el sexo del animal
    fun corregirSexoAnimal() {
        // Validar que todos los campos requeridos estén completos
        if (!esFormularioCorreccionSexoValido()) {
            val mensajeError = when {
                _identificadorCorreccionSexo.value.isNullOrEmpty() ->
                    "Por favor, introduzca el identificador del animal"
                _sexoCorreccionSeleccionado.value.isNullOrEmpty() ->
                    "Por favor, seleccione el sexo correcto"
                else ->
                    "Por favor, complete todos los campos obligatorios marcados con *"
            }
            _mensajeErrorCorreccionSexo.value = mensajeError
            Log.e("Validación Corrección Sexo", mensajeError)
            return
        }

        viewModelScope.launch {
            try {
                // Convertir sexo al formato de la API
                val sexoAPI = when (_sexoCorreccionSeleccionado.value) {
                    "Macho" -> "02"
                    "Hembra" -> "01"
                    else -> ""
                }

                // Crear objeto de petición
                val request = PetModicarAnimal(
                    identificador = _identificadorCorreccionSexo.value ?: "",
                    nif = "S0800608B",
                    passwordMobilitat = "L1855m58",
                    sexe = sexoAPI
                )

                Log.d("Corrección Sexo", "📤 Enviando petición a la API...")
                Log.d("Corrección Sexo", "Request: $request")

                // Llamar a la API
                val response = repositorio.putMoficarAnimal(request)

                // Procesar respuesta
                withContext(Dispatchers.Main) {
                    when {
                        // Caso 1: HTTP 200 OK
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

                            // Verificar si hay errores en el body
                            if (body.errors != null && body.errors.isNotEmpty()) {
                                // La API devolvió errores
                                val erroresTexto = body.errors.joinToString("\n") { error ->
                                    "• [${error.codi}] ${error.descripcio}"
                                }

                                _correccionSexoExitosa.value = false
                                _mensajeErrorCorreccionSexo.value = "Error al corregir sexo:\n$erroresTexto"

                                body.errors.forEach { error ->
                                    Log.e("Error Corrección Sexo", "  - [${error.codi}] ${error.descripcio}")
                                }
                            }
                            // Verificar si es respuesta exitosa (codi = "0")
                            else if (body.codi == "0" || body.descripcio == "OK") {
                                _correccionSexoExitosa.value = true
                                _mensajeErrorCorreccionSexo.value = ""

                                Log.d("Corrección Sexo", "Sexo corregido exitosamente")
                                Log.d("Corrección Sexo", "Respuesta: [${body.codi}] ${body.descripcio}")
                                Log.d("Corrección Sexo", "Identificador: ${_identificadorCorreccionSexo.value}")
                                Log.d("Corrección Sexo", "Sexo: $sexoAPI (${_sexoCorreccionSeleccionado.value})")

                                // Limpiar formulario después de corregir exitosamente
                                limpiarFormularioCorreccionSexo()
                            }
                            // Caso inesperado
                            else {
                                _correccionSexoExitosa.value = false
                                _mensajeErrorCorreccionSexo.value = "Respuesta inesperada del servidor: [${body.codi}] ${body.descripcio}"
                                Log.w("Corrección Sexo", "Respuesta inesperada: [${body.codi}] ${body.descripcio}")
                            }
                        }

                        // Caso 2: HTTP Error (4xx, 5xx)
                        !response.isSuccessful -> {
                            val errorBody = response.errorBody()?.string()
                            _correccionSexoExitosa.value = false
                            _mensajeErrorCorreccionSexo.value = "Error HTTP ${response.code()}: ${response.message()}"

                            if (errorBody != null) {
                                Log.e("Error Corrección Sexo", "Body: $errorBody")
                            }
                        }

                        // Caso 3: Respuesta exitosa pero sin body
                        else -> {
                            _correccionSexoExitosa.value = false
                            _mensajeErrorCorreccionSexo.value = "Error: Respuesta vacía del servidor"
                            Log.e("Error Corrección Sexo", "Respuesta vacía del servidor")
                        }
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _correccionSexoExitosa.value = false
                    _mensajeErrorCorreccionSexo.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Corrección Sexo", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _correccionSexoExitosa.value = false
                    _mensajeErrorCorreccionSexo.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Corrección Sexo", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _correccionSexoExitosa.value = false
                    _mensajeErrorCorreccionSexo.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Corrección Sexo", "Error general: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }
    }

    // Función para limpiar el formulario
    fun limpiarFormularioCorreccionSexo() {
        _identificadorCorreccionSexo.value = ""
        _sexoCorreccionSeleccionado.value = ""
    }

    // Función para resetear el estado de registro
    fun resetearEstadoCorreccionSexo() {
        _correccionSexoExitosa.value = false
        _mensajeErrorCorreccionSexo.value = ""
    }

}