package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.material.PetSolicitudMaterial
import com.example.terrabit_app.data.network.material.Unitat
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val repositorio: Repositorio,
    private val userPreferences: UserPreferences
) : ViewModel() {

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
                                _registroMaterialExitoso.value = true; _mensajeErrorMaterial.value = ""
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
                    _cargandoMaterial.value = false; _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Tiempo de espera agotado. La operación puede haberse completado, por favor verifique."
                    Log.e("Error Solicitud Material", "Timeout: ${e.message}", e)
                }
            } catch (e: java.io.IOException) {
                withContext(Dispatchers.Main) {
                    _cargandoMaterial.value = false; _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Error de conexión. Verifique su conexión a internet."
                    Log.e("Error Solicitud Material", "Error de red: ${e.message}", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _cargandoMaterial.value = false; _registroMaterialExitoso.value = false
                    _mensajeErrorMaterial.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                    Log.e("Error Solicitud Material", "Error general: ${e.message}", e); e.printStackTrace()
                }
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
    }

    fun resetearEstadoRegistroMaterial() { _registroMaterialExitoso.value = false; _mensajeErrorMaterial.value = "" }
}