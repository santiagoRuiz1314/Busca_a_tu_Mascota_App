package com.santiagoruiz.buscamascota.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.ui.common.format.displayName
import com.santiagoruiz.buscamascota.ui.theme.appColors

/**
 * Badge sólido con el tipo de reporte. Colores semánticos del diseño:
 * Perdido = rojo, Avistamiento = azul, Maltrato = naranja. Texto blanco,
 * forma de tag (radio 6dp) según el sistema de Stitch.
 */
@Composable
fun ReportTypeBadge(
    type: ReportType,
    modifier: Modifier = Modifier,
) {
    val color = when (type) {
        ReportType.LOST -> MaterialTheme.appColors.statusLost
        ReportType.SIGHTING -> MaterialTheme.appColors.statusSighting
        ReportType.ABUSE -> MaterialTheme.appColors.statusAbuse
    }
    Text(
        text = type.displayName().uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 5.dp),
    )
}
