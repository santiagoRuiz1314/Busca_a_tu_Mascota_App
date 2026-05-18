package com.santiagoruiz.buscamascota

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application raíz de la app. Punto de entrada de la inyección de
 * dependencias con Hilt: genera el componente de aplicación que alimenta
 * a Activities, ViewModels y demás puntos de inyección.
 */
@HiltAndroidApp
class BuscaMascotaApp : Application()
