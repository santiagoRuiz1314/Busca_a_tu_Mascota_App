package com.santiagoruiz.buscamascota.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.santiagoruiz.buscamascota.ui.alerts.AlertsScreen
import com.santiagoruiz.buscamascota.ui.detail.ReportDetailScreen
import com.santiagoruiz.buscamascota.ui.feed.FeedScreen
import com.santiagoruiz.buscamascota.ui.map.MapScreen
import com.santiagoruiz.buscamascota.ui.matching.MatchesScreen
import com.santiagoruiz.buscamascota.ui.profile.EditProfileScreen
import com.santiagoruiz.buscamascota.ui.profile.ProfileScreen
import com.santiagoruiz.buscamascota.ui.report.CreateReportScreen
import com.santiagoruiz.buscamascota.ui.search.SearchScreen

/**
 * Grafo principal: [Scaffold] con barra inferior personalizada (4 pestañas
 * + FAB central de "Nuevo Reporte") y un [NavHost] interno con preservación
 * de estado por pestaña.
 *
 * El cierre de sesión y la redirección al flujo de auth los maneja de forma
 * reactiva el observador de sesión del grafo raíz.
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val innerNav = rememberNavController()
    val backStackEntry by innerNav.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    fun switchTab(route: Any) {
        innerNav.navigate(route) {
            popUpTo(innerNav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // El bottom bar solo se muestra en las pestañas, no en pantallas apiladas.
    val isTab = BottomNavItem.entries.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (isTab) {
                AppBottomBar(
                    isSelected = { route ->
                        currentDestination?.hierarchy?.any {
                            it.hasRoute(route::class)
                        } == true
                    },
                    onSelect = ::switchTab,
                    onCreateReport = { innerNav.navigate(CreateReportRoute) },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = innerNav,
            startDestination = FeedRoute,
            modifier = Modifier.padding(innerPadding),
        ) {
            val openReport: (String) -> Unit = { id ->
                innerNav.navigate(ReportDetailRoute(id))
            }
            composable<FeedRoute> { FeedScreen(onOpenReport = openReport) }
            composable<MapRoute> { MapScreen(onOpenReport = openReport) }
            composable<AlertsRoute> { AlertsScreen(onOpenReport = openReport) }
            composable<ProfileRoute> {
                ProfileScreen(
                    onOpenReport = openReport,
                    onEditProfile = { innerNav.navigate(EditProfileRoute) },
                    onSeeAllReports = { innerNav.navigate(SearchRoute) },
                )
            }
            composable<SearchRoute> {
                SearchScreen(
                    onOpenMatches = { id -> innerNav.navigate(MatchesRoute(id)) },
                    onBack = { innerNav.popBackStack() },
                    onReportSighting = { innerNav.navigate(CreateReportRoute) },
                )
            }
            composable<EditProfileRoute> {
                EditProfileScreen(onBack = { innerNav.popBackStack() })
            }
            composable<CreateReportRoute> {
                CreateReportScreen(onClose = { innerNav.popBackStack() })
            }
            composable<ReportDetailRoute> {
                ReportDetailScreen(onBack = { innerNav.popBackStack() })
            }
            composable<MatchesRoute> {
                MatchesScreen(
                    onBack = { innerNav.popBackStack() },
                    onOpenReport = openReport,
                )
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    isSelected: (Any) -> Boolean,
    onSelect: (Any) -> Unit,
    onCreateReport: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            BottomTab(BottomNavItem.FEED, isSelected, onSelect, Modifier.weight(1f))
            BottomTab(BottomNavItem.MAP, isSelected, onSelect, Modifier.weight(1f))

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onCreateReport),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Nuevo reporte",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }

            BottomTab(BottomNavItem.ALERTS, isSelected, onSelect, Modifier.weight(1f))
            BottomTab(BottomNavItem.PROFILE, isSelected, onSelect, Modifier.weight(1f))
        }
    }
}

@Composable
private fun BottomTab(
    item: BottomNavItem,
    isSelected: (Any) -> Boolean,
    onSelect: (Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = isSelected(item.route)
    val color = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = modifier.clickable { onSelect(item.route) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TabIcon(item.icon, color)
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun TabIcon(icon: ImageVector, color: androidx.compose.ui.graphics.Color) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(24.dp),
    )
}
