
package com.example.monitoreossilos.ui

import android.Manifest
import android.net.Uri
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.animation.AnimatedVisibility

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Scale

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil.compose.AsyncImage

import com.example.monitoreossilos.model.EstadoSilo
import com.example.monitoreossilos.model.Medicion
import com.example.monitoreossilos.model.Movimiento
import com.example.monitoreossilos.model.Silo
import com.example.monitoreossilos.model.TipoMovimiento

import com.example.monitoreossilos.util.CameraHelper
import com.example.monitoreossilos.util.aFechaHora
import com.example.monitoreossilos.util.aKg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiloDetailScreen(
    viewModel: SiloViewModel,
    idSilo: String,
    onVolverClick: () -> Unit,
    onInicioClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(idSilo) {
        viewModel.seleccionarSilo(idSilo)
    }

    val silo by viewModel.siloSeleccionado.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()
    val mediciones by viewModel.mediciones.collectAsState()
    val movForm by viewModel.movimientoForm.collectAsState()

    // Cámara y fotografía de inspección
    val cameraHelper = remember(context) {
        CameraHelper(context)
    }

    var uriPendiente by remember {
        mutableStateOf<Uri?>(null)
    }

    val tomarFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->

        val uri = uriPendiente

        if (ok && uri != null) {
            viewModel.guardarFoto(
                idSilo,
                uri.toString()
            )

            Toast.makeText(
                context,
                "Foto guardada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val lanzarCamara: () -> Unit = {
        val uri = cameraHelper.crearArchivoFotoTemporal()
        uriPendiente = uri
        tomarFoto.launch(uri)
    }

    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->

        if (concedido) {
            lanzarCamara()
        } else {
            Toast.makeText(
                context,
                "Se necesita permiso para utilizar la cámara",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = silo?.idSilo ?: "Detalle del silo"
                    )
                },

                // Flecha: volver a la pantalla anterior
                navigationIcon = {
                    IconButton(
                        onClick = onVolverClick
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },

                // NUEVO: casita para regresar al Inicio
                actions = {
                    IconButton(
                        onClick = onInicioClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Ir al inicio"
                        )
                    }
                }
            )
        }

    ) { padding ->

        val actual = silo

        if (actual == null) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),

                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),

                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Información general del silo
                ResumenSilo(actual)

                // Registro fotográfico
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            "Registro fotográfico",
                            fontWeight = FontWeight.Bold
                        )

                        AnimatedVisibility(
                            visible = actual.fotoUri != null
                        ) {
                            AsyncImage(
                                model = actual.fotoUri,

                                contentDescription =
                                    "Foto del silo ${actual.idSilo}",

                                contentScale = ContentScale.Crop,

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(
                                        MaterialTheme.shapes.medium
                                    )
                            )
                        }

                        OutlinedButton(
                            onClick = {

                                if (cameraHelper.tienePermisoCamara()) {
                                    lanzarCamara()
                                } else {
                                    permisoCamara.launch(
                                        Manifest.permission.CAMERA
                                    )
                                }
                            },

                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null
                            )

                            Spacer(
                                Modifier.width(8.dp)
                            )

                            Text(
                                if (actual.fotoUri == null) {
                                    "Tomar foto de inspección"
                                } else {
                                    "Reemplazar foto"
                                }
                            )
                        }
                    }
                }

                // Registrar cargas y descargas
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            "Registrar movimiento",
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            FilterChip(
                                selected =
                                    movForm.tipo == TipoMovimiento.CARGA,

                                onClick = {
                                    viewModel.onTipoMovimientoChange(
                                        TipoMovimiento.CARGA
                                    )
                                },

                                label = {
                                    Text("Carga")
                                },

                                leadingIcon = {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = null
                                    )
                                }
                            )

                            FilterChip(
                                selected =
                                    movForm.tipo == TipoMovimiento.DESCARGA,

                                onClick = {
                                    viewModel.onTipoMovimientoChange(
                                        TipoMovimiento.DESCARGA
                                    )
                                },

                                label = {
                                    Text("Descarga")
                                },

                                leadingIcon = {
                                    Icon(
                                        Icons.Default.ArrowDownward,
                                        contentDescription = null
                                    )
                                }
                            )
                        }

                        CampoValidado(
                            value = movForm.cantidad,
                            onValueChange =
                                viewModel::onCantidadChange,

                            label = "Cantidad (kg)",
                            icono = Icons.Default.Scale,
                            error = movForm.errorCantidad,
                            keyboardType = KeyboardType.Decimal
                        )

                        CampoValidado(
                            value = movForm.observacion,
                            onValueChange =
                                viewModel::onObservacionChange,

                            label = "Observación (opcional)",
                            icono = Icons.Default.Edit,
                            error = null,
                            mostrarExito = false
                        )

                        Button(
                            onClick = {

                                if (viewModel.registrarMovimiento()) {
                                    Toast.makeText(
                                        context,
                                        "Movimiento registrado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },

                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Registrar movimiento")
                        }
                    }
                }

                // Historial de movimientos
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            "Últimos movimientos",
                            fontWeight = FontWeight.Bold
                        )

                        if (movimientos.isEmpty()) {
                            Text(
                                "Sin movimientos registrados",
                                fontSize = 13.sp
                            )
                        }

                        movimientos.take(5).forEach {
                            FilaMovimiento(it)
                        }
                    }
                }

                // Historial de niveles
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            "Historial de niveles",
                            fontWeight = FontWeight.Bold
                        )

                        if (mediciones.isEmpty()) {
                            Text(
                                "Sin mediciones",
                                fontSize = 13.sp
                            )
                        }

                        mediciones.take(7).forEach {
                            FilaMedicion(
                                it,
                                actual.capacidadTotalKg
                            )
                        }
                    }
                }
            }
        }
    }
}

// Información general del silo
@Composable
private fun ResumenSilo(silo: Silo) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    silo.granjaNombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                BadgeEstado(silo.estado)
            }

            Text(
                "Alimento: ${silo.tipoAlimento}"
            )

            BarraNivel(silo)

            Text(
                "Disponible: ${
                    "%.1f".format(silo.porcentajeDisponible)
                }%"
            )

            Text(
                "Nivel actual: ${silo.nivelActualKg.aKg()}"
            )

            Text(
                "Capacidad total: ${silo.capacidadTotalKg.aKg()}"
            )

            Text(
                "Consumo diario: ${silo.consumoDiarioKg.aKg()}"
            )

            silo.diasRestantes?.let {
                Text(
                    "Días restantes estimados: $it"
                )
            }

            Text(
                "Última actualización: ${
                    silo.fechaActualizacion.aFechaHora()
                }",
                fontSize = 12.sp
            )
        }
    }
}

// Fila del historial de cargas y descargas
@Composable
private fun FilaMovimiento(m: Movimiento) {

    val esCarga =
        m.tipo == TipoMovimiento.CARGA

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = if (esCarga) {
                Icons.Default.ArrowUpward
            } else {
                Icons.Default.ArrowDownward
            },

            contentDescription = null,

            tint = if (esCarga) {
                colorEstado(EstadoSilo.NORMAL)
            } else {
                colorEstado(EstadoSilo.CRITICO)
            }
        )

        Spacer(
            Modifier.width(8.dp)
        )

        Column {

            Text(
                "${if (esCarga) "Carga" else "Descarga"}: ${
                    m.cantidadKg.aKg()
                }"
            )

            Text(
                m.fecha.aFechaHora() +
                        if (m.observacion.isNotBlank()) {
                            " - ${m.observacion}"
                        } else {
                            ""
                        },

                fontSize = 12.sp
            )
        }
    }
}

// Fila del historial de niveles
@Composable
private fun FilaMedicion(
    m: Medicion,
    capacidad: Double
) {

    val porcentaje = if (capacidad > 0) {
        m.nivelKg / capacidad * 100
    } else {
        0.0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            m.fecha.aFechaHora(),
            fontSize = 13.sp
        )

        Text(
            "${m.nivelKg.aKg()} (${
                "%.0f".format(porcentaje)
            }%)",

            fontSize = 13.sp
        )
    }
}
