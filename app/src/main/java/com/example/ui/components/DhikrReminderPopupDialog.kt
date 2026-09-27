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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
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
import com.example.ui.theme.InkTeal
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSubtle
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.TextSoft
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
      colors = CardDefaults.cardColors(containerColor = ParchmentSurface),
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
          .padding(20.dp),
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
            color = ParchmentSubtle
          ) {
            Text(
              text = "وقت ہو گیا ہے • یاد دہانی",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Medium,
              fontSize = 11.sp,
              color = BronzeGold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Share Button
            Surface(
              shape = CircleShape,
              color = ParchmentSubtle,
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable { ShareHelper.shareDhikr(context, dhikr) }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Filled.Share,
                  contentDescription = "Share",
                  tint = BronzeGold,
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
                tint = TextSoft,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Official Brand Logo in the Notification Popup
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .border(2.dp, BronzeGold, CircleShape)
            .background(Color.White)
            .testTag("popup_brand_logo")
        ) {
          Image(
            painter = painterResource(id = R.drawable.app_brand_logo),
            contentDescription = "15 Seconds for Allah Logo",
            modifier = Modifier.size(72.dp),
            contentScale = ContentScale.Fit
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "15 Seconds for Allah • ۱۵ سیکنڈز",
          fontFamily = FontFamily.Serif,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = InkTeal,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Sacred Content Box
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = ParchmentCard,
          border = BorderStroke(1.dp, ParchmentBorder),
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
              BismillahCalligraphyHeader(isDarkTheme = false)
              Spacer(modifier = Modifier.height(8.dp))
            }

            // Arabic text in Amiri Font
            Text(
              text = dhikr.arabic,
              fontFamily = ArabicFontFamily,
              fontWeight = FontWeight.Normal,
              fontSize = if (dhikr.arabic.length > 70) 22.sp else 24.sp,
              lineHeight = if (dhikr.arabic.length > 70) 33.sp else 36.sp,
              color = InkTeal,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Urdu Translation with Authentic Noto Nastaliq Font
            Text(
              text = dhikr.translationUrdu,
              fontFamily = UrduNastaliqFontFamily,
              fontWeight = FontWeight.Normal,
              fontSize = 14.sp,
              color = Color(0xFF784508),
              textAlign = TextAlign.Center,
              lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // English Translation
            Text(
              text = dhikr.translation,
              fontFamily = FontFamily.Serif,
              fontSize = 11.5.sp,
              color = TextSoft,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Source reference
            Text(
              text = dhikr.source,
              fontFamily = FontFamily.SansSerif,
              fontSize = 10.5.sp,
              color = BronzeGold,
              textAlign = TextAlign.Center
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Share Bar for WhatsApp, Facebook, X (Twitter)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF25D366).copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.5f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { ShareHelper.shareToWhatsApp(context, dhikr) }
          ) {
            Text(
              text = "WhatsApp",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.5.sp,
              color = Color(0xFF0F8A3C),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 7.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1877F2).copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Color(0xFF1877F2).copy(alpha = 0.5f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { ShareHelper.shareToFacebook(context, dhikr) }
          ) {
            Text(
              text = "Facebook",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.5.sp,
              color = Color(0xFF1877F2),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 7.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1DA1F2).copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Color(0xFF1DA1F2).copy(alpha = 0.5f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { ShareHelper.shareToTwitter(context, dhikr) }
          ) {
            Text(
              text = "X (Twitter)",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.5.sp,
              color = Color(0xFF0C7ABF),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 7.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Big Action Button: Start 15s Moment
        Surface(
          shape = RoundedCornerShape(22.dp),
          color = InkTeal,
          border = BorderStroke(1.dp, BronzeGoldLight),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onStartMoment)
            .testTag("popup_start_moment_btn")
        ) {
          Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.Timer,
              contentDescription = null,
              tint = BronzeGoldLight,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "15 سیکنڈ کا ذکر شروع کریں",
              fontFamily = FontFamily.Serif,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Remind Later Button
        Text(
          text = "1 گھنٹے بعد دوبارہ یاد دلائیں",
          fontFamily = FontFamily.SansSerif,
          fontSize = 12.sp,
          color = TextSoft,
          modifier = Modifier
            .clickable(onClick = onDismiss)
            .padding(6.dp)
        )
      }
    }
  }
}
