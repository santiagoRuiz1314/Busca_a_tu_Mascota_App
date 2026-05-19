package com.santiagoruiz.buscamascota.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Esquema de color Material 3 de marca. `dynamicColor` desactivado: la app
 * debe ser fiel al mockup, no al wallpaper del dispositivo.
 */
private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = OnBrand,
    primaryContainer = SurfaceChip,
    onPrimaryContainer = TextPrimary,
    secondary = BrandSecondary,
    onSecondary = OnBrand,
    secondaryContainer = SurfaceSelected,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentCta,
    onTertiary = OnAccentCta,
    tertiaryContainer = AccentCta,
    onTertiaryContainer = OnAccentCta,
    background = BackgroundDefault,
    onBackground = TextPrimary,
    surface = SurfaceDefault,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundCool,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = SurfaceDefault,
    surfaceContainerLow = BackgroundCoolLight,
    surfaceContainer = BackgroundDefault,
    surfaceContainerHigh = SurfaceSelected,
    surfaceContainerHighest = SurfaceChip,
    outline = BorderDefault,
    outlineVariant = DividerNeutral,
    error = StatusLost,
    onError = OnBrand,
    scrim = Color(0xFF000000),
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimaryDarkMode,
    onPrimary = OnBrandDarkMode,
    primaryContainer = BrandPrimaryDeep,
    onPrimaryContainer = TextPrimaryDarkMode,
    secondary = BrandSecondary,
    onSecondary = OnBrand,
    tertiary = AccentCta,
    onTertiary = OnAccentCta,
    background = BackgroundDarkMode,
    onBackground = TextPrimaryDarkMode,
    surface = SurfaceDarkMode,
    onSurface = TextPrimaryDarkMode,
    surfaceVariant = SurfaceVariantDarkMode,
    onSurfaceVariant = OnSurfaceVariantDarkMode,
    outline = OutlineDarkMode,
    outlineVariant = SurfaceVariantDarkMode,
    error = StatusLost,
    onError = OnBrand,
)

/**
 * Colores semánticos por uso que Material 3 no modela como rol propio
 * (CTA amber, colores de estado de reporte, bordes/superficies de marca).
 * Se consumen vía `MaterialTheme.appColors`.
 */
data class BuscaMascotaColors(
    val primaryAction: Color,
    val onPrimaryAction: Color,
    val statusLost: Color,
    val statusSighting: Color,
    val statusAbuse: Color,
    val statusResolved: Color,
    val statusSick: Color,
    val infoLink: Color,
    val border: Color,
    val divider: Color,
    val iconInactive: Color,
    val textSecondary: Color,
    val textNeutral: Color,
    val surfaceSelected: Color,
    val surfaceChip: Color,
    val backgroundCool: Color,
    val backgroundCoolLight: Color,
)

private val LightAppColors = BuscaMascotaColors(
    primaryAction = AccentCta,
    onPrimaryAction = OnAccentCta,
    statusLost = StatusLost,
    statusSighting = InfoLink,
    statusAbuse = StatusAbuse,
    statusResolved = StatusResolved,
    statusSick = StatusSick,
    infoLink = InfoLink,
    border = BorderDefault,
    divider = DividerNeutral,
    iconInactive = IconInactive,
    textSecondary = TextSecondary,
    textNeutral = TextNeutral,
    surfaceSelected = SurfaceSelected,
    surfaceChip = SurfaceChip,
    backgroundCool = BackgroundCool,
    backgroundCoolLight = BackgroundCoolLight,
)

private val DarkAppColors = LightAppColors.copy(
    onPrimaryAction = OnAccentCta,
    textSecondary = OnSurfaceVariantDarkMode,
    border = OutlineDarkMode,
    divider = SurfaceVariantDarkMode,
    surfaceSelected = SurfaceVariantDarkMode,
    surfaceChip = BrandPrimaryDeep,
    backgroundCool = SurfaceDarkMode,
    backgroundCoolLight = SurfaceDarkMode,
)

private val LocalBuscaMascotaColors = staticCompositionLocalOf { LightAppColors }

/** Acceso a los colores semánticos extendidos: `MaterialTheme.appColors`. */
val MaterialTheme.appColors: BuscaMascotaColors
    @Composable
    @ReadOnlyComposable
    get() = LocalBuscaMascotaColors.current

@Composable
fun BuscaMascotaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    CompositionLocalProvider(LocalBuscaMascotaColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}
