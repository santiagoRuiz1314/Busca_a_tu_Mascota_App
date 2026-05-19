package com.santiagoruiz.buscamascota.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import com.santiagoruiz.buscamascota.ui.common.components.AppTopBar
import com.santiagoruiz.buscamascota.ui.common.components.PrimaryButton
import com.santiagoruiz.buscamascota.ui.common.components.ReportCardCompact
import com.santiagoruiz.buscamascota.ui.common.components.comingSoon

/**
 * «Mi Actividad»: reportes de mascotas perdidas del usuario. Tocar uno lanza
 * la búsqueda de coincidencias visuales (avistamientos parecidos) — la
 * funcionalidad estrella. La pestaña «Ayudados» no tiene lógica: inerte.
 */
@Composable
fun SearchScreen(
    onOpenMatches: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onReportSighting: () -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppTopBar(title = "Mi Actividad", onBack = onBack) {
            // Ajustes sin lógica: deshabilitado.
            IconButton(onClick = {}, modifier = Modifier.comingSoon()) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ActivityTab(text = "Mis Reportes", selected = true)
            Box(modifier = Modifier.comingSoon()) {
                ActivityTab(text = "Ayudados", selected = false)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (state) {
                ReportListUiState.Loading -> Centered {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                ReportListUiState.Empty -> Centered {
                    Message(
                        "Crea un reporte de mascota perdida para buscar " +
                            "avistamientos que se le parezcan.",
                    )
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
                        ReportCardCompact(
                            report = report,
                            onClick = { onOpenMatches(report.id) },
                        )
                    }
                }
            }
        }

        SightingCta(
            onClick = onReportSighting,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun ActivityTab(text: String, selected: Boolean) {
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

@Composable
private fun SightingCta(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "¿Viste a un peludito?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "Tu reporte puede salvar una vida y devolver la " +
                    "alegría a una familia.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 4.dp),
            )
            PrimaryButton(
                text = "Reportar Avistamiento",
                onClick = onClick,
                modifier = Modifier.padding(top = 12.dp),
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
