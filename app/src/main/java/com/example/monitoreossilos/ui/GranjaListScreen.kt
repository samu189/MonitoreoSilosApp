
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications

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

import com.example.monitoreossilos.ui.theme.AzulPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GranjaListScreen(
    viewModel: SiloViewModel,
    onGranjaClick: (String) -> Unit,
    onVolverClick: () -> Unit
) {
    val silos by viewModel.silos.collectAsState()

    // Granjas registradas en Room
    val granjas = silos
        .map { it.granjaNombre }
        .distinct()
        .sorted()

    // Texto ingresado en el buscador
    var busqueda by remember {
        mutableStateOf("")
    }

    // Filtrar granjas por nombre
    val granjasFiltradas = granjas.filter { nombre ->
        nombre.contains(
            busqueda.trim(),
            ignoreCase = true
        )
    }

    Scaffold(
        containerColor = Color(0xFFEDF2F7),

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Granjas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
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

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Buscador de granjas
            item {
                OutlinedTextField(
                    value = busqueda,

                    onValueChange = {
                        busqueda = it
                    },

                    placeholder = {
                        Text("Buscar granja...")
                    },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = AzulPrincipal
                        )
                    },

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(12.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = AzulPrincipal,
                        unfocusedBorderColor = Color(0xFFD4E0EA)
                    )
                )
            }

            // Contador de granjas
            item {
                Text(
                    text = if (busqueda.isBlank()) {
                        "${granjas.size} granjas registradas"
                    } else {
                        "${granjasFiltradas.size} resultados encontrados"
                    },

                    fontSize = 14.sp,
                    color = Color(0xFF507091)
                )
            }

            // Mensaje cuando no hay resultados
            if (granjasFiltradas.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),

                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),

                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),

                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = AzulPrincipal,
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = if (granjas.isEmpty()) {
                                    "No hay granjas registradas"
                                } else {
                                    "No se encontraron granjas"
                                },

                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF37474F)
                            )
                        }
                    }
                }
            }

            // Tarjetas de granjas
            items(
                items = granjasFiltradas,
                key = { it }
            ) { granja ->

                val silosDeGranja = silos.filter {
                    it.granjaNombre == granja
                }

                val cantidad = silosDeGranja.size

                val alertas = silosDeGranja.count {
                    it.porcentajeDisponible.toDouble() <= 30.0
                }

                Card(
                    onClick = {
                        onGranjaClick(granja)
                    },

                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(16.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),

                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Icono de granja
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(
                                    color = Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(12.dp)
                                ),

                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = AzulPrincipal,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        // Información de la granja
                        Column(
                            modifier = Modifier.weight(1f),

                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {

                            Text(
                                text = granja,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF123F70)
                            )

                            Text(
                                text = "$cantidad silos registrados",
                                color = Color(0xFF607D8B),
                                fontSize = 13.sp
                            )

                            // Mostrar alertas solo si existen
                            if (alertas > 0) {
                                Text(
                                    text = "$alertas silos en alerta",
                                    color = Color(0xFFFB8C00),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "Sin alertas",
                                    color = Color(0xFF2E7D32),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Flecha para entrar a la granja
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowForward,

                            contentDescription = "Ver silos de $granja",

                            tint = AzulPrincipal,

                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
