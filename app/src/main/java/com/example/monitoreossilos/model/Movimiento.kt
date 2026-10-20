package com.example.monitoreossilos.model

enum class TipoMovimiento { CARGA, DESCARGA }

data class Movimiento(
    val id: Long,
    val idSilo: String,
    val tipo: TipoMovimiento,
    val cantidadKg: Double,
    val observacion: String,
    val fecha: Long
)

data class Medicion(
    val id: Long,
    val idSilo: String,
    val nivelKg: Double,
    val fecha: Long
)