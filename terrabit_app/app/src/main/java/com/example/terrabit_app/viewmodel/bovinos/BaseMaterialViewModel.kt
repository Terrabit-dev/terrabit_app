package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class BaseMaterialViewModel : BaseBovinoViewModel() {

    // ─── Bloque envío — 100% idéntico en ambos VMs ────────────────────────────
    protected val _empresaSubministradora = MutableLiveData("")
    val empresaSubministradora: LiveData<String> = _empresaSubministradora
    protected var codigoEmpresa = ""

    protected val _tipoEnviamiento = MutableLiveData(0)
    val tipoEnviamiento: LiveData<Int> = _tipoEnviamiento
    protected var codigoTipoEnvio = ""

    protected val _destinoEnvio = MutableLiveData(0)
    val destinoEnvio: LiveData<Int> = _destinoEnvio
    protected var codigoDestino = ""

    protected val _oficinaComarcal = MutableLiveData("")
    val oficinaComarcal: LiveData<String> = _oficinaComarcal
    protected var codigoOC = ""

    protected val _direccion = MutableLiveData("")
    val direccion: LiveData<String> = _direccion

    protected val _poblacion = MutableLiveData("")
    val poblacion: LiveData<String> = _poblacion

    protected val _codigoPostal = MutableLiveData("")
    val codigoPostal: LiveData<String> = _codigoPostal

    protected val _municipio = MutableLiveData("")
    val municipio: LiveData<String> = _municipio

    protected val _telefonoContacto = MutableLiveData("")
    val telefonoContacto: LiveData<String> = _telefonoContacto

    // ─── Dropdowns del bloque envío ───────────────────────────────────────────
    private val _empresaExpandida = MutableLiveData(false)
    val empresaExpandida: LiveData<Boolean> = _empresaExpandida

    private val _tipoEnviamientoExpandido = MutableLiveData(false)
    val tipoEnviamientoExpandido: LiveData<Boolean> = _tipoEnviamientoExpandido

    private val _destinoExpandido = MutableLiveData(false)
    val destinoExpandido: LiveData<Boolean> = _destinoExpandido

    private val _oficinaComarcalExpandida = MutableLiveData(false)
    val oficinaComarcalExpandida: LiveData<Boolean> = _oficinaComarcalExpandida

    // ─── Acciones del bloque envío ────────────────────────────────────────────
    fun seleccionarEmpresa(nombre: String, codigo: String) {
        _empresaSubministradora.value = nombre
        codigoEmpresa = codigo
        _empresaExpandida.value = false
    }

    fun seleccionarTipoEnviamiento(tipo: Int, codigo: String) {
        _tipoEnviamiento.value = tipo
        codigoTipoEnvio = codigo
        _tipoEnviamientoExpandido.value = false
    }

    fun seleccionarDestino(destino: Int, codigo: String) {
        _destinoEnvio.value = destino
        codigoDestino = codigo
        // Limpia campos del destino contrario al seleccionado
        when (codigo) {
            "01" -> { _direccion.value = ""; _poblacion.value = ""; _codigoPostal.value = ""; _municipio.value = ""; _telefonoContacto.value = "" }
            "02", "03" -> { _oficinaComarcal.value = ""; codigoOC = "" }
        }
        _destinoExpandido.value = false
    }

    fun seleccionarOficinaComarcal(nombre: String, codigo: String) {
        _oficinaComarcal.value = nombre
        codigoOC = codigo
        _oficinaComarcalExpandida.value = false
    }

    fun actualizarDireccion(valor: String)       { _direccion.value = valor }
    fun actualizarPoblacion(valor: String)        { _poblacion.value = valor }
    fun actualizarMunicipio(valor: String)        { _municipio.value = valor }
    fun actualizarCodigoPostal(valor: String) {
        if (valor.length <= 5 && (valor.isEmpty() || valor.all { it.isDigit() }))
            _codigoPostal.value = valor
    }
    fun actualizarTelefonoContacto(valor: String) {
        if (valor.all { it.isDigit() || it.isWhitespace() }) _telefonoContacto.value = valor
    }

    fun getCodiDestinoEnvio() : String{
        return codigoDestino
    }

    fun toggleEmpresaExpandida()         { _empresaExpandida.value = !(_empresaExpandida.value ?: false) }
    fun toggleTipoEnviamientoExpandido() { _tipoEnviamientoExpandido.value = !(_tipoEnviamientoExpandido.value ?: false) }
    fun toggleDestinoExpandido()         { _destinoExpandido.value = !(_destinoExpandido.value ?: false) }
    fun toggleOficinaComarcalExpandida() { _oficinaComarcalExpandida.value = !(_oficinaComarcalExpandida.value ?: false) }
    fun cerrarEmpresaMenu()              { _empresaExpandida.value = false }
    fun cerrarTipoEnviamientoMenu()      { _tipoEnviamientoExpandido.value = false }
    fun cerrarDestinoMenu()              { _destinoExpandido.value = false }
    fun cerrarOficinaComarcalMenu()      { _oficinaComarcalExpandida.value = false }

    // ─── Serialización del bloque envío (reutilizada por getDatosFormulario) ──
    protected fun getDatosEnvio(): Map<String, Any?> = mapOf(
        "empresaSubministradora" to _empresaSubministradora.value,
        "codigoEmpresa"          to codigoEmpresa,
        "tipoEnviamiento"        to _tipoEnviamiento.value,
        "codigoTipoEnvio"        to codigoTipoEnvio,
        "destinoEnvio"           to _destinoEnvio.value,
        "codigoDestino"          to codigoDestino,
        "oficinaComarcal"        to _oficinaComarcal.value,
        "codigoOC"               to codigoOC,
        "direccion"              to _direccion.value,
        "poblacion"              to _poblacion.value,
        "codigoPostal"           to _codigoPostal.value,
        "municipio"              to _municipio.value,
        "telefonoContacto"       to _telefonoContacto.value
    )

    protected fun restaurarDatosEnvio(datos: Map<String, Any?>) {
        _empresaSubministradora.value = datos["empresaSubministradora"] as? String ?: ""
        codigoEmpresa                 = datos["codigoEmpresa"] as? String ?: ""
        _tipoEnviamiento.value        = (datos["tipoEnviamiento"] as? Double)?.toInt() ?: 0
        codigoTipoEnvio               = datos["codigoTipoEnvio"] as? String ?: ""
        _destinoEnvio.value           = (datos["destinoEnvio"] as? Double)?.toInt() ?: 0
        codigoDestino                 = datos["codigoDestino"] as? String ?: ""
        _oficinaComarcal.value        = datos["oficinaComarcal"] as? String ?: ""
        codigoOC                      = datos["codigoOC"] as? String ?: ""
        _direccion.value              = datos["direccion"] as? String ?: ""
        _poblacion.value              = datos["poblacion"] as? String ?: ""
        _codigoPostal.value           = datos["codigoPostal"] as? String ?: ""
        _municipio.value              = datos["municipio"] as? String ?: ""
        _telefonoContacto.value       = datos["telefonoContacto"] as? String ?: ""
    }

    protected fun limpiarBloquEnvio() {
        _empresaSubministradora.value = ""; codigoEmpresa = ""
        _tipoEnviamiento.value = 0;         codigoTipoEnvio = ""
        _destinoEnvio.value = 0;            codigoDestino = ""
        _oficinaComarcal.value = "";        codigoOC = ""
        _direccion.value = ""; _poblacion.value = ""; _codigoPostal.value = ""
        _municipio.value = ""; _telefonoContacto.value = ""
    }
}