package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ParchmentSurface

@Composable
fun ParchmentBackground(
  modifier: Modifier = Modifier,
  showMadinahBackdrop: Boolean = true,
  content: @Composable () -> Unit
) {
  val isDark = isSystemInDarkTheme()
  val overlayColor = if (isDark) {
    DarkBackground.copy(alpha = 0.92f) // Clean nocturnal deep slate overlay
  } else {
    ParchmentSurface.copy(alpha = 0.88f) // Radiant warm parchment overlay
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(if (isDark) DarkBackground else ParchmentSurface)
  ) {
    if (showMadinahBackdrop) {
      // Subtle, serene, atmospheric Madinah Munawwarah / Masjid an-Nabawi backdrop
      Image(
        painter = painterResource(id = R.drawable.img_madinah_bg),
        contentDescription = "Madinah Munawwarah Background",
        contentScale = ContentScale.Crop,
        alpha = if (isDark) 0.15f else 0.20f,
        modifier = Modifier.fillMaxSize()
      )
    }

    // Soft parchment tone layer for crystal-clear text readability
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(overlayColor)
    )

    content()
  }
}
