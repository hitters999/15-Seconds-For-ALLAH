package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.DhikrItem
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.BismillahCalligraphyHeader
import com.example.ui.components.ParchmentBackground
import com.example.ui.components.PosterPreviewDialog
import com.example.ui.components.PrayerTimesSection
import com.example.ui.components.SerenePrimaryButton
import com.example.ui.components.TimezoneSelectionDialog
import com.example.util.AppTimeHelper
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldDark
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.DarkAccentGold
import com.example.ui.theme.DarkArabicText
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.DarkTextSoft
import com.example.ui.theme.DarkUrduText
import com.example.ui.theme.InkTeal
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft
import com.example.ui.theme.UrduFontFamily
import com.example.util.ShareHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val userSettings by viewModel.userSettings.collectAsState()
  val todayLogs by viewModel.todayLogs.collectAsState()
  val streak by viewModel.streakCount.collectAsState()
  val featuredItem by viewModel.rotatingFeaturedDhikr.collectAsState()
  val prayerTimesState by viewModel.prayerTimesState.collectAsState()
  val allItems by viewModel.allItems.collectAsState()
  val scrollState = rememberScrollState()
  val context = LocalContext.current

  // State to control Prayer Times dialog (moved off the front page so 15s for Allah is prominent)
  var showPrayerTimesDialog by remember { mutableStateOf(false) }

  // Theme-aware high contrast tokens to avoid dark green on black
  val isDark = isSystemInDarkTheme()
  val cardBg = if (isDark) DarkCardSurface else Color.White
  val cardBorder = if (isDark) DarkCardBorder else BronzeGoldLight.copy(alpha = 0.55f)
  val titleColor = if (isDark) DarkTextPrimary else Color(0xFF0F172A)
  val subtitleColor = if (isDark) DarkTextSoft else Color(0xFF4B5563)
  val arabicColor = if (isDark) DarkArabicText else Color(0xFF064E3B)
  val urduColor = if (isDark) DarkUrduText else Color(0xFF78350F)
  val goldColor = if (isDark) DarkAccentGold else BronzeGold

  val locationPermissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (fine || coarse) {
      viewModel.fetchPrayerTimes(useGps = true)
    }
  }

  val dynamicGreeting by viewModel.dynamicGreeting.collectAsState()
  val (urduGreeting, salamText) = dynamicGreeting
  val currentDateFormatted by viewModel.currentDateFormatted.collectAsState()
  val currentTimeFormatted by viewModel.currentTimeFormatted.collectAsState()
  var showTimezoneDialog by remember { mutableStateOf(false) }
  var previewPosterItem by remember { mutableStateOf<DhikrItem?>(null) }

  val dailyGoal = userSettings.dailyGoal.coerceAtLeast(1)
  val todayCount = todayLogs.size

  // Filter curated Juz 30 items
  val juz30List = remember(allItems) {
    allItems.filter { it.category.contains("Juz 30") }
  }

  ParchmentBackground(modifier = modifier, showMadinahBackdrop = true) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 18.dp)
        .padding(bottom = 90.dp)
        .testTag("home_screen")
    ) {
      Spacer(modifier = Modifier.height(12.dp))

      // ==========================================
      // SECTION 1: ROYAL ISLAMIC BRAND BAR & SPIRITUAL MARQUEE
      // ==========================================
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.2.dp, goldColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        ) {
          // Top Row: Logo + App Name + Live Timezone Capsule
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.clickable { viewModel.navigateTo(Screen.Profile) }
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .border(1.5.dp, goldColor, CircleShape)
                  .background(Color.White)
                  .testTag("home_brand_logo"),
                contentAlignment = Alignment.Center
              ) {
                Image(
                  painter = painterResource(id = R.drawable.app_brand_logo),
                  contentDescription = "App Logo",
                  modifier = Modifier.size(38.dp),
                  contentScale = ContentScale.Fit
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "15 Seconds for Allah",
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.5.sp,
                  color = titleColor,
                  letterSpacing = 0.3.sp
                )
                Text(
                  text = "۱۵ سیکنڈز برائے اللہ • لمحہِ ذکر و دعا",
                  fontFamily = UrduFontFamily,
                  fontSize = 11.5.sp,
                  color = goldColor
                )
              }
            }

            // Live Clock & Timezone Pill
            val tzOption = AppTimeHelper.getTimezoneOption(userSettings.selectedTimezone)
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = goldColor.copy(alpha = 0.12f),
              border = BorderStroke(1.dp, goldColor.copy(alpha = 0.40f)),
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { showTimezoneDialog = true }
                .testTag("timezone_badge")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Filled.AccessTime, contentDescription = null, tint = goldColor, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "$currentTimeFormatted • ${tzOption.flagEmoji}",
                  fontFamily = FontFamily.Serif,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = titleColor
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Subtle hairline divider
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(0.8.dp)
              .background(goldColor.copy(alpha = 0.25f))
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Bottom Row: Salam & Dynamic Greeting + Prayer Times Action
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "السلام علیکم ورحمۃ اللہ",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = titleColor
              )
              Text(
                text = "$urduGreeting • $salamText",
                fontFamily = UrduFontFamily,
                fontSize = 12.sp,
                color = goldColor
              )
            }

            // Prayer Times Button
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = goldColor.copy(alpha = 0.14f),
              border = BorderStroke(1.dp, goldColor.copy(alpha = 0.45f)),
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { showPrayerTimesDialog = true }
                .testTag("open_prayer_times_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Filled.Mosque, contentDescription = null, tint = goldColor, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = "اوقاتِ نماز",
                  fontFamily = UrduFontFamily,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = titleColor
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // ==========================================
      // SECTION 2: THE HERO CENTERPIECE — "15 Seconds for ALLAH"
      // (Brings our core value to the undisputed front and center!)
      // ==========================================
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.8.dp, goldColor.copy(alpha = 0.70f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("hero_15_seconds_card")
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top Ribbon: Core Spiritual Mission
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = goldColor.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, goldColor.copy(alpha = 0.35f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Timer,
                  contentDescription = null,
                  tint = goldColor,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = "CORE SPIRITUAL VALUE",
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  color = goldColor
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFE65100).copy(alpha = 0.12f),
              border = BorderStroke(1.dp, Color(0xFFE65100).copy(alpha = 0.30f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.LocalFireDepartment,
                  contentDescription = null,
                  tint = Color(0xFFE65100),
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "$streak دن تسلسل",
                  fontFamily = UrduFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = Color(0xFFE65100)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Big Core Title: 15 Seconds for ALLAH
          Text(
            text = "15 Seconds for ALLAH",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp,
            color = titleColor,
            textAlign = TextAlign.Center
          )

          Text(
            text = "پندرہ سیکنڈ اللہ کے لیے",
            fontFamily = UrduFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = urduColor,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(6.dp))

          // Core Value Tagline
          Text(
            text = "اپنے مصروف ترین دن میں سے صرف 15 سیکنڈ اپنے رب کو یاد کرنے کے لیے وقف کریں۔",
            fontFamily = UrduFontFamily,
            fontSize = 13.5.sp,
            color = subtitleColor,
            textAlign = TextAlign.Center,
            lineHeight = 21.sp,
            modifier = Modifier.fillMaxWidth(0.92f)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Visual 15-Second Circular Dial Indicator
          Box(
            modifier = Modifier
              .size(92.dp)
              .clip(CircleShape)
              .background(goldColor.copy(alpha = 0.08f))
              .border(2.5.dp, goldColor, CircleShape)
              .clickable {
                viewModel.selectDhikrForMoment(featuredItem, startImmediately = true)
              },
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "15s",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = titleColor
              )
              Text(
                text = "لمحہِ ذکر",
                fontFamily = UrduFontFamily,
                fontSize = 10.sp,
                color = goldColor
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Grand Action Button: Begin 15 Seconds Moment
          SerenePrimaryButton(
            text = "⚡ 15 سیکنڈ کا ذکر شروع کریں (Begin Moment)",
            onClick = {
              viewModel.selectDhikrForMoment(featuredItem, startImmediately = true)
            },
            modifier = Modifier.testTag("begin_moment_button")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Daily Progress counter
          Text(
            text = "آج کا ہدف: $todayCount / $dailyGoal مکمل • تسلسل جاری رکھیں",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.5.sp,
            color = subtitleColor
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // SECTION 3: HERO POPUP NOTIFICATION STATUS PILL (ONLY POPUP WINDOW)
      // ==========================================
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = cardBg,
        border = BorderStroke(1.2.dp, cardBorder),
        shadowElevation = 2.dp,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_popup_status_pill")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(goldColor.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.NotificationsActive,
                contentDescription = null,
                tint = goldColor,
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "فلوٹنگ پاپ اپ بینر (5s Non-Blocking)",
                  fontFamily = UrduFontFamily,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = titleColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                ) {
                  Text(
                    text = "5 سیکنڈز • فعال ✓",
                    fontFamily = UrduFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "5 سیکنڈز نان بلاکنگ پاپ اپ • بغیر رکے کام جاری رہے گا (15 منٹ شیڈول)",
                fontFamily = UrduFontFamily,
                fontSize = 11.sp,
                color = subtitleColor
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          // 1-Click Test Button for Hero Popup Window
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = goldColor,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { viewModel.sendTestNotificationNow() }
              .testTag("test_popup_window_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "ٹیسٹ 5s پاپ اپ",
                fontFamily = UrduFontFamily,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // ==========================================
      // SECTION 4: FEATURED AYAH OF THE MOMENT (High-Contrast Reference Design)
      // ==========================================
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.8.dp, goldColor.copy(alpha = 0.65f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("featured_dhikr_card")
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top Ribbon: "TODAY'S MOMENT • آج کا لمحہ" + 2-Hour Auto-Rotation Tag & Shuffle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = goldColor.copy(alpha = 0.12f),
              border = BorderStroke(1.dp, goldColor.copy(alpha = 0.30f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.AutoAwesome,
                  contentDescription = null,
                  tint = goldColor,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = "آج کا درسِ قرآن • 2 گھنٹے میں تبدیلی",
                  fontFamily = UrduFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = titleColor
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = cardBg,
              border = BorderStroke(1.dp, cardBorder),
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { viewModel.rotateFeaturedDhikrNow() }
                .testTag("shuffle_dhikr_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Refresh,
                  contentDescription = "Shuffle",
                  tint = goldColor,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "اگلی آیت",
                  fontFamily = UrduFontFamily,
                  fontSize = 11.sp,
                  color = goldColor,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Ornate Bismillah Header if Quranic
          if (featuredItem.isQuranic) {
            BismillahCalligraphyHeader(isDarkTheme = isDark)
            Spacer(modifier = Modifier.height(10.dp))
          }

          // Sacred Arabic Text in Amiri Typography with High Contrast
          Text(
            text = featuredItem.arabic,
            fontFamily = ArabicFontFamily,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = arabicColor,
            lineHeight = 40.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Transliteration
          Text(
            text = featuredItem.transliteration,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            color = subtitleColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Sacred Urdu Translation in high-contrast color
          Text(
            text = featuredItem.translationUrdu,
            fontFamily = UrduFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = urduColor,
            textAlign = TextAlign.Center,
            lineHeight = 25.sp,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(6.dp))

          // English Translation
          Text(
            text = featuredItem.translation,
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = titleColor,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Citation Source Badge
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = goldColor.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, goldColor.copy(alpha = 0.25f))
          ) {
            Text(
              text = "📖 ${featuredItem.source}",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = goldColor,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
            )
          }

          if (featuredItem.contemplativeNote.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isDark) DarkCardBorder.copy(alpha = 0.5f) else Color(0xFFF9F5EC),
              border = BorderStroke(0.8.dp, cardBorder)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.FormatQuote,
                  contentDescription = null,
                  tint = goldColor,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "سبق و تدبر: ${featuredItem.contemplativeNote}",
                  fontFamily = UrduFontFamily,
                  fontSize = 12.sp,
                  color = subtitleColor,
                  lineHeight = 18.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Action Row: Bookmark, WhatsApp Status Poster, All Apps Poster
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = cardBg,
              border = BorderStroke(1.dp, cardBorder),
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable { viewModel.toggleBookmark(featuredItem.id) }
                .testTag("bookmark_button")
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (featuredItem.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                  contentDescription = null,
                  tint = if (featuredItem.isBookmarked) goldColor else subtitleColor,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (featuredItem.isBookmarked) "محفوظ ہے ✓" else "محفوظ کریں",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (featuredItem.isBookmarked) goldColor else titleColor
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF25D366).copy(alpha = 0.12f),
              border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.35f)),
              modifier = Modifier
                .weight(1.2f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  ShareHelper.shareToWhatsApp(context, featuredItem)
                }
                .testTag("whatsapp_share_button")
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Share,
                  contentDescription = null,
                  tint = Color(0xFF1E8E3E),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "پوسٹر WhatsApp",
                  fontFamily = UrduFontFamily,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E8E3E)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = goldColor.copy(alpha = 0.14f),
              border = BorderStroke(1.dp, goldColor.copy(alpha = 0.4f)),
              modifier = Modifier
                .weight(1.1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  previewPosterItem = featuredItem
                }
                .testTag("all_share_button")
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.AutoAwesome,
                  contentDescription = null,
                  tint = goldColor,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "پوسٹر دیکھیں",
                  fontFamily = UrduFontFamily,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = titleColor
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // ==========================================
      // SECTION 5: "تیسواں سپارہ - حکمت و عبرت کے موتی"
      // (Curated meaningful Ayat of Juz 30 as requested by the user!)
      // ==========================================
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "تیسواں پارہ • منتخب موتی",
              fontFamily = UrduFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = titleColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = goldColor.copy(alpha = 0.15f)
            ) {
              Text(
                text = "${juz30List.size} اہم اسباق",
                fontFamily = UrduFontFamily,
                fontSize = 10.sp,
                color = goldColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
              )
            }
          }
          Text(
            text = "پورے سپارے سے دل کو چھو لینے والی اہم ترین آیات و اسباق",
            fontFamily = UrduFontFamily,
            fontSize = 12.sp,
            color = subtitleColor
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = cardBg,
          border = BorderStroke(1.dp, cardBorder),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { viewModel.navigateTo(Screen.Library) }
        ) {
          Text(
            text = "سب دیکھیں →",
            fontFamily = UrduFontFamily,
            fontSize = 11.sp,
            color = goldColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Horizontal list of Curated Juz 30 Gems
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(juz30List.take(12)) { juzItem ->
          Juz30Card(
            item = juzItem,
            isDark = isDark,
            cardBg = cardBg,
            cardBorder = cardBorder,
            titleColor = titleColor,
            subtitleColor = subtitleColor,
            arabicColor = arabicColor,
            urduColor = urduColor,
            goldColor = goldColor,
            onSelect = {
              viewModel.selectDhikrForMoment(juzItem, startImmediately = true)
            },
            onShare = {
              ShareHelper.shareToWhatsApp(context, juzItem)
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // ==========================================
      // SECTION 6: SPIRITUAL QUICK ACCESS TILES
      // ==========================================
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionCard(
          title = "مکینیکل گھڑی",
          subtitle = "15s اینالاگ ڈائل",
          icon = Icons.Filled.Timer,
          accentColor = goldColor,
          cardBg = cardBg,
          cardBorder = cardBorder,
          titleColor = titleColor,
          subtitleColor = subtitleColor,
          modifier = Modifier.weight(1f),
          onClick = { viewModel.navigateTo(Screen.Moment) }
        )

        QuickActionCard(
          title = "$streak دن تسلسل",
          subtitle = "$todayCount / $dailyGoal مکمل لمحات",
          icon = Icons.Filled.LocalFireDepartment,
          accentColor = Color(0xFFE65100),
          cardBg = cardBg,
          cardBorder = cardBorder,
          titleColor = titleColor,
          subtitleColor = subtitleColor,
          modifier = Modifier.weight(1f),
          onClick = { viewModel.navigateTo(Screen.Insights) }
        )

        QuickActionCard(
          title = "جامع لائبریری",
          subtitle = "1000+ اذکار و دعائیں",
          icon = Icons.Filled.Book,
          accentColor = goldColor,
          cardBg = cardBg,
          cardBorder = cardBorder,
          titleColor = titleColor,
          subtitleColor = subtitleColor,
          modifier = Modifier.weight(1f),
          onClick = { viewModel.navigateTo(Screen.Library) }
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // ==========================================
      // SECTION 7: SERENE WISDOM QUOTE
      // ==========================================
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.2.dp, cardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(goldColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.FormatQuote,
              contentDescription = null,
              tint = goldColor,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
              fontFamily = ArabicFontFamily,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = arabicColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "خبردار! اللہ کے ذکر ہی سے دلوں کو سچا اطمینان حاصل ہوتا ہے۔",
              fontFamily = UrduFontFamily,
              fontSize = 12.sp,
              color = subtitleColor
            )
          }
        }
      }
    }
  }

  // ==========================================
  // PRAYER TIMES DIALOG (Subtly available on request, NOT blocking front page!)
  // ==========================================
  if (showPrayerTimesDialog) {
    Dialog(onDismissRequest = { showPrayerTimesDialog = false }) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = cardBg,
        border = BorderStroke(1.5.dp, goldColor.copy(alpha = 0.6f)),
        shadowElevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          // Dialog Header with Close Button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.Mosque,
                contentDescription = null,
                tint = goldColor,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "اوقاتِ نماز (Local Prayer Times)",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = titleColor
              )
            }

            IconButton(
              onClick = { showPrayerTimesDialog = false },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                tint = subtitleColor,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Embedded Prayer Times Section
          PrayerTimesSection(
            prayerState = prayerTimesState,
            onRefreshClick = { viewModel.fetchPrayerTimes(useGps = true) },
            onRequestLocationClick = {
              val fineGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
              ) == PackageManager.PERMISSION_GRANTED
              val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
              ) == PackageManager.PERMISSION_GRANTED

              if (fineGranted || coarseGranted) {
                viewModel.fetchPrayerTimes(useGps = true)
              } else {
                locationPermissionLauncher.launch(
                  arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                  )
                )
              }
            },
            currentTimeFormatted = currentTimeFormatted,
            onOpenTimezoneClick = { showTimezoneDialog = true }
          )
        }
      }
    }
  }

  // Timezone Selection Dialog
  if (showTimezoneDialog) {
    TimezoneSelectionDialog(
      selectedTimezoneId = userSettings.selectedTimezone,
      onDismissRequest = { showTimezoneDialog = false },
      onSelectTimezone = { tzId ->
        viewModel.updateSelectedTimezone(tzId)
      }
    )
  }

  // Islamic Poster Full-Screen Preview Dialog
  previewPosterItem?.let { item ->
    PosterPreviewDialog(
      dhikr = item,
      onDismiss = { previewPosterItem = null }
    )
  }
}

@Composable
private fun Juz30Card(
  item: DhikrItem,
  isDark: Boolean,
  cardBg: Color,
  cardBorder: Color,
  titleColor: Color,
  subtitleColor: Color,
  arabicColor: Color,
  urduColor: Color,
  goldColor: Color,
  onSelect: () -> Unit,
  onShare: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    border = BorderStroke(1.2.dp, cardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .width(260.dp)
      .clip(RoundedCornerShape(18.dp))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Citation Source Tag
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = goldColor.copy(alpha = 0.12f),
          border = BorderStroke(0.8.dp, goldColor.copy(alpha = 0.3f))
        ) {
          Text(
            text = item.source,
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = goldColor,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
          )
        }

        Icon(
          imageVector = Icons.Filled.Share,
          contentDescription = "Share",
          tint = subtitleColor,
          modifier = Modifier
            .size(15.dp)
            .clickable(onClick = onShare)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Arabic Snippet
      Text(
        text = item.arabic,
        fontFamily = ArabicFontFamily,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = arabicColor,
        lineHeight = 26.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Urdu Translation
      Text(
        text = item.translationUrdu,
        fontFamily = UrduFontFamily,
        fontSize = 12.5.sp,
        color = urduColor,
        lineHeight = 18.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Contemplative Note (سبق)
      if (item.contemplativeNote.isNotBlank()) {
        Text(
          text = "• ${item.contemplativeNote}",
          fontFamily = UrduFontFamily,
          fontSize = 11.sp,
          color = subtitleColor,
          lineHeight = 15.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Button: 15s Moment
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = goldColor,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable(onClick = onSelect)
      ) {
        Row(
          modifier = Modifier.padding(vertical = 6.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "15s ذکر شروع کریں",
            fontFamily = UrduFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun QuickActionCard(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  cardBg: Color,
  cardBorder: Color,
  titleColor: Color,
  subtitleColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    border = BorderStroke(1.2.dp, cardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = accentColor,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 11.5.sp,
        color = titleColor,
        textAlign = TextAlign.Center,
        maxLines = 1
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        fontFamily = FontFamily.SansSerif,
        fontSize = 9.5.sp,
        color = subtitleColor,
        textAlign = TextAlign.Center,
        maxLines = 1
      )
    }
  }
}
