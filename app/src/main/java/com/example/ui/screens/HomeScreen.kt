package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DhikrItem
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.DailyMomentRing
import com.example.ui.components.ParchmentBackground
import com.example.ui.components.QuickCategoryCard
import com.example.ui.components.SerenePrimaryButton
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft

@Composable
fun HomeScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val userSettings by viewModel.userSettings.collectAsState()
  val todayLogs by viewModel.todayLogs.collectAsState()
  val allItems by viewModel.allItems.collectAsState()
  val scrollState = rememberScrollState()

  val dailyGoal = userSettings.dailyGoal.coerceAtLeast(1)
  val todayCount = todayLogs.size
  val progress = (todayCount.toFloat() / dailyGoal).coerceIn(0f, 1f)

  // Choose a featured moment for the day
  val featuredItem = allItems.firstOrNull { it.id == "subhan_wa_bihamdihi" } ?: allItems.first()

  ParchmentBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 22.dp)
        .padding(bottom = 90.dp)
        .testTag("home_screen")
    ) {
      Spacer(modifier = Modifier.height(14.dp))

      // Top Header: Greeting & User Name (Screen 2 Home from mockup)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "As-salamu alaykum",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = TextSoft,
            letterSpacing = 0.3.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = userSettings.userName,
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal,
            color = InkTeal,
            letterSpacing = 0.2.sp
          )
        }

        // Circular subtle avatar / icon
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(BronzeGoldLight)
            .clickable { viewModel.navigateTo(Screen.Profile) }
            .testTag("home_avatar"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = userSettings.userName.take(1).uppercase(),
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = InkTeal
          )
        }
      }

      Spacer(modifier = Modifier.height(26.dp))

      // The Signature 15s Circular Dial Ring from the mockup
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        DailyMomentRing(
          progressPercent = progress,
          todayDone = todayCount,
          dailyGoal = dailyGoal,
          onClick = {
            viewModel.selectDhikrForMoment(featuredItem, startImmediately = true)
          }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Quick Categories Row: Dhikr, Dua, Qur'an, Names
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickCategoryCard(
          title = "Dhikr",
          icon = Icons.Filled.Spa,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.setSelectedCategory("Gratitude")
            viewModel.navigateTo(Screen.Library)
          }
        )
        QuickCategoryCard(
          title = "Dua",
          icon = Icons.Filled.AutoAwesome,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.setSelectedCategory("Forgiveness")
            viewModel.navigateTo(Screen.Library)
          }
        )
        QuickCategoryCard(
          title = "Qur'an",
          icon = Icons.Filled.Book,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.setSelectedCategory("Quranic Gems")
            viewModel.navigateTo(Screen.Library)
          }
        )
        QuickCategoryCard(
          title = "Morning",
          icon = Icons.Filled.Mosque,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.setSelectedCategory("Morning Remembrance")
            viewModel.navigateTo(Screen.Library)
          }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Primary Action Card: Featured Moment
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ParchmentCard),
        border = BorderStroke(1.dp, ParchmentBorder),
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .clickable {
            viewModel.selectDhikrForMoment(featuredItem, startImmediately = false)
          }
          .testTag("featured_moment_card")
      ) {
        Column(
          modifier = Modifier.padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "MOMENT FOR NOW",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 10.sp,
              color = BronzeGold,
              letterSpacing = 1.2.sp
            )
            Text(
              text = "15 SECONDS",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Medium,
              fontSize = 10.sp,
              color = TextSoft
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = featuredItem.arabic,
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            color = InkTeal,
            lineHeight = 34.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = featuredItem.transliteration,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            color = TextSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = featuredItem.translation,
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(16.dp))

          SerenePrimaryButton(
            text = "Begin 15-Second Moment",
            onClick = {
              viewModel.selectDhikrForMoment(featuredItem, startImmediately = true)
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Serene Insight Banner
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ParchmentSurface),
        border = BorderStroke(1.dp, ParchmentBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Filled.FormatQuote,
            contentDescription = null,
            tint = BronzeGold,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Your calmest hour is usually Fajr.",
              fontFamily = FontFamily.Serif,
              fontSize = 13.sp,
              color = InkTeal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Take 15 seconds before the world awakens.",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = TextSoft
            )
          }
        }
      }
    }
  }
}
