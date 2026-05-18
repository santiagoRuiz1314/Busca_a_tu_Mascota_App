package com.santiagoruiz.buscamascota.ui.alerts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.santiagoruiz.buscamascota.ui.common.PlaceholderScreen

/** Lista de alertas. Placeholder de la Fase 1; UI real en la Fase 4. */
@Composable
fun AlertsScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = "Alertas",
        subtitle = "Aquí aparecerán las alertas y reportes recientes.",
        modifier = modifier,
    )
}
