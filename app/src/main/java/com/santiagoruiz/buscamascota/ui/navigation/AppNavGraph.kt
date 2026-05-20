package com.santiagoruiz.buscamascota.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.santiagoruiz.buscamascota.ui.auth.splash.SplashScreen
import com.santiagoruiz.buscamascota.ui.session.SessionState
import com.santiagoruiz.buscamascota.ui.session.SessionViewModel

/**
 * Grafo de navegación raíz. El [SessionViewModel] garantiza que siempre haya
 * sesión (real o invitado anónimo) e indica a dónde derivar tras el splash:
 * - [SessionState.Active] → grafo principal (incluye modo invitado).
 * - [SessionState.ManualAuthRequired] → flujo de auth (fallback si el
 *   proveedor anónimo está deshabilitado o no hay red).
 *
 * Ya no se expulsa al usuario al cerrar sesión: el logout cae a modo
 * invitado (re-login anónimo automático) sin abandonar el grafo principal.
 */
@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isGuest by sessionViewModel.isGuest.collectAsState()

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
    ) {
        composable<SplashRoute> {
            val sessionState by sessionViewModel.sessionState.collectAsState()
            LaunchedEffect(sessionState) {
                when (sessionState) {
                    SessionState.Active -> navController.navigate(MainGraphRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }

                    SessionState.ManualAuthRequired -> navController.navigate(AuthGraphRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }

                    SessionState.Loading -> Unit
                }
            }
            SplashScreen()
        }

        authGraph(navController)

        composable<MainGraphRoute> {
            MainScreen(
                isGuest = isGuest,
                onRequireAuth = { startWithSignUp ->
                    navController.navigate(if (startWithSignUp) SignUpRoute else SignInRoute)
                },
            )
        }
    }
}
