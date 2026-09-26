package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkTextSoft
import com.example.ui.theme.InkTeal
import com.example.ui.theme.InkTealDeep
import com.example.ui.theme.ParchmentSurface

@Composable
fun MomentScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
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
      .padding(horizontal = 24.dp, vertical = 12.dp)
      .testTag("moment_screen")
  ) {
    // Top Bar: Back to Home, Category tag, Bookmark
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
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
          tint = ParchmentSurface.copy(alpha = 0.8f)
        )
      }

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x22FFFFFF),
        border = BorderStroke(1.dp, BronzeGoldLight.copy(alpha = 0.3f))
      ) {
        Text(
          text = selectedDhikr.category.uppercase(),
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.Medium,
          fontSize = 10.sp,
          color = BronzeGoldLight,
          letterSpacing = 1.2.sp,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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

    // Main Content: Centered contemplation as in Mockup Screen 3
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 56.dp, bottom = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Arabic Text (Screen 3 Moment)
      Text(
        text = selectedDhikr.arabic,
        fontFamily = FontFamily.Serif,
        fontSize = 30.sp,
        lineHeight = 44.sp,
        color = ParchmentSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Transliteration
      Text(
        text = selectedDhikr.transliteration,
        fontFamily = FontFamily.Serif,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = BronzeGoldLight,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Translation / Contemplation (Mockup Screen 3: "Glory be to Allah — said with full presence, just for this moment.")
      Text(
        text = selectedDhikr.translation,
        fontFamily = FontFamily.SansSerif,
        fontSize = 13.sp,
        color = Color(0xFF9FB6B3),
        textAlign = TextAlign.Center,
        lineHeight = 20.sp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(36.dp))

      // Timer Circle Disc from the mockup
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(136.dp)
      ) {
        // Outer thin animated circular track
        CircularProgressIndicator(
          progress = { progress },
          modifier = Modifier.size(136.dp),
          color = BronzeGoldLight,
          trackColor = Color(0x33B8863B),
          strokeWidth = 3.dp,
        )

        // Center Content
        if (timerState.isCompleted) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = "Completed",
              tint = BronzeGoldLight,
              modifier = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Completed",
              fontFamily = FontFamily.Serif,
              fontSize = 12.sp,
              color = ParchmentSurface
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

      Spacer(modifier = Modifier.height(34.dp))

      // Controls from Mockup Screen 3: Previous, Play/Pause, Next
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Previous Dhikr
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

        Spacer(modifier = Modifier.width(24.dp))

        // Center Play / Pause / Reset button
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

        Spacer(modifier = Modifier.width(24.dp))

        // Next Dhikr
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

      Spacer(modifier = Modifier.height(26.dp))

      // Virtue / Source reference
      Text(
        text = "${selectedDhikr.source} • ${selectedDhikr.virtue}",
        fontFamily = FontFamily.SansSerif,
        fontSize = 11.sp,
        color = Color(0x99FFFFFF),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 20.dp)
      )
    }
  }
}
