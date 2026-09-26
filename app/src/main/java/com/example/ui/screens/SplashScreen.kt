package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SereneOutlineButton
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.InkTealDeep
import com.example.ui.theme.ParchmentSurface

@Composable
fun SplashScreen(
  onContinue: () -> Unit,
  modifier: Modifier = Modifier
) {
  val alphaAnim = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    alphaAnim.animateTo(1f, animationSpec = tween(1000))
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(InkTeal)
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(24.dp)
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .alpha(alphaAnim.value)
        .fillMaxWidth()
    ) {
      // Elegant Islamic Geometric / Teardrop Mark from the design mockup
      Box(
        modifier = Modifier
          .size(56.dp)
          .border(
            width = 2.5.dp,
            color = BronzeGoldLight,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomEnd = 28.dp, bottomStart = 0.dp)
          )
          .rotate(45f)
      )

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = "15 Seconds\nfor Allah",
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        color = ParchmentSurface,
        textAlign = TextAlign.Center,
        letterSpacing = 0.5.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "A MOMENT, GIVEN FULLY",
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        color = BronzeGoldLight,
        letterSpacing = 2.5.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(64.dp))

      // Button to enter
      Box(
        modifier = Modifier
          .border(1.2.dp, BronzeGoldLight, RoundedCornerShape(24.dp))
          .clickable(onClick = onContinue)
          .padding(horizontal = 32.dp, vertical = 12.dp)
          .testTag("enter_app_button")
      ) {
        Text(
          text = "Enter Sanctuary",
          fontFamily = FontFamily.Serif,
          fontSize = 14.sp,
          color = ParchmentSurface,
          letterSpacing = 0.8.sp
        )
      }
    }
  }
}
