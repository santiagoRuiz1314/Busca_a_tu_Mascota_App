package com.santiagoruiz.buscamascota.ui.auth.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Inicio de sesión. Placeholder de la Fase 1: los botones solo navegan.
 * La autenticación real contra Firebase Auth se implementa en la Fase 2.
 */
@Composable
fun SignInScreen(
    onSignedIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
        )
        Button(
            onClick = onSignedIn,
            modifier = Modifier.padding(top = 24.dp),
        ) {
            Text("Entrar")
        }
        TextButton(
            onClick = onNavigateToSignUp,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text("Crear cuenta")
        }
    }
}
