package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun BismillahCalligraphyHeader(
  modifier: Modifier = Modifier,
  isDarkTheme: Boolean = false
) {
  val borderColor = if (isDarkTheme) BronzeGoldLight.copy(alpha = 0.6f) else BronzeGold.copy(alpha = 0.5f)
  val textColor = if (isDarkTheme) Color(0xFFFFE8B2) else InkTeal
  val bgColor = if (isDarkTheme) Color(0x33B8863B) else Color(0x15B8863B)

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor,
    border = BorderStroke(1.dp, borderColor),
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = null,
        tint = BronzeGoldLight,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))

      Text(
        text = "۞ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۞",
        fontFamily = ArabicFontFamily,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        textAlign = TextAlign.Center,
        modifier = Modifier.weight(1f)
      )

      Spacer(modifier = Modifier.width(6.dp))
      Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = null,
        tint = BronzeGoldLight,
        modifier = Modifier.size(12.dp)
      )
    }
  }
}
