package com.santiagoruiz.buscamascota.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Ítems de la barra de navegación inferior del grafo principal. El FAB
 * central de "Nuevo Reporte" va aparte (no es un ítem). «Buscar» salió del
 * bottom nav: se alcanza desde Perfil ("Mi Actividad").
 */
enum class BottomNavItem(
    val route: Any,
    val label: String,
    val icon: ImageVector,
) {
    FEED(FeedRoute, "Inicio", Icons.Filled.Home),
    MAP(MapRoute, "Mapa", Icons.Filled.Map),
    ALERTS(AlertsRoute, "Alertas", Icons.Filled.Notifications),
    PROFILE(ProfileRoute, "Perfil", Icons.Filled.Person),
}
