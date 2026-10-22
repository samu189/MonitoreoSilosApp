
package com.example.monitoreossilos.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiloFormScreen(
    viewModel: SiloViewModel,
    onVolverClick: () -> Unit
) {
    val form by viewModel.formState.collectAsState()

    var intentoFallido by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.limpiarFormulario()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Registrar nuevo silo")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onVolverClick
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),

            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            CampoValidado(
                value = form.idSilo,
                onValueChange = viewModel::onIdChange,
                label = "ID del silo (ej. SILO-13)",
                icono = Icons.Default.Badge,
                error = form.errorId
            )

            CampoValidado(
                value = form.granjaNombre,
                onValueChange = viewModel::onGranjaChange,
                label = "Nombre de la granja",
                icono = Icons.Default.Agriculture,
                error = form.errorGranja
            )

            CampoValidado(
                value = form.tipoAlimento,
                onValueChange = viewModel::onTipoChange,
                label = "Tipo de alimento",
                icono = Icons.Default.Grass,
                error = form.errorTipo
            )

            CampoValidado(
                value = form.capacidad,
                onValueChange = viewModel::onCapacidadChange,
                label = "Capacidad total (kg)",
                icono = Icons.Default.Inventory2,
                error = form.errorCapacidad,
                keyboardType = KeyboardType.Decimal
            )

            CampoValidado(
                value = form.nivelActual,
                onValueChange = viewModel::onNivelChange,
                label = "Nivel actual (kg)",
                icono = Icons.Default.Scale,
                error = form.errorNivel,
                keyboardType = KeyboardType.Decimal
            )

            CampoValidado(
                value = form.consumoDiario,
                onValueChange = viewModel::onConsumoChange,
                label = "Consumo diario estimado (kg)",
                icono = Icons.Default.Speed,
                error = form.errorConsumo,
                keyboardType = KeyboardType.Decimal
            )

            AnimatedVisibility(
                visible = intentoFallido && !form.esValido
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            "Corrige los campos marcados antes de guardar"
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (viewModel.guardarSilo()) {
                        onVolverClick()
                    } else {
                        intentoFallido = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text("Guardar silo")
            }
        }
    }
}
