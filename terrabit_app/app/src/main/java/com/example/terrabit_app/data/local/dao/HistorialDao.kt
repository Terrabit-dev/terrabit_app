package com.example.terrabit_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.terrabit_app.data.local.database.HistorialEntity

@Dao
interface HistorialDao {
    @Query("SELECT * FROM historial") suspend fun getAll(): List<HistorialEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(historial: HistorialEntity)
    @Query("DELETE FROM historial WHERE id = :id") suspend fun deleteById(id: String)
    @Query("DELETE FROM historial") suspend fun deleteAll()
}