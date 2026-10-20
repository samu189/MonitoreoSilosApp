package com.example.monitoreossilos.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val localeCl = Locale("es", "CL")

fun Long.aFechaHora(): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", localeCl).format(Date(this))

fun Double.aKg(): String = String.format(localeCl, "%,.0f kg", this)