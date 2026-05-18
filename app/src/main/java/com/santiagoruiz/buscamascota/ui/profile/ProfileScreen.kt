package com.santiagoruiz.buscamascota.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.santiagoruiz.buscamascota.ui.common.PlaceholderScreen

/**
 * Perfil del usuario. Placeholder de la Fase 1; UI real (y cierre de
 * sesión) en la Fase 7.
 */
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "Perfil",
        subtitle = "Aquí aparecerán los datos del usuario.",
        primaryActionLabel = "Cerrar sesión",
        onPrimaryAction = onSignOut,
        modifier = modifier,
    )
}
