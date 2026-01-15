package com.example.terrabit_app.data.network

import com.example.terrabit_app.data.network.modelos.Guias
import com.example.terrabit_app.data.network.modelos.Identificadores
import com.example.terrabit_app.data.network.modelos.Movimientos

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
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

    companion object {
        val BASE_URL = "https://preproduccio.aplicacions.agricultura.gencat.cat/gtr/"
        fun create(): ApiInterface {
            val client = OkHttpClient.Builder().build()
            val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
                GsonConverterFactory.create()
            ).client(client).build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}