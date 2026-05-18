package com.santiagoruiz.buscamascota.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.ui.common.format.displayName
import com.santiagoruiz.buscamascota.ui.theme.appColors

/** Chip de color con el tipo de reporte (perdido / avistamiento / maltrato). */
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
        text = type.displayName(),
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
