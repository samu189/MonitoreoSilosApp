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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GranjaListScreen(
    viewModel: SiloViewModel,
    onGranjaClick: (String) -> Unit,
    onVolverClick: () -> Unit
) {
    val silos by viewModel.silos.collectAsState()

    val granjas = silos
        .map { it.granjaNombre }
        .distinct()
        .sorted()

    Scaffold(
        containerColor = Color(0xFFEDF2F7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Granjas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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

            item {
                Text(
                    text = "${granjas.size} granjas registradas",
                    fontSize = 16.sp,
                    color = Color(0xFF507091)
                )
            }

            items(granjas) { granja ->

                val cantidad = silos.count {
                    it.granjaNombre == granja
                }

                val alertas = silos.count {
                    it.granjaNombre == granja &&
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
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = AzulPrincipal,
                            modifier = Modifier.size(38.dp)
                        )

                        Spacer(Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = granja,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )

                            Text(
                                text = "$cantidad silos",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )

                            if (alertas > 0) {
                                Text(
                                    text = "$alertas silos en alerta",
                                    color = Color(0xFFFB8C00),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Ver granja",
                            tint = AzulPrincipal
                        )
                    }
                }
            }
        }
    }
}
