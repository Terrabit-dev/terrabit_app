package com.example.terrabit_app.data.network

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("identificadores/")
    suspend fun getIdentificadoresDisponibles(
        @Query("nif") nif: String,
        @Query("passwordMobilitat") password: String,
        @Query("codiMO") codiMO: String
    ): Response<Identificadores>
    companion object {
        val BASE_URL = "https://preproduccio.aplicacions.agricultura.gencat.cat/gtr/WSBovi/AppJava/Bovi/WSIdentificadorsDisponibles/"
        fun create(): ApiInterface {
            val client = OkHttpClient.Builder().build()
            val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
                GsonConverterFactory.create()
            ).client(client).build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}