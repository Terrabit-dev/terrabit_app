package com.example.terrabit_app.data.network

import android.content.Context
import com.example.terrabit_app.data.local.database.AppDatabase
import com.example.terrabit_app.data.local.database.toAnimal
import com.example.terrabit_app.data.local.database.toEntity
import com.example.terrabit_app.data.network.animales.PetIdentificacion
import com.example.terrabit_app.data.network.animales.PetModicarAnimal
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
import com.example.terrabit_app.data.network.animales.RegistroNacimientoBovi
import com.example.terrabit_app.data.network.guias.PeticionAltaGuia
import com.example.terrabit_app.data.network.guias.PeticionModificarGuia
import com.example.terrabit_app.data.network.guiasPorcinos.CrearGuiaMobilitatPorcinos
import com.example.terrabit_app.data.network.guiasPorcinos.GuiaMobilitatPorcinos
import com.example.terrabit_app.data.network.guiasPorcinos.PeticionModificarGuiaPorcinos
import com.example.terrabit_app.data.network.material.PetSolicitudDuplicado
import com.example.terrabit_app.data.network.material.PetSolicitudMaterial
import com.example.terrabit_app.data.network.moviminetos.modelos.PetConfirmacionMovi
import com.example.terrabit_app.data.network.moviminetos.modelos.PetModificacioMovi
import com.example.terrabit_app.data.network.moviminetos.modelos.PetRegistroIntercanvi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.terrabit_app.data.network.DataClassPorcinos.AltaMovimientoGTR
import com.example.terrabit_app.data.network.DataClassPorcinos.ConfirmarMovimientosRequest
import com.example.terrabit_app.data.network.DataClassPorcinos.ModificarMovimentsAGias

class Repositorio {
    val apiInterface = ApiInterface.create()
    private val bovinoDao = AppDatabase.getDatabase(context).bovinoDao()

    private val CACHE_EXPIRATION_MS = 24 * 60 * 60 * 1000L // 24 horas

    suspend fun getIdentificadoresDisponibles(
        nif: String,
        passwordMobilitat: String,
        codiMO: String
    ) = apiInterface.getIdentificadoresDisponibles(nif, passwordMobilitat, codiMO)

    suspend fun getListaBovinos(
        nif: String,
        password: String,
        tipusVinculacio: String,
        explotacio: String
    ) = apiInterface.getListaBovinos(nif, password, tipusVinculacio, explotacio)

    // Obtener bovinos con caché
    suspend fun getBovinosWithCache(
        nif: String,
        password: String,
        tipusVinculacio: String,
        explotacio: String,
        forceRefresh: Boolean = false
    ): List<Animal> = withContext(Dispatchers.IO) {
        val cacheValid = !forceRefresh && isCacheValid()

        if (cacheValid) {
            // Devolver desde caché
            bovinoDao.getAllBovinos().map { it.toAnimal() }
        } else {
            // Obtener desde API y guardar en caché
            val response = apiInterface.getListaBovinos(nif, password, tipusVinculacio, explotacio)

            if (response.isSuccessful && !response.body()?.identificadors.isNullOrEmpty()) {
                val animales = response.body()?.identificadors ?: emptyList()

                // Guardar en Room
                bovinoDao.deleteAll()
                bovinoDao.insertAll(animales.map { it.toEntity() })

                animales
            } else {
                // Si falla API, intentar devolver caché antigua
                bovinoDao.getAllBovinos().map { it.toAnimal() }
            }
        }
    }

    // Buscar bovinos en caché
    suspend fun searchBovinosLocal(query: String): List<Animal> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            emptyList()
        } else {
            bovinoDao.searchBovinos(query).map { it.toAnimal() }
        }
    }

    private suspend fun isCacheValid(): Boolean {
        val lastUpdate = bovinoDao.getLastUpdateTime() ?: return false
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastUpdate) < CACHE_EXPIRATION_MS
    }

    suspend fun getDescargaGuiasMobilitat(
        nif: String,
        passwordMobilitat: String,
        codiMo: String,
        codiRega: String,
        dataSortida: String
    ) = apiInterface.getDescargaGuiasMobilitat(nif, passwordMobilitat, codiMo, codiRega, dataSortida)

    suspend fun getConfirmacionMovimientos(
        nif: String,
        passwordMobilitat: String,
        explotacioDestinacio: String,
        dataSortida: String
    ) = apiInterface.getConfirmacionMovimientos(nif, passwordMobilitat, explotacioDestinacio, dataSortida)

    suspend fun putRegistrarMuerte(request: RegistroMuerteBovi) =
        apiInterface.putRegistrarMuerte(request)
    suspend fun putRegistrarNacimiento(request: RegistroNacimientoBovi) =
        apiInterface.putRegistrarNacimiento(request)

    suspend fun putMoficarAnimal(request: PetModicarAnimal) =
        apiInterface.putMoficarAnimal(request)

    suspend fun putAltaGuia(request: PeticionAltaGuia) =
        apiInterface.putAltaGuia(request)

    suspend fun putModificarGuia(request: PeticionModificarGuia) =
        apiInterface.putModificarGuia(request)


    suspend fun putConfirmarMovi(request: PetConfirmacionMovi) =
        apiInterface.putConfirmarMovi(request)

    suspend fun putRegistroIntercanvio(request: PetRegistroIntercanvi) =
        apiInterface.putRegistroIntercanvio(request)

    suspend fun putMoficarMovi(request: PetModificacioMovi) =
        apiInterface.putMoficarMovi(request)

    suspend fun putIdentificacionPendiente(request: PetIdentificacion) =
        apiInterface.putIdentificacionPendiente(request)

    suspend fun putSolicitudDuplicado(request: PetSolicitudDuplicado) =
        apiInterface.putSolicitudDuplicado(request)

    suspend fun putSolicitudMaterial(request: PetSolicitudMaterial) =
        apiInterface.putSolicitudMaterial(request)

    //------------------- Porcinos -------------------

    // --- FLUJO DE SALIDAS (ORIGEN) ---

    // 5.1 Alta de guías
    suspend fun altaGuiaPorcinas(request: AltaMovimientoGTR) =
        apiInterface.altaMovimientoPorcino(request)

    // 5.2 Descarga de movimientos para movilidad
    suspend fun getGuiasMobilitatPorcinas(
        nif: String,
        password: String,
        codiMo: String,
        codiRega: String,
        dataSortida: String
    ) = apiInterface.listarMovimientosOrigenPorcino(nif, password, codiMo, codiRega, dataSortida)

    // 5.3 Modificar y emitir guía (Cierre)
    suspend fun tramitarGuiaPorcina(request: ModificarMovimentsAGias) =
        apiInterface.tramitarMovimientoMovilidadPorcina(request)


    // --- FLUJO DE ENTRADAS (DESTINO) ---

    // 5.2 Consulta de movimientos pendientes de confirmar entrada
    suspend fun getPendientesConfirmarEntradaPorcina(
        nif: String,
        password: String,
        moDesti: String,
        desde: String,
        fins: String
    ) = apiInterface.listarMovimientosPendientesEntradaPorcina(nif, password, moDesti, desde, fins)

    // 5.1 Confirmación oficial de la entrada
    suspend fun confirmarEntradaPorcina(request: ConfirmarMovimientosRequest) =
        apiInterface.confirmarEntradaMovimientoPorcina(request)
}