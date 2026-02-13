package com.example.terrabit_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.terrabit_app.data.local.database.BovinoEntity

@Dao
interface BovinoDao {

    @Query("SELECT * FROM bovinos ORDER BY identificador ASC")
    suspend fun getAllBovinos(): List<BovinoEntity>

    @Query("SELECT * FROM bovinos WHERE identificador LIKE '%' || :query || '%' OR identificadorMare LIKE '%' || :query || '%' LIMIT 15")
    suspend fun searchBovinos(query: String): List<BovinoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bovinos: List<BovinoEntity>)

    @Query("DELETE FROM bovinos")
    suspend fun deleteAll()

    @Query("SELECT lastUpdated FROM bovinos LIMIT 1")
    suspend fun getLastUpdateTime(): Long?
}