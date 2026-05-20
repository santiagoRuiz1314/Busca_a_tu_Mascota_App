package com.santiagoruiz.buscamascota.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.santiagoruiz.buscamascota.ui.auth.signin.SignInScreen
import com.santiagoruiz.buscamascota.ui.auth.signup.SignUpScreen

/**
 * Subgrafo de autenticación: Iniciar sesión ↔ Registro. Al autenticarse,
 * navega al grafo principal limpiando todo el back stack de auth.
 *
 * En la Fase 1 las acciones son placeholders (no validan credenciales);
 * la lógica real contra Firebase Auth entra en la Fase 2.
 */
fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation<AuthGraphRoute>(startDestination = SignInRoute) {
        composable<SignInRoute> {
            SignInScreen(
                onSignedIn = {
                    navController.navigate(MainGraphRoute) {
                        popUpTo(AuthGraphRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToSignUp = { navController.navigate(SignUpRoute) },
            )
        }
        composable<SignUpRoute> {
            SignUpScreen(
                onSignedUp = {
                    navController.navigate(MainGraphRoute) {
                        popUpTo(AuthGraphRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToSignIn = { navController.popBackStack() },
            )
        }
    }
}
