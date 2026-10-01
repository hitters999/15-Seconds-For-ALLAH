@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DhikrItem
import com.example.data.PrayerTimesState
import com.example.ui.MainViewModel
import com.example.ui.MomentTimerUiState
import com.example.ui.Screen
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
import com.example.ui.theme.UrduFontFamily
import com.example.ui.theme.UrduNastaliqFontFamily
import com.example.util.ShareHelper

// Luxury Islamic Wellness Palette
private val Ivory = Color(0xFFF8F3E8)
private val CardIvory = Color(0xFFFFFCF5)
private val Emerald = Color(0xFF075B4B)
private val DeepEmerald = Color(0xFF034438)
private val Gold = Color(0xFFC7953E)
private val SoftGold = Color(0xFFE8D4A8)
private val Brown = Color(0xFF755B42)
private val Muted = Color(0xFF8C8174)

@Composable
fun HomeScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isDark = isSystemInDarkTheme()

  val currentTime by viewModel.currentTimeFormatted.collectAsState()
  val prayerTimesState by viewModel.prayerTimesState.collectAsState()
  val featuredItem by viewModel.featuredDhikrItem.collectAsState()
  val streakCount by viewModel.currentStreak.collectAsState()
  val timerState by viewModel.momentTimerState.collectAsState()
  val totalMoments = viewModel.totalMomentsCount.collectAsState().value
  val reminderInterval = viewModel.reminderInterval.collectAsState().value
  val dynamicGreeting by viewModel.dynamicGreeting.collectAsState()

  var previewPosterItem by remember { mutableStateOf<DhikrItem?>(null) }

  ParchmentBackground(modifier = modifier, showMadinahBackdrop = true) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 18.dp)
        .testTag("home_screen"),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

      /*
       * PREMIUM HEADER
       */
      item {
        PremiumTopHeader(
          currentTime = currentTime,
          dynamicGreeting = dynamicGreeting,
          streakCount = streakCount,
          onProfileClick = { viewModel.navigateTo(Screen.Profile) }
        )
      }

      /*
       * PRAYER SECTION
       */
      item {
        PremiumSectionTitle(
          eyebrow = "TODAY'S RHYTHM • نماز و اوقات",
          title = "Prayer & Presence"
        )

        Spacer(modifier = Modifier.height(7.dp))

        LivePrayerTimesCard(
          prayerState = prayerTimesState,
          onRefreshLocation = {
            viewModel.fetchPrayerTimes(useGps = true)
          }
        )
      }

      /*
       * MAIN 15 SECOND HERO
       */
      item {
        PremiumMomentHero(
          timerState = timerState,
          onStart = {
            viewModel.startCurrentMoment()
          }
        )
      }

      /*
       * FEATURED DHIKR
       */
      item {
        PremiumSectionTitle(
          eyebrow = "DAILY REFLECTION • آج کا لمحہ",
          title = "A Moment Worth Remembering"
        )

        Spacer(modifier = Modifier.height(7.dp))

        PremiumFeaturedDhikrCard(
          content = {
            FeaturedDhikrCard(
              item = featuredItem,
              isDark = isDark,
              onStart15s = {
                viewModel.selectDhikrForMoment(featuredItem, startImmediately = true)
              },
              onSharePoster = {
                previewPosterItem = featuredItem
              },
              onOpenHadithExplorer = {
                viewModel.navigateTo(Screen.HadithExplorer)
              }
            )
          }
        )
      }

      /*
       * SPIRITUAL JOURNEY
       */
      item {
        PremiumJourneyCard(
          streakCount = streakCount,
          totalMoments = totalMoments,
          onViewInsights = { viewModel.navigateTo(Screen.Insights) }
        )
      }

      /*
       * REMINDER
       */
      item {
        PremiumReminderCard(
          currentInterval = reminderInterval,
          onIntervalSelected = {
            viewModel.updateReminderInterval(it)
          }
        )
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  // High-Resolution Social Media Poster Preview & Share Dialog
  if (previewPosterItem != null) {
    PosterPreviewDialog(
      dhikr = previewPosterItem!!,
      onDismiss = { previewPosterItem = null }
    )
  }
}

// =========================================================================
// 1. PREMIUM TOP HEADER
// =========================================================================

@Composable
private fun PremiumTopHeader(
  currentTime: String,
  dynamicGreeting: Pair<String, String>,
  streakCount: Int,
  onProfileClick: () -> Unit
) {
  val (urduGreeting, salamText) = dynamicGreeting
  val isDark = isSystemInDarkTheme()

  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) DarkCardSurface else CardIvory
    ),
    border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.45f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 13.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: App Logo + Greetings
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clickable { onProfileClick() }
          .weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(SoftGold.copy(alpha = 0.45f), Gold.copy(alpha = 0.2f))
              )
            )
            .border(1.4.dp, Gold, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.app_brand_logo),
            contentDescription = "Brand Logo",
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "15 Seconds 4 Allah",
              fontFamily = FontFamily.Serif,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) DarkTextPrimary else DeepEmerald
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Filled.AutoAwesome,
              contentDescription = null,
              tint = Gold,
              modifier = Modifier.size(13.dp)
            )
          }

          Text(
            text = "$salamText • $currentTime",
            fontFamily = UrduFontFamily,
            fontSize = 11.5.sp,
            color = if (isDark) DarkTextSoft else Brown
          )
        }
      }

      // Right: Streak Badge
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Gold.copy(alpha = 0.15f) else SoftGold.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = "Streak",
            tint = Color(0xFFE65100),
            modifier = Modifier.size(17.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$streakCount Days",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Gold else DeepEmerald
          )
        }
      }
    }
  }
}

// =========================================================================
// 2. SECTION TITLE
// =========================================================================

@Composable
private fun PremiumSectionTitle(
  eyebrow: String,
  title: String
) {
  val isDark = isSystemInDarkTheme()

  Column(modifier = Modifier.padding(start = 4.dp, top = 2.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(Gold)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = eyebrow.uppercase(),
        fontFamily = FontFamily.SansSerif,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = Gold
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = title,
      fontFamily = FontFamily.Serif,
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = if (isDark) DarkTextPrimary else DeepEmerald
    )
  }
}

// =========================================================================
// 3. LIVE PRAYER TIMES CARD
// =========================================================================

@Composable
private fun LivePrayerTimesCard(
  prayerState: PrayerTimesState,
  onRefreshLocation: () -> Unit
) {
  val isDark = isSystemInDarkTheme()

  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) DarkCardSurface else CardIvory
    ),
    border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.45f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Top row: Next Prayer Alert + Location refresh
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Emerald.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.Mosque,
              contentDescription = null,
              tint = if (isDark) Gold else Emerald,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            val nextPrayer = prayerState.nextPrayer
            val nextPrayerName = nextPrayer?.nameUrdu ?: "اگلی نماز"
            val nextTime = nextPrayer?.time12 ?: ""
            Text(
              text = if (nextTime.isNotBlank()) "اگلی نماز: $nextPrayerName ($nextTime)" else "اوقاتِ نماز",
              fontFamily = UrduFontFamily,
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) DarkTextPrimary else DeepEmerald
            )
            Text(
              text = "${prayerState.countdownText} • ${prayerState.locationName}",
              fontFamily = UrduFontFamily,
              fontSize = 11.sp,
              color = if (isDark) DarkTextSoft else Brown
            )
          }
        }

        IconButton(
          onClick = onRefreshLocation,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = "Refresh Location",
            tint = Gold,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 5 Prayers Grid Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        val displayPrayers = prayerState.items.filter { it.id != "sunrise" }
        displayPrayers.forEach { prayer ->
          val isNext = prayer.isNext
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
              isNext -> if (isDark) Emerald.copy(alpha = 0.4f) else Emerald
              else -> if (isDark) Color(0xFF13221E) else Color(0xFFF7F3E9)
            },
            border = BorderStroke(
              1.dp,
              if (isNext) Gold else Gold.copy(alpha = 0.2f)
            ),
            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
          ) {
            Column(
              modifier = Modifier.padding(vertical = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = prayer.nameUrdu,
                fontFamily = UrduFontFamily,
                fontSize = 11.5.sp,
                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                color = if (isNext) Color.White else (if (isDark) DarkTextPrimary else DeepEmerald)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = prayer.time12,
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.5.sp,
                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                color = if (isNext) SoftGold else (if (isDark) DarkTextSoft else Brown)
              )
            }
          }
        }
      }
    }
  }
}

// =========================================================================
// 4. MAIN 15 SECOND HERO (Visual Centerpiece of the App)
// =========================================================================

@Composable
private fun PremiumMomentHero(
  timerState: MomentTimerUiState,
  onStart: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "hero_glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.98f,
    targetValue = 1.025f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Card(
    shape = RoundedCornerShape(26.dp),
    colors = CardDefaults.cardColors(
      containerColor = DeepEmerald
    ),
    border = BorderStroke(1.6.dp, Gold),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("premium_moment_hero")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            listOf(
              DeepEmerald,
              Emerald,
              DeepEmerald
            )
          )
        )
        .padding(20.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Top Ribbon
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Gold.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, Gold.copy(alpha = 0.6f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.AutoAwesome,
              contentDescription = null,
              tint = SoftGold,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "15 SECONDS FOR ALLAH • پندرہ سیکنڈ اللہ کے لیے",
              fontFamily = UrduFontFamily,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = SoftGold
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Center Animated Breathing Ring & 15-Second Dial
        Box(
          modifier = Modifier
            .size(164.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  Gold.copy(alpha = 0.35f),
                  Emerald.copy(alpha = 0.6f),
                  Color.Transparent
                )
              )
            )
            .border(2.5.dp, Gold, CircleShape)
            .clickable { onStart() },
          contentAlignment = Alignment.Center
        ) {
          // Inner Golden Ring
          Box(
            modifier = Modifier
              .size(138.dp)
              .clip(CircleShape)
              .background(DeepEmerald)
              .border(1.2.dp, SoftGold.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "15s",
                fontFamily = FontFamily.Serif,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SoftGold,
                letterSpacing = (-1).sp
              )
              Text(
                text = "ایک سانس، ایک ذکر",
                fontFamily = UrduFontFamily,
                fontSize = 11.5.sp,
                color = Color.White.copy(alpha = 0.9f)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
          text = "دل کا سکون اور رب کی رضا کا لمحہ",
          fontFamily = UrduFontFamily,
          fontSize = 14.sp,
          color = Color.White.copy(alpha = 0.95f),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Main Golden CTA Button
        Button(
          onClick = onStart,
          colors = ButtonDefaults.buttonColors(
            containerColor = Gold,
            contentColor = Color(0xFF1B1305)
          ),
          shape = RoundedCornerShape(16.dp),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("start_15s_hero_button")
        ) {
          Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = Color(0xFF1B1305),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "▶ ابھی ۱۵ سیکنڈ ذکر شروع کریں",
            fontFamily = UrduFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B1305)
          )
        }
      }
    }
  }
}

// =========================================================================
// 5. FEATURED DHIKR / AYAH / HADITH CARD
// =========================================================================

@Composable
private fun PremiumFeaturedDhikrCard(
  content: @Composable () -> Unit
) {
  content()
}

@Composable
private fun FeaturedDhikrCard(
  item: DhikrItem,
  isDark: Boolean,
  onStart15s: () -> Unit,
  onSharePoster: () -> Unit,
  onOpenHadithExplorer: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) DarkCardSurface else CardIvory
    ),
    border = BorderStroke(1.4.dp, Gold.copy(alpha = 0.5f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("featured_dhikr_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Tag & Hadith Explorer Link
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Gold.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f))
        ) {
          Text(
            text = if (item.isQuranic) "✨ درسِ قرآن • آج کا لمحہ ✨" else "✨ مسنون ذکر و دعا • آج کا لمحہ ✨",
            fontFamily = UrduFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Gold else DeepEmerald,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Emerald.copy(alpha = 0.12f),
          border = BorderStroke(1.dp, Emerald.copy(alpha = 0.35f)),
          modifier = Modifier.clickable { onOpenHadithExplorer() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.MenuBook, contentDescription = null, tint = Emerald, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "کتبِ احادیث (36,000+)",
              fontFamily = UrduFontFamily,
              fontSize = 11.sp,
              color = Emerald
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Arabic Calligraphy Text
      Text(
        text = item.arabic,
        fontFamily = ArabicFontFamily,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = if (isDark) DarkArabicText else DeepEmerald,
        textAlign = TextAlign.Center,
        lineHeight = 36.sp,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Urdu Translation
      Text(
        text = item.translationUrdu,
        fontFamily = UrduNastaliqFontFamily,
        fontSize = 14.5.sp,
        color = if (isDark) DarkUrduText else Color(0xFF6B2D10),
        textAlign = TextAlign.Center,
        lineHeight = 24.sp,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(6.dp))

      // English Translation
      Text(
        text = item.translation,
        fontFamily = FontFamily.Serif,
        fontSize = 12.sp,
        color = if (isDark) DarkTextSoft else Muted,
        textAlign = TextAlign.Center,
        lineHeight = 17.sp,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Citation Ribbon
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = Gold.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
      ) {
        Text(
          text = "📖 ${item.source}",
          fontFamily = UrduFontFamily,
          fontSize = 11.sp,
          color = Gold,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onStart15s,
          colors = ButtonDefaults.buttonColors(containerColor = Emerald),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.weight(1f).height(44.dp)
        ) {
          Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("۱۵ سیکنڈ شروع کریں", fontFamily = UrduFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onSharePoster,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E5A)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.weight(1f).height(44.dp)
        ) {
          Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("واٹس ایپ پوسٹر", fontFamily = UrduFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// =========================================================================
// 6. SPIRITUAL JOURNEY & CONSISTENCY CARD
// =========================================================================

@Composable
private fun PremiumJourneyCard(
  streakCount: Int,
  totalMoments: Int,
  onViewInsights: () -> Unit
) {
  val isDark = isSystemInDarkTheme()

  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) DarkCardSurface else CardIvory
    ),
    border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.45f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onViewInsights() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Gold.copy(alpha = 0.18f))
            .border(1.2.dp, Gold, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = Color(0xFFE65100),
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "روحانی تسلسل و استقامت",
            fontFamily = UrduFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) DarkTextPrimary else DeepEmerald
          )
          Text(
            text = "$streakCount مسلسل دن • $totalMoments لمحات مکمل",
            fontFamily = UrduFontFamily,
            fontSize = 11.5.sp,
            color = if (isDark) DarkTextSoft else Brown
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Emerald.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Emerald.copy(alpha = 0.35f))
      ) {
        Text(
          text = "تفصیلات ➔",
          fontFamily = UrduFontFamily,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Emerald,
          modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
      }
    }
  }
}

// =========================================================================
// 7. REMINDER INTERVAL SELECTOR CARD
// =========================================================================

@Composable
private fun PremiumReminderCard(
  currentInterval: String,
  onIntervalSelected: (String) -> Unit
) {
  val isDark = isSystemInDarkTheme()
  val intervals = listOf(
    "Every 15 min (15 منٹ)" to "15 منٹ",
    "Every 30 min (30 منٹ)" to "30 منٹ",
    "Every 1 hour (1 گھنٹہ)" to "1 گھنٹہ",
    "Every 2 hours (2 گھنٹے)" to "2 گھنٹے"
  )

  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) DarkCardSurface else CardIvory
    ),
    border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.45f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.NotificationsActive,
            contentDescription = null,
            tint = Gold,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "یاد دہانی کا وقفہ (Reminder Rhythm)",
            fontFamily = UrduFontFamily,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) DarkTextPrimary else DeepEmerald
          )
        }

        Text(
          text = "۵ سیکنڈ پاپ اپ",
          fontFamily = UrduFontFamily,
          fontSize = 11.sp,
          color = Gold
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        intervals.forEach { (key, label) ->
          val isSelected = currentInterval.contains(label.take(2))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
              isSelected -> Emerald
              else -> if (isDark) Color(0xFF13221E) else Color(0xFFF7F3E9)
            },
            border = BorderStroke(
              1.dp,
              if (isSelected) Gold else Gold.copy(alpha = 0.25f)
            ),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { onIntervalSelected(key) }
          ) {
            Text(
              text = label,
              fontFamily = UrduFontFamily,
              fontSize = 11.5.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) Color.White else (if (isDark) DarkTextPrimary else DeepEmerald),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 8.dp)
            )
          }
        }
      }
    }
  }
}
