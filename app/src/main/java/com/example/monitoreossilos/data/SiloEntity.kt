package com.example.monitoreossilos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "silos")
data class SiloEntity(
    @PrimaryKey val idSilo: String,
    val granjaNombre: String,
    val tipoAlimento: String,
    val capacidadTotalKg: Double,
    val nivelActualKg: Double,
    val consumoDiarioKg: Double,
    val fechaActualizacion: Long,
    val fotoUri: String? = null
)

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idSilo: String,
    val tipo: String,
    val cantidadKg: Double,
    val observacion: String,
    val fecha: Long
)

@Entity(tableName = "mediciones")
data class MedicionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idSilo: String,
    val nivelKg: Double,
    val fecha: Long
)