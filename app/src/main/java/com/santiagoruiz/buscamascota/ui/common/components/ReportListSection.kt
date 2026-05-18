package com.santiagoruiz.buscamascota.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState

/**
 * Cuerpo reutilizable de una pantalla de lista de reportes: cabecera +
 * estado (cargando / vacío / error / lista). La navegación a detalle se
 * delega vía [onOpenReport] con el id del reporte.
 */
@Composable
fun ReportListSection(
    title: String,
    state: ReportListUiState,
    emptyMessage: String,
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp),
        )
        when (state) {
            ReportListUiState.Loading -> CenteredBox {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            ReportListUiState.Empty -> CenteredBox {
                CenteredMessage(emptyMessage)
            }

            is ReportListUiState.Error -> CenteredBox {
                CenteredMessage(state.message)
            }

            is ReportListUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.reports, key = { it.id }) { report ->
                    ReportCard(report = report, onClick = { onOpenReport(report.id) })
                }
            }
        }
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

@Composable
private fun CenteredMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
