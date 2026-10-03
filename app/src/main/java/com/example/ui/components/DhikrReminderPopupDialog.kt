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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DhikrItem
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.UrduNastaliqFontFamily
import com.example.util.ShareHelper
import kotlinx.coroutines.delay

/**
 * Custom Hero Popup Notification Banner
 * Exactly matched to user's design reference:
 * - "5 seconds • non-blocking • work continues"
 * - Midnight teal gradient card with glowing cyan/teal border
 * - Top row: App icon ("15 Seconds for Allah"), "A moment. A brighter day.", "now"
 * - Center: Illuminated golden ornate crescent moon on left, magnificent Arabic calligraphy & translation on right
 * - Bottom: 5s countdown progress bar on left, "Later" pill button on right
 * - Auto-dismisses in 5 seconds without blocking ongoing work
 */
@Composable
fun HeroPopupNotificationCard(
  dhikr: DhikrItem,
  onStartMoment: () -> Unit,
  onDismiss: () -> Unit,
  countdownSeconds: Int = 5,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var progressTarget by remember { mutableFloatStateOf(1f) }

  LaunchedEffect(dhikr.id) {
    progressTarget = 0f
  }

  val animatedProgress by animateFloatAsState(
    targetValue = progressTarget,
    animationSpec = tween(
      durationMillis = countdownSeconds * 1000,
      easing = LinearEasing
    ),
    label = "popupCountdown"
  )

  // 5-second automatic dismissal (non-blocking)
  LaunchedEffect(dhikr.id) {
    delay(countdownSeconds * 1000L)
    onDismiss()
  }

  Card(
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    border = BorderStroke(
      1.4.dp,
      Brush.horizontalGradient(
        listOf(
          Color(0xFF2C7A7B),
          Color(0xFF4FD1C5),
          Color(0xFF285E61)
        )
      )
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 18.dp),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color(0xFF319795))
      .clip(RoundedCornerShape(26.dp))
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF09292F),
            Color(0xFF051B20),
            Color(0xFF021014)
          )
        )
      )
      .clickable(onClick = onStartMoment)
      .testTag("hero_popup_notification_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      // 1. Top Header Row: App Icon + App Name + Slogan + "now"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          // Dark Teal Squircle App Icon with Dome
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0D3337),
            border = BorderStroke(1.dp, Color(0xFF319795)),
            modifier = Modifier.size(36.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.app_brand_logo),
              contentDescription = "App Logo",
              modifier = Modifier
                .padding(4.dp)
                .size(28.dp),
              contentScale = ContentScale.Fit
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "15 Seconds for Allah",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Bold,
              fontSize = 13.5.sp,
              color = Color.White
            )
            Text(
              text = "✨ ثواب بھی ، Rewards بھی • 1 Pt = 1 Paisa",
              fontFamily = UrduNastaliqFontFamily,
              fontSize = 11.sp,
              color = Color(0xFFFDE68A)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFE5C07B)
        ) {
          Text(
            text = "+10 Pts (10 پیسے) 🪙",
            fontFamily = UrduNastaliqFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            color = Color(0xFF041F1A),
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Middle Row: Glowing Golden Crescent Moon on left + Arabic Calligraphy & Translation on right
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Glowing Ornate Islamic Crescent Moon
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.golden_crescent_ornate),
            contentDescription = "Crescent Moon",
            modifier = Modifier.size(74.dp),
            contentScale = ContentScale.Fit
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Arabic Calligraphy + Translations + Source
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Large, crisp, prominent white Arabic
          Text(
            text = dhikr.arabic,
            fontFamily = ArabicFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(3.dp))

          // English Translation (like screenshot: "Indeed, with hardship comes ease.")
          val englishText = if (dhikr.translation.isNotBlank()) {
            dhikr.translation
          } else {
            dhikr.translationUrdu
          }
          Text(
            text = englishText,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Normal,
            fontSize = 13.sp,
            color = Color(0xFFE2E8F0),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
          )

          // Urdu translation snippet
          if (dhikr.translationUrdu.isNotBlank() && dhikr.translation.isNotBlank()) {
            Text(
              text = dhikr.translationUrdu,
              fontFamily = UrduNastaliqFontFamily,
              fontSize = 12.sp,
              color = Color(0xFFCBD5E1),
              textAlign = TextAlign.Center,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.fillMaxWidth()
            )
          }

          Spacer(modifier = Modifier.height(3.dp))

          // Citation (e.g. Qur’an — Surah Ash-Sharh 94:5)
          val sourceText = if (dhikr.isQuranic) "Qur’an — ${dhikr.source}" else dhikr.source
          Text(
            text = sourceText,
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.5.sp,
            color = Color(0xFF7FA3A9),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Bottom Row: 5s Countdown Progress Bar on Left + "Later" Pill Button on Right
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Countdown Bar + 5s
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
              .weight(1f)
              .height(4.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF5CE1E6), // Glowing cyan
            trackColor = Color(0xFF13383E)
          )

          Spacer(modifier = Modifier.width(8.dp))

          Text(
            text = "${countdownSeconds}s",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp,
            color = Color(0xFF5CE1E6)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Prominent Golden "GET REWARDS" Button connected directly to Toolyfi 15s Timer Page
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xFFE5C07B),
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
              onDismiss()
              ShareHelper.openWebTimerPage(context, dhikr)
            }
            .testTag("popup_get_rewards_button")
        ) {
          Text(
            text = "🎁 GET REWARDS",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.5.sp,
            color = Color(0xFF041F1A),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // "Later" Pill Button
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xFF174340), // Dark cyan-teal pill
          border = BorderStroke(1.dp, Color(0xFF2C6861)),
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onDismiss)
            .testTag("later_button")
        ) {
          Text(
            text = "Later",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.5.sp,
            color = Color(0xFFE2E8F0),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }
  }
}

/**
 * Drop-in wrapper keeping existing callers compatible.
 */
@Composable
fun DhikrReminderPopupDialog(
  dhikr: DhikrItem,
  onStartMoment: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  HeroPopupNotificationCard(
    dhikr = dhikr,
    onStartMoment = onStartMoment,
    onDismiss = onDismiss,
    countdownSeconds = 5,
    modifier = modifier
  )
}
