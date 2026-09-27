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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.InkTealDeep
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.UrduFontFamily
import com.example.util.ShareHelper

@Composable
fun MomentScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val selectedDhikr by viewModel.selectedDhikr.collectAsState()
  val timerState by viewModel.timerState.collectAsState()

  val remaining = timerState.remainingSeconds
  val total = timerState.totalSeconds
  val progress = if (total > 0) (remaining / total.toFloat()).coerceIn(0f, 1f) else 0f

  val secondsInt = remaining.toInt()
  val timeText = String.format("0:%02d", secondsInt)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(InkTeal)
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 22.dp, vertical = 10.dp)
      .testTag("moment_screen")
  ) {
    // Top Bar: Back to Home, Brand Logo & Category, Bookmark & Share
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { viewModel.navigateTo(Screen.Home) },
        modifier = Modifier.testTag("moment_back_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = ParchmentSurface.copy(alpha = 0.85f)
        )
      }

      // Small Brand Logo in Header
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .border(1.dp, BronzeGoldLight, CircleShape)
            .background(Color.White)
        ) {
          Image(
            painter = painterResource(id = R.drawable.app_brand_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0x22FFFFFF),
          border = BorderStroke(1.dp, BronzeGoldLight.copy(alpha = 0.4f))
        ) {
          Text(
            text = selectedDhikr.category,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = BronzeGoldLight,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Share Button
        IconButton(
          onClick = { ShareHelper.shareDhikr(context, selectedDhikr) },
          modifier = Modifier.testTag("moment_share_btn")
        ) {
          Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = "Share",
            tint = BronzeGoldLight
          )
        }

        IconButton(
          onClick = { viewModel.toggleBookmark(selectedDhikr.id) },
          modifier = Modifier.testTag("moment_bookmark_btn")
        ) {
          Icon(
            imageVector = if (selectedDhikr.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (selectedDhikr.isBookmarked) BronzeGoldLight else ParchmentSurface.copy(alpha = 0.7f)
          )
        }
      }
    }

    // Main Content
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 44.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Distinct Bismillah Header on Top if Quranic Ayah
      if (selectedDhikr.isQuranic) {
        com.example.ui.components.BismillahCalligraphyHeader(isDarkTheme = true)
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Arabic Text with Amiri font
      Text(
        text = selectedDhikr.arabic,
        fontFamily = ArabicFontFamily,
        fontSize = 28.sp,
        lineHeight = 42.sp,
        color = ParchmentSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Transliteration
      Text(
        text = selectedDhikr.transliteration,
        fontFamily = FontFamily.Serif,
        fontSize = 14.5.sp,
        fontWeight = FontWeight.Medium,
        color = BronzeGoldLight,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Urdu Translation (Prominently featured with Noto Nastaliq Urdu font)
      Text(
        text = selectedDhikr.translationUrdu,
        fontFamily = UrduFontFamily,
        fontSize = 14.5.sp,
        fontWeight = FontWeight.Normal,
        color = Color(0xFFFFE8B2),
        textAlign = TextAlign.Center,
        lineHeight = 24.sp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp)
      )

      Spacer(modifier = Modifier.height(4.dp))

      // English Translation
      Text(
        text = selectedDhikr.translation,
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        color = Color(0xFFB5CCC9),
        textAlign = TextAlign.Center,
        lineHeight = 17.sp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Timer Circle Disc
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(134.dp)
      ) {
        CircularProgressIndicator(
          progress = { progress },
          modifier = Modifier.size(134.dp),
          color = BronzeGoldLight,
          trackColor = Color(0x33B8863B),
          strokeWidth = 3.5.dp,
        )

        if (timerState.isCompleted) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = "Completed",
              tint = BronzeGoldLight,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "ماشاء اللہ • +10 پوائنٹس",
              fontFamily = FontFamily.Serif,
              fontSize = 12.5.sp,
              fontWeight = FontWeight.Bold,
              color = BronzeGoldLight
            )
          }
        } else {
          Text(
            text = timeText,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            color = BronzeGoldLight,
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(26.dp))

      // Controls: Previous, Play/Pause, Next
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        IconButton(
          onClick = { viewModel.previousDhikr() },
          modifier = Modifier
            .size(46.dp)
            .border(1.dp, Color(0x44E4C98A), CircleShape)
            .testTag("moment_prev_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Previous",
            tint = ParchmentSurface,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(22.dp))

        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(BronzeGold)
            .clickable { viewModel.toggleTimer() }
            .testTag("moment_play_pause_btn")
        ) {
          Icon(
            imageVector = when {
              timerState.isCompleted -> Icons.Filled.Refresh
              timerState.isRunning -> Icons.Filled.Pause
              else -> Icons.Filled.PlayArrow
            },
            contentDescription = if (timerState.isRunning) "Pause" else "Play",
            tint = InkTealDeep,
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.width(22.dp))

        IconButton(
          onClick = { viewModel.nextDhikr() },
          modifier = Modifier
            .size(46.dp)
            .border(1.dp, Color(0x44E4C98A), CircleShape)
            .testTag("moment_next_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Next",
            tint = ParchmentSurface,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Virtue & Source note + Quick Share pill
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0x33B8863B),
          border = BorderStroke(1.dp, BronzeGoldLight.copy(alpha = 0.4f)),
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { ShareHelper.shareDhikr(context, selectedDhikr) }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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
              text = "شیئر کریں (Share WhatsApp/X)",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = ParchmentSurface
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "${selectedDhikr.source} • ${selectedDhikr.virtue}",
        fontFamily = FontFamily.SansSerif,
        fontSize = 11.sp,
        color = Color(0xCCFFFFFF),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
      )
    }
  }
}
