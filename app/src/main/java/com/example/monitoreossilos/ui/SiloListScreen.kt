package com.example.monitoreossilos.ui
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreossilos.model.EstadoSilo
import com.example.monitoreossilos.model.Silo
import com.example.monitoreossilos.util.aFechaHora
import com.example.monitoreossilos.util.aKg
import com.example.monitoreossilos.ui.theme.AzulPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiloListScreen(
    viewModel: SiloViewModel,
    onNuevoSiloClick: () -> Unit,
    onSiloClick: (String) -> Unit,
    granjaSeleccionada: String? = null,
    onVolverClick: () -> Unit = {}
) {
    val todos by viewModel.silos.collectAsState()
    val filtradosEstado by viewModel.silosFiltrados.collectAsState()
    val filtro by viewModel.filtroEstado.collectAsState()

    // Primero seleccionar los silos de la granja
    val silosGranja = if (granjaSeleccionada == null) {
        todos
    } else {
        todos.filter {
            it.granjaNombre == granjaSeleccionada
        }
    }

    // Aplicar también el filtro de estado
    val silosVisibles = if (granjaSeleccionada == null) {
        filtradosEstado
    } else {
        filtradosEstado.filter {
            it.granjaNombre == granjaSeleccionada
        }
    }

    Scaffold(
        containerColor = Color(0xFFEDF2F7),

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = granjaSeleccionada ?: "Todos los silos",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = onVolverClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.cargarDatosPrueba()
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Cargar datos de prueba",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrincipal
                )
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoSiloClick,
                containerColor = AzulPrincipal,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar silo"
                )
            }
        }
    ) { innerPadding ->

        if (todos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No hay registros en la base de datos local")

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.cargarDatosPrueba()
                        }
                    ) {
                        Text("Cargar datos de prueba")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    ResumenEstados(silosGranja)
                }

                item {
                    FiltrosEstado(
                        filtro = filtro,
                        onFiltroChange = viewModel::onFiltroChange
                    )
                }

                if (granjaSeleccionada != null) {
                    item {
                        Text(
                            text = "${silosGranja.size} silos registrados",
                            color = AzulPrincipal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (silosVisibles.isEmpty()) {
                    item {
                        Text(
                            text = "No hay silos que coincidan con el filtro",
                            color = Color.Gray
                        )
                    }
                }

                silosVisibles
                    .groupBy { it.granjaNombre }
                    .forEach { (granja, lista) ->

                        if (granjaSeleccionada == null) {
                            item(key = "granja-$granja") {
                                Text(
                                    text = granja,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPrincipal
                                )
                            }
                        }

                        items(
                            items = lista,
                            key = { it.idSilo }
                        ) { silo ->
                            SiloCard(
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
}

@Composable
private fun ResumenEstados(silos: List<Silo>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EstadoSilo.values().forEach { estado ->

            val cantidad by animateIntAsState(
                targetValue = silos.count {
                    it.estado == estado
                },
                animationSpec = tween(500),
                label = "contador"
            )

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = cantidad.toString(),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorEstado(estado)
                    )

                    Text(
                        text = etiquetaEstado(estado),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FiltrosEstado(
    filtro: EstadoSilo?,
    onFiltroChange: (EstadoSilo?) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(
            rememberScrollState()
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = filtro == null,
            onClick = { onFiltroChange(null) },
            label = { Text("Todos") }
        )

        EstadoSilo.values().forEach { estado ->
            FilterChip(
                selected = filtro == estado,
                onClick = {
                    onFiltroChange(estado)
                },
                label = {
                    Text(etiquetaEstado(estado))
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiloCard(
    silo: Silo,
    onClick: () -> Unit
) {
    val porcentaje = silo.porcentajeDisponible
        .toFloat()
        .coerceIn(0f, 100f)

    val colorNivel = when {
        porcentaje < 20f -> Color(0xFFE53935)
        porcentaje <= 30f -> Color(0xFFFB8C00)
        else -> Color(0xFF2E7D32)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // Indicador circular del silo
                Box(
                    modifier = Modifier.size(82.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { porcentaje / 100f },
                        modifier = Modifier.size(76.dp),
                        color = colorNivel,
                        trackColor = Color(0xFFE2E9ED),
                        strokeWidth = 9.dp,
                        strokeCap = StrokeCap.Round
                    )

                    Text(
                        text = "${porcentaje.toInt()}%",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF123F70)
                    )
                }

                // Información principal del silo
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = silo.idSilo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AzulPrincipal
                    )

                    Text(
                        text = silo.tipoAlimento,
                        fontSize = 13.sp,
                        color = Color(0xFF37474F)
                    )

                    BadgeEstado(silo.estado)

                    Text(
                        text = "Capacidad: ${silo.capacidadTotalKg.aKg()}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Disponible: ${silo.nivelActualKg.aKg()}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                // Flecha para abrir el detalle
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Ver detalle del silo",
                    tint = AzulPrincipal,
                    modifier = Modifier.size(22.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFECEFF1)
            )

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Última actualización",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = silo.fechaActualizacion.aFechaHora(),
                        fontSize = 11.sp,
                        color = Color(0xFF37474F)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Tiempo restante",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = silo.diasRestantes?.let {
                            "Aprox. $it días"
                        } ?: "Sin estimación",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorNivel
                    )
                }
            }
        }
    }
}
