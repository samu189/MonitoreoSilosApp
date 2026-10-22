
package com.example.monitoreossilos.ui

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monitoreossilos.model.EstadoSilo
import com.example.monitoreossilos.model.Silo
import com.example.monitoreossilos.util.aFechaHora
import com.example.monitoreossilos.util.aKg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiloListScreen(
    viewModel: SiloViewModel,
    onNuevoSiloClick: () -> Unit,
    onSiloClick: (String) -> Unit
) {
    val todos by viewModel.silos.collectAsState()
    val silos by viewModel.silosFiltrados.collectAsState()
    val filtro by viewModel.filtroEstado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Monitoreo de Silos",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.cargarDatosPrueba()
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Cargar datos de prueba"
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoSiloClick
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
                    Text(
                        "No hay registros en la base de datos local"
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

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

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                item {
                    ResumenEstados(todos)
                }

                item {
                    FiltrosEstado(
                        filtro,
                        viewModel::onFiltroChange
                    )
                }

                if (silos.isEmpty()) {
                    item {
                        Text("No hay silos con este estado")
                    }
                }

                silos.groupBy {
                    it.granjaNombre
                }.forEach { (granja, lista) ->

                    item(key = "granja-$granja") {
                        Text(
                            text = granja,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(
                        lista,
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

// Resumen de silos por estado
@Composable
private fun ResumenEstados(
    silos: List<Silo>
) {
    Row(
        horizontalArrangement =
            Arrangement.spacedBy(8.dp),

        modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = cantidad.toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorEstado(estado)
                    )

                    Text(
                        text = etiquetaEstado(estado),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Filtros: todos, normal, advertencia y crítico
@Composable
private fun FiltrosEstado(
    filtro: EstadoSilo?,
    onFiltroChange: (EstadoSilo?) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(
            rememberScrollState()
        ),

        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        FilterChip(
            selected = filtro == null,
            onClick = {
                onFiltroChange(null)
            },
            label = {
                Text("Todos")
            }
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

// Tarjeta individual de un silo
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiloCard(
    silo: Silo,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,

        modifier = Modifier.fillMaxWidth(),

        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    silo.idSilo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                BadgeEstado(silo.estado)
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                "Alimento: ${silo.tipoAlimento}"
            )

            Text(
                "Nivel: ${silo.nivelActualKg.aKg()} / " +
                        "${silo.capacidadTotalKg.aKg()} " +
                        "(${"%.1f".format(silo.porcentajeDisponible)}%)"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            BarraNivel(silo)

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    "Act.: ${silo.fechaActualizacion.aFechaHora()}",
                    fontSize = 12.sp
                )

                silo.diasRestantes?.let {
                    Text(
                        "Quedan ~$it días",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
