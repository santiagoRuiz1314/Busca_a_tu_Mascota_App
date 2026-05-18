package com.santiagoruiz.buscamascota.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Escala tipográfica de BuscaMascota según el mockup de Figma.
 *
 * Familia: Roboto. En Android `FontFamily.Default` ES Roboto, así que no se
 * empaqueta ningún recurso de fuente.
 *
 * Tokens del diseño:
 *  - display : 32 / 40, Bold(700)            → títulos grandes (splash, headers de pantalla)
 *  - h3      : ~18-20, SemiBold(600)         → headers de sección
 *  - body    : 16, Regular(400)              → texto de párrafo
 *  - caption : 12-14, Regular(400)           → labels, timestamps
 *  - button  : 14 / 20, Bold(700), ls 0.35   → botones primarios (uppercase en uso)
 */
private val Roboto = FontFamily.Default

val Typography = Typography(
    // display 32/40/700
    displayLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp,
    ),
    // h3 ~20/600
    titleLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp, letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.sp,
    ),
    // body 16/400
    bodyLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp,
    ),
    // caption 12/400
    bodySmall = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.sp,
    ),
    // button 14/20/700, ls 0.35
    labelLarge = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Bold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.35.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp,
    ),
)
