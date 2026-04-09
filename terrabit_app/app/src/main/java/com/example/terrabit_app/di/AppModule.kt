package com.example.terrabit_app.di

import android.content.Context
import android.util.Base64
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
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import androidx.room.Room
import java.security.SecureRandom
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
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        secureStorage: SecureStorage
    ): AppDatabase {
        // Recupera la clave existente o genera una nueva y la guarda en Keystore
        val dbKey = secureStorage.getDbKey() ?: run {
            val newKey = generateDbKey()
            secureStorage.saveDbKey(newKey)
            newKey
        }

        val passphrase = SQLiteDatabase.getBytes(dbKey.toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "terrabit_database"
        )
            .openHelperFactory(factory)
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun provideBorradorDao(database: AppDatabase): BorradorDao = database.borradorDao()

    @Provides
    @Singleton
    fun provideHistorialDao(database: AppDatabase): HistorialDao = database.historialDao()

    private fun generateDbKey(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}