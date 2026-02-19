package com.example.terrabit_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.network.Identificadores.IdenSolicitudDupli
import com.example.terrabit_app.data.network.Repositorio
import kotlin.collections.plus

class MaterialDuplicadoViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del repositorio
    private var repositorio = Repositorio(application)

    // Estados del formulario de material
    private val _empresaSubministradora = MutableLiveData("")
    val empresaSubministradora = _empresaSubministradora

     private var codigoEmpresaSubministradora = ""


    private val _tipoEnviamiento = MutableLiveData("")
    val tipoEnviamiento = _tipoEnviamiento

    private var codigoTipoEnviamiento = ""

    private val _direccionEnvio = MutableLiveData("")
    val direccionEnvio = _direccionEnvio

    private var codigoDireccionEnvio = ""

    private  val _oficinaComarcal = MutableLiveData("")
    val oficinaComarcal = _oficinaComarcal

    private var codigoOficinaComarcal = ""

    private val _dirrecionEnvio = MutableLiveData("")
    val dirrecionEnvio = _dirrecionEnvio

    private val _poblacion = MutableLiveData("")
    val poblacion = _poblacion

    private val _codigoPostal = MutableLiveData("")
    val codigoPostal = _codigoPostal

    private val _municipio = MutableLiveData("")
    val municipio = _municipio

    private val _telefonoContacto = MutableLiveData("")
    val telefonoContacto = _telefonoContacto

    private val _listaIdentificadores = MutableLiveData<List<IdenSolicitudDupli>>(
        listOf(
            IdenSolicitudDupli(
                identificador = "",
                tipusMaterial = ""
            )
        )
    )
    val listaIdentificadores = _listaIdentificadores

    // Variables de control de

    private val _empresaExpandida = MutableLiveData(false)
    val empresaExpandida = _empresaExpandida


    private val _tipoEnviamientoExpandido = MutableLiveData(false)
    val tipoEnviamientoExpandido = _tipoEnviamientoExpandido

    private val _direccionEnvioExpandido = MutableLiveData(false)
    val direccionEnvioExpandido = _direccionEnvioExpandido

    private val _oficinaComarcalExpandido = MutableLiveData(false)
    val oficinaComarcalExpandido = _oficinaComarcalExpandido

    private val _tipoMaterialExpandido = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val tipoMaterialExpandido = _tipoMaterialExpandido

    // ============================================
    // FUNCIONES PARA CONTROLAR EXPANSIÓN DE MENÚS
    // ============================================

    fun toggleEmpresaExpandida(){
        _empresaExpandida.value = !(_empresaExpandida.value ?: false)
    }


    fun toggleTipoEnviamientoExpandido(){
        _tipoEnviamientoExpandido.value = !(_tipoEnviamientoExpandido.value ?: false)
    }

    fun toggleDireccionEnvioExpandido(){
        _direccionEnvioExpandido.value = !(_direccionEnvioExpandido.value ?: false)
    }

    fun toggleOficinaComarcalExpandido(){
        _oficinaComarcalExpandido.value = !(_oficinaComarcalExpandido.value ?: false)
    }

    fun toggleTipoMaterialExpandido(indice: Int) {
        val mapaActual = _tipoMaterialExpandido.value ?: emptyMap()
        val valorActual = mapaActual[indice] ?: false
        _tipoMaterialExpandido.value = mapaActual + (indice to !valorActual)
    }

    fun cerrarEmpresaMenu(){
        _empresaExpandida.value = false
    }

    fun cerrarTipoEnviamientoMenu(){
        _tipoEnviamientoExpandido.value = false
    }

    fun cerrarDireccionEnvioMenu(){
        _direccionEnvioExpandido.value = false
    }

    fun cerrarOficinaComarcalMenu(){
        _oficinaComarcalExpandido.value = false
    }

    // ============================================
    // FUNCIONES mostrar texfields
    // ============================================

    fun seleccionarEmpresa(codigo: String, nombre: String){
        _empresaSubministradora.value = nombre
        codigoEmpresaSubministradora = codigo
        cerrarEmpresaMenu()

    }

    fun seleccionarTipoEnviamiento(codigo: String, nombre: String){
        _tipoEnviamiento.value = nombre
        codigoTipoEnviamiento = codigo
        cerrarTipoEnviamientoMenu()
    }

    fun seleccionarDireccionEnvio(codigo: String, nombre: String){
        _direccionEnvio.value = nombre
        codigoDireccionEnvio = codigo
        cerrarDireccionEnvioMenu()
    }

    fun seleccionarOficinaComarcal(codigo: String, nombre: String){
        _oficinaComarcal.value = nombre
        codigoOficinaComarcal = codigo
        cerrarOficinaComarcalMenu()
    }

    fun actualizarDireccionEnvio(valor: String){
        _dirrecionEnvio.value = valor
    }

    fun actualizarPoblacion(valor: String){
        _poblacion.value = valor
    }

    fun actualizarCodigoPostal(valor: String){
        _codigoPostal.value = valor
    }

    fun actualizarMunicipio(valor: String){
        _municipio.value = valor
    }

    fun actualizarTelefonoContacto(valor: String){
        _telefonoContacto.value = valor
    }



}