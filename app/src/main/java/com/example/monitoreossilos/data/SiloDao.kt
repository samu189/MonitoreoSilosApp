package com.example.monitoreossilos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SiloDao {

    @Query("SELECT * FROM silos ORDER BY granjaNombre, idSilo")
    fun getAllSilos(): Flow<List<SiloEntity>>

    @Query("SELECT * FROM silos WHERE idSilo = :id LIMIT 1")
    fun observarSilo(id: String): Flow<SiloEntity?>

    @Query("SELECT * FROM silos WHERE idSilo = :id LIMIT 1")
    suspend fun getSiloById(id: String): SiloEntity?

    @Query("SELECT COUNT(*) FROM silos")
    suspend fun contarSilos(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSilo(silo: SiloEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSilos(silos: List<SiloEntity>): List<Long>

    @Query("UPDATE silos SET fotoUri = :uri WHERE idSilo = :id")
    suspend fun actualizarFoto(id: String, uri: String): Int

    @Query("SELECT * FROM movimientos WHERE idSilo = :id ORDER BY fecha DESC")
    fun observarMovimientos(id: String): Flow<List<MovimientoEntity>>

    @Insert
    suspend fun insertMovimiento(movimiento: MovimientoEntity): Long

    @Query("DELETE FROM movimientos")
    suspend fun borrarMovimientos(): Int

    @Query("SELECT * FROM mediciones WHERE idSilo = :id ORDER BY fecha DESC")
    fun observarMediciones(id: String): Flow<List<MedicionEntity>>

    @Insert
    suspend fun insertMedicion(medicion: MedicionEntity): Long

    @Insert
    suspend fun insertAllMediciones(mediciones: List<MedicionEntity>): List<Long>

    @Query("DELETE FROM mediciones")
    suspend fun borrarMediciones(): Int
}