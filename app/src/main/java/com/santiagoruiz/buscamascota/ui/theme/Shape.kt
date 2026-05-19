package com.santiagoruiz.buscamascota.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Lenguaje de formas "Rounded" del diseño de Stitch:
 *  - badges/tags : 4dp  (extraSmall)
 *  - chips/menús : 8dp  (small)
 *  - inputs      : 12dp (medium)
 *  - cards       : 16dp (large)
 *  - hojas/modal : 28dp (extraLarge)
 *
 * Los botones CTA usan forma de píldora (CircleShape) directamente en
 * `AppButton`, no se modelan aquí.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
