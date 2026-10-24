
package com.example.monitoreossilos.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreossilos.ui.theme.AzulPrincipal

private val AzulFuerte = Color(0xFF0864AD)
private val AzulTexto = Color(0xFF123F70)
private val Fondo = Color(0xFFEDF2F7)

@Composable
fun HomeScreen(
    viewModel: SiloViewModel,
    onVerSilosClick: () -> Unit,
    onAlertasClick: () -> Unit,
    onGranjaClick: (String) -> Unit
) {
    val silos by viewModel.silos.collectAsState()

    val granjas = silos
        .map { it.granjaNombre }
        .distinct()
        .sorted()

    val advertencias = silos.count {
        it.porcentajeDisponible.toDouble() in 20.0..30.0
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

    Scaffold(
        containerColor = Fondo,

        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AzulFuerte)
                    .height(62.dp)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menú decorativo",
                    tint = Color.White
                )

                Text(
                    text = "SiloControl",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onAlertasClick
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Ver alertas",
                        tint = Color.White
                    )
                }
            }
        },

        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null
                        )
                    },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal,
                        indicatorColor = Color(0xFFE3F2FD)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onVerSilosClick,
                    icon = {
                        Icon(
                            Icons.Default.Warehouse,
                            contentDescription = null
                        )
                    },
                    label = { Text("Silos") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onAlertasClick,
                    icon = {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null
                        )
                    },
                    label = { Text("Alertas") }
                )

                NavigationBarItem(
                    selected = false,
                    enabled = false,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 20.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Column {
                    Text(
                        text = "Hola, bienvenido",
                        color = AzulTexto,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "Aquí tienes el resumen de tus silos.",
                        color = Color(0xFF507091),
                        fontSize = 14.sp
                    )
                }
            }

            // Tarjetas Granjas y Silos
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TarjetaResumen(
                        titulo = "Granjas",
                        cantidad = granjas.size,
                        colores = listOf(
                            Color(0xFF0873D1),
                            Color(0xFF249CF0)
                        ),
                        modifier = Modifier.weight(1f),
                        icono = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )

                    TarjetaResumen(
                        titulo = "Silos",
                        cantidad = silos.size,
                        colores = listOf(
                            Color(0xFF249CF0),
                            Color(0xFF64B5F6)
                        ),
                        modifier = Modifier.weight(1f),
                        icono = {
                            Icon(
                                Icons.Default.Warehouse,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )
                }
            }

            // Tarjetas Advertencias y Criticos
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TarjetaResumen(
                        titulo = "Advertencias",
                        cantidad = advertencias,
                        colores = listOf(
                            Color(0xFFFB8C00),
                            Color(0xFFFFB300)
                        ),
                        modifier = Modifier.weight(1f),
                        icono = {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )

                    TarjetaResumen(
                        titulo = "Críticos",
                        cantidad = criticos,
                        colores = listOf(
                            Color(0xFFE53935),
                            Color(0xFFFF5252)
                        ),
                        modifier = Modifier.weight(1f),
                        icono = {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )
                }
            }

            // Promedio circular
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        IndicadorCircular(
                            porcentaje = promedio,
                            modifier = Modifier.size(94.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Nivel promedio de silos",
                                fontWeight = FontWeight.Bold,
                                color = AzulTexto,
                                fontSize = 15.sp
                            )

                            Spacer(Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { promedio / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(9.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                color = AzulPrincipal,
                                trackColor = Color(0xFFE0EBF2)
                            )

                            Spacer(Modifier.height(7.dp))

                            Text(
                                text = "$promedio% de llenado promedio",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Boton principal
            item {
                Button(
                    onClick = onVerSilosClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver todos los silos")

                    Spacer(Modifier.width(10.dp))

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }

            // Titulo de granjas
            item {
                Text(
                    text = "Mis granjas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulTexto,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Tarjetas de granjas
            items(granjas) { granja ->

                val cantidad = silos.count {
                    it.granjaNombre == granja
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onGranjaClick(granja)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                tint = AzulPrincipal,
                                modifier = Modifier.size(27.dp)
                            )
                        }

                        Spacer(Modifier.width(13.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = granja,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AzulTexto
                            )

                            Text(
                                text = "$cantidad silos registrados",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Ver silos de esta granja",
                            tint = AzulPrincipal
                        )
                    }
                }
            }

            if (silos.isEmpty()) {
                item {
                    Text(
                        text = "No hay silos registrados.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaResumen(
    titulo: String,
    cantidad: Int,
    colores: List<Color>,
    modifier: Modifier = Modifier,
    icono: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                Brush.horizontalGradient(colores)
            )
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                icono()

                Text(
                    text = cantidad.toString(),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = titulo,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun IndicadorCircular(
    porcentaje: Int,
    modifier: Modifier = Modifier
) {
    val valor = porcentaje.coerceIn(0, 100)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val ancho = 10.dp.toPx()

            drawArc(
                color = Color(0xFFDEE8EE),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = ancho,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = Color(0xFF1682D7),
                startAngle = -90f,
                sweepAngle = 360f * valor / 100f,
                useCenter = false,
                style = Stroke(
                    width = ancho,
                    cap = StrokeCap.Round
                )
            )
        }

        Text(
            text = "$valor%",
            color = AzulTexto,
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp
        )
    }
}
