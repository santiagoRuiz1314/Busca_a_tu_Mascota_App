package com.santiagoruiz.buscamascota.ui.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.domain.model.ReportType
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import com.santiagoruiz.buscamascota.ui.common.components.AppTopBar
import com.santiagoruiz.buscamascota.ui.common.components.comingSoon
import com.santiagoruiz.buscamascota.ui.common.format.relativeTime
import com.santiagoruiz.buscamascota.ui.theme.appColors

/** Alertas: animales perdidos y casos de maltrato activos. */
@Composable
fun AlertsScreen(
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlertsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppTopBar(title = "Alertas") {
            // "Marcar leídas" sin lógica de notificaciones: deshabilitado.
            Text(
                text = "Marcar leídas",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .comingSoon(),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            AlertTab(text = "Alertas", selected = true)
            // Pestaña "Consejos de Cuidado" fuera de alcance: inerte.
            Box(modifier = Modifier.comingSoon()) {
                AlertTab(text = "Consejos de Cuidado", selected = false)
            }
        }

        when (state) {
            ReportListUiState.Loading -> Centered {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            ReportListUiState.Empty -> Centered {
                Message("No hay alertas activas por ahora.")
            }

            is ReportListUiState.Error -> Centered {
                Message((state as ReportListUiState.Error).message)
            }

            is ReportListUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    (state as ReportListUiState.Success).reports,
                    key = { it.id },
                ) { report ->
                    AlertRow(report = report, onClick = { onOpenReport(report.id) })
                }
            }
        }
    }
}

@Composable
private fun AlertTab(text: String, selected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 12.dp),
        )
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(if (selected) 48.dp else 0.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

private fun Report.alertTitle(): String {
    val name = animal.name?.takeIf { it.isNotBlank() }
        ?: animal.species.replaceFirstChar { it.uppercase() }
    return when (type) {
        ReportType.LOST -> "Mascota perdida: $name"
        ReportType.ABUSE -> "Alerta de maltrato"
        ReportType.SIGHTING -> "Avistamiento: $name"
    }
}

@Composable
private fun AlertRow(report: Report, onClick: () -> Unit) {
    val color = when (report.type) {
        ReportType.LOST -> MaterialTheme.appColors.statusLost
        ReportType.ABUSE -> MaterialTheme.appColors.statusAbuse
        ReportType.SIGHTING -> MaterialTheme.appColors.statusSighting
    }
    val icon: ImageVector = when (report.type) {
        ReportType.ABUSE -> Icons.Filled.Warning
        else -> Icons.Filled.Pets
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.alertTitle(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = relativeTime(report.createdAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = MaterialTheme.appColors.iconInactive,
            )
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun Message(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
