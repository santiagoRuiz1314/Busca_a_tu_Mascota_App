package com.santiagoruiz.buscamascota.ui.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Aviso que se muestra cuando un invitado intenta una acción que requiere
 * cuenta (crear un reporte). Explica por qué y ofrece iniciar sesión o
 * registrarse; descartar (back/scrim) deja al usuario seguir explorando.
 */
@Composable
fun AuthPromptDialog(
    onDismiss: () -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = { Text("Crea una cuenta para reportar") },
        text = {
            Text(
                "Para publicar un reporte necesitas una cuenta. Inicia sesión " +
                    "o regístrate; mientras tanto puedes seguir explorando los " +
                    "reportes de la comunidad.",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onSignUp) { Text("Registrarse") }
        },
        dismissButton = {
            TextButton(onClick = onSignIn) { Text("Iniciar sesión") }
        },
    )
}
