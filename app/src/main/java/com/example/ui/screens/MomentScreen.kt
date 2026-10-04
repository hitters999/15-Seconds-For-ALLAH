package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.BismillahCalligraphyHeader
import com.example.ui.components.MechanicalClock15s
import com.example.ui.components.ParchmentBackground
import com.example.ui.components.PosterPreviewDialog
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.DarkAccentGold
import com.example.ui.theme.DarkArabicText
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSoft
import com.example.ui.theme.DarkUrduText
import com.example.ui.theme.InkTeal
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.TextSoft
import com.example.ui.theme.UrduFontFamily
import com.example.util.ShareHelper
import java.util.Locale

@Composable
fun MomentScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val selectedDhikr by viewModel.selectedDhikr.collectAsState()
  val timerState by viewModel.timerState.collectAsState()
  val userSettings by viewModel.userSettings.collectAsState()
  val scrollState = rememberScrollState()
  val context = LocalContext.current
  val isDark = isSystemInDarkTheme()
  var showPosterPreview by remember { mutableStateOf(false) }

  val cardBg = if (isDark) DarkCardSurface else ParchmentCard
  val cardBorder = if (isDark) DarkCardBorder else ParchmentBorder
  val titleColor = if (isDark) DarkTextPrimary else InkTeal
  val subtitleColor = if (isDark) DarkTextSoft else TextSoft
  val goldColor = if (isDark) DarkAccentGold else BronzeGold
  val arabicColor = if (isDark) DarkArabicText else InkTeal
  val urduColor = if (isDark) DarkUrduText else Color(0xFF8A5A1A)
  val pkrBalance = String.format(Locale.US, "%.2f", userSettings.totalScore * 0.01)
  val identifier = userSettings.userEmail.ifBlank { userSettings.userName }

  ParchmentBackground(modifier = modifier, showMadinahBackdrop = true) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp)
        .padding(bottom = 90.dp)
        .testTag("moment_screen"),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Top Bar: Back to Home + Official Brand Logo + Live Points/PKR Pill + Share Poster + Bookmark
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = titleColor
            )
          }

          Image(
            painter = painterResource(id = R.drawable.ic_launcher_fg_img),
            contentDescription = "15 Seconds for Allah Logo",
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .border(1.2.dp, goldColor, CircleShape),
            contentScale = ContentScale.Crop
          )
        }

        // Live Points & PKR Counter Pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = goldColor.copy(alpha = 0.16f),
          border = BorderStroke(1.dp, goldColor.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("🪙", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${userSettings.totalScore} Pts • Rs. $pkrBalance",
              fontFamily = FontFamily.Serif,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = goldColor
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { showPosterPreview = true },
            modifier = Modifier
              .size(38.dp)
              .testTag("moment_top_share_button")
          ) {
            Icon(
              imageVector = Icons.Filled.Share,
              contentDescription = "Share Poster",
              tint = goldColor,
              modifier = Modifier.size(21.dp)
            )
          }

          IconButton(
            onClick = { viewModel.toggleBookmark(selectedDhikr.id) },
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = if (selectedDhikr.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = if (selectedDhikr.isBookmarked) goldColor else subtitleColor,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Prominent Top Share & GET REWARDS Action Bar (So User Immediately Sees Share on 15s Page!)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { showPosterPreview = true },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E8E5A),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .testTag("moment_prominent_poster_share_btn")
        ) {
          Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "پوسٹر شیئر کریں",
            fontFamily = UrduFontFamily,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Button(
          onClick = {
            ShareHelper.openWebTimerPage(context, selectedDhikr, identifier)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = goldColor,
            contentColor = Color(0xFF071714)
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1.1f)
            .height(44.dp)
            .testTag("claim_points_button")
        ) {
          Text(
            text = "🎁 GET REWARDS (15s)",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF071714)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // THE 15-SECOND MECHANICAL CLOCK DIAL
      MechanicalClock15s(
        remainingSeconds = timerState.remainingSeconds,
        totalSeconds = timerState.totalSeconds,
        isRunning = timerState.isRunning,
        isCompleted = timerState.isCompleted,
        onClick = { viewModel.toggleTimer() }
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Control Buttons Row: Prev, Play/Pause, Next
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.previousDhikr() },
          modifier = Modifier.size(44.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint = titleColor)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Surface(
          shape = CircleShape,
          color = goldColor,
          border = BorderStroke(2.dp, BronzeGoldLight),
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .clickable { viewModel.toggleTimer() }
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = when {
                timerState.isCompleted -> Icons.Filled.Replay
                timerState.isRunning -> Icons.Filled.Pause
                else -> Icons.Filled.PlayArrow
              },
              contentDescription = "Play/Pause",
              tint = Color.White,
              modifier = Modifier.size(28.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
          onClick = { viewModel.nextDhikr() },
          modifier = Modifier.size(44.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = titleColor)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Sacred Ayah / Hadith Reading Card (With Brand Logo + Built-in Share & Poster Buttons!)
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.4.dp, goldColor.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Brand Header inside Card
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Image(
                painter = painterResource(id = R.drawable.ic_launcher_fg_img),
                contentDescription = "Logo",
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .border(1.dp, goldColor, CircleShape),
                contentScale = ContentScale.Crop
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "✨ ثواب بھی ، Rewards بھی ✨",
                fontFamily = UrduFontFamily,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = goldColor
              )
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF25D366),
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { showPosterPreview = true }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Filled.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "پوسٹر شیئر",
                  fontFamily = UrduFontFamily,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          if (selectedDhikr.isQuranic) {
            BismillahCalligraphyHeader(isDarkTheme = isDark)
            Spacer(modifier = Modifier.height(12.dp))
          }

          // Arabic with Amiri font
          Text(
            text = selectedDhikr.arabic,
            fontFamily = ArabicFontFamily,
            fontSize = 24.sp,
            color = arabicColor,
            lineHeight = 36.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Transliteration
          Text(
            text = selectedDhikr.transliteration,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            color = subtitleColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Urdu with Noto Nastaliq font
          Text(
            text = selectedDhikr.translationUrdu,
            fontFamily = UrduFontFamily,
            fontSize = 14.sp,
            color = urduColor,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(6.dp))

          // English Translation
          Text(
            text = selectedDhikr.translation,
            fontFamily = FontFamily.Serif,
            fontSize = 12.sp,
            color = titleColor,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = goldColor.copy(alpha = 0.14f),
            border = BorderStroke(0.8.dp, goldColor.copy(alpha = 0.35f))
          ) {
            Text(
              text = "📖 حوالہ: ${selectedDhikr.source}",
              fontFamily = UrduFontFamily,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = goldColor,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          if (selectedDhikr.contemplativeNote.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = goldColor.copy(alpha = 0.10f),
              border = BorderStroke(0.8.dp, goldColor.copy(alpha = 0.25f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "💡 ${selectedDhikr.contemplativeNote}",
                fontFamily = UrduFontFamily,
                fontSize = 11.5.sp,
                color = titleColor,
                modifier = Modifier.padding(10.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Inside-Card Share & WhatsApp Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { ShareHelper.shareToWhatsApp(context, selectedDhikr) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1.2f)
                .height(44.dp)
            ) {
              Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("واٹس ایپ شیئر", fontFamily = UrduFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = { showPosterPreview = true },
              colors = ButtonDefaults.buttonColors(containerColor = InkTeal, contentColor = Color.White),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
            ) {
              Text("پوسٹر ڈاؤن لوڈ و شیئر", fontFamily = UrduFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  if (showPosterPreview) {
    PosterPreviewDialog(
      dhikr = selectedDhikr,
      onDismiss = { showPosterPreview = false }
    )
  }
}
