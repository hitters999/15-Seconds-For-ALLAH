package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.PointsEarnedEvent
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.Emerald
import com.example.ui.theme.Gold
import com.example.ui.theme.SoftGold
import com.example.ui.theme.UrduFontFamily
import com.example.ui.theme.UrduNastaliqFontFamily

/**
 * Glorious, high-contrast Islamic Points & Hasanat Celebration Dialog.
 * Shown instantly whenever a 15-second moment is completed to confirm points are awarded.
 */
@Composable
fun PointsCelebrationDialog(
  event: PointsEarnedEvent,
  onDismiss: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_star")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "star_scale"
  )

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF041F1A)),
      border = BorderStroke(2.dp, Gold),
      elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(
                Color(0xFF021612),
                Color(0xFF06332B),
                Color(0xFF021612)
              )
            )
          )
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Glowing Golden Cup Medal
        Box(
          modifier = Modifier
            .size(76.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  Gold.copy(alpha = 0.4f),
                  Color(0xFF0A443A),
                  Color.Transparent
                )
              )
            )
            .border(2.dp, Gold, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = SoftGold,
            modifier = Modifier.size(40.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Points Pill
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Gold,
          border = BorderStroke(1.dp, SoftGold)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color(0xFF1B1305), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "+${event.pointsAwarded} حسنات / پوائنٹس شامل ہوئے!",
              fontFamily = UrduFontFamily,
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1B1305)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dhikr Calligraphy / Transliteration
        Text(
          text = "مَا شَاءَ اللَّهُ لَا قُوَّةَ إِلَّا بِاللَّهِ",
          fontFamily = ArabicFontFamily,
          fontSize = 19.sp,
          fontWeight = FontWeight.Bold,
          color = SoftGold,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "آپ نے ۱۵ سیکنڈز کا ذکر کامیابی سے مکمل کر لیا ہے۔",
          fontFamily = UrduFontFamily,
          fontSize = 13.sp,
          color = Color.White.copy(alpha = 0.9f),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Updated Stats Capsule
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xFF0A2B24),
          border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "مجموعی پوائنٹس",
                fontFamily = UrduFontFamily,
                fontSize = 11.5.sp,
                color = SoftGold.copy(alpha = 0.8f)
              )
              Text(
                text = "${event.totalScore} pts",
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SoftGold
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "روحانی درجہ",
                fontFamily = UrduFontFamily,
                fontSize = 11.5.sp,
                color = SoftGold.copy(alpha = 0.8f)
              )
              Text(
                text = event.spiritualRank,
                fontFamily = UrduFontFamily,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(
            containerColor = Gold,
            contentColor = Color(0xFF1B1305)
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
          Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF1B1305), modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "الحمد للہ • قبول فرمائیں",
            fontFamily = UrduFontFamily,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B1305)
          )
        }
      }
    }
  }
}
