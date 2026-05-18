package com.santiagoruiz.buscamascota.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.santiagoruiz.buscamascota.ui.auth.splash.SplashScreen

/**
 * Grafo de navegación raíz. Arranca en [SplashRoute] y deriva al subgrafo
 * de autenticación o al principal.
 *
 * En la Fase 1 la transición es fija (Splash → Auth). En la Fase 2 el splash
 * observará el estado de sesión real para enrutar a [MainGraphRoute] cuando
 * exista usuario autenticado.
 */
@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onContinue = {
                    navController.navigate(AuthGraphRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                },
            )
        }

        authGraph(navController)

        composable<MainGraphRoute> {
            MainScreen(
                onSignOut = {
                    navController.navigate(AuthGraphRoute) {
                        popUpTo(MainGraphRoute) { inclusive = true }
                    }
                },
            )
        }
    }
}
