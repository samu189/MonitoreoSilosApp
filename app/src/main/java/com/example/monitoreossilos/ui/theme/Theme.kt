
package com.example.monitoreossilos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SiloControlColors = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,

    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,

    secondary = Naranja,
    onSecondary = Blanco,

    tertiary = Amarillo,

    background = FondoClaro,
    onBackground = GrisOscuro,

    surface = Blanco,
    onSurface = GrisOscuro,

    error = RojoCritico,
    onError = Blanco
)

@Composable
fun MonitoreosSilosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SiloControlColors,
        typography = Typography,
        content = content
    )
}
