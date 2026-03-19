package com.example.terrabit_app.viewmodel.porcinos

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.DataClassPorcinos.GtrErrorResponseLista
import com.example.terrabit_app.data.network.DataClassPorcinos.GtrStandardResponse
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.DataClassPorcinos.ModificarMovimentsAGias
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.GestionarGuiasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull
import okhttp3.ResponseBody

class GestionarGuiasViewModel(application: Application): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GestionarGuiasPorcinosUiState())
    val uiState: StateFlow<GestionarGuiasPorcinosUiState> = _uiState.asStateFlow()
    private val repo = Repositorio(application)
    private lateinit var userPreferences: UserPreferences

    // Funciones del formulario
    fun actualizarRega(valor: String) {
        _uiState.update { it.copy(rega = valor) }
    }

    fun actualizarFechaCorte(valor: String) {
        _uiState.update { it.copy(fechaCorte = valor) }
    }

    fun consultarLista(nif: String, pass: String, codiMo: String) {
        val state = _uiState.value

        if (state.rega.isBlank() || state.fechaCorte.isBlank()) {
            _uiState.update { it.copy(mensajeError = "El código REGA y la fecha son obligatorios.") }
            return
        }

        // Marcamos que la consulta ha iniciado → desaparece el formulario
        _uiState.update { it.copy(consultaIniciada = true, mensajeError = null) }
        cargarMovimientosDesdeApi(nif, pass, codiMo, state.rega, state.fechaCorte)
    }

    fun cargarMovimientosDesdeApi(
        nif: String, pass: String, codiMo: String, rega: String, fechaCorte: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repo.getGuiasMobilitatPorcinas(nif, pass, codiMo, rega, fechaCorte)

                if (response.isSuccessful) {
                    val rawJson = response.body()?.string() ?: ""
                    Log.d("DEBUG_API", "Raw JSON: $rawJson")

                    val gson = Gson()

                    // Comprobamos si el primer elemento tiene "codi" → es un error
                    // Si tiene "moOrigen" → es una lista de guías
                    val jsonArray = com.google.gson.JsonParser.parseString(rawJson).asJsonArray
                    val primerElemento = jsonArray.firstOrNull()?.asJsonObject

                    if (primerElemento?.has("moOrigen") == true) {
                        // Es una lista de guías válida
                        val listaGuias = gson.fromJson(rawJson, Array<GuiaGTRLista>::class.java)
                        Log.d("DEBUG_API", "Guías recibidas: ${listaGuias.size}")
                        _uiState.update { it.copy(
                            listaGuiasPorcinos = listaGuias.toList(),
                            isLoading = false,
                            mensajeError = null
                        )}
                    } else {
                        // Es una lista de errores
                        val errores = gson.fromJson(rawJson, Array<GtrErrorResponseLista>::class.java)
                        val mensajeError = errores.firstOrNull()?.descripcio ?: "Error desconocido"
                        Log.e("DEBUG_API", "Error de la API: $mensajeError")
                        _uiState.update { it.copy(
                            isLoading = false,
                            mensajeError = mensajeError,
                            consultaIniciada = false
                        )}
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.e("DEBUG_API", "Error HTTP ${response.code()}: $errorMsg")
                    _uiState.update { it.copy(
                        isLoading = false,
                        mensajeError = "Error ${response.code()}: $errorMsg",
                        consultaIniciada = false
                    )}
                }
            } catch (e: Exception) {
                Log.e("DEBUG_API", "EXCEPCIÓN: ${e.message}")
                _uiState.update { it.copy(
                    isLoading = false,
                    mensajeError = "Excepción: ${e.localizedMessage}",
                    consultaIniciada = false
                )}
            }
        }
    }

    // Para volver al formulario desde la lista
    fun resetearConsulta() {
        _uiState.update { it.copy(
            consultaIniciada = false,
            listaGuiasPorcinos = emptyList(),
            mensajeError = null
        )}
    }


    fun confirmarGuia(guia: GuiaGTRLista) {
        // Validación previa: La API no acepta campos vacíos o nulos para el transporte
        if (guia.transportista.isNullOrBlank() || guia.vehicle.isNullOrBlank() || guia.responsable.isNullOrBlank()) {
            _uiState.update { it.copy(
                mensajeError = "Faltan datos obligatorios (Transportista, Vehículo o Responsable). Por favor, edite la guía antes de confirmar."

            )}
            Log.d("DEBUG_API", "Faltan datos obligatorios (Transportista, Vehículo o Responsable). Por favor, edite la guía antes de confirmar.")

            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Mapeo seguro: Ya sabemos que no son nulos por la validación anterior
            val request = ModificarMovimentsAGias(
                nif = "37370803N", // TODO: Obtener de UserPreferences
                password = "5Q62h4rP",
                remo = guia.remo,
                categoria = guia.categoria,
                nombreAnimals = guia.nombreAnimals.toString(),
                transportista = guia.transportista,
                responsable = guia.responsable,
                vehicle = guia.vehicle,
                dataSortida = guia.dataSortida.toString(),
                dataArribada = guia.dataArribada.toString()
            )

            try {
                val response = repo.tramitarGuiaPorcina(request)

                if (response.isSuccessful) {
                    // Forzamos el tratamiento del cuerpo como lista para evitar errores de GSON
                    val listaRespuesta = response.body() as? List<GtrStandardResponse>
                    val resultado = listaRespuesta?.firstOrNull()

                    if (resultado?.codi == "OK") {
                        Log.d("DEBUG_API", "Guía tramitada con éxito: ${guia.remo}")

                        // Eliminamos de la lista local la guía que ya ha sido enviada
                        val listaActualizada = _uiState.value.listaGuiasPorcinos.filter {
                            it.remo != guia.remo
                        }

                        _uiState.update { it.copy(
                            listaGuiasPorcinos = listaActualizada,
                            isLoading = false,
                            mensajeError = null
                        )}
                    } else {
                        // Error de lógica de negocio (el servidor rechazó los datos)
                        val errorDesc = resultado?.descripcio ?: "Error desconocido en el servidor"
                        Log.e("DEBUG_API", "Error Negocio: $errorDesc")
                        _uiState.update { it.copy(
                            isLoading = false,
                            mensajeError = errorDesc
                        )}
                    }
                } else {
                    // Error de protocolo (404, 500, etc.)
                    val errorMsg = response.errorBody()?.string() ?: "Error de comunicación"
                    _uiState.update { it.copy(
                        isLoading = false,
                        mensajeError = "Error ${response.code()}: $errorMsg"
                    )}
                }
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Excepción crítica: ${e.message}")
                _uiState.update { it.copy(
                    isLoading = false,
                    mensajeError = "Error de conexión: ${e.localizedMessage}"
                )}
            }
        }
    }
}