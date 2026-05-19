package com.santiagoruiz.buscamascota.ui.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.santiagoruiz.buscamascota.ui.common.components.AppTextField
import com.santiagoruiz.buscamascota.ui.common.components.BrandPawAvatar
import com.santiagoruiz.buscamascota.ui.common.components.PrimaryButton

@Composable
fun SignUpScreen(
    onSignedUp: () -> Unit,
    onBackToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is SignUpUiState.Success) onSignedUp()
    }

    val submitting = uiState is SignUpUiState.Submitting
    val errorMessage = (uiState as? SignUpUiState.Error)?.message

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
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = "Únete a la comunidad BuscaMascota",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )

        AppTextField(
            value = name,
            onValueChange = { name = it; viewModel.clearError() },
            placeholder = "Nombre completo",
            leadingIcon = Icons.Filled.Person,
            isError = errorMessage != null,
            modifier = Modifier.padding(top = 28.dp),
        )
        AppTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            placeholder = "Email o celular",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email,
            isError = errorMessage != null,
            modifier = Modifier.padding(top = 12.dp),
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
            text = "CREAR CUENTA",
            onClick = { viewModel.signUp(name, email, password) },
            loading = submitting,
            modifier = Modifier.padding(top = 24.dp),
        )

        Row(
            modifier = Modifier.padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Inicia sesión",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(enabled = !submitting, onClick = onBackToSignIn),
            )
        }
    }
}
