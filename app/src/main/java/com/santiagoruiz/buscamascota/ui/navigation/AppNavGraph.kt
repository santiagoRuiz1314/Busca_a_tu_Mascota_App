package com.santiagoruiz.buscamascota.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.santiagoruiz.buscamascota.ui.auth.splash.SplashScreen
import com.santiagoruiz.buscamascota.ui.session.SessionViewModel
import kotlinx.coroutines.flow.filterNotNull

/**
 * Grafo de navegación raíz. El splash decide el destino inicial según el
 * estado de sesión observado en Firebase. Un observador de sesión a nivel
 * raíz redirige de forma reactiva al flujo de auth cuando se cierra sesión
 * (o expira) desde cualquier pantalla.
 */
@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val sessionViewModel: SessionViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        var wasAuthenticated = false
        sessionViewModel.isAuthenticated.filterNotNull().collect { authenticated ->
            if (wasAuthenticated && !authenticated) {
                navController.navigate(AuthGraphRoute) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            wasAuthenticated = authenticated
        }
    }

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onAuthenticated = {
                    navController.navigate(MainGraphRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                },
                onUnauthenticated = {
                    navController.navigate(AuthGraphRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                },
            )
        }

        authGraph(navController)

        composable<MainGraphRoute> {
            MainScreen()
        }
    }
}
