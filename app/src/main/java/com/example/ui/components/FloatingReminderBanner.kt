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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DhikrItem
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.UrduNastaliqFontFamily
import com.example.util.ShareHelper
import kotlinx.coroutines.delay

@Composable
fun FloatingReminderBanner(
  dhikr: DhikrItem,
  onDismiss: () -> Unit,
  onStartMoment: () -> Unit,
  modifier: Modifier = Modifier,
  durationSeconds: Int = 5
) {
  val context = LocalContext.current
  var remainingSeconds by remember { mutableIntStateOf(durationSeconds) }
  var progressTarget by remember { mutableStateOf(1f) }

  val animatedProgress by animateFloatAsState(
    targetValue = progressTarget,
    animationSpec = tween(durationMillis = durationSeconds * 1000, easing = LinearEasing),
    label = "5s_countdown"
  )

  LaunchedEffect(dhikr.id) {
    progressTarget = 0f
    for (sec in durationSeconds downTo 1) {
      remainingSeconds = sec
      delay(1000L)
    }
    remainingSeconds = 0
    delay(200L)
    onDismiss()
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(horizontal = 12.dp, vertical = 4.dp)
      .testTag("floating_reminder_banner")
  ) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFF091F1D)
      ),
      border = BorderStroke(1.2.dp, Color(0xFF225752)),
      elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(26.dp))
        .clickable(onClick = onStartMoment)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0xFF0F322F),
                Color(0xFF071917)
              )
            )
          )
          .padding(horizontal = 16.dp, vertical = 13.dp)
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Top Header Row: Logo, Title, Subtitle, Share Button, "now"
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              // Brand Logo Badge
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .border(1.2.dp, BronzeGoldLight, RoundedCornerShape(10.dp))
                  .background(Color.White)
              ) {
                Image(
                  painter = painterResource(id = R.drawable.app_brand_logo),
                  contentDescription = "Logo",
                  modifier = Modifier.size(34.dp),
                  contentScale = ContentScale.Fit
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column {
                Text(
                  text = "15 Seconds for Allah",
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Medium,
                  fontSize = 13.5.sp,
                  color = Color.White
                )
                Text(
                  text = "A moment. A brighter day. • ۱۵ سیکنڈز",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 10.5.sp,
                  color = Color(0xFFA5C4C0)
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              // Prominent Share Button in Top Bar
              Surface(
                shape = CircleShape,
                color = Color(0x33B8863B),
                border = BorderStroke(1.dp, BronzeGoldLight.copy(alpha = 0.5f)),
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .clickable {
                    ShareHelper.shareDhikr(context, dhikr)
                  }
                  .testTag("floating_banner_share_btn")
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = BronzeGoldLight,
                    modifier = Modifier.size(15.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              Text(
                text = "now",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = Color(0xFF7FA8A3)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // If Quranic Ayah: Distinct Bismillah header with separate artistic calligraphy!
          if (dhikr.isQuranic) {
            BismillahCalligraphyHeader(isDarkTheme = true)
            Spacer(modifier = Modifier.height(6.dp))
          }

          // Center Section: Crescent/Emblem on left + Arabic Ayah in Amiri font + Urdu in Nastaliq
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Crescent Moon Motif Emblem from user design
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(0x22B8863B))
                .border(1.dp, BronzeGold.copy(alpha = 0.5f), CircleShape)
            ) {
              Image(
                painter = painterResource(id = R.drawable.app_brand_logo),
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              // Arabic Calligraphy with distinct Amiri Font
              Text(
                text = dhikr.arabic,
                fontFamily = ArabicFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = if (dhikr.arabic.length > 70) 20.sp else 22.sp,
                lineHeight = if (dhikr.arabic.length > 70) 30.sp else 33.sp,
                color = Color.White,
                textAlign = TextAlign.Start
              )

              Spacer(modifier = Modifier.height(6.dp))

              // Urdu Translation in authentic Noto Nastaliq Urdu Font
              Text(
                text = dhikr.translationUrdu,
                fontFamily = UrduNastaliqFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.5.sp,
                color = Color(0xFFFFE3A3),
                lineHeight = 22.sp,
                textAlign = TextAlign.Start
              )

              Spacer(modifier = Modifier.height(4.dp))

              // English Translation
              Text(
                text = dhikr.translation,
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                color = Color(0xFFC3D8D5),
                lineHeight = 15.sp,
                textAlign = TextAlign.Start
              )

              Spacer(modifier = Modifier.height(2.dp))

              // Source Reference (Surah name, Ayah number)
              Text(
                text = dhikr.source,
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = Color(0xFF88ADA8)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Bottom Controls: Progress bar, Share quick badges (WhatsApp, X, Facebook), "Later" button
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Progress Bar
            LinearProgressIndicator(
              progress = { animatedProgress },
              modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
              color = Color(0xFF5CD8B8),
              trackColor = Color(0x335CD8B8)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
              text = "${remainingSeconds}s",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Medium,
              fontSize = 11.5.sp,
              color = Color(0xFF89ABA6)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Quick Social Share Pill (WhatsApp / X / FB)
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = Color(0xFF0F3E38),
              border = BorderStroke(1.dp, Color(0xFF2C746B)),
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { ShareHelper.shareDhikr(context, dhikr) }
                .testTag("floating_share_pill")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Share,
                  contentDescription = "Share",
                  tint = BronzeGoldLight,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "شیئر (Share)",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  color = Color.White
                )
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // "Later" (بعد میں) Button
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = Color(0xFF1B4E48),
              border = BorderStroke(1.dp, Color(0xFF2C746B)),
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onDismiss)
                .testTag("floating_banner_later_btn")
            ) {
              Text(
                text = "بعد میں",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }
      }
    }
  }
}
