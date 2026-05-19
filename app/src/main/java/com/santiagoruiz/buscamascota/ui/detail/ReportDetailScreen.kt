package com.santiagoruiz.buscamascota.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.santiagoruiz.buscamascota.domain.model.Report
import com.santiagoruiz.buscamascota.ui.common.components.AppTopBar
import com.santiagoruiz.buscamascota.ui.common.components.Base64Image
import com.santiagoruiz.buscamascota.ui.common.components.PrimaryButton
import com.santiagoruiz.buscamascota.ui.common.components.ReportTypeBadge
import com.santiagoruiz.buscamascota.ui.common.components.SecondaryButton
import com.santiagoruiz.buscamascota.ui.common.components.comingSoon
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppTopBar(title = "Rescate Animal", onBack = onBack) {
            IconButton(onClick = {}, modifier = Modifier.comingSoon()) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        when (val s = state) {
            ReportDetailUiState.Loading -> CenteredBox {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }

            is ReportDetailUiState.Error -> CenteredBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = s.message,
                        style = MaterialTheme.typography.bodyLarge,
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
        Box {
            Base64Image(
                base64 = report.photoBase64,
                contentDescription = "Foto del reporte",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )
            ReportTypeBadge(
                type = report.type,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
            )
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val title = report.animal.name?.takeIf { it.isNotBlank() }
                    ?: report.animal.species.replaceFirstChar { it.uppercase() }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = listOfNotNull(
                            report.animal.species,
                            report.animal.breed,
                        ).joinToString(" · "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.appColors.textSecondary,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                DateChip(report.createdAt)
            }

            Text(
                text = "Estado: ${report.status.displayName()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.appColors.textSecondary,
                modifier = Modifier.padding(top = 8.dp),
            )

            SectionTitle("Descripción")
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            SectionTitle("Última vez visto")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = address ?: "%.5f, %.5f".format(
                        report.location.latitude,
                        report.location.longitude,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            LocationSnippet(
                lat = report.location.latitude,
                lng = report.location.longitude,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(MaterialTheme.shapes.large),
            )

            // Autor + contacto. El modelo solo guarda ownerId: sin datos de
            // contacto, "Contactar" se muestra deshabilitado.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Spacer(Modifier.size(12.dp))
                Text(
                    text = "Autor del reporte",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Box(modifier = Modifier.comingSoon()) {
                    SecondaryButton(
                        text = "Contactar",
                        onClick = {},
                        leadingIcon = Icons.Filled.Phone,
                        modifier = Modifier.size(width = 140.dp, height = 48.dp),
                    )
                }
            }

            // "Tengo información" no tiene lógica de enlace: deshabilitado.
            Box(modifier = Modifier
                .padding(top = 16.dp)
                .comingSoon()) {
                PrimaryButton(text = "Tengo información", onClick = {})
            }
        }
    }
}

@Composable
private fun DateChip(createdAt: Long) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.CalendarMonth,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = relativeTime(createdAt),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun LocationSnippet(lat: Double, lng: Double, modifier: Modifier = Modifier) {
    val target = LatLng(lat, lng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(target, 15f)
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        googleMapOptionsFactory = { GoogleMapOptions().liteMode(true) },
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            scrollGesturesEnabled = false,
            zoomGesturesEnabled = false,
        ),
    ) {
        Marker(state = rememberMarkerState(position = target))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
    )
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
