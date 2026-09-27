package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.TextSoft
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MechanicalClock15s(
  remainingSeconds: Float,
  totalSeconds: Int = 15,
  isRunning: Boolean,
  isCompleted: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val elapsed = (totalSeconds - remainingSeconds).coerceIn(0f, totalSeconds.toFloat())
  val progressFraction = (elapsed / totalSeconds).coerceIn(0f, 1f)

  // Hand angle: Starts at 12 o'clock (-90 degrees), sweeps 360 degrees or across the 15-second dial
  // In a standard 60-second clock, 15 seconds corresponds to 90 degrees (12 o'clock to 3 o'clock).
  // Or across full dial: sweeps full 360 degrees as 15 seconds progress!
  // Sweeping 360 degrees gives the most dramatic, visible mechanical movement for a 15-second clock.
  val animatedAngle by animateFloatAsState(
    targetValue = (progressFraction * 360f) - 90f,
    animationSpec = tween(durationMillis = 100),
    label = "clockHandAngle"
  )

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(240.dp)
      .shadow(12.dp, CircleShape)
      .clip(CircleShape)
      .background(
        Brush.radialGradient(
          colors = listOf(
            Color(0xFF265A54),
            Color(0xFF133632),
            Color(0xFF0A1C1A)
          )
        )
      )
      .border(
        width = 4.dp,
        brush = Brush.sweepGradient(
          listOf(
            BronzeGoldLight,
            BronzeGold,
            Color(0xFFFFDF9E),
            BronzeGold,
            BronzeGoldLight
          )
        ),
        shape = CircleShape
      )
      .clickable(onClick = onClick)
  ) {
    Canvas(modifier = Modifier.size(220.dp)) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = size.width / 2f

      // 1. Dial Ring
      drawCircle(
        color = Color(0x33B8863B),
        radius = radius - 8.dp.toPx(),
        center = center,
        style = Stroke(width = 2.dp.toPx())
      )

      // 2. 15-Second Active Arc (Glows as user makes progress)
      val sweepAngle = progressFraction * 360f
      drawArc(
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFF81C784),
            Color(0xFFFFD54F),
            BronzeGoldLight
          )
        ),
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(14.dp.toPx(), 14.dp.toPx()),
        size = Size(size.width - 28.dp.toPx(), size.height - 28.dp.toPx()),
        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
      )

      // 3. Dial Tick Marks (60 seconds, with prominent 15s marks)
      val numTicks = 60
      for (i in 0 until numTicks) {
        val angleRad = (i * (360f / numTicks) - 90f) * (PI / 180f)
        val isMajorTick = i % 5 == 0
        val isFifteenMark = i % 15 == 0

        val tickLength = when {
          isFifteenMark -> 14.dp.toPx()
          isMajorTick -> 9.dp.toPx()
          else -> 4.dp.toPx()
        }

        val outerRadius = radius - 12.dp.toPx()
        val innerRadius = outerRadius - tickLength

        val startX = center.x + innerRadius * cos(angleRad).toFloat()
        val startY = center.y + innerRadius * sin(angleRad).toFloat()
        val endX = center.x + outerRadius * cos(angleRad).toFloat()
        val endY = center.y + outerRadius * sin(angleRad).toFloat()

        val tickColor = when {
          isFifteenMark -> BronzeGoldLight
          isMajorTick -> Color(0xFFD6C7A8)
          else -> Color(0x55E5C388)
        }

        drawLine(
          color = tickColor,
          start = Offset(startX, startY),
          end = Offset(endX, endY),
          strokeWidth = if (isFifteenMark) 3.5.dp.toPx() else if (isMajorTick) 2.dp.toPx() else 1.dp.toPx(),
          cap = StrokeCap.Round
        )
      }

      // 4. Moving Hand / Needle ("Ghari ki Sooi")
      val handAngleRad = animatedAngle * (PI / 180f)
      val handLength = radius - 26.dp.toPx()
      val tailLength = 22.dp.toPx()

      val handTipX = center.x + handLength * cos(handAngleRad).toFloat()
      val handTipY = center.y + handLength * sin(handAngleRad).toFloat()

      val handTailX = center.x - tailLength * cos(handAngleRad).toFloat()
      val handTailY = center.y - tailLength * sin(handAngleRad).toFloat()

      // Shadow / glow for the needle
      drawLine(
        color = Color(0x44000000),
        start = Offset(handTailX + 2.dp.toPx(), handTailY + 2.dp.toPx()),
        end = Offset(handTipX + 2.dp.toPx(), handTipY + 2.dp.toPx()),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
      )

      // The Golden Needle Hand
      drawLine(
        brush = Brush.linearGradient(
          colors = listOf(BronzeGoldLight, Color(0xFFFFE082), BronzeGold)
        ),
        start = Offset(handTailX, handTailY),
        end = Offset(handTipX, handTipY),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
      )

      // Arrow point at needle tip
      val arrowAngle1 = handAngleRad + (145f * PI / 180f)
      val arrowAngle2 = handAngleRad - (145f * PI / 180f)
      val arrowSize = 8.dp.toPx()
      drawLine(
        color = BronzeGoldLight,
        start = Offset(handTipX, handTipY),
        end = Offset(
          handTipX + arrowSize * cos(arrowAngle1).toFloat(),
          handTipY + arrowSize * sin(arrowAngle1).toFloat()
        ),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
      )
      drawLine(
        color = BronzeGoldLight,
        start = Offset(handTipX, handTipY),
        end = Offset(
          handTipX + arrowSize * cos(arrowAngle2).toFloat(),
          handTipY + arrowSize * sin(arrowAngle2).toFloat()
        ),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
      )

      // 5. Central Gemstone Jewel Bearing
      // Outer brass bushing
      drawCircle(
        color = BronzeGold,
        radius = 11.dp.toPx(),
        center = center
      )
      // Inner glowing ruby/amber jewel
      drawCircle(
        brush = Brush.radialGradient(
          listOf(Color(0xFFFF5252), Color(0xFFB71C1C))
        ),
        radius = 7.dp.toPx(),
        center = center
      )
      // Highlight glint
      drawCircle(
        color = Color.White,
        radius = 2.dp.toPx(),
        center = Offset(center.x - 2.dp.toPx(), center.y - 2.dp.toPx())
      )
    }

    // Digital Countdown in Center Below Jewel
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(44.dp))
      val displayNum = if (isCompleted) "00" else String.format("%02d", kotlin.math.ceil(remainingSeconds).toInt())
      Text(
        text = displayNum,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = if (isCompleted) Color(0xFF81C784) else Color(0xFFFFE8B2)
      )
      Text(
        text = if (isCompleted) "مُکَمَّل ✓" else if (isRunning) "چل رہا ہے..." else "روکا ہوا",
        fontFamily = FontFamily.SansSerif,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Medium,
        color = if (isCompleted) Color(0xFF81C784) else BronzeGoldLight
      )
    }
  }
}
