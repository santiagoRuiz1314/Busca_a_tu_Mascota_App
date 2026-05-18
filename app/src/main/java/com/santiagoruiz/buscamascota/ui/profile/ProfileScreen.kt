package com.santiagoruiz.buscamascota.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.ui.common.PlaceholderScreen

/**
 * Perfil del usuario. UI completa en la Fase 7; por ahora expone el cierre
 * de sesión real (la redirección la maneja el observador de sesión raíz).
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    PlaceholderScreen(
        title = "Perfil",
        subtitle = "Aquí aparecerán los datos del usuario.",
        primaryActionLabel = "Cerrar sesión",
        onPrimaryAction = viewModel::signOut,
        modifier = modifier,
    )
}
