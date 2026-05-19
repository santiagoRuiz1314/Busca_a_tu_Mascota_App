package com.santiagoruiz.buscamascota.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Tokens de color del sistema de diseño de BuscaMascota (extraídos del
 * proyecto de Stitch "BuscaMascota — Premium Indigo / Community Pet Rescue").
 * Nombrados de forma semántica por uso, no por color. El mapeo a roles
 * Material 3 y a los colores extendidos está en Theme.kt.
 */

// --- Marca (indigo) ---
val BrandPrimary = Color(0xFF635295)        // títulos, top bars, splash, links activos
val BrandPrimaryDeep = Color(0xFF4C3C7D)    // acento más profundo (on-primary-fixed-variant)
val BrandSecondary = Color(0xFF7C6BB0)      // contenedor/FAB lavanda más claro
val OnBrand = Color(0xFFFFFFFF)

// --- Acento / CTA (amber) ---
val AccentCta = Color(0xFFF5C842)           // botones primarios de acción (pill)
val OnAccentCta = Color(0xFF1C1B1F)         // texto sobre amber (negro, WCAG AA)

// --- Texto ---
val TextPrimary = Color(0xFF1C1B1F)         // títulos y texto principal
val TextSecondary = Color(0xFF6B638A)       // texto/íconos secundarios (lavanda apagado)
val TextNeutral = Color(0xFF494550)         // texto secundario neutro (on-surface-variant)

// --- Estado / reportes ---
val StatusLost = Color(0xFFBA1A1A)          // tag "Perdido" (rojo, urgencia)
val StatusAbuse = Color(0xFFE65100)         // tag "Maltrato" (naranja, atención)
val StatusResolved = Color(0xFF2E7D32)      // estado "Resuelto" (verde)
val StatusSick = Color(0xFF00897B)          // categoría "Enfermo" (teal-verde, deshabilitada)
val InfoLink = Color(0xFF1565C0)            // "Encontrado"/avistamiento (azul), links de referencia

// --- Superficies y fondos ---
val SurfaceDefault = Color(0xFFFFFFFF)      // cards, inputs, navegación
val BackgroundDefault = Color(0xFFFDF8FE)   // fondo principal de la app
val BackgroundCool = Color(0xFFF2ECF3)      // fondo alterno (surface-container)
val BackgroundCoolLight = Color(0xFFF7F2F8) // fondo ultra claro (surface-container-low)
val SurfaceSelected = Color(0xFFECE6ED)     // estados hover/selected (surface-container-high)
val SurfaceChip = Color(0xFFE8DDFF)         // fondos suaves de chips (primary-fixed)

// --- Grises ---
val DividerNeutral = Color(0xFFE6E1E7)      // dividers, separadores (surface-variant)
val IconInactive = Color(0xFF7A7581)        // íconos inactivos, placeholders (outline)
val BorderDefault = Color(0xFFCAC4D1)       // border de inputs/cards/secciones (outline-variant)

// --- Tonos derivados para modo oscuro (el diseño es solo claro) ---
val BrandPrimaryDarkMode = Color(0xFFCEBDFF)
val OnBrandDarkMode = Color(0xFF200C4F)
val TextPrimaryDarkMode = Color(0xFFECE9F5)
val SurfaceDarkMode = Color(0xFF1C1A26)
val BackgroundDarkMode = Color(0xFF14121F)
val SurfaceVariantDarkMode = Color(0xFF2E2A3C)
val OnSurfaceVariantDarkMode = Color(0xFFC7C0D6)
val OutlineDarkMode = Color(0xFF6B638A)
