package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Master Urdu Nastaliq Font Family with complete font weight mappings
val UrduFontFamily = try {
  FontFamily(
    Font(R.font.noto_nastaliq_urdu, FontWeight.Normal),
    Font(R.font.noto_nastaliq_urdu, FontWeight.Medium),
    Font(R.font.noto_nastaliq_urdu, FontWeight.SemiBold),
    Font(R.font.noto_nastaliq_urdu, FontWeight.Bold),
    Font(R.font.noto_nastaliq, FontWeight.Normal),
    Font(R.font.noto_nastaliq, FontWeight.Bold)
  )
} catch (_: Exception) {
  FontFamily.Serif
}

// Sacred Quran Arabic Font Family
val QuranArabicFontFamily = try {
  FontFamily(
    Font(R.font.amiri_quran, FontWeight.Normal),
    Font(R.font.amiri_quran, FontWeight.Medium),
    Font(R.font.amiri_quran, FontWeight.SemiBold),
    Font(R.font.amiri_quran, FontWeight.Bold),
    Font(R.font.scheherazade_new, FontWeight.Normal),
    Font(R.font.scheherazade_new, FontWeight.Bold)
  )
} catch (_: Exception) {
  FontFamily.Serif
}

// Classical Arabic Calligraphy Font Family
val ArabicFontFamily = try {
  FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri, FontWeight.Medium),
    Font(R.font.amiri, FontWeight.SemiBold),
    Font(R.font.amiri_quran, FontWeight.Bold),
    Font(R.font.scheherazade_new, FontWeight.Normal),
    Font(R.font.scheherazade_new, FontWeight.Bold)
  )
} catch (_: Exception) {
  FontFamily.Serif
}

// Royal Title Font Family for Headings & Scores
val TitleFontFamily = FontFamily.Serif

// Aliases for compatibility
val UrduNastaliqFontFamily = UrduFontFamily
val QuranCalligraphyFontFamily = QuranArabicFontFamily

val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = 0.sp
  ),
  titleLarge = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
  ),
  titleMedium = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.15.sp
  ),
  bodyLarge = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp
  ),
  labelLarge = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
  ),
  labelSmall = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
  )
)
