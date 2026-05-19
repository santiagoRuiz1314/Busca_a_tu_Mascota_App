package com.santiagoruiz.buscamascota.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Escala tipográfica de BuscaMascota según el sistema de diseño de Stitch.
 *
 * Familia: Roboto. En Android `FontFamily.Default` ES Roboto, así que no se
 * empaqueta ningún recurso de fuente.
 *
 * Tokens del diseño (Stitch):
 *  - headline-lg : 32 / 40, Bold(700)   → títulos grandes (splash)
 *  - headline-md : 24 / 32, Bold(700)   → headers de pantalla
 *  - headline-sm : 20 / 28, Bold(700)   → headers de sección
 *  - body-lg     : 18 / 26, Regular(400)
 *  - body-md     : 16 / 24, Regular(400) → texto de párrafo
 *  - body-sm     : 14 / 20, Regular(400)
 *  - label-lg    : 14 / 20, Bold(700)   → botones
 *  - label-md    : 12 / 16, Bold(700)   → labels, badges, timestamps
 */
private val Roboto = FontFamily.Default

val Typography = Typography(
    // headline-lg 32/40/700
    displayLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp,
    ),
    // headline-md 24/32/700
    headlineMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp,
    ),
    // headline-sm 20/28/700
    headlineSmall = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp,
    ),
    // body-lg 18/26/400
    bodyLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.sp,
    ),
    // body-sm 14/20/400
    bodyMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.sp,
    ),
    // label-lg 14/20/700 (botones)
    labelLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.35.sp,
    ),
    // label-md 12/16/700
    labelMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp,
    ),
)
