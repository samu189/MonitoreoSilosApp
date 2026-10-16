package com.example.monitoreossilos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "silos")
data class SiloEntity(
    @PrimaryKey val idSilo: String,
    val granjaNombre: String,
    val capacidadTotalKg: Double,
    val nivelActualKg: Double
)
