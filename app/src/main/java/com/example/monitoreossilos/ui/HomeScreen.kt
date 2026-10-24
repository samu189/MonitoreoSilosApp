
package com.example.monitoreossilos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreossilos.ui.theme.AzulPrincipal
import com.example.monitoreossilos.ui.theme.AzulClaro
import com.example.monitoreossilos.ui.theme.Naranja
import com.example.monitoreossilos.ui.theme.RojoCritico

@Composable
fun HomeScreen(
    viewModel: SiloViewModel,
    onVerSilosClick: () -> Unit
) {
    val silos by viewModel.silos.collectAsState()

    val granjas = silos
        .map { it.granjaNombre }
        .distinct()
        .sorted()

    val totalGranjas = granjas.size
    val totalSilos = silos.size

    val advertencias = silos.count {
        val porcentaje = it.porcentajeDisponible.toDouble()
        porcentaje >= 20.0 && porcentaje <= 30.0
    }

    val criticos = silos.count {
        it.porcentajeDisponible.toDouble() < 20.0
    }

    val promedio = if (silos.isEmpty()) {
        0
    } else {
        silos.map {
            it.porcentajeDisponible.toDouble()
        }.average().toInt().coerceIn(0, 100)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {

        // Encabezado azul
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AzulPrincipal)
                    .padding(20.dp)
            ) {
                Text(
                    text = "SiloControl",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Panel de monitoreo de alimento",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        // Saludo
        item {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "¡Bienvenido a SiloControl!",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Aquí tienes el resumen de tus granjas y silos.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Primera fila de indicadores
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenCard(
                    titulo = "Granjas",
                    cantidad = totalGranjas,
                    color = AzulPrincipal,
                    icono = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                ResumenCard(
                    titulo = "Silos",
                    cantidad = totalSilos,
                    color = AzulClaro,
                    icono = {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Segunda fila de indicadores
        item {
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenCard(
                    titulo = "Advertencias",
                    cantidad = advertencias,
                    color = Naranja,
                    icono = {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                ResumenCard(
                    titulo = "Críticos",
                    cantidad = criticos,
                    color = RojoCritico,
                    icono = {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Porcentaje promedio
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Nivel promedio de los silos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "$promedio%",
                        color = AzulPrincipal,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = {
                            promedio / 100f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = AzulPrincipal,
                        trackColor = Color(0xFFDCE6ED)
                    )
                }
            }
        }

        // Botón para abrir listado
        item {
            Button(
                onClick = onVerSilosClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver todos los silos")

                Spacer(Modifier.width(8.dp))

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }

        // Título de granjas
        item {
            Text(
                text = "Mis granjas",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 12.dp
                )
            )
        }

        // Lista dinámica de granjas
        items(granjas) { nombreGranja ->

            val cantidadSilos = silos.count {
                it.granjaNombre == nombreGranja
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 5.dp
                    )
                    .clickable {
                        onVerSilosClick()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        tint = AzulPrincipal,
                        modifier = Modifier.size(34.dp)
                    )

                    Spacer(Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = nombreGranja,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "$cantidadSilos silos",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ver silos",
                        tint = AzulPrincipal
                    )
                }
            }
        }

        if (silos.isEmpty()) {
            item {
                Text(
                    text = "Todavía no hay silos registrados.",
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ResumenCard(
    titulo: String,
    cantidad: Int,
    color: Color,
    modifier: Modifier = Modifier,
    icono: @Composable () -> Unit
) {
    Card(
        modifier = modifier.height(125.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            icono()

            Text(
                text = cantidad.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = titulo,
                fontSize = 13.sp,
                color = Color.White
            )
        }
    }
}
