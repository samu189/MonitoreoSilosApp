package com.example.monitoreossilos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SiloDao {
    @Query("SELECT * FROM silos")
    fun getAllSilos(): Flow<List<SiloEntity>>

    @Query("SELECT * FROM silos WHERE idSilo = :id LIMIT 1")
    suspend fun getSiloById(id: String): SiloEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSilo(silo: SiloEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSilos(silos: List<SiloEntity>)
}