package com.santiagoruiz.buscamascota.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Rutas de navegación type-safe (Navigation Compose + kotlinx.serialization).
 *
 * Estructura: el grafo raíz arranca en [SplashRoute] y deriva a uno de dos
 * subgrafos anidados: [AuthGraphRoute] (sin sesión) o [MainGraphRoute] (con
 * sesión). En esta fase la decisión es un placeholder; la lógica real de
 * sesión se cablea en la Fase 2 (autenticación).
 */

// --- Grafo raíz ---
@Serializable
object SplashRoute

@Serializable
object AuthGraphRoute

@Serializable
object MainGraphRoute

// --- Subgrafo de autenticación ---
@Serializable
object SignInRoute

@Serializable
object SignUpRoute

// --- Pestañas del subgrafo principal (bottom navigation) ---
@Serializable
object FeedRoute

@Serializable
object AlertsRoute

@Serializable
object SearchRoute

@Serializable
object ProfileRoute

// --- Destinos apilados sobre el grafo principal ---
@Serializable
object CreateReportRoute

@Serializable
data class ReportDetailRoute(val reportId: String)
