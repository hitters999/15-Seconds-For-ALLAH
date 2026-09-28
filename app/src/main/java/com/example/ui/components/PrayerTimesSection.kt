package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerTimeItem
import com.example.data.PrayerTimesState
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.InkTealDark
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSubtle
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft
import com.example.ui.theme.UrduFontFamily

@Composable
fun PrayerTimesSection(
  prayerState: PrayerTimesState,
  onRefreshClick: () -> Unit,
  onRequestLocationClick: () -> Unit,
  currentTimeFormatted: String? = null,
  onOpenTimezoneClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = ParchmentCard.copy(alpha = 0.95f)),
    border = BorderStroke(1.2.dp, BronzeGold.copy(alpha = 0.35f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("prayer_times_section")
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      // Header: Mosque Icon + Title + Location Badge + Refresh
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
              .background(InkTeal.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.Mosque,
              contentDescription = "Prayer Times",
              tint = InkTeal,
              modifier = Modifier.size(19.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "اوقاتِ نماز",
                fontFamily = UrduFontFamily,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = InkTeal
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "• Prayer Times",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = TextSoft
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${prayerState.hijriDate} | ${prayerState.locationName}",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.5.sp,
                color = BronzeGold,
                maxLines = 1
              )
              if (currentTimeFormatted != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = InkTeal.copy(alpha = 0.1f),
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .then(if (onOpenTimezoneClick != null) Modifier.clickable { onOpenTimezoneClick() } else Modifier)
                ) {
                  Text(
                    text = "🕒 $currentTimeFormatted",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkTeal,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                  )
                }
              }
            }
          }
        }

        // Actions: GPS detect or Refresh
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (prayerState.isGpsEnabled) InkTeal.copy(alpha = 0.1f) else BronzeGold.copy(alpha = 0.15f),
            border = BorderStroke(
              1.dp,
              if (prayerState.isGpsEnabled) InkTeal.copy(alpha = 0.3f) else BronzeGold.copy(alpha = 0.4f)
            ),
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable(onClick = onRequestLocationClick)
              .testTag("prayer_gps_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (prayerState.isGpsEnabled) Icons.Filled.MyLocation else Icons.Filled.LocationOn,
                contentDescription = "Detect Location",
                tint = if (prayerState.isGpsEnabled) InkTeal else BronzeGold,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (prayerState.isGpsEnabled) "GPS آن" else "مقام GPS",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (prayerState.isGpsEnabled) InkTeal else BronzeGold
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = ParchmentSubtle,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable(onClick = onRefreshClick)
              .testTag("prayer_refresh_button")
          ) {
            Box(
              modifier = Modifier.padding(6.dp),
              contentAlignment = Alignment.Center
            ) {
              if (prayerState.isLoading) {
                CircularProgressIndicator(
                  modifier = Modifier.size(14.dp),
                  color = BronzeGold,
                  strokeWidth = 1.5.dp
                )
              } else {
                Icon(
                  imageVector = Icons.Filled.Refresh,
                  contentDescription = "Refresh Prayer Times",
                  tint = InkTeal,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }

      // Next Prayer Highlight Banner if available
      prayerState.nextPrayer?.let { next ->
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = InkTeal.copy(alpha = 0.08f),
          border = BorderStroke(1.dp, BronzeGold.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = null,
                tint = BronzeGold,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "اگلی نماز: ${next.nameUr} (${next.time12})",
                fontFamily = UrduFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = InkTeal
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = BronzeGold
            ) {
              Text(
                text = prayerState.countdownText,
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Horizontal Scrollable / Clean Row of 6 Times (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        prayerState.items.forEach { item ->
          PrayerTimePill(item = item)
        }
      }
    }
  }
}

@Composable
private fun PrayerTimePill(item: PrayerTimeItem) {
  val isHighlight = item.isNext
  val isPassed = item.isPassed && !item.isNext

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (isHighlight) {
      InkTeal
    } else if (isPassed) {
      ParchmentSurface.copy(alpha = 0.6f)
    } else {
      ParchmentSurface
    },
    border = BorderStroke(
      width = if (isHighlight) 1.5.dp else 1.dp,
      color = if (isHighlight) BronzeGold else ParchmentBorder
    ),
    shadowElevation = if (isHighlight) 4.dp else 0.dp,
    modifier = Modifier.width(76.dp)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Urdu Name
      Text(
        text = item.nameUr,
        fontFamily = UrduFontFamily,
        fontSize = 13.sp,
        fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
        color = if (isHighlight) BronzeGoldLight else if (isPassed) TextSoft else InkTeal,
        textAlign = TextAlign.Center
      )

      // English Name
      Text(
        text = item.nameEn,
        fontFamily = FontFamily.SansSerif,
        fontSize = 9.sp,
        color = if (isHighlight) Color.White.copy(alpha = 0.75f) else TextSoft,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(4.dp))

      // 12-Hour formatted Time (e.g. 5:08 AM)
      Text(
        text = item.time12,
        fontFamily = FontFamily.SansSerif,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        color = if (isHighlight) Color.White else if (isPassed) TextSoft else TextPrimary,
        textAlign = TextAlign.Center
      )

      if (isHighlight) {
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = BronzeGold
        ) {
          Text(
            text = "اگلی",
            fontFamily = FontFamily.SansSerif,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
      }
    }
  }
}
