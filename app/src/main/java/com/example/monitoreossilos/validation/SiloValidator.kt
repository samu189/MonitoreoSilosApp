package com.example.monitoreossilos.validation

import com.example.monitoreossilos.model.Silo
import com.example.monitoreossilos.model.TipoMovimiento

// Toda la lógica de validación está fuera de la interfaz.
object SiloValidator {

    private val regexId = Regex("^SILO-\\d{2,3}$")

    fun parsearNumero(texto: String): Double? =
        texto.trim().replace(',', '.').toDoubleOrNull()

    fun validarId(valor: String): String? = when {
        valor.isBlank() -> "El ID del silo es obligatorio"
        !regexId.matches(valor.trim().uppercase()) ->
            "Formato invalido. Ejemplo: SILO-04"
        else -> null
    }

    fun validarTexto(valor: String, campo: String): String? = when {
        valor.isBlank() -> "$campo es obligatorio"
        valor.trim().length < 3 ->
            "$campo debe tener al menos 3 caracteres"
        valor.trim().length > 40 ->
            "$campo no puede superar los 40 caracteres"
        else -> null
    }

    fun validarCapacidad(valor: String): String? {
        val n = parsearNumero(valor)
        return when {
            valor.isBlank() -> "La capacidad es obligatoria"
            n == null -> "Ingrese un numero valido"
            n <= 0 -> "Debe ser mayor a 0"
            n > 100_000 -> "Maximo 100.000 kg"
            else -> null
        }
    }

    fun validarNivel(valor: String, capacidad: String): String? {
        val n = parsearNumero(valor)
        val cap = parsearNumero(capacidad)

        return when {
            valor.isBlank() -> "El nivel actual es obligatorio"
            n == null -> "Ingrese un numero valido"
            n < 0 -> "No puede ser negativo"
            cap != null && n > cap ->
                "No puede superar la capacidad total"
            else -> null
        }
    }

    fun validarConsumo(valor: String): String? {
        val n = parsearNumero(valor)

        return when {
            valor.isBlank() -> "El consumo diario es obligatorio"
            n == null -> "Ingrese un numero valido"
            n < 0 -> "No puede ser negativo"
            n > 50_000 -> "Valor demasiado alto"
            else -> null
        }
    }

    fun validarCantidadMovimiento(
        valor: String,
        tipo: TipoMovimiento,
        silo: Silo?
    ): String? {
        val n = parsearNumero(valor)

        return when {
            silo == null -> "Silo no disponible"
            valor.isBlank() -> "La cantidad es obligatoria"
            n == null -> "Ingrese un numero valido"
            n <= 0 -> "Debe ser mayor a 0"

            tipo == TipoMovimiento.DESCARGA &&
                    n > silo.nivelActualKg ->
                "No puede superar el nivel actual (${"%.0f".format(silo.nivelActualKg)} kg)"

            tipo == TipoMovimiento.CARGA &&
                    silo.nivelActualKg + n > silo.capacidadTotalKg ->
                "Excede la capacidad. Espacio libre: ${"%.0f".format(silo.capacidadTotalKg - silo.nivelActualKg)} kg"

            else -> null
        }
    }
}
