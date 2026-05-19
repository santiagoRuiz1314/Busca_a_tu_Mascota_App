package com.santiagoruiz.buscamascota.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Campaign
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.domain.model.UserProfile
import com.santiagoruiz.buscamascota.ui.common.ReportListUiState
import com.santiagoruiz.buscamascota.ui.common.components.AppTopBar
import com.santiagoruiz.buscamascota.ui.common.components.Base64Image
import com.santiagoruiz.buscamascota.ui.common.components.ReportCardCompact
import com.santiagoruiz.buscamascota.ui.common.components.SectionHeader
import com.santiagoruiz.buscamascota.ui.common.components.StatCard
import com.santiagoruiz.buscamascota.ui.theme.appColors

@Composable
fun ProfileScreen(
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
    onEditProfile: () -> Unit = {},
    onSeeAllReports: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val myReports by viewModel.myReports.collectAsState()

    when (val s = state) {
        ProfileUiState.Loading -> CenteredBox {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        is ProfileUiState.Error -> CenteredBox {
            Text(
                text = s.message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        is ProfileUiState.Success -> ProfileContent(
            profile = s.profile,
            myReports = myReports,
            onEditProfile = onEditProfile,
            onSeeAllReports = onSeeAllReports,
            onOpenReport = onOpenReport,
            modifier = modifier,
        )
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    myReports: ReportListUiState,
    onEditProfile: () -> Unit,
    onSeeAllReports: () -> Unit,
    onOpenReport: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val reportCount = (myReports as? ReportListUiState.Success)?.reports?.size ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        AppTopBar(title = "Mi Perfil", centered = true) {
            IconButton(onClick = onEditProfile) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar perfil",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Avatar(photoBase64 = profile.photoBase64, fallbackName = profile.displayName)
            Text(
                text = profile.displayName.ifBlank { "Sin nombre" },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.appColors.textSecondary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                icon = Icons.Filled.Campaign,
                value = reportCount.toString(),
                label = "Reportes",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f),
            )
            // "Ayudados" y "Puntos" no están en el modelo de datos.
            StatCard(
                icon = Icons.Filled.Favorite,
                value = "—",
                label = "Ayudados",
                tint = MaterialTheme.colorScheme.primary,
                enabled = false,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                icon = Icons.Filled.EmojiEvents,
                value = "—",
                label = "Puntos",
                tint = MaterialTheme.appColors.statusResolved,
                enabled = false,
                modifier = Modifier.weight(1f),
            )
        }

        SectionHeader(
            title = "Mis Reportes Activos",
            actionText = "Ver todos",
            onActionClick = onSeeAllReports,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (myReports) {
                ReportListUiState.Loading -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                )

                ReportListUiState.Empty -> Text(
                    text = "Aún no has creado reportes.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                is ReportListUiState.Error -> Text(
                    text = myReports.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                is ReportListUiState.Success -> myReports.reports.take(3).forEach { report ->
                    ReportCardCompact(report = report, onClick = { onOpenReport(report.id) })
                }
            }
        }
    }
}

@Composable
private fun Avatar(photoBase64: String?, fallbackName: String) {
    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (photoBase64?.isNotBlank() == true) {
            Base64Image(
                base64 = photoBase64,
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = fallbackName.trim().firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
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
