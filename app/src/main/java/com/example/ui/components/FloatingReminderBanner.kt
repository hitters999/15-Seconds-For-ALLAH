package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DhikrItem
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.UrduFontFamily

@Composable
fun FloatingReminderBanner(
  dhikr: DhikrItem,
  countdownSeconds: Int = 5,
  onDismiss: () -> Unit,
  onBegin: () -> Unit,
  onShare: () -> Unit,
  onWhatsAppShare: () -> Unit,
  modifier: Modifier = Modifier
) {
  var progressTarget by remember { mutableFloatStateOf(1f) }

  LaunchedEffect(Unit) {
    progressTarget = 0f
  }

  val animatedProgress by animateFloatAsState(
    targetValue = progressTarget,
    animationSpec = tween(
      durationMillis = countdownSeconds * 1000,
      easing = LinearEasing
    ),
    label = "countdownProgress"
  )

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xF2122B27)),
    border = BorderStroke(1.5.dp, BronzeGoldLight),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      // Top Row: Brand & Countdown
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .border(1.dp, BronzeGoldLight, CircleShape)
              .background(Color.White),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.app_brand_logo),
              contentDescription = "Logo",
              modifier = Modifier.size(32.dp),
              contentScale = ContentScale.Fit
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Text(
              text = "15 Seconds 4 Allah",
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color.White
            )
            Text(
              text = "یاد دہانی • ${dhikr.source}",
              fontFamily = UrduFontFamily,
              fontSize = 11.sp,
              color = BronzeGoldLight
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x33B8863B)
          ) {
            Text(
              text = "5s Auto",
              fontFamily = FontFamily.SansSerif,
              fontSize = 10.sp,
              color = BronzeGoldLight,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Arabic text
      Text(
        text = dhikr.arabic,
        fontFamily = ArabicFontFamily,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFFE8B2),
        textAlign = TextAlign.Right,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(3.dp))

      // Urdu text
      Text(
        text = dhikr.translationUrdu,
        fontFamily = UrduFontFamily,
        fontSize = 12.5.sp,
        color = Color(0xFFE0E0E0),
        textAlign = TextAlign.Right,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Action buttons: Begin, WhatsApp Poster, All Share
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = BronzeGold,
          modifier = Modifier
            .weight(1.2f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onBegin)
        ) {
          Row(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color(0xFF09221D), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("15s ذکر شروع کریں", fontFamily = UrduFontFamily, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF09221D))
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF25D366),
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onWhatsAppShare)
        ) {
          Row(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("پوسٹر WhatsApp", fontFamily = UrduFontFamily, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }

        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x33B8863B))
            .clickable(onClick = onShare),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Filled.Share, contentDescription = "Share", tint = BronzeGoldLight, modifier = Modifier.size(14.dp))
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // 5-second countdown progress bar
      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = Color(0xFF81C784),
        trackColor = Color(0x33FFFFFF)
      )
    }
  }
}
