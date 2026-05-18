package com.santiagoruiz.buscamascota.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.santiagoruiz.buscamascota.ui.profile.ProfileScreen
import com.santiagoruiz.buscamascota.ui.report.CreateReportScreen
import com.santiagoruiz.buscamascota.ui.search.SearchScreen

/**
 * Grafo principal de la app: un [Scaffold] con barra de navegación inferior
 * (4 pestañas) y FAB para crear reporte. Mantiene su propio [NavHost] interno
 * con preservación de estado por pestaña.
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

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.hasRoute(item.route::class)
                    } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            innerNav.navigate(item.route) {
                                popUpTo(innerNav.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(item.glyph) },
                        label = { Text(item.label) },
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { innerNav.navigate(CreateReportRoute) }) {
                Text("+")
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
            composable<SearchRoute> {
                SearchScreen(
                    onOpenMatches = { id -> innerNav.navigate(MatchesRoute(id)) },
                )
            }
            composable<ProfileRoute> { ProfileScreen() }
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
