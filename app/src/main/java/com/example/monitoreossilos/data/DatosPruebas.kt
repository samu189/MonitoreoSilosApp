package com.example.monitoreossilos.data

object DatosPrueba {

    private val granjas = listOf("Granja Norte", "Granja Sur", "Granja Este")
    private val alimentos = listOf("Iniciador", "Crecimiento", "Engorda", "Terminador")

    private val porcentajes = listOf(
        85.0, 62.0, 28.0, 12.0,
        45.0, 18.0, 72.0, 33.0,
        25.0, 9.0, 55.0, 90.0
    )

    fun generarSilos(ahora: Long): List<SiloEntity> =
        porcentajes.mapIndexed { i, porcentaje ->
            val capacidad = 10000.0 + (i % 3) * 2000.0
            SiloEntity(
                idSilo = "SILO-%02d".format(i + 1),
                granjaNombre = granjas[i / 4],
                tipoAlimento = alimentos[i % 4],
                capacidadTotalKg = capacidad,
                nivelActualKg = capacidad * porcentaje / 100.0,
                consumoDiarioKg = capacidad * 0.06,
                fechaActualizacion = ahora
            )
        }

    fun generarMediciones(silos: List<SiloEntity>, ahora: Long): List<MedicionEntity> =
        silos.flatMap { silo ->
            (6 downTo 0).map { dias ->
                MedicionEntity(
                    idSilo = silo.idSilo,
                    nivelKg = (silo.nivelActualKg + silo.consumoDiarioKg * dias)
                        .coerceAtMost(silo.capacidadTotalKg),
                    fecha = ahora - dias * 86_400_000L
                )
            }
        }
}