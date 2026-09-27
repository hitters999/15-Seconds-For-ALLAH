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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DhikrItem
import com.example.data.MomentLogEntity
import com.example.notification.FloatingPopupManager
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.BismillahCalligraphyHeader
import com.example.ui.components.ParchmentBackground
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val userSettings by viewModel.userSettings.collectAsState()
  val totalMoments by viewModel.totalMomentsCount.collectAsState()
  val streak by viewModel.streakCount.collectAsState()
  val bookmarkedItems by viewModel.bookmarkedItems.collectAsState()
  val recentLogs by viewModel.recentLogs.collectAsState()
  val scrollState = rememberScrollState()
  val context = LocalContext.current

  var showNameDialog by remember { mutableStateOf(false) }
  var showIntervalDialog by remember { mutableStateOf(false) }
  var showGoalDialog by remember { mutableStateOf(false) }
  var showClearDialog by remember { mutableStateOf(false) }
  var showAuthDialog by remember { mutableStateOf(false) }
  var tempName by remember { mutableStateOf("") }
  var tempEmail by remember { mutableStateOf("") }

  ParchmentBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp)
        .padding(bottom = 90.dp)
        .testTag("profile_screen"),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(14.dp))

      // 1. Google Account & Profile Header Banner
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = InkTeal),
        border = BorderStroke(1.5.dp, BronzeGoldLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("account_profile_card")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0x33B8863B),
              border = BorderStroke(1.dp, BronzeGoldLight.copy(alpha = 0.5f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Star,
                  contentDescription = null,
                  tint = BronzeGoldLight,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (userSettings.isSignedIn) "گوگل سے محفوظ شدہ ✓" else "مہمان موڈ (Guest)",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  color = ParchmentSurface
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (userSettings.isSignedIn) Color(0xFF1B5E20) else BronzeGold,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  tempName = userSettings.userName
                  tempEmail = userSettings.userEmail
                  showAuthDialog = true
                }
                .testTag("google_auth_btn")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (userSettings.isSignedIn) Icons.Filled.Sync else Icons.Filled.Login,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (userSettings.isSignedIn) "Google Profile" else "Google Sign-In",
                  fontFamily = FontFamily.SansSerif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = Color.White
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Avatar / Brand Logo
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(76.dp)
              .clip(CircleShape)
              .border(2.dp, BronzeGoldLight, CircleShape)
              .background(Color.White)
          ) {
            Image(
              painter = painterResource(id = R.drawable.app_brand_logo),
              contentDescription = "Official Logo",
              modifier = Modifier.size(72.dp),
              contentScale = ContentScale.Fit
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // User Name & Email
          Text(
            text = userSettings.userName,
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = userSettings.userEmail,
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = BronzeGoldLight
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Spiritual Rank Badge
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0x33000000),
            border = BorderStroke(1.dp, BronzeGold.copy(alpha = 0.4f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = userSettings.spiritualRank,
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFE8B2)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. Spiritual Score & Milestone System Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ParchmentCard),
        border = BorderStroke(1.2.dp, BronzeGold),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("score_system_card")
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
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color(0x22B8863B))
              ) {
                Icon(
                  imageVector = Icons.Filled.EmojiEvents,
                  contentDescription = null,
                  tint = BronzeGold,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "روحانی اسکور (Spiritual Score)",
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = InkTeal
                )
                Text(
                  text = "ہر 15 سیکنڈ کے ذکر پر 10 حسنات / پوائنٹس",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 10.5.sp,
                  color = TextSoft
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFF9F3EA),
              border = BorderStroke(1.dp, BronzeGoldLight)
            ) {
              Text(
                text = "${userSettings.totalScore} pts",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = BronzeGold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Progress bar toward next rank
          val nextRankThreshold = when {
            userSettings.totalScore < 800 -> 800
            userSettings.totalScore < 2000 -> 2000
            userSettings.totalScore < 5000 -> 5000
            else -> 10000
          }
          val progress = (userSettings.totalScore.toFloat() / nextRankThreshold).coerceIn(0f, 1f)

          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "اگلا درجہ (Next Rank):",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = TextSoft
              )
              Text(
                text = "${userSettings.totalScore} / $nextRankThreshold pts",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = BronzeGold,
                fontWeight = FontWeight.SemiBold
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = BronzeGold,
              trackColor = ParchmentBorder
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4-Metric Score Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            ScoreTile(
              title = "مستقل مزاجی",
              value = "$streak دن",
              subtitle = "Streak",
              modifier = Modifier.weight(1f)
            )
            ScoreTile(
              title = "مکمل اذکار",
              value = "$totalMoments",
              subtitle = "Moments",
              modifier = Modifier.weight(1f)
            )
            ScoreTile(
              title = "محفوظ آیات",
              value = "${bookmarkedItems.size}",
              subtitle = "Saved",
              modifier = Modifier.weight(1f)
            )
            ScoreTile(
              title = "کلاؤڈ سنک",
              value = if (userSettings.isSignedIn) "محفوظ ✓" else "آف لائن",
              subtitle = "Cloud",
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 3. Saved Ayaat & Duas Section (محفوظ آیات)
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.2.dp, BronzeGold),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("saved_ayaat_card")
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
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(Color(0x221B4E48))
              ) {
                Icon(
                  imageVector = Icons.Filled.Bookmark,
                  contentDescription = null,
                  tint = InkTeal,
                  modifier = Modifier.size(19.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "محفوظ شدہ آیات و اذکار (Saved Ayaat)",
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.5.sp,
                  color = InkTeal
                )
                Text(
                  text = "آپ کی پسندیدہ قرآنی آیات اور دعائیں",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 10.5.sp,
                  color = TextSoft
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = InkTeal
            ) {
              Text(
                text = "${bookmarkedItems.size} آیات",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          if (bookmarkedItems.isEmpty()) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = ParchmentSubtle,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "ابھی تک کوئی آیت محفوظ نہیں کی گئی",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 12.sp,
                  color = TextSoft
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = BronzeGold,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.navigateTo(Screen.Library) }
                ) {
                  Text(
                    text = "30ویں سپارے سے آیات محفوظ کریں",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }
              }
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              bookmarkedItems.take(5).forEach { item ->
                SavedAyahItemRow(
                  item = item,
                  onRead = { viewModel.selectDhikrForMoment(item, startImmediately = true) },
                  onShare = { ShareHelper.shareDhikr(context, item) },
                  onRemove = { viewModel.toggleBookmark(item.id) }
                )
              }

              if (bookmarkedItems.size > 5) {
                TextButton(
                  onClick = { viewModel.navigateTo(Screen.Library) },
                  modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                  Text(
                    text = "تمام ${bookmarkedItems.size} محفوظ آیات دیکھیں (View All in Library)",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.5.sp,
                    color = BronzeGold,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 4. Recent Presence History (تازہ ترین ہسٹری)
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ParchmentCard),
        border = BorderStroke(1.dp, ParchmentBorder),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("presence_history_card")
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
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(Color(0x22B8863B))
              ) {
                Icon(
                  imageVector = Icons.Filled.History,
                  contentDescription = null,
                  tint = BronzeGold,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "اذکار کی ہسٹری (Recent Moments)",
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.5.sp,
                  color = InkTeal
                )
                Text(
                  text = "آپ کے مکمل کردہ 15 سیکنڈ کے اذکار",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 10.5.sp,
                  color = TextSoft
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = BronzeGold,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { viewModel.navigateTo(Screen.Insights) }
            ) {
              Text(
                text = "مکمل ریکارڈ",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
          recentLogs.take(4).forEach { log ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(BronzeGold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = log.title,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${log.category} • ${timeFormat.format(Date(log.timestamp))}",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    color = TextSoft
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFE8F5E9)
              ) {
                Text(
                  text = "+10 pts",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF2E7D32),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 5. Social Sharing Hub (WhatsApp, X, Facebook)
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.2.dp, BronzeGold),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("social_share_hub_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0x221B4E48))
            ) {
              Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = null,
                tint = InkTeal,
                modifier = Modifier.size(19.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "شیئر کریں • صدقہ جاریہ (Share & Earn)",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = InkTeal
              )
              Text(
                text = "قرآنی آیات اور اذکار دوستوں اور احباب سے شیئر کریں",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.5.sp,
                color = TextSoft
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // App / Ayah Share Buttons Row
          val featuredItem = viewModel.allItems.collectAsState().value.firstOrNull() ?: bookmarkedItems.firstOrNull()
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ShareAppButton(
              title = "WhatsApp",
              color = Color(0xFF25D366),
              onClick = {
                featuredItem?.let { ShareHelper.shareToWhatsApp(context, it) }
              },
              modifier = Modifier.weight(1f)
            )
            ShareAppButton(
              title = "X (Twitter)",
              color = Color(0xFF000000),
              onClick = {
                featuredItem?.let { ShareHelper.shareToTwitter(context, it) }
              },
              modifier = Modifier.weight(1f)
            )
            ShareAppButton(
              title = "Facebook",
              color = Color(0xFF1877F2),
              onClick = {
                featuredItem?.let { ShareHelper.shareToFacebook(context, it) }
              },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 6. Test Notification Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.2.dp, BronzeGold),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.sendTestNotificationNow() }
          .testTag("test_notification_btn")
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(InkTeal)
            ) {
              Icon(
                imageVector = Icons.Filled.NotificationsActive,
                contentDescription = null,
                tint = BronzeGoldLight,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "نوٹیفکیشن اور پاپ اپ چیک کریں",
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkTeal
              )
              Text(
                text = "فوری 5s فلوٹنگ ونڈو اور بیپ ٹیسٹ کریں",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
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
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 7. Settings Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ParchmentCard),
        border = BorderStroke(1.dp, ParchmentBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
          SettingRow(
            title = "نام تبدیل کریں (Display Name)",
            value = userSettings.userName,
            onClick = {
              tempName = userSettings.userName
              showNameDialog = true
            }
          )

          SettingRow(
            title = "یاد دہانی کا وقفہ (Reminder)",
            value = userSettings.reminderInterval,
            onClick = { showIntervalDialog = true }
          )

          SettingRow(
            title = "روزانہ کا ہدف (Daily Goal)",
            value = "${userSettings.dailyGoal} moments",
            onClick = { showGoalDialog = true }
          )

          // Overlay Permission Row for 5s Floating Window
          val hasOverlay = FloatingPopupManager.canDrawOverlays(context)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                if (!hasOverlay) {
                  FloatingPopupManager.requestOverlayPermission(context)
                }
              }
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
              Text(
                text = "فلوٹنگ ونڈو (Floating Over Apps)",
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.5.sp,
                color = TextPrimary
              )
              Text(
                text = "یوٹیوب یا کال کے دوران 5 سیکنڈ کا پرسکون پاپ اپ",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = TextSoft
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (hasOverlay) Color(0xFF1B4E48) else BronzeGold
            ) {
              Text(
                text = if (hasOverlay) "فعال ✓" else "اجازت دیں",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
          DividerLine()

          // Toggle: Gentle Haptics
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "وائبریشن (Gentle Vibration)",
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.5.sp,
                color = TextPrimary
              )
              Text(
                text = "مکمل ہونے پر ہلکی لرزش",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = TextSoft
              )
            }
            Switch(
              checked = userSettings.hapticsEnabled,
              onCheckedChange = { viewModel.toggleHaptics(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = InkTeal,
                checkedTrackColor = BronzeGoldLight,
                uncheckedTrackColor = ParchmentBorder
              )
            )
          }
          DividerLine()

          // Toggle: Chime Sound
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "آواز (Completion Bell)",
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.5.sp,
                color = TextPrimary
              )
              Text(
                text = "15 سیکنڈ ختم ہونے پر گھنٹی کی آواز",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = TextSoft
              )
            }
            Switch(
              checked = userSettings.soundEnabled,
              onCheckedChange = { viewModel.toggleSound(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = InkTeal,
                checkedTrackColor = BronzeGoldLight,
                uncheckedTrackColor = ParchmentBorder
              )
            )
          }
          DividerLine()

          // Dark Ink Theme Toggle
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "رات کی تھیم (Night Ink Theme)",
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.5.sp,
                color = TextPrimary
              )
              Text(
                text = "گہرا سبز نیلا اور سنہری رنگ",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = TextSoft
              )
            }
            Switch(
              checked = userSettings.isDarkMode == true,
              onCheckedChange = { viewModel.setDarkModePreference(if (it) true else null) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = InkTeal,
                checkedTrackColor = BronzeGoldLight,
                uncheckedTrackColor = ParchmentBorder
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "تاریخ ری سیٹ کریں (Reset Presence Logs)",
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        color = Color(0xFFA83232),
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { showClearDialog = true }
          .padding(8.dp)
      )
    }
  }

  // Dialog: Google Sign In / Account Setup
  if (showAuthDialog) {
    AlertDialog(
      onDismissRequest = { showAuthDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = InkTeal)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Google Account Setup", fontFamily = FontFamily.Serif, color = InkTeal)
        }
      },
      text = {
        Column {
          Text(
            text = "اپنے گوگل اکاؤنٹ سے سائن ان کریں تاکہ آپ کی محفوظ کردہ آیات، ہسٹری اور روحانی اسکور ہر وقت کلاؤڈ پر محفوظ رہیں:",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.5.sp,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(12.dp))

          // 1-Tap Google Quick Options
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFF1F5F9),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                viewModel.updateAccountProfile("محمد عمر (Muhammad Umer)", "umerm1992@gmail.com")
                showAuthDialog = false
              }
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(BronzeGold)
              ) {
                Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Continue as umerm1992@gmail.com",
                  fontFamily = FontFamily.SansSerif,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 12.sp,
                  color = InkTeal
                )
                Text(
                  text = "1-Tap Google Sign-In",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 10.sp,
                  color = TextSoft
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "یا دوسرا اکاؤنٹ درج کریں:",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            color = TextSoft
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = tempName,
            onValueChange = { tempName = it },
            singleLine = true,
            label = { Text("User Name") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = tempEmail,
            onValueChange = { tempEmail = it },
            singleLine = true,
            label = { Text("Google Email (e.g. user@gmail.com)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        TextButton(onClick = {
          val name = if (tempName.isNotBlank()) tempName else userSettings.userName
          val email = if (tempEmail.isNotBlank()) tempEmail else "user@gmail.com"
          viewModel.updateAccountProfile(name, email)
          showAuthDialog = false
        }) {
          Text("Google کے ساتھ لاگ ان", color = BronzeGold, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        if (userSettings.isSignedIn) {
          TextButton(onClick = {
            viewModel.signOutAccount()
            showAuthDialog = false
          }) {
            Text("لاگ آؤٹ (Sign Out)", color = Color(0xFFA83232))
          }
        } else {
          TextButton(onClick = { showAuthDialog = false }) {
            Text("منسوخ", color = TextSoft)
          }
        }
      }
    )
  }

  // Dialog: Edit Name
  if (showNameDialog) {
    AlertDialog(
      onDismissRequest = { showNameDialog = false },
      title = { Text("آپ کا نام (Your Name)", fontFamily = FontFamily.Serif, color = InkTeal) },
      text = {
        OutlinedTextField(
          value = tempName,
          onValueChange = { tempName = it },
          singleLine = true,
          label = { Text("Display Name") }
        )
      },
      confirmButton = {
        TextButton(onClick = {
          if (tempName.isNotBlank()) viewModel.updateUserName(tempName)
          showNameDialog = false
        }) {
          Text("محفوظ کریں", color = BronzeGold, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showNameDialog = false }) {
          Text("منسوخ", color = TextSoft)
        }
      }
    )
  }

  // Dialog: Reminder Interval
  if (showIntervalDialog) {
    val intervals = listOf(
      "Every 1 hour (1 گھنٹہ بعد)",
      "Every 30 min (30 منٹ بعد)",
      "Every 15 min (15 منٹ بعد)",
      "Every 2 hours (2 گھنٹے بعد)"
    )
    AlertDialog(
      onDismissRequest = { showIntervalDialog = false },
      title = { Text("یاد دہانی کا وقفہ منتخب کریں", fontFamily = FontFamily.Serif, color = InkTeal) },
      text = {
        Column {
          intervals.forEach { interval ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  viewModel.updateReminderInterval(interval)
                  showIntervalDialog = false
                }
                .padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(interval, fontFamily = FontFamily.SansSerif, fontSize = 13.5.sp, color = TextPrimary)
              if (userSettings.reminderInterval == interval || (userSettings.reminderInterval.contains("1 hour") && interval.contains("1 hour"))) {
                Text("✓", color = BronzeGold, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showIntervalDialog = false }) {
          Text("ٹھیک ہے", color = BronzeGold)
        }
      }
    )
  }

  // Dialog: Daily Goal
  if (showGoalDialog) {
    val goals = listOf(3, 5, 8, 10, 12, 15, 20)
    AlertDialog(
      onDismissRequest = { showGoalDialog = false },
      title = { Text("روزانہ کا ہدف منتخب کریں", fontFamily = FontFamily.Serif, color = InkTeal) },
      text = {
        Column {
          goals.forEach { g ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  viewModel.updateDailyGoal(g)
                  showGoalDialog = false
                }
                .padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("$g moments per day (بار روزانہ)", fontFamily = FontFamily.SansSerif, fontSize = 13.5.sp, color = TextPrimary)
              if (userSettings.dailyGoal == g) {
                Text("✓", color = BronzeGold, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showGoalDialog = false }) {
          Text("ٹھیک ہے", color = BronzeGold)
        }
      }
    )
  }

  // Dialog: Clear Confirmation
  if (showClearDialog) {
    AlertDialog(
      onDismissRequest = { showClearDialog = false },
      title = { Text("کیا آپ ریکارڈ صاف کرنا چاہتے ہیں؟", fontFamily = FontFamily.Serif, color = Color(0xFFA83232)) },
      text = { Text("یہ آپ کے تمام مکمل شدہ اذکار کا ریکارڈ صاف کر دے گا۔") },
      confirmButton = {
        TextButton(onClick = {
          viewModel.clearAllHistory()
          showClearDialog = false
        }) {
          Text("صاف کریں", color = Color(0xFFA83232))
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearDialog = false }) {
          Text("منسوخ", color = TextSoft)
        }
      }
    )
  }
}

@Composable
private fun SavedAyahItemRow(
  item: DhikrItem,
  onRead: () -> Unit,
  onShare: () -> Unit,
  onRemove: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = ParchmentSurface,
    border = BorderStroke(1.dp, ParchmentBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = ParchmentSubtle
        ) {
          Text(
            text = item.source,
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            color = BronzeGold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
            Icon(
              imageVector = Icons.Filled.Share,
              contentDescription = "Share",
              tint = InkTeal,
              modifier = Modifier.size(16.dp)
            )
          }
          IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(
              imageVector = Icons.Filled.BookmarkRemove,
              contentDescription = "Remove Bookmark",
              tint = Color(0xFFA83232),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Arabic snippet in Amiri
      Text(
        text = item.arabic,
        fontFamily = ArabicFontFamily,
        fontSize = 17.sp,
        color = InkTeal,
        lineHeight = 25.sp,
        textAlign = TextAlign.Right,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Urdu translation snippet in Noto Nastaliq
      Text(
        text = item.translationUrdu,
        fontFamily = UrduFontFamily,
        fontSize = 12.sp,
        color = Color(0xFF8A5A1A),
        lineHeight = 18.sp,
        textAlign = TextAlign.Right,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(8.dp))

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = InkTeal,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable(onClick = onRead)
      ) {
        Row(
          modifier = Modifier.padding(vertical = 6.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = BronzeGoldLight,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "15 سیکنڈ ذکر شروع کریں (Begin Moment)",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun ShareAppButton(
  title: String,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = color.copy(alpha = 0.12f),
    border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Filled.Share,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = title,
        fontFamily = FontFamily.SansSerif,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        color = color
      )
    }
  }
}

@Composable
private fun ScoreTile(
  title: String,
  value: String,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color.White,
    border = BorderStroke(1.dp, ParchmentBorder),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        fontFamily = FontFamily.SansSerif,
        fontSize = 9.5.sp,
        color = TextSoft,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = value,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 13.5.sp,
        color = InkTeal,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        fontFamily = FontFamily.SansSerif,
        fontSize = 8.5.sp,
        color = BronzeGold,
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
private fun SettingRow(
  title: String,
  value: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      fontFamily = FontFamily.SansSerif,
      fontSize = 13.5.sp,
      color = TextPrimary
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = value,
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.5.sp,
        color = BronzeGold
      )
      Spacer(modifier = Modifier.width(6.dp))
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
        contentDescription = null,
        tint = TextSoft,
        modifier = Modifier.size(12.dp)
      )
    }
  }
  DividerLine()
}

@Composable
private fun DividerLine() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(1.dp)
      .background(ParchmentBorder.copy(alpha = 0.5f))
  )
}
