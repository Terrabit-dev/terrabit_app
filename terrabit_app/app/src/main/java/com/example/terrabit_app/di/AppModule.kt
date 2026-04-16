package com.example.terrabit_app.di

import android.content.Context
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.local.SecureStorage
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.dao.HistorialDao
import com.example.terrabit_app.data.local.database.AppDatabase
import com.example.terrabit_app.utils.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.room.Room
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSecureStorage(@ApplicationContext context: Context): SecureStorage =
        SecureStorage(context)

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context,
        secureStorage: SecureStorage
    ): UserPreferences = UserPreferences(context, secureStorage)

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager =
        SharedPreferencesManager(context)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "terrabit_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()

    @Provides
    @Singleton
    fun provideBorradorDao(database: AppDatabase): BorradorDao = database.borradorDao()

    @Provides
    @Singleton
    fun provideHistorialDao(database: AppDatabase): HistorialDao = database.historialDao()
}