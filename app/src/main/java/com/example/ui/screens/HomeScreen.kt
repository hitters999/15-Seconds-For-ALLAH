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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.components.DailyMomentRing
import com.example.ui.components.ParchmentBackground
import com.example.ui.components.QuickCategoryCard
import com.example.ui.components.SerenePrimaryButton
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSubtle
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft
import com.example.ui.theme.UrduFontFamily
import com.example.util.ShareHelper

@Composable
fun HomeScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val userSettings by viewModel.userSettings.collectAsState()
  val todayLogs by viewModel.todayLogs.collectAsState()
  val allItems by viewModel.allItems.collectAsState()
  val scrollState = rememberScrollState()
  val context = LocalContext.current

  val dailyGoal = userSettings.dailyGoal.coerceAtLeast(1)
  val todayCount = todayLogs.size
  val progress = (todayCount.toFloat() / dailyGoal).coerceIn(0f, 1f)

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

      // Top Header: Greeting, User Name & Official Brand Logo
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "السَّلَامُ عَلَيْكُمْ • As-salamu alaykum",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.5.sp,
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

        // Official Brand Logo in the Home Header
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .border(1.5.dp, BronzeGold, CircleShape)
            .background(Color.White)
            .clickable { viewModel.navigateTo(Screen.Profile) }
            .testTag("home_brand_logo"),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.app_brand_logo),
            contentDescription = "15 Seconds for Allah Logo",
            modifier = Modifier.size(46.dp),
            contentScale = ContentScale.Fit
          )
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // Notification Reminder Status Pill / Quick Tester
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BronzeGold.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.sendTestNotificationNow() }
          .testTag("home_test_notification_pill")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.NotificationsActive,
              contentDescription = null,
              tint = BronzeGold,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "یاد دہانی: ہر 1 گھنٹے بعد (Active • Beep)",
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = InkTeal
              )
              Text(
                text = "5 سیکنڈ فلوٹنگ پاپ اپ اور بیپ ساؤنڈ چیک کریں",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.5.sp,
                color = TextSoft
              )
            }
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = BronzeGold
          ) {
            Text(
              text = "ٹیسٹ 5s",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

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

      // Quick Categories Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickCategoryCard(
          title = "Dhikr ذکر",
          icon = Icons.Filled.Spa,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.setSelectedCategory("Gratitude")
            viewModel.navigateTo(Screen.Library)
          }
        )
        QuickCategoryCard(
          title = "Dua دعا",
          icon = Icons.Filled.AutoAwesome,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.setSelectedCategory("Forgiveness")
            viewModel.navigateTo(Screen.Library)
          }
        )
        QuickCategoryCard(
          title = "Qur'an قرآن",
          icon = Icons.Filled.Book,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.setSelectedCategory("Quranic Gems")
            viewModel.navigateTo(Screen.Library)
          }
        )
        QuickCategoryCard(
          title = "صبح و شام",
          icon = Icons.Filled.Mosque,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.setSelectedCategory("Morning Remembrance")
            viewModel.navigateTo(Screen.Library)
          }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Primary Action Card: Featured Moment with Urdu & English
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
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "اس وقت کا ذکر • MOMENT FOR NOW",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 10.sp,
              color = BronzeGold,
              letterSpacing = 1.sp
            )
            Text(
              text = "15 سیکنڈز",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Medium,
              fontSize = 11.sp,
              color = TextSoft
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Distinct Bismillah Header if Quranic Ayah
          if (featuredItem.isQuranic) {
            com.example.ui.components.BismillahCalligraphyHeader(isDarkTheme = false)
            Spacer(modifier = Modifier.height(10.dp))
          }

          // Arabic with Amiri font
          Text(
            text = featuredItem.arabic,
            fontFamily = ArabicFontFamily,
            fontSize = 25.sp,
            color = InkTeal,
            lineHeight = 36.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Transliteration
          Text(
            text = featuredItem.transliteration,
            fontFamily = FontFamily.Serif,
            fontSize = 13.5.sp,
            color = TextSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Urdu Translation (Prominent with Noto Nastaliq font)
          Text(
            text = featuredItem.translationUrdu,
            fontFamily = UrduFontFamily,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF8A5A1A),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(4.dp))

          // English Translation
          Text(
            text = featuredItem.translation,
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(16.dp))

          SerenePrimaryButton(
            text = "15 سیکنڈ کا ذکر شروع کریں (Begin Moment)",
            onClick = {
              viewModel.selectDhikrForMoment(featuredItem, startImmediately = true)
            }
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Share & Bookmark Quick Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = ParchmentSurface,
              border = BorderStroke(1.dp, ParchmentBorder),
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  viewModel.toggleBookmark(featuredItem.id)
                }
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (featuredItem.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                  contentDescription = null,
                  tint = if (featuredItem.isBookmarked) BronzeGold else TextSoft,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (featuredItem.isBookmarked) "محفوظ ہے ✓" else "محفوظ کریں",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (featuredItem.isBookmarked) BronzeGold else TextPrimary
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF25D366).copy(alpha = 0.12f),
              border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.35f)),
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  ShareHelper.shareToWhatsApp(context, featuredItem)
                }
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
                  text = "WhatsApp",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E8E3E)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = InkTeal.copy(alpha = 0.08f),
              border = BorderStroke(1.dp, InkTeal.copy(alpha = 0.3f)),
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  ShareHelper.shareDhikr(context, featuredItem)
                }
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Share,
                  contentDescription = null,
                  tint = InkTeal,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "شیئر (All)",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = InkTeal
                )
              }
            }
          }
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
              text = "سب سے بابرکت گھڑی فجر کی ہے۔",
              fontFamily = FontFamily.Serif,
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Medium,
              color = InkTeal
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "دنیا جاگنے سے پہلے 15 سیکنڈ اپنے رب کے لیے نکالیں۔",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.5.sp,
              color = TextSoft
            )
          }
        }
      }
    }
  }
}
