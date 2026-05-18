package com.santiagoruiz.buscamascota.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Tokens de color del sistema de diseño de BuscaMascota (extraídos del
 * mockup en Figma). Nombrados de forma semántica por uso, no por color.
 * El mapeo a roles Material 3 y a los colores extendidos está en Theme.kt.
 */

// --- Marca (indigo) ---
val BrandPrimary = Color(0xFF5B4FCF)        // FAB (+), splash, logo, links activos
val BrandPrimaryDeep = Color(0xFF4338CA)    // acento más profundo
val BrandSecondary = Color(0xFF635295)      // variante intermedia
val OnBrand = Color(0xFFFFFFFF)

// --- Acento / CTA (amber) ---
val AccentCta = Color(0xFFF0A500)           // botones primarios de acción
val OnAccentCta = Color(0xFF1A1535)         // texto sobre amber (contraste alto)

// --- Texto ---
val TextPrimary = Color(0xFF1A1535)         // títulos y texto principal
val TextSecondary = Color(0xFF6B638A)       // texto/íconos secundarios
val TextNeutral = Color(0xFF494550)         // texto secundario neutro

// --- Estado / reportes ---
val StatusLost = Color(0xFFD32F2F)          // tag "Perdido"
val StatusAbuse = Color(0xFFE65100)         // tag "Maltrato"
val StatusResolved = Color(0xFF2E7D32)      // tag "Encontrado"
val InfoLink = Color(0xFF1565C0)            // links de referencia, marker de mapa

// --- Superficies y fondos ---
val SurfaceDefault = Color(0xFFFFFFFF)      // cards, inputs, navegación
val BackgroundDefault = Color(0xFFF7F6FB)   // fondo principal de la app
val BackgroundCool = Color(0xFFF1F5F9)      // fondo alterno (tono frío)
val BackgroundCoolLight = Color(0xFFF8FAFC) // fondo ultra claro
val SurfaceSelected = Color(0xFFEEF2FF)     // estados hover/selected
val SurfaceChip = Color(0xFFEDE9FF)         // fondos suaves de chips

// --- Grises ---
val DividerNeutral = Color(0xFFE2E8F0)      // dividers, separadores
val IconInactive = Color(0xFF94A3B8)        // íconos inactivos, placeholders
val BorderDefault = Color(0xFFCAC4D1)       // border de inputs/cards/secciones

// --- Tonos derivados para modo oscuro (el mockup es solo claro) ---
val BrandPrimaryDarkMode = Color(0xFFC3BBF2)
val OnBrandDarkMode = Color(0xFF241B52)
val TextPrimaryDarkMode = Color(0xFFECE9F5)
val SurfaceDarkMode = Color(0xFF1C1A26)
val BackgroundDarkMode = Color(0xFF14121F)
val SurfaceVariantDarkMode = Color(0xFF2E2A3C)
val OnSurfaceVariantDarkMode = Color(0xFFC7C0D6)
val OutlineDarkMode = Color(0xFF6B638A)
