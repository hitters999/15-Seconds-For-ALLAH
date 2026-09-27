package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.ParchmentSurface

@Composable
fun SplashScreen(
  onContinue: () -> Unit,
  modifier: Modifier = Modifier
) {
  val alphaAnim = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    alphaAnim.animateTo(1f, animationSpec = tween(900))
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
      // Official Brand Logo Emblem
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(140.dp)
          .clip(CircleShape)
          .border(2.5.dp, BronzeGoldLight, CircleShape)
          .background(Color.White)
          .testTag("splash_brand_logo")
      ) {
        Image(
          painter = painterResource(id = R.drawable.app_brand_logo),
          contentDescription = "15 Seconds for Allah Official Logo",
          modifier = Modifier.size(134.dp),
          contentScale = ContentScale.Fit
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      Text(
        text = "15 Seconds for Allah",
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        color = ParchmentSurface,
        textAlign = TextAlign.Center,
        letterSpacing = 0.5.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "۱۵ سیکنڈز اللہ کے لیے",
        fontFamily = FontFamily.Serif,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = BronzeGoldLight,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "A MOMENT, GIVEN FULLY\nصرف پندرہ سیکنڈ، خلوص کے ساتھ",
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.5.sp,
        color = Color(0xFFC7DBD8),
        letterSpacing = 1.2.sp,
        lineHeight = 18.sp,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(52.dp))

      // Button to enter
      Box(
        modifier = Modifier
          .border(1.2.dp, BronzeGoldLight, RoundedCornerShape(24.dp))
          .clip(RoundedCornerShape(24.dp))
          .clickable(onClick = onContinue)
          .background(Color(0x33B8863B))
          .padding(horizontal = 36.dp, vertical = 13.dp)
          .testTag("enter_app_button")
      ) {
        Text(
          text = "شروع کریں • Enter Sanctuary",
          fontFamily = FontFamily.Serif,
          fontSize = 14.5.sp,
          color = ParchmentSurface,
          letterSpacing = 0.8.sp
        )
      }
    }
  }
}
