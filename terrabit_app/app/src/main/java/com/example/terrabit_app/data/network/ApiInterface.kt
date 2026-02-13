package com.example.terrabit_app.data.network

import com.example.terrabit_app.data.network.guias.Guias
import com.example.terrabit_app.data.network.Identificadores.Identificadores
import com.example.terrabit_app.data.network.moviminetos.modelos.Movimientos
import com.example.terrabit_app.data.network.moviminetos.modelos.PetConfirmacionMovi
import com.example.terrabit_app.data.network.animales.PetIdentificacion
import com.example.terrabit_app.data.network.animales.PetModicarAnimal
import com.example.terrabit_app.data.network.moviminetos.modelos.PetModificacioMovi
import com.example.terrabit_app.data.network.moviminetos.modelos.PetRegistroIntercanvi
import com.example.terrabit_app.data.network.material.PetSolicitudDuplicado
import com.example.terrabit_app.data.network.material.PetSolicitudMaterial
import com.example.terrabit_app.data.network.guias.PeticionAltaGuia
import com.example.terrabit_app.data.network.guias.PeticionModificarGuia
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
import com.example.terrabit_app.data.network.animales.RegistroNacimientoBovi
import com.example.terrabit_app.data.network.lista_bovinos.ListaBovinos
import com.example.terrabit_app.data.network.respuestas.ResAltaGuia
import com.example.terrabit_app.data.network.respuestas.ResBasica
import com.example.terrabit_app.data.network.respuestas.ResConfirmacionMovi
import com.example.terrabit_app.data.network.respuestas.ResModificarGuia
import com.example.terrabit_app.data.network.respuestas.RespuestaUnificada
import okhttp3.logging.HttpLoggingInterceptor

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiInterface {

    @GET("WSEnregistramentIDT/AppJava/WSConsultaAnimals/")
    suspend fun getListaBovinos(
        @Query("nif") nif: String,
        @Query("password") password: String,
        @Query("tipusVinculacio") tipusVinculacio: String,
        @Query("explotacio") explotacio: String
    ): Response<ListaBovinos>
    @GET("WSBovi/AppJava/Bovi/WSIdentificadorsDisponibles/")
    suspend fun getIdentificadoresDisponibles(
        @Query("nif") nif: String,
        @Query("passwordMobilitat") password: String,
        @Query("codiMO") codiMO: String
    ): Response<Identificadores>

    @GET("WSBoviGuies/AppJava/guies/WSGuiaMobilitat/")
    suspend fun getDescargaGuiasMobilitat(
        @Query("nif") nif: String,
        @Query("passwordMobilitat") password: String,
        @Query("codiMo") codiMo: String,
        @Query("codiRega") codiRega: String,
        @Query("dataSortida") dataSortida: String
    ): Response<Guias>

    @GET("WSBoviGuies/AppJava/movs/WSConsultaConfirmacioMoviment/")
    suspend fun getConfirmacionMovimientos(
        @Query("nif") nif: String,
        @Query("passwordMobilitat") password: String,
        @Query("explotacioDestinacio") explotacioDestinacio: String,
        @Query("dataSortida") dataSortida: String
    ): Response<Movimientos>

    @PUT("WSBovi/AppJava/Bovi/WSEnregistramentMort/")
    suspend fun putRegistrarMuerte(
        @Body request: RegistroMuerteBovi
    ): Response<RespuestaUnificada>

    @PUT("WSBovi/AppJava/Bovi/WSEnregistramentNaixement/")
    suspend fun putRegistrarNacimiento(
        @Body request: RegistroNacimientoBovi
    ): Response<RespuestaUnificada>

    @PUT("WSBoviGuies/AppJava/guies/WSAltaGuia/")
    suspend fun putAltaGuia(
        @Body request: PeticionAltaGuia
    ): Response<ResAltaGuia>

    @PUT("WSBoviGuies/AppJava/guies/WSAltaGuia/")
    suspend fun putModificarGuia(
        @Body request: PeticionModificarGuia
    ): Response<ResModificarGuia>

    @PUT("WSBoviGuies/AppJava/movs/WSConfirmacioMoviment/")
    suspend fun putConfirmarMovi(
        @Body request: PetConfirmacionMovi
    ): Response<ResConfirmacionMovi>

    @PUT("WSBoviGuies/AppJava/movs/WSAltaIntercanviEntradaImportacio/")
    suspend fun putRegistroIntercanvio(
        @Body request: PetRegistroIntercanvi
    ): Response<ResConfirmacionMovi>

    @PUT("WSBoviGuies/AppJava/movs/WSModificacioIntercanviEntradaImportacio/")
    suspend fun putMoficarMovi(
        @Body request: PetModificacioMovi
    ): Response<ResConfirmacionMovi>

    @PUT("WSBovi/AppJava/Bovi/WSModificacioAnimal/")
    suspend fun putMoficarAnimal(
        @Body request: PetModicarAnimal
    ): Response<RespuestaUnificada>

    @PUT("WSBovi/AppJava/Bovi/WSModificacioDataIdentificacioAnimal/")
    suspend fun putIdentificacionPendiente(
        @Body request: PetIdentificacion
    ): Response<RespuestaUnificada>

    @PUT("WSBovi/AppJava/Bovi/WSSolicitudDuplicat/")
    suspend fun putSolicitudDuplicado(
        @Body request: PetSolicitudDuplicado
    ): Response<ResBasica>

    @PUT("WSEnviamentDuplicatES/AppJava/WSSolicitudMaterial/")
    suspend fun putSolicitudMaterial(
        @Body request: PetSolicitudMaterial
    ): Response<ResBasica>
    companion object {
        val BASE_URL = "https://preproduccio.aplicacions.agricultura.gencat.cat/gtr/"

        fun create(): ApiInterface {

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Configurar timeouts más largos
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS)  // Timeout de conexión: 60 segundos
                .readTimeout(60, TimeUnit.SECONDS)     // Timeout de lectura: 60 segundos
                .writeTimeout(60, TimeUnit.SECONDS)    // Timeout de escritura: 60 segundos
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiInterface::class.java)
        }
    }
}