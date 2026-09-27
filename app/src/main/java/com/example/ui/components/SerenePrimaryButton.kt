package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal

@Composable
fun SerenePrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(
      containerColor = InkTeal,
      contentColor = Color.White
    ),
    border = BorderStroke(1.dp, BronzeGoldLight),
    shape = RoundedCornerShape(14.dp),
    modifier = modifier
      .fillMaxWidth()
      .height(50.dp)
  ) {
    Text(
      text = text,
      fontFamily = FontFamily.SansSerif,
      fontSize = 13.5.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
  }
}
