
package com.example.monitoreossilos

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

import androidx.core.content.ContextCompat

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.monitoreossilos.data.AppDatabase
import com.example.monitoreossilos.data.SiloRepository

import com.example.monitoreossilos.model.EstadoSilo

import com.example.monitoreossilos.ui.GranjaListScreen
import com.example.monitoreossilos.ui.HomeScreen
import com.example.monitoreossilos.ui.LoginScreen
import com.example.monitoreossilos.ui.SiloDetailScreen
import com.example.monitoreossilos.ui.SiloFormScreen
import com.example.monitoreossilos.ui.SiloListScreen
import com.example.monitoreossilos.ui.SiloViewModel
import com.example.monitoreossilos.ui.SiloViewModelFactory
import com.example.monitoreossilos.ui.SplashScreen

import com.example.monitoreossilos.ui.theme.MonitoreosSilosTheme
import com.example.monitoreossilos.util.NotificationHelper

class MainActivity : ComponentActivity() {

    // Base de datos Room
    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    // Repositorio que conecta Room con el ViewModel
    private val repository by lazy {
        SiloRepository(database.siloDao())
    }

    // Administrador de notificaciones
    private val notificationHelper by lazy {
        NotificationHelper(this)
    }

    // ViewModel principal
    private val siloViewModel: SiloViewModel by viewModels {
        SiloViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MonitoreosSilosTheme {

                // Permisos de notificaciones
                SolicitarPermisoNotificaciones()

                // Navegación de SiloControl
                AppNavigation(
                    viewModel = siloViewModel,
                    notificationHelper = notificationHelper
                )
            }
        }
    }
}

// Solicita permiso de notificaciones en Android 13+
@Composable
private fun SolicitarPermisoNotificaciones() {

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }
}

// Navegación principal de SiloControl
@Composable
fun AppNavigation(
    viewModel: SiloViewModel,
    notificationHelper: NotificationHelper
) {

    val navController = rememberNavController()

    // Convierte alertas críticas en notificaciones
    LaunchedEffect(viewModel, notificationHelper) {
        viewModel.alertas.collect { silo ->
            notificationHelper.enviarAlertaSiloCritico(
                silo.idSilo,
                silo.granjaNombre,
                silo.porcentajeDisponible
            )
        }
    }

    NavHost(
        navController = navController,

        // La aplicación comienza en Splash
        startDestination = "splash",

        // Animación al entrar
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(300)
            ) { it } + fadeIn(tween(300))
        },

        // Animación al salir
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(300)
            ) { -it / 3 } + fadeOut(tween(300))
        },

        // Animación al retroceder
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(300)
            ) { -it / 3 } + fadeIn(tween(300))
        },

        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(300)
            ) { it } + fadeOut(tween(300))
        }

    ) {

        // PANTALLA 0: Splash
        composable("splash") {

            SplashScreen(
                onSplashFinished = {
                    navController.navigate("login") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // PANTALLA 1: Inicio de sesión
        composable("login") {

            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("inicio") {
                        popUpTo("login") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // PANTALLA 2: Menú principal

        composable("inicio") {
            HomeScreen(
                viewModel = viewModel,

                onVerSilosClick = {
                    viewModel.onFiltroChange(null)
                    navController.navigate("lista")
                },

                onAlertasClick = {
                    viewModel.onFiltroChange(EstadoSilo.CRITICO)
                    navController.navigate("lista")
                },

                onGranjaClick = { granja ->
                    viewModel.onFiltroChange(null)
                    navController.navigate(
                        "silosGranja/${Uri.encode(granja)}"
                    )
                },

                onVerGranjasClick = {
                    navController.navigate("granjas")
                }
            )
        }


        // PANTALLA 3: Lista de granjas
        composable("granjas") {

            GranjaListScreen(
                viewModel = viewModel,

                onGranjaClick = { nombre ->
                    viewModel.onFiltroChange(null)

                    navController.navigate(
                        "silosGranja/${Uri.encode(nombre)}"
                    )
                },

                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // PANTALLA 4: Silos de una granja específica
        composable(
            route = "silosGranja/{nombre}",
            arguments = listOf(
                navArgument("nombre") {
                    type = NavType.StringType
                }
            )
        ) { entry ->

            val nombre = entry.arguments
                ?.getString("nombre")
                ?: return@composable

            SiloListScreen(
                viewModel = viewModel,
                granjaSeleccionada = nombre,

                onNuevoSiloClick = {
                    navController.navigate("formulario")
                },

                onSiloClick = { id ->
                    navController.navigate(
                        "detalle/${Uri.encode(id)}"
                    )
                },

                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // PANTALLA 5: Listado general de silos

        composable("lista") {
            SiloListScreen(
                viewModel = viewModel,

                onNuevoSiloClick = {
                    navController.navigate("formulario")
                },

                onSiloClick = { id ->
                    navController.navigate(
                        "detalle/${Uri.encode(id)}"
                    )
                },

                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }


        // PANTALLA 6: Formulario para registrar silos
        composable("formulario") {

            SiloFormScreen(
                viewModel = viewModel,

                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // PANTALLA 7: Detalle de un silo
        composable(
            route = "detalle/{idSilo}",
            arguments = listOf(
                navArgument("idSilo") {
                    type = NavType.StringType
                }
            )
        ) { entry ->

            val id = entry.arguments
                ?.getString("idSilo")
                ?: return@composable

            SiloDetailScreen(
                viewModel = viewModel,
                idSilo = id,

                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
