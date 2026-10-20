package com.example.monitoreossilos.model

enum class EstadoSilo { NORMAL, ADVERTENCIA, CRITICO }

data class Silo(
    val idSilo: String,
    val granjaNombre: String,
    val tipoAlimento: String,
    val capacidadTotalKg: Double,
    val nivelActualKg: Double,
    val consumoDiarioKg: Double,
    val fechaActualizacion: Long,
    val fotoUri: String? = null
) {
    val porcentajeDisponible: Double
        get() = if (capacidadTotalKg > 0) (nivelActualKg / capacidadTotalKg) * 100 else 0.0

    val estado: EstadoSilo
        get() = when {
            porcentajeDisponible < 20.0 -> EstadoSilo.CRITICO
            porcentajeDisponible <= 30.0 -> EstadoSilo.ADVERTENCIA
            else -> EstadoSilo.NORMAL
        }

    val diasRestantes: Int?
        get() = if (consumoDiarioKg > 0) (nivelActualKg / consumoDiarioKg).toInt() else null
}