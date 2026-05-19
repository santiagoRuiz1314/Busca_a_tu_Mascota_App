package com.santiagoruiz.buscamascota.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Marca un control como "sin lógica todavía": lo atenúa y bloquea la
 * interacción, sin ocultarlo. Decisión de alcance: los controles del diseño
 * que no tienen backend se muestran pero deshabilitados.
 */
fun Modifier.comingSoon(active: Boolean = true): Modifier =
    if (!active) this
    else this
        .alpha(0.45f)
        .pointerInput(Unit) { /* consume gestos: control inerte */ }

/** Etiqueta pequeña "Próximamente" para acompañar un control inerte. */
@Composable
fun ComingSoonTag(
    modifier: Modifier = Modifier,
    text: String = "Próximamente",
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}
