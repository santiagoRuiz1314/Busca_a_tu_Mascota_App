package com.santiagoruiz.buscamascota.ui.auth.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.ui.common.components.AppTextField
import com.santiagoruiz.buscamascota.ui.common.components.BrandPawAvatar
import com.santiagoruiz.buscamascota.ui.common.components.PrimaryButton
import com.santiagoruiz.buscamascota.ui.common.components.SecondaryButton
import com.santiagoruiz.buscamascota.ui.common.components.comingSoon

@Composable
fun SignInScreen(
    onSignedIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is SignInUiState.Success) onSignedIn()
    }

    val submitting = uiState is SignInUiState.Submitting
    val errorMessage = (uiState as? SignInUiState.Error)?.message

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BrandPawAvatar()

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = "Bienvenido de vuelta a BuscaMascota",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )

        AppTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            placeholder = "Email o celular",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email,
            isError = errorMessage != null,
            modifier = Modifier.padding(top = 28.dp),
        )
        AppTextField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            placeholder = "Contraseña",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isError = errorMessage != null,
            modifier = Modifier.padding(top = 12.dp),
        )

        Text(
            text = "¿Olvidaste tu contraseña?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
                .comingSoon(),
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
        }

        PrimaryButton(
            text = "INICIAR SESIÓN",
            onClick = { viewModel.signIn(email, password) },
            loading = submitting,
            modifier = Modifier.padding(top = 24.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 20.dp),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "o",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        // Sin lógica de Google Sign-In: se muestra deshabilitado.
        Box(modifier = Modifier.comingSoon()) {
            SecondaryButton(
                text = "Continuar con Google",
                onClick = {},
                leadingIcon = Icons.Filled.Pets,
            )
        }

        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "¿No tienes cuenta? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Regístrate aquí",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(enabled = !submitting, onClick = onNavigateToSignUp),
            )
        }
    }
}
