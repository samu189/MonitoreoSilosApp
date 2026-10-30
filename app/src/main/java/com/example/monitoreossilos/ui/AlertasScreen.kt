package com.example.monitoreossilos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.monitoreossilos.model.EstadoSilo
import com.example.monitoreossilos.model.Silo
import com.example.monitoreossilos.ui.theme.AzulPrincipal

private val FondoAlertas = Color(0xFFEDF2F7)
private val RojoAlerta = Color(0xFFE53935)
private val NaranjaAlerta = Color(0xFFFB8C00)
private val AzulTextoAlerta = Color(0xFF123F70)

private enum class FiltroAlertas {
    TODAS,
    CRITICAS,
    ADVERTENCIAS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    viewModel: SiloViewModel,
    onVolverClick: () -> Unit,
    onInicioClick: () -> Unit,
    onSiloClick: (String) -> Unit
) {
    val silos by viewModel.silos.collectAsState()

    var filtro by remember {
        mutableStateOf(FiltroAlertas.TODAS)
    }

    // Cantidades obtenidas desde Room
    val criticos = silos.count {
        it.estado == EstadoSilo.CRITICO
    }

    val advertencias = silos.count {
        it.estado == EstadoSilo.ADVERTENCIA
    }

    // Solo mostrar silos que necesitan atención
    val silosConAlerta = silos.filter {
        it.estado == EstadoSilo.CRITICO ||
                it.estado == EstadoSilo.ADVERTENCIA
    }

    val silosMostrados = silosConAlerta.filter {
        when (filtro) {
            FiltroAlertas.TODAS -> true

            FiltroAlertas.CRITICAS ->
                it.estado == EstadoSilo.CRITICO

            FiltroAlertas.ADVERTENCIAS ->
                it.estado == EstadoSilo.ADVERTENCIA
        }
    }.sortedBy {
        it.porcentajeDisponible.toDouble()
    }

    Scaffold(
        containerColor = FondoAlertas,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Alertas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = onVolverClick
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },

                actions = {
                    IconButton(
                        onClick = onInicioClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Ir al inicio",
                            tint = Color.White
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrincipal
                )
            )
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Encabezado
            item {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(5.dp)
                ) {

                    Text(
                        text = "Estado de tus silos",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulTextoAlerta
                    )

                    Text(
                        text = "Revisa los silos que necesitan atención.",
                        fontSize = 14.sp,
                        color = Color(0xFF607D8B)
                    )
                }
            }

            // Resumen de críticos y advertencias
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    TarjetaConteoAlerta(
                        titulo = "Críticos",
                        cantidad = criticos,
                        color = RojoAlerta,
                        modifier = Modifier.weight(1f)
                    )

                    TarjetaConteoAlerta(
                        titulo = "Advertencias",
                        cantidad = advertencias,
                        color = NaranjaAlerta,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Filtros
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    FilterChip(
                        selected =
                            filtro == FiltroAlertas.TODAS,

                        onClick = {
                            filtro = FiltroAlertas.TODAS
                        },

                        label = {
                            Text("Todas")
                        }
                    )

                    FilterChip(
                        selected =
                            filtro == FiltroAlertas.CRITICAS,

                        onClick = {
                            filtro = FiltroAlertas.CRITICAS
                        },

                        label = {
                            Text("Críticas")
                        }
                    )

                    FilterChip(
                        selected =
                            filtro == FiltroAlertas.ADVERTENCIAS,

                        onClick = {
                            filtro = FiltroAlertas.ADVERTENCIAS
                        },

                        label = {
                            Text("Advertencias")
                        }
                    )
                }
            }

            item {
                Text(
                    text = "${silosMostrados.size} alertas encontradas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AzulTextoAlerta
                )
            }

            // Si no existen alertas
            if (silosMostrados.isEmpty()) {

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(16.dp),

                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally,

                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = "Sin alertas",
                                color = AzulTextoAlerta,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Text(
                                text = "No hay silos con alertas en este filtro.",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

            } else {

                // Tarjetas de silos con alerta
                items(silosMostrados) { silo ->

                    TarjetaSiloAlerta(
                        silo = silo,
                        onClick = {
                            onSiloClick(silo.idSilo)
                        }
                    )
                }
            }
        }
    }
}

// Tarjetas pequeñas del resumen
@Composable
private fun TarjetaConteoAlerta(
    titulo: String,
    cantidad: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = color
            )

            Text(
                text = cantidad.toString(),
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = titulo,
                fontSize = 13.sp,
                color = AzulTextoAlerta
            )
        }
    }
}

// Tarjeta individual de una alerta
@Composable
private fun TarjetaSiloAlerta(
    silo: Silo,
    onClick: () -> Unit
) {
    val esCritico =
        silo.estado == EstadoSilo.CRITICO

    val colorAlerta = if (esCritico) {
        RojoAlerta
    } else {
        NaranjaAlerta
    }

    val estadoTexto = if (esCritico) {
        "Nivel crítico"
    } else {
        "Advertencia"
    }

    val porcentaje = silo.porcentajeDisponible
        .toDouble()
        .coerceIn(0.0, 100.0)

    Card(
        onClick = onClick,

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = colorAlerta.copy(
                                alpha = 0.12f
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = colorAlerta
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = silo.idSilo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulTextoAlerta
                    )

                    Text(
                        text = silo.granjaNombre,
                        fontSize = 13.sp,
                        color = Color(0xFF607D8B)
                    )
                }

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowForward,

                    contentDescription = "Ver detalle",
                    tint = AzulPrincipal
                )
            }

            Text(
                text = estadoTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorAlerta
            )

            Text(
                text = "Alimento: ${silo.tipoAlimento}",
                fontSize = 13.sp,
                color = AzulTextoAlerta
            )

            LinearProgressIndicator(
                progress = {
                    (porcentaje / 100.0).toFloat()
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp),

                color = colorAlerta,
                trackColor = Color(0xFFE5ECF2)
            )

            Text(
                text = "Alimento disponible: ${
                    "%.1f".format(porcentaje)
                }%",

                fontSize = 13.sp,
                color = colorAlerta,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Presiona para ver el detalle del silo",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
