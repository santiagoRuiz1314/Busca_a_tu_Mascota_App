package com.santiagoruiz.buscamascota.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.ui.common.components.Base64Image
import com.santiagoruiz.buscamascota.ui.common.components.ReportTypeBadge
import com.santiagoruiz.buscamascota.ui.common.format.displayName
import com.santiagoruiz.buscamascota.ui.common.format.relativeTime
import com.santiagoruiz.buscamascota.ui.theme.appColors

@Composable
fun ReportDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val address by viewModel.address.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
        ) {
            Text("← Volver")
        }

        when (val s = state) {
            ReportDetailUiState.Loading -> CenteredBox {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            is ReportDetailUiState.Error -> CenteredBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = s.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    TextButton(
                        onClick = viewModel::load,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            is ReportDetailUiState.Success -> ReportDetailContent(s.report, address)
        }
    }
}

@Composable
private fun ReportDetailContent(report: Report, address: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Base64Image(
            base64 = report.photoBase64,
            contentDescription = "Foto del reporte",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        )

        Column(modifier = Modifier.padding(24.dp)) {
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
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = "Estado: ${report.status.displayName()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.padding(top = 4.dp),
            )

            DetailSection("Descripción") {
                Text(
                    text = report.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            DetailSection("Animal") {
                InfoRow("Especie", report.animal.species)
                report.animal.breed?.let { InfoRow("Raza", it) }
                report.animal.color?.let { InfoRow("Color", it) }
                report.animal.name?.let { InfoRow("Nombre", it) }
            }

            DetailSection("Ubicación") {
                // Lugar legible (ciudad, país). Las coordenadas solo se
                // muestran como respaldo si el geocoder no resolvió aún o
                // falló (p. ej. sin conexión): el usuario nunca queda sin
                // dato, pero ve un nombre en cuanto está disponible.
                Text(
                    text = address ?: "%.5f, %.5f".format(
                        report.location.latitude,
                        report.location.longitude,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
    )
    content()
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.appColors.textSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
