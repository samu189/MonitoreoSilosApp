
package com.example.monitoreossilos.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreossilos.model.EstadoSilo
import com.example.monitoreossilos.model.Silo

// Colores según el estado del silo
fun colorEstado(estado: EstadoSilo): Color = when (estado) {
    EstadoSilo.NORMAL -> Color(0xFF2E7D32)
    EstadoSilo.ADVERTENCIA -> Color(0xFFF57F17)
    EstadoSilo.CRITICO -> Color(0xFFC62828)
}

// Texto correspondiente al estado
fun etiquetaEstado(estado: EstadoSilo): String = when (estado) {
    EstadoSilo.NORMAL -> "Normal"
    EstadoSilo.ADVERTENCIA -> "Advertencia"
    EstadoSilo.CRITICO -> "Crítico"
}

// Insignia de estado con icono y color animado
@Composable
fun BadgeEstado(estado: EstadoSilo) {
    val color by animateColorAsState(
        colorEstado(estado),
        tween(400),
        label = "colorBadge"
    )

    val icono = when (estado) {
        EstadoSilo.NORMAL -> Icons.Default.CheckCircle
        EstadoSilo.ADVERTENCIA -> Icons.Default.Warning
        EstadoSilo.CRITICO -> Icons.Default.Error
    }

    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icono,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = " " + etiquetaEstado(estado),
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

// Barra animada que representa el nivel de alimento
@Composable
fun BarraNivel(
    silo: Silo,
    modifier: Modifier = Modifier
) {
    var animar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animar = true
    }

    val objetivo = if (animar) {
        (silo.porcentajeDisponible / 100.0)
            .toFloat()
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    val progreso by animateFloatAsState(
        objetivo,
        tween(800),
        label = "progresoNivel"
    )

    val color by animateColorAsState(
        colorEstado(silo.estado),
        tween(400),
        label = "colorNivel"
    )

    LinearProgressIndicator(
        progress = { progreso },
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp),
        color = color,
        trackColor = color.copy(alpha = 0.2f)
    )
}

// Campo de texto con validación visual y mensaje de error
@Composable
fun CampoValidado(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icono: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    mostrarExito: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },

        leadingIcon = {
            Icon(icono, contentDescription = null)
        },

        trailingIcon = {
            when {
                error != null -> Icon(
                    Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )

                mostrarExito && value.isNotBlank() -> Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Valido",
                    tint = Color(0xFF2E7D32)
                )
            }
        },

        isError = error != null,

        supportingText = {
            AnimatedVisibility(visible = error != null) {
                Text(error ?: "")
            }
        },

        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),

        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}
