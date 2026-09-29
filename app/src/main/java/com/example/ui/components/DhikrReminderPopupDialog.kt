package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.DhikrItem
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.InkTeal
import com.example.ui.theme.UrduFontFamily
import com.example.ui.theme.UrduNastaliqFontFamily
import com.example.util.ShareHelper

@Composable
fun DhikrReminderPopupDialog(
  dhikr: DhikrItem,
  onStartMoment: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
  ) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF071F1B)),
      border = BorderStroke(1.5.dp, BronzeGold),
      elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp)
        .testTag("dhikr_reminder_popup_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState)
          .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Row: Dismiss icon & Share Icon
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0x33B8863B),
            border = BorderStroke(0.8.dp, BronzeGold.copy(alpha = 0.4f))
          ) {
            Text(
              text = "وقت ہو گیا ہے • یاد دہانی",
              fontFamily = UrduFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp,
              color = BronzeGoldLight,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Share Button
            Surface(
              shape = CircleShape,
              color = Color(0x33B8863B),
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable { ShareHelper.shareDhikr(context, dhikr) }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Filled.Share,
                  contentDescription = "Share",
                  tint = BronzeGoldLight,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(30.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Official Brand Logo in the Notification Popup
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .border(2.dp, BronzeGold, CircleShape)
            .background(Color.White)
            .testTag("popup_brand_logo")
        ) {
          Image(
            painter = painterResource(id = R.drawable.app_brand_logo),
            contentDescription = "15 Seconds 4 Allah Logo",
            modifier = Modifier.size(68.dp),
            contentScale = ContentScale.Fit
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "15 Seconds 4 Allah • یادِ الٰہی",
          fontFamily = UrduFontFamily,
          fontSize = 16.5.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFFFF4D6)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Sacred Content Box (Emerald Glass Frame)
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = Color(0x4003110E),
          border = BorderStroke(1.dp, BronzeGold.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Distinct Bismillah Header if Quranic Ayah
            if (dhikr.isQuranic) {
              BismillahCalligraphyHeader(isDarkTheme = true)
              Spacer(modifier = Modifier.height(8.dp))
            }

            // Arabic text in Amiri Font
            Text(
              text = dhikr.arabic,
              fontFamily = ArabicFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = if (dhikr.arabic.length > 70) 22.sp else 25.sp,
              lineHeight = if (dhikr.arabic.length > 70) 34.sp else 38.sp,
              color = Color(0xFFFFFFFF),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Urdu Translation with Authentic Noto Nastaliq Font
            Text(
              text = dhikr.translationUrdu,
              fontFamily = UrduNastaliqFontFamily,
              fontWeight = FontWeight.Normal,
              fontSize = 14.sp,
              color = Color(0xFFFFE8B2),
              textAlign = TextAlign.Center,
              lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // English Translation
            if (dhikr.translation.isNotBlank()) {
              Text(
                text = dhikr.translation,
                fontFamily = FontFamily.Serif,
                fontSize = 11.5.sp,
                color = Color(0xFFD4DCE8),
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(4.dp))
            }

            // Source reference
            Text(
              text = "📍 ${dhikr.source}",
              fontFamily = UrduFontFamily,
              fontSize = 11.sp,
              color = BronzeGoldLight,
              textAlign = TextAlign.Center
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Action Button: Begin 15s Moment
        Button(
          onClick = onStartMoment,
          colors = ButtonDefaults.buttonColors(
            containerColor = BronzeGold,
            contentColor = Color(0xFF09221D)
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("popup_start_moment_btn")
        ) {
          Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "15 سیکنڈ کا ذکر شروع کریں (Begin Moment)",
            fontFamily = UrduFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Share Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF25D366).copy(alpha = 0.16f),
            border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.5f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable { ShareHelper.shareToWhatsApp(context, dhikr) }
          ) {
            Text(
              text = "واٹس ایپ اسٹیٹس",
              fontFamily = UrduFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp,
              color = Color(0xFF25D366),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 7.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = BronzeGold.copy(alpha = 0.14f),
            border = BorderStroke(1.dp, BronzeGold.copy(alpha = 0.4f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable { ShareHelper.shareDhikrPoster(context, dhikr) }
          ) {
            Text(
              text = "پوسٹر شیئر کریں",
              fontFamily = UrduFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp,
              color = BronzeGoldLight,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 7.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Remind Later Button
        Text(
          text = "1 گھنٹے بعد دوبارہ یاد دلائیں",
          fontFamily = UrduFontFamily,
          fontSize = 12.sp,
          color = Color(0xFF94A3B8),
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onDismiss)
            .padding(horizontal = 12.dp, vertical = 4.dp)
        )
      }
    }
  }
}
