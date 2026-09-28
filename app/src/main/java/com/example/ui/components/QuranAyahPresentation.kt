package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.QuranCalligraphyFontFamily
import com.example.ui.theme.UrduNastaliqFontFamily

@Composable
fun SacredTextBlock(
  arabicText: String,
  urduText: String,
  englishText: String,
  sourceText: String,
  isQuranic: Boolean,
  isDarkTheme: Boolean = false,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // 1. If it's a Quranic Ayah, show distinct styled Bismillah on top
    if (isQuranic) {
      BismillahCalligraphyHeader(isDarkTheme = isDarkTheme)
      Spacer(modifier = Modifier.height(10.dp))
    }

    // 2. Sacred Arabic Text in Amiri Font (distinct font from Bismillah)
    Text(
      text = arabicText,
      fontFamily = ArabicFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = if (arabicText.length > 80) 22.sp else 26.sp,
      lineHeight = if (arabicText.length > 80) 36.sp else 42.sp,
      color = if (isDarkTheme) Color.White else InkTeal,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 6.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    // 3. Urdu Translation in Authentic Noto Nastaliq Font
    Text(
      text = urduText,
      fontFamily = UrduNastaliqFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 14.5.sp,
      lineHeight = 26.sp,
      color = if (isDarkTheme) Color(0xFFFFE3A3) else Color(0xFF784508),
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
    )

    Spacer(modifier = Modifier.height(6.dp))

    // 4. English Translation
    if (englishText.isNotBlank()) {
      Text(
        text = englishText,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        color = if (isDarkTheme) Color(0xFFC4DCD9) else Color(0xFF4A6864),
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp)
      )
    }

    if (sourceText.isNotBlank()) {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = sourceText,
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontSize = 11.sp,
        color = if (isDarkTheme) Color(0xFF88ADA8) else Color(0xFF73938E),
        textAlign = TextAlign.Center
      )
    }
  }
}
