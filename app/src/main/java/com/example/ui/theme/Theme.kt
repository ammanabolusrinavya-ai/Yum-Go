package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val AestheticColorScheme = darkColorScheme(
  primary = AestheticPrimary,
  onPrimary = AestheticOnPrimary,
  secondary = AestheticSecondary,
  background = AestheticBackground,
  surface = AestheticSurface,
  surfaceVariant = AestheticSurfaceVariant,
  onBackground = AestheticTextPrimary,
  onSurface = AestheticTextPrimary,
  onSurfaceVariant = AestheticTextSecondary
)

private val AestheticLightColorScheme = lightColorScheme(
  primary = AestheticLightPrimary,
  onPrimary = AestheticLightOnPrimary,
  secondary = AestheticLightSecondary,
  background = AestheticLightBackground,
  surface = AestheticLightSurface,
  surfaceVariant = AestheticLightSurfaceVariant,
  onBackground = AestheticLightTextPrimary,
  onSurface = AestheticLightTextPrimary,
  onSurfaceVariant = AestheticLightTextSecondary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) AestheticColorScheme else AestheticLightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
