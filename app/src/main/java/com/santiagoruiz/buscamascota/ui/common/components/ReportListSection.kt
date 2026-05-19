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
 * Cuerpo reutilizable de una lista de reportes: cabecera de sección
 * opcional + estado (cargando / vacío / error / lista) con las tarjetas
 * del nuevo sistema de diseño. La navegación a detalle se delega vía
 * [onOpenReport]. [compact] usa la tarjeta tipo lista (miniatura + ›).
 */
@Composable
fun ReportListSection(
    title: String,
    state: ReportListUiState,
    emptyMessage: String,
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showTitle: Boolean = true,
    header: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (showTitle) {
            SectionHeader(
                title = title,
                modifier = Modifier.padding(
                    start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp,
                ),
            )
        }
        header?.invoke()
        when (state) {
            ReportListUiState.Loading -> CenteredBox {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            ReportListUiState.Empty -> CenteredBox { CenteredMessage(emptyMessage) }

            is ReportListUiState.Error -> CenteredBox { CenteredMessage(state.message) }

            is ReportListUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.reports, key = { it.id }) { report ->
                    if (compact) {
                        ReportCardCompact(report, onClick = { onOpenReport(report.id) })
                    } else {
                        ReportCard(report, onClick = { onOpenReport(report.id) })
                    }
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
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}
