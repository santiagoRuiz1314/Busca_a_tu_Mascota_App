package com.santiagoruiz.buscamascota.ui.auth.signup

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
 * Registro de usuario. Placeholder de la Fase 1: los botones solo navegan.
 * El registro real contra Firebase Auth se implementa en la Fase 2.
 */
@Composable
fun SignUpScreen(
    onSignedUp: () -> Unit,
    onBackToSignIn: () -> Unit,
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
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
        )
        Button(
            onClick = onSignedUp,
            modifier = Modifier.padding(top = 24.dp),
        ) {
            Text("Registrarme")
        }
        TextButton(
            onClick = onBackToSignIn,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Text("Ya tengo cuenta")
        }
    }
}
