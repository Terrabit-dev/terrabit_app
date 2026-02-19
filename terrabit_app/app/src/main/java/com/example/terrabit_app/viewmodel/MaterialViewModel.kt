package com.example.terrabit_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.material.PetSolicitudMaterial
import com.example.terrabit_app.data.network.material.Unitat
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaterialViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del repositorio
    private var repositorio = Repositorio(application)

    // ============================================
    // SECCIÓN: SOLICITUD DE MATERIAL
    // ============================================

    // Estados del formulario de material
    private val _empresaSubministradora = MutableLiveData("")
    val empresaSubministradora = _empresaSubministradora

    private val _codigoEmpresa = MutableLiveData("")

    private val _tipoEnviamiento = MutableLiveData("")

    private var codigoTipoEnvio = ""

    val tipoEnviamiento = _tipoEnviamiento

    private val _destinoLliurament = MutableLiveData("")
    val destinoLliurament = _destinoLliurament

    private var codiDestinoEnvio = ""

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

    // Estado de carga - NUEVO
    private val _cargandoMaterial = MutableLiveData(false)
    val cargandoMaterial = _cargandoMaterial

    // Data classes para opciones UI
    data class EmpresaSubministradora(val nif: String, val nombre: String)
    data class OficinaComarcal(val codigo: String, val nombre: String)
    data class TipoMaterial(val codigo: String, val nombre: String)

    // Instanciar UserPreferences directamente con la Application
    private val userPreferences = UserPreferences(application)

    // Leer las credenciales del login guardadas automáticamente
    val nif = userPreferences.getNif() ?: ""
    val password = userPreferences.getPassword() ?: ""

    // Listas de opciones - Material
    val listaEmpresas = listOf(
        EmpresaSubministradora("A60229508", "Tecnología Agrícola S.L."),
        EmpresaSubministradora("B65432109", "Ganadera del Norte S.A."),
        EmpresaSubministradora("C78945612", "Suministros Ganaderos Catalunya")
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

    fun seleccionarTipoEnviamiento(tipo: String, codigo: String) {
        _tipoEnviamiento.value = tipo
        codigoTipoEnvio = codigo
        _tipoEnviamientoExpandido.value = false
    }

    fun seleccionarDestino(destino: String, codigo: String) {
        _destinoLliurament.value = destino
        codiDestinoEnvio = codigo
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
            // Activar indicador de carga
            _cargandoMaterial.postValue(true)

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
                    nif = nif,
                    passwordMobilitat = password,
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
                    // Desactivar indicador de carga
                    _cargandoMaterial.value = false

                    when {
                        response.isSuccessful && response.body() != null -> {
                            val body = response.body()!!

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
}