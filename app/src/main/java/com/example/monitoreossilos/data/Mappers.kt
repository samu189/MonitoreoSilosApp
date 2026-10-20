package com.example.monitoreossilos.data

import com.example.monitoreossilos.model.Medicion
import com.example.monitoreossilos.model.Movimiento
import com.example.monitoreossilos.model.Silo
import com.example.monitoreossilos.model.TipoMovimiento

fun SiloEntity.toDomain() = Silo(
    idSilo = idSilo,
    granjaNombre = granjaNombre,
    tipoAlimento = tipoAlimento,
    capacidadTotalKg = capacidadTotalKg,
    nivelActualKg = nivelActualKg,
    consumoDiarioKg = consumoDiarioKg,
    fechaActualizacion = fechaActualizacion,
    fotoUri = fotoUri
)

fun Silo.toEntity() = SiloEntity(
    idSilo = idSilo,
    granjaNombre = granjaNombre,
    tipoAlimento = tipoAlimento,
    capacidadTotalKg = capacidadTotalKg,
    nivelActualKg = nivelActualKg,
    consumoDiarioKg = consumoDiarioKg,
    fechaActualizacion = fechaActualizacion,
    fotoUri = fotoUri
)

fun MovimientoEntity.toDomain() = Movimiento(
    id = id,
    idSilo = idSilo,
    tipo = TipoMovimiento.valueOf(tipo),
    cantidadKg = cantidadKg,
    observacion = observacion,
    fecha = fecha
)

fun MedicionEntity.toDomain() = Medicion(
    id = id,
    idSilo = idSilo,
    nivelKg = nivelKg,
    fecha = fecha
)