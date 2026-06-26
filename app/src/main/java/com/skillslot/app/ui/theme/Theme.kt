package com.skillslot.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val SkillSlotColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    inversePrimary = InversePrimary,
    surfaceTint = SurfaceTint,
)

data class SkillSlotBrandColors(
    val glassFill: Color = GlassFill,
    val glassBorderGold: Color = GlassBorderGold,
    val glassBorderPurple: Color = GlassBorderPurple,
    val neonGold: Color = NeonGoldGlow,
    val neonPurple: Color = NeonPurpleGlow,
    val primaryFixed: Color = PrimaryFixed,
)

val LocalSkillSlotBrandColors = staticCompositionLocalOf { SkillSlotBrandColors() }

@Composable
fun SkillSlotTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalSkillSlotBrandColors provides SkillSlotBrandColors()) {
        MaterialTheme(
            colorScheme = SkillSlotColorScheme,
            typography = SkillSlotTypography,
            content = content,
        )
    }
}

object SkillSlotTheme {
    val brandColors: SkillSlotBrandColors
        @Composable get() = LocalSkillSlotBrandColors.current
}
