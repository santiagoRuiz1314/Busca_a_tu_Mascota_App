package com.santiagoruiz.buscamascota.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.ui.common.format.relativeTime
import com.santiagoruiz.buscamascota.ui.theme.appColors

/** Tarjeta de un reporte en el feed/alertas. Toda la tarjeta es pulsable. */
@Composable
fun ReportCard(
    report: Report,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Base64Image(
            base64 = report.photoBase64,
            contentDescription = "Foto del reporte",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ReportTypeBadge(report.type)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = relativeTime(report.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }

            val title = report.animal.name?.takeIf { it.isNotBlank() }
                ?: report.animal.species.replaceFirstChar { it.uppercase() }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
