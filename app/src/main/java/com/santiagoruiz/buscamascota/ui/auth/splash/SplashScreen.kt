package com.santiagoruiz.buscamascota.ui.auth.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

/**
 * Pantalla de arranque. En esta fase solo espera un instante y deriva al
 * flujo de autenticación. En la Fase 2 observará el estado de sesión real
 * para decidir entre el grafo de auth y el principal.
 */
@Composable
fun SplashScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        delay(1200)
        onContinue()
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "BuscaMascota",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
