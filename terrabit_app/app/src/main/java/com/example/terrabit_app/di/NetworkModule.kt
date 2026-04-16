package com.example.terrabit_app.di

import com.example.terrabit_app.data.network.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL =
        "https://aplicacions.agricultura.gencat.cat/gtr/"

    private const val GTR_HOST =
        "aplicacions.agricultura.gencat.cat"

    // Pines obtenidos con printCertPin() — actualizar antes de ir a producción
    private const val GTR_PIN        = "sha256/iQtsyFXPKCKfjjgrS/Cp0qbEYNmueGHXwDXwxancVHQ="
    private const val GTR_PIN_BACKUP = "sha256/KqkYYX5LYAYP7XGemqzbtPPIA8x7BS/BbOIcAXf3j2k="

    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner =
        CertificatePinner.Builder()
            .add(GTR_HOST, GTR_PIN)
            .add(GTR_HOST, GTR_PIN_BACKUP)
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(certificatePinner: CertificatePinner): OkHttpClient =
        OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiInterface(retrofit: Retrofit): ApiInterface =
        retrofit.create(ApiInterface::class.java)
}