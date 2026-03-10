package com.example.terrabit_app.di

import android.content.Context
import com.example.terrabit_app.data.SharedPreferencesManager
import com.example.terrabit_app.data.local.dao.BorradorDao
import com.example.terrabit_app.data.local.database.AppDatabase
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.utils.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideRepositorio(@ApplicationContext context: Context): Repositorio {
        return Repositorio(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideBorradorDao(database: AppDatabase): BorradorDao {
        return database.borradorDao()
    }
}