package com.example.monitoreossilos.model

enum class EstadoSilo { NORMAL, ADVERTENCIA, CRITICO }

data class Silo(
    val idSilo: String,
    val granjaNombre: String,
    val capacidadTotalKg: Double,
    val nivelActualKg: Double
) {
    val porcentajeDisponible: Double
        get() = if (capacidadTotalKg > 0) (nivelActualKg / capacidadTotalKg) * 100 else 0.0

    val estado: EstadoSilo
        get() = when {
            porcentajeDisponible < 15.0 -> EstadoSilo.CRITICO
            porcentajeDisponible <= 30.0 -> EstadoSilo.ADVERTENCIA
            else -> EstadoSilo.NORMAL
        }
}
