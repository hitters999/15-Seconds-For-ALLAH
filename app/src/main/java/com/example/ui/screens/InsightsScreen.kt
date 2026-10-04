package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.DayActivity
import com.example.ui.MainViewModel
import com.example.ui.components.ParchmentBackground
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSubtle
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.SoftEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft
import com.example.ui.theme.UrduFontFamily
import com.example.util.ShareHelper

@Composable
fun InsightsScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val streak by viewModel.streakCount.collectAsState()
  val totalMoments by viewModel.totalMomentsCount.collectAsState()
  val userSettings by viewModel.userSettings.collectAsState()
  val activityGrid by viewModel.activityGrid.collectAsState()
  val scrollState = rememberScrollState()
  val context = LocalContext.current

  ParchmentBackground(modifier = modifier, showMadinahBackdrop = true) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp)
        .padding(bottom = 90.dp)
        .testTag("insights_screen"),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(14.dp))

      // Top Title with Official Logo
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_launcher_fg_img),
          contentDescription = "15 Seconds for Allah Logo",
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .border(1.4.dp, BronzeGold, CircleShape),
          contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "Spiritual Streak • روحانی استقامت",
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = InkTeal
          )
          Text(
            text = "مسلسل اذکار کا ریکارڈ اور روحانی درجات کا مقابلہ",
            fontFamily = UrduFontFamily,
            fontSize = 12.5.sp,
            color = BronzeGold
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 1. FLAMING CRESCENT STREAK HERO CARD
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132B27)),
        border = BorderStroke(2.dp, BronzeGoldLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Flame Icon Halo
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(90.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(Color(0xFFFF9800), Color(0xFFE65100), Color(0x33000000))
                )
              )
              .border(2.dp, BronzeGoldLight, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Filled.LocalFireDepartment,
              contentDescription = "Streak Fire",
              tint = Color(0xFFFFEB3B),
              modifier = Modifier.size(54.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Streak Number
          Text(
            text = "$streak",
            fontFamily = FontFamily.Serif,
            fontSize = 58.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFE8B2)
          )

          Text(
            text = "دن کی مسلسل استقامت (Days Devotion Streak)",
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "ماشاء اللہ! آپ کی روحانی زنجیر الحمد للہ قائم ہے۔",
            fontFamily = UrduFontFamily,
            fontSize = 13.sp,
            color = Color(0xFFD6C7A8)
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Share My Streak Poster Button (WhatsApp Status)
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF25D366),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                ShareHelper.shareStreakPoster(
                  context,
                  streak,
                  totalMoments,
                  userSettings.totalScore,
                  userSettings.spiritualRank
                )
              }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Filled.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "میری اسٹریک کا پوسٹر شیئر کریں (Share Streak Poster)",
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 2. SPIRITUAL DEVOTION LEAGUE (روحانی مقابلہ • Leaderboard)
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ParchmentCard),
        border = BorderStroke(1.2.dp, BronzeGold),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0x22B8863B))
              ) {
                Icon(
                  imageVector = Icons.Filled.EmojiEvents,
                  contentDescription = null,
                  tint = BronzeGold,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "روحانی لیگ (Devotion League)",
                  fontFamily = UrduFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.5.sp,
                  color = InkTeal
                )
                Text(
                  text = "روزانہ اذکار کا روحانی مقابلہ",
                  fontFamily = UrduFontFamily,
                  fontSize = 11.sp,
                  color = TextSoft
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF1B4E48)
            ) {
              Text(
                text = "${userSettings.totalScore} pts",
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BronzeGoldLight,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // League Ranks Comparison Table
          LeagueRow(rank = "1", title = "صاحبِ استقامت (Master League)", pts = "5,000+ pts", isCurrent = userSettings.totalScore >= 5000)
          LeagueRow(rank = "2", title = "ذاکرِ مداوم (Diamond League)", pts = "2,000+ pts", isCurrent = userSettings.totalScore in 2000..4999)
          LeagueRow(rank = "3", title = "محبِ ذکر (Gold League)", pts = "800+ pts", isCurrent = userSettings.totalScore in 800..1999)
          LeagueRow(rank = "4", title = "مبتدی (Seeker of Peace)", pts = "0 - 799 pts", isCurrent = userSettings.totalScore < 800)
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 3. 35-DAY PRESENCE HEATMAP
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ParchmentCard),
        border = BorderStroke(1.dp, ParchmentBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "35 روزہ حاضری نامہ (Presence Grid)",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = InkTeal
              )
              Text(
                text = "ہر بلاک ایک دن کے اذکار کی نشاندہی کرتا ہے",
                fontFamily = UrduFontFamily,
                fontSize = 11.sp,
                color = TextSoft
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = ParchmentSubtle
            ) {
              Text(
                text = "$totalMoments مکمل",
                fontFamily = UrduFontFamily,
                fontSize = 11.sp,
                color = BronzeGold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Heatmap 7x5 Grid
          HeatmapGrid(activityGrid = activityGrid)
        }
      }
    }
  }
}

@Composable
private fun LeagueRow(
  rank: String,
  title: String,
  pts: String,
  isCurrent: Boolean
) {
  val bgColor = if (isCurrent) Color(0x221B4E48) else Color.Transparent
  val border = if (isCurrent) BorderStroke(1.dp, BronzeGold) else null

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = bgColor,
    border = border,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (isCurrent) BronzeGold else ParchmentSubtle)
        ) {
          Text(
            text = rank,
            fontFamily = FontFamily.Serif,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCurrent) Color.White else TextPrimary
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = title,
          fontFamily = UrduFontFamily,
          fontSize = 13.sp,
          fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
          color = if (isCurrent) InkTeal else TextPrimary
        )
      }

      Text(
        text = if (isCurrent) "آپ کا درجہ ★" else pts,
        fontFamily = if (isCurrent) UrduFontFamily else FontFamily.Serif,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (isCurrent) BronzeGold else TextSoft
      )
    }
  }
}

@Composable
private fun HeatmapGrid(activityGrid: List<DayActivity>) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(5.dp)
  ) {
    // 5 rows of 7 days
    val chunks = activityGrid.chunked(7)
    chunks.forEach { week ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
      ) {
        week.forEach { day ->
          val cellColor = when {
            day.count >= 8 -> SoftEmerald
            day.count >= 4 -> Color(0xFF66BB6A)
            day.count >= 1 -> Color(0xFFA5D6A7)
            else -> ParchmentSubtle
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .height(30.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(cellColor)
              .border(
                width = if (day.isToday) 1.5.dp else 0.dp,
                color = if (day.isToday) BronzeGold else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${day.dayOfMonth}",
              fontFamily = FontFamily.SansSerif,
              fontSize = 9.sp,
              color = if (day.count >= 4) Color.White else TextSoft
            )
          }
        }
      }
    }
  }
}
