package com.santiagoruiz.buscamascota.ui.navigation

/**
 * Ítems de la barra de navegación inferior del grafo principal.
 *
 * Los `glyph` son placeholders temporales: los íconos definitivos se
 * incorporan en la pasada de theme/diseño fiel a Figma.
 */
enum class BottomNavItem(
    val route: Any,
    val label: String,
    val glyph: String,
) {
    FEED(FeedRoute, "Inicio", "🏠"),
    ALERTS(AlertsRoute, "Alertas", "🔔"),
    SEARCH(SearchRoute, "Buscar", "🔍"),
    PROFILE(ProfileRoute, "Perfil", "👤"),
}
