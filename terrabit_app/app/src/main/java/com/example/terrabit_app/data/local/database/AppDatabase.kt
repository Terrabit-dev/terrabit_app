package com.example.terrabit_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.terrabit_app.data.local.dao.BovinoDao
import com.example.terrabit_app.data.local.dao.BorradorDao

@Database(
    entities = [BovinoEntity::class, BorradorEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bovinoDao(): BovinoDao
    abstract fun borradorDao(): BorradorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS borradores (
                        id TEXT NOT NULL PRIMARY KEY,
                        tipo TEXT NOT NULL,
                        fecha TEXT NOT NULL,
                        hora TEXT NOT NULL,
                        datos TEXT NOT NULL,
                        estado TEXT NOT NULL DEFAULT 'PENDIENTE'
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "terrabit_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}