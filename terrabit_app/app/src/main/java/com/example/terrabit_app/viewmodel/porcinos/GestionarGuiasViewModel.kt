package com.example.terrabit_app.viewmodel.porcinos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terrabit_app.data.network.ApiInterface
import com.example.terrabit_app.data.network.DataClassPorcinos.GtrStandardResponse
import com.example.terrabit_app.data.network.DataClassPorcinos.GuiaGTRLista
import com.example.terrabit_app.data.network.DataClassPorcinos.ModificarMovimentsAGias
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.ui.screen.porcinos.GestionarGuiasPorcinosUiState
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

class GestionarGuiasViewModel(
    private val repo: Repositorio,
    private val userPreferences: UserPreferences // Para obtener NIF/Pass
) : ViewModel() {

    private val _uiState = MutableStateFlow(GestionarGuiasPorcinosUiState())
    val uiState: StateFlow<GestionarGuiasPorcinosUiState> = _uiState.asStateFlow()

    fun cargarMovimientosDesdeApi() {
        viewModelScope.launch {
            Log.d("DEBUG_API", "--- Iniciando llamada a la API ---")
            _uiState.update { it.copy(isLoading = true) }

            // Datos del administrador (Recordar cambiarlo por userPreferences después)
            val nif = "37370803N"
            val pass = "5Q62h4rP"
            val codiMo = "1880AE"
            val rega = "ES080470001881"
            val fechaCorte = "202401010000"

            Log.d("DEBUG_API", "Parámetros enviados: NIF=$nif, MO=$codiMo, REGA=$rega, Fecha=$fechaCorte")

            try {
                val response = repo.getGuiasMobilitatPorcinas(nif, pass, codiMo, rega, fechaCorte)

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("DEBUG_API", "Respuesta Exitosa (Código ${response.code()})")
                    Log.d("DEBUG_API", "Número de elementos recibidos: ${body?.size ?: 0}")

                    if (body != null) {
                        // Logueamos el primer elemento para ver si los nombres de los campos coinciden
                        if (body.isNotEmpty()) {
                            Log.d("DEBUG_API", "Ejemplo primer elemento: ${body[0]}")
                        }

                        _uiState.update { it.copy(
                            listaGuiasPorcinos = body,
                            isLoading = false
                        )}
                    }
                } else {
                    // Error de la API (ej: 404, 500, 401)
                    val errorMsg = response.errorBody()?.string()
                    Log.e("DEBUG_API", "Error de la API: Código ${response.code()}")
                    Log.e("DEBUG_API", "Cuerpo del error: $errorMsg")

                    _uiState.update { it.copy(
                        isLoading = false,
                        mensajeError = "Error ${response.code()}: $errorMsg"
                    )}
                }
            } catch (e: Exception) {
                // Error de conexión o crash de la App
                Log.e("DEBUG_API", "EXCEPCIÓN CRÍTICA: ${e.message}")
                e.printStackTrace() // Esto imprime toda la traza en la consola

                _uiState.update { it.copy(
                    isLoading = false,
                    mensajeError = "Excepción: ${e.localizedMessage}"
                )}
            }
            Log.d("DEBUG_API", "--- Finalización llamada a la API ---")
        }
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