package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = BronzeGoldLight,
  onPrimary = InkTeal,
  primaryContainer = InkTealLight,
  onPrimaryContainer = BronzeGoldLight,
  secondary = BronzeGold,
  onSecondary = InkTealDeep,
  secondaryContainer = InkTealSoft,
  onSecondaryContainer = ParchmentSurface,
  tertiary = BronzeGoldLight,
  onTertiary = InkTealDeep,
  background = InkTealDeep,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSoft,
  outline = DarkBorder,
  outlineVariant = InkTealSoft
)

private val LightColorScheme = lightColorScheme(
  primary = InkTeal,
  onPrimary = ParchmentSurface,
  primaryContainer = ParchmentSubtle,
  onPrimaryContainer = InkTeal,
  secondary = BronzeGold,
  onSecondary = ParchmentSurface,
  secondaryContainer = BronzeGoldLight,
  onSecondaryContainer = InkTeal,
  tertiary = InkTealLight,
  onTertiary = ParchmentSurface,
  background = ParchmentBg,
  onBackground = TextPrimary,
  surface = ParchmentSurface,
  onSurface = TextPrimary,
  surfaceVariant = ParchmentSubtle,
  onSurfaceVariant = TextSoft,
  outline = ParchmentBorder,
  outlineVariant = ParchmentBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep bespoke serene spiritual aesthetic
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
