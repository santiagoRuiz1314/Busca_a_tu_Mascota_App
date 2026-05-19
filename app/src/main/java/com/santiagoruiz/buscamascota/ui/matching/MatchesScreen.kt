package com.santiagoruiz.buscamascota.ui.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.domain.model.VisualMatch
import com.santiagoruiz.buscamascota.ui.common.components.AppTopBar
import com.santiagoruiz.buscamascota.ui.common.components.ReportCard
import com.santiagoruiz.buscamascota.ui.theme.appColors
import kotlin.math.roundToInt

/**
 * Coincidencias visuales del reporte: avistamientos activos en la zona
 * ordenados por parecido (similitud coseno de los embeddings). Tocar uno
 * abre su detalle para verificar y contactar.
 */
@Composable
fun MatchesScreen(
    onBack: () -> Unit,
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MatchesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppTopBar(title = "Posibles coincidencias", onBack = onBack)

        when (val s = state) {
            MatchesUiState.Loading -> CenteredBox {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            MatchesUiState.Empty -> CenteredBox {
                CenteredMessage(
                    "Aún no hay avistamientos parecidos en la zona. " +
                        "Revisa de nuevo más tarde.",
                )
            }

            is MatchesUiState.Error -> CenteredBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CenteredMessage(s.message)
                    TextButton(
                        onClick = viewModel::load,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            is MatchesUiState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(s.matches, key = { it.report.id }) { match ->
                    MatchItem(match = match, onClick = { onOpenReport(match.report.id) })
                }
            }
        }
    }
}

@Composable
private fun MatchItem(match: VisualMatch, onClick: () -> Unit) {
    Column {
        val percent = (match.similarity.coerceIn(0f, 1f) * 100).roundToInt()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 6.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.appColors.infoLink.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.appColors.infoLink,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "  Parecido: $percent%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.appColors.infoLink,
            )
        }
        ReportCard(report = match.report, onClick = onClick)
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
