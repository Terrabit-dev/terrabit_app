package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.data.network.modelos.Identificadores
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewmodel : ViewModel() {
    private val repositorio = Repositorio()

    private val _identificadores = MutableLiveData<Identificadores>()
    val identificadores = _identificadores

    // Estados del formulario de nacimiento
    private val _idMadre = MutableLiveData("")
    val idMadre = _idMadre

    private val _idCria = MutableLiveData("")
    val idCria = _idCria

    private val _fechaNacimiento = MutableLiveData("")
    val fechaNacimiento = _fechaNacimiento

    private val _sexoSeleccionado = MutableLiveData("")
    val sexoSeleccionado = _sexoSeleccionado

    private val _razaSeleccionada = MutableLiveData("")
    val razaSeleccionada = _razaSeleccionada

    private val _aptitudSeleccionada = MutableLiveData("")
    val aptitudSeleccionada = _aptitudSeleccionada

    // Estados de expansión de menús desplegables
    private val _sexoExpandido = MutableLiveData(false)
    val sexoExpandido = _sexoExpandido

    private val _razaExpandida = MutableLiveData(false)
    val razaExpandida = _razaExpandida

    private val _aptitudExpandida = MutableLiveData(false)
    val aptitudExpandida = _aptitudExpandida

    // Estado para mostrar el DatePicker
    private val _mostrarDatePicker = MutableLiveData(false)
    val mostrarDatePicker = _mostrarDatePicker

    // Estados para feedback del registro
    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso = _registroExitoso

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError = _mensajeError

    // Listas de opciones
    val listaSexos = listOf("Macho", "Hembra")
    val listaRazas = listOf("Holstein", "Angus", "Hereford", "Simmental", "Charolais", "Jersey", "Limousin")
    val listaAptitudes = listOf("Carne", "Leche", "Doble propósito")

    // Funciones para actualizar los campos
    fun actualizarIdMadre(nuevoId: String) {
        _idMadre.value = nuevoId
    }

    fun actualizarIdCria(nuevoId: String) {
        _idCria.value = nuevoId
    }

    fun actualizarFechaNacimiento(nuevaFecha: String) {
        _fechaNacimiento.value = nuevaFecha
    }

    fun seleccionarSexo(sexo: String) {
        _sexoSeleccionado.value = sexo
        _sexoExpandido.value = false
    }

    fun seleccionarRaza(raza: String) {
        _razaSeleccionada.value = raza
        _razaExpandida.value = false
    }

    fun seleccionarAptitud(aptitud: String) {
        _aptitudSeleccionada.value = aptitud
        _aptitudExpandida.value = false
    }

    // Funciones para controlar la expansión de menús
    fun toggleSexoExpandido() {
        _sexoExpandido.value = !(_sexoExpandido.value ?: false)
    }

    fun toggleRazaExpandida() {
        _razaExpandida.value = !(_razaExpandida.value ?: false)
    }

    fun toggleAptitudExpandida() {
        _aptitudExpandida.value = !(_aptitudExpandida.value ?: false)
    }

    fun cerrarSexoMenu() {
        _sexoExpandido.value = false
    }

    fun cerrarRazaMenu() {
        _razaExpandida.value = false
    }

    fun cerrarAptitudMenu() {
        _aptitudExpandida.value = false
    }

    // Funciones para controlar el DatePicker
    fun mostrarDatePicker() {
        _mostrarDatePicker.value = true
    }

    fun ocultarDatePicker() {
        _mostrarDatePicker.value = false
    }

    fun seleccionarFecha(fechaMillis: Long) {
        // Convertir milisegundos a formato de fecha legible
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = fechaMillis
        val dia = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val mes = calendar.get(java.util.Calendar.MONTH) + 1
        val anio = calendar.get(java.util.Calendar.YEAR)

        _fechaNacimiento.value = String.format("%02d/%02d/%04d", dia, mes, anio)
        _mostrarDatePicker.value = false
    }

    // Función para obtener identificadores
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

    // Función para registrar un nacimiento
    fun registrarNacimiento() {
        val idMadreVal = _idMadre.value ?: ""
        val idCriaVal = _idCria.value ?: ""
        val fechaVal = _fechaNacimiento.value ?: ""
        val sexoVal = _sexoSeleccionado.value ?: ""
        val razaVal = _razaSeleccionada.value ?: ""
        val aptitudVal = _aptitudSeleccionada.value ?: ""

        // Validar que todos los campos estén completos
        if (idMadreVal.isEmpty() || idCriaVal.isEmpty() || fechaVal.isEmpty() ||
            sexoVal.isEmpty() || razaVal.isEmpty() || aptitudVal.isEmpty()
        ) {
            _mensajeError.value = "Por favor, complete todos los campos obligatorios"
            return
        }

        // Aquí iría la llamada a la API para registrar el nacimiento
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Simulación de registro (reemplaza con tu llamada real a la API)
                // val response = repositorio.registrarNacimiento(...)

                withContext(Dispatchers.Main) {
                    // Si la respuesta es exitosa
                    _registroExitoso.value = true
                    Log.d(
                        "Registro Nacimiento",
                        "Madre: $idMadreVal, Cría: $idCriaVal, Fecha: $fechaVal, " +
                                "Sexo: $sexoVal, Raza: $razaVal, Aptitud: $aptitudVal"
                    )

                    // Limpiar formulario después de registrar
                    limpiarFormulario()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _mensajeError.value = "Error al registrar: ${e.message}"
                    Log.e("Error Registro", e.message ?: "Error desconocido")
                }
            }
        }
    }

    // Función para validar el formulario
    fun esFormularioValido(): Boolean {
        return !(_idMadre.value.isNullOrEmpty() ||
                _idCria.value.isNullOrEmpty() ||
                _fechaNacimiento.value.isNullOrEmpty() ||
                _sexoSeleccionado.value.isNullOrEmpty() ||
                _razaSeleccionada.value.isNullOrEmpty() ||
                _aptitudSeleccionada.value.isNullOrEmpty())
    }

    // Función para limpiar el formulario
    fun limpiarFormulario() {
        _idMadre.value = ""
        _idCria.value = ""
        _fechaNacimiento.value = ""
        _sexoSeleccionado.value = ""
        _razaSeleccionada.value = ""
        _aptitudSeleccionada.value = ""
    }

    // Función para resetear el estado de registro
    fun resetearEstadoRegistro() {
        _registroExitoso.value = false
        _mensajeError.value = ""
    }

    // Función para validar formato de identificador
    fun validarIdentificador(id: String): Boolean {
        // Implementa tu lógica de validación aquí
        // Por ejemplo: verificar longitud, formato, etc.
        return id.length >= 5 // Ejemplo simple
    }
}