package com.santiagoruiz.buscamascota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.santiagoruiz.buscamascota.ui.navigation.AppNavGraph
import com.santiagoruiz.buscamascota.ui.theme.BuscaMascotaTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity de la app (single-activity + Compose). Hospeda el grafo de
 * navegación raíz. `@AndroidEntryPoint` habilita la inyección de Hilt en los
 * ViewModels obtenidos dentro del árbol de Compose.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuscaMascotaTheme {
                AppNavGraph()
            }
        }
    }
}
