package com.example.terrabit_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.terrabit_app.data.local.database.BorradorEntity


@Dao
interface BorradorDao {

    @Query("SELECT * FROM borradores")
    suspend fun getAll(): List<BorradorEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(borrador: BorradorEntity)

    @Query("DELETE FROM borradores WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM borradores")
    suspend fun deleteAll()
}