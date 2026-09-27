package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val ArabicFontFamily = try {
  FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri_quran, FontWeight.Bold)
  )
} catch (_: Exception) {
  FontFamily.Serif
}

val QuranArabicFontFamily = try {
  FontFamily(
    Font(R.font.amiri_quran, FontWeight.Normal)
  )
} catch (_: Exception) {
  ArabicFontFamily
}

val UrduFontFamily = try {
  FontFamily(
    Font(R.font.noto_nastaliq_urdu, FontWeight.Normal)
  )
} catch (_: Exception) {
  FontFamily.Serif
}

val Typography = Typography(
  bodyLarge = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
  ),
  titleLarge = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
  ),
  labelSmall = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
  )
)
