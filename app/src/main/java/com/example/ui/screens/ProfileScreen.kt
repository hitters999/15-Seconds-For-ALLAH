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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.notification.FloatingPopupManager
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.ParchmentBackground
import com.example.ui.components.TimezoneSelectionDialog
import com.example.util.AppTimeHelper
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
fun ProfileScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val userSettings by viewModel.userSettings.collectAsState()
  val totalMoments by viewModel.totalMomentsCount.collectAsState()
  val streak by viewModel.streakCount.collectAsState()
  val bookmarkedItems by viewModel.bookmarkedItems.collectAsState()
  val registeredAccounts by viewModel.registeredAccounts.collectAsState()
  val scrollState = rememberScrollState()
  val context = LocalContext.current

  var showAuthDialog by remember { mutableStateOf(false) }
  var showNameDialog by remember { mutableStateOf(false) }
  var showIntervalDialog by remember { mutableStateOf(false) }
  var showGoalDialog by remember { mutableStateOf(false) }
  var showClearDialog by remember { mutableStateOf(false) }
  var showTimezoneDialog by remember { mutableStateOf(false) }
  val currentTimeFormatted by viewModel.currentTimeFormatted.collectAsState()
  var tempName by remember { mutableStateOf("") }
  var tempIdentifier by remember { mutableStateOf("") }
  var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Google, 1: Mobile, 2: Email

  ParchmentBackground(modifier = modifier, showMadinahBackdrop = false) {
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

      // 1. Google / Email / Mobile Account Header
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = InkTeal),
        border = BorderStroke(1.5.dp, BronzeGoldLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
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
              shape = RoundedCornerShape(10.dp),
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
                  text = when {
                    userSettings.isSignedIn -> "${userSettings.authProvider} اکاؤنٹ منسلک ✓"
                    else -> "مہمان موڈ (Guest)"
                  },
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  color = ParchmentSurface
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (userSettings.isSignedIn) Color(0xFF1B5E20) else BronzeGold,
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  tempName = userSettings.userName
                  tempIdentifier = if (userSettings.userPhone.isNotBlank()) userSettings.userPhone else userSettings.userEmail
                  showAuthDialog = true
                }
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
                  text = if (userSettings.isSignedIn) "اکاؤنٹ تبدیل کریں" else "لاگ ان / سائن اپ",
                  fontFamily = FontFamily.SansSerif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = Color.White
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Avatar
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
              contentDescription = "Avatar",
              modifier = Modifier.size(72.dp),
              contentScale = ContentScale.Fit
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // User Name
          Text(
            text = userSettings.userName,
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )

          // Email or Phone Identifier
          val identifierDisplay = when {
            userSettings.userPhone.isNotBlank() -> "📱 ${userSettings.userPhone}"
            userSettings.userEmail.isNotBlank() -> "✉ ${userSettings.userEmail}"
            else -> "مہمان صارف"
          }
          Text(
            text = identifierDisplay,
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

      // 2. Spiritual Score & Milestone Card
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
                  text = "ہر 15 سیکنڈ ذکر پر 10 حسنات / پوائنٹس",
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

          // 4-Metric Score Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            ScoreTile(title = "مستقل مزاجی", value = "$streak دن", subtitle = "Streak", modifier = Modifier.weight(1f))
            ScoreTile(title = "مکمل اذکار", value = "$totalMoments", subtitle = "Moments", modifier = Modifier.weight(1f))
            ScoreTile(title = "محفوظ آیات", value = "${bookmarkedItems.size}", subtitle = "Saved", modifier = Modifier.weight(1f))
            ScoreTile(title = "کلاؤڈ سنک", value = if (userSettings.isSignedIn) "فعال ✓" else "آف لائن", subtitle = "Cloud", modifier = Modifier.weight(1f))
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Saved Ayaat Section
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
              Icon(Icons.Filled.Bookmark, contentDescription = null, tint = InkTeal, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "محفوظ شدہ آیات (${bookmarkedItems.size})",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = InkTeal
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = ParchmentSubtle,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { viewModel.navigateTo(Screen.Library) }
            ) {
              Text(
                text = "مزید تلاش کریں",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = BronzeGold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          if (bookmarkedItems.isEmpty()) {
            Text(
              text = "ابھی تک کوئی آیت محفوظ نہیں ہوئی۔ لائبریری سے اپنی پسندیدہ آیات محفوظ کریں۔",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.5.sp,
              color = TextSoft,
              textAlign = TextAlign.Center,
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
            )
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              bookmarkedItems.take(4).forEach { item ->
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = ParchmentSurface,
                  border = BorderStroke(1.dp, ParchmentBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(item.source, fontFamily = FontFamily.SansSerif, fontSize = 10.sp, color = BronzeGold)
                      Row {
                        IconButton(
                          onClick = { ShareHelper.shareDhikrPoster(context, item) },
                          modifier = Modifier.size(26.dp)
                        ) {
                          Icon(Icons.Filled.Share, contentDescription = "Share", tint = InkTeal, modifier = Modifier.size(15.dp))
                        }
                        IconButton(
                          onClick = { viewModel.toggleBookmark(item.id) },
                          modifier = Modifier.size(26.dp)
                        ) {
                          Icon(Icons.Filled.BookmarkRemove, contentDescription = "Remove", tint = Color(0xFFA83232), modifier = Modifier.size(15.dp))
                        }
                      }
                    }
                    Text(
                      text = item.arabic,
                      fontFamily = ArabicFontFamily,
                      fontSize = 16.sp,
                      color = InkTeal,
                      maxLines = 2,
                      overflow = TextOverflow.Ellipsis,
                      textAlign = TextAlign.Right,
                      modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = InkTeal,
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { viewModel.selectDhikrForMoment(item, startImmediately = true) }
                    ) {
                      Row(
                        modifier = Modifier.padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("15 سیکنڈ ذکر شروع کریں", fontFamily = FontFamily.SansSerif, fontSize = 10.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Viewers & Community Directory Card (ڈیٹا بیس اور ناظرین کا ریکارڈ)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Filled.Groups, contentDescription = null, tint = InkTeal, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "رجسٹرڈ صارفین کا ریکارڈ (Viewers Database)",
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = InkTeal
                )
                Text(
                  text = "ایپ میں محفوظ شدہ ناظرین و اراکین",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 10.5.sp,
                  color = TextSoft
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = InkTeal
            ) {
              Text(
                text = "${registeredAccounts.size} رجسٹرڈ",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          registeredAccounts.take(3).forEach { account ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(BronzeGold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(account.displayName, fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                  Text("${account.accountType} • ${account.identifier}", fontFamily = FontFamily.SansSerif, fontSize = 10.sp, color = TextSoft)
                }
              }
              Text("${account.totalScore} pts", fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = BronzeGold, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 5. App Settings Card
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

          val tzOption = AppTimeHelper.getTimezoneOption(userSettings.selectedTimezone)
          SettingRow(
            title = "وقت اور ٹائم زون (Time & Timezone)",
            value = "${tzOption.flagEmoji} ${tzOption.urduName.split(" ")[0]} ($currentTimeFormatted)",
            onClick = { showTimezoneDialog = true }
          )

          // Floating window permission
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
              Text("فلوٹنگ ونڈو (Floating Over Apps)", fontFamily = FontFamily.SansSerif, fontSize = 13.5.sp, color = TextPrimary)
              Text("کال یا ویڈیو کے دوران 5s پرسکون پاپ اپ", fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = TextSoft)
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

          // Vibration toggle
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("وائبریشن (Gentle Vibration)", fontFamily = FontFamily.SansSerif, fontSize = 13.5.sp, color = TextPrimary)
            Switch(
              checked = userSettings.hapticsEnabled,
              onCheckedChange = { viewModel.toggleHaptics(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = InkTeal, checkedTrackColor = BronzeGoldLight)
            )
          }
          DividerLine()

          // Sound toggle
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("گھنٹی اور بیپ (Beep & Chime)", fontFamily = FontFamily.SansSerif, fontSize = 13.5.sp, color = TextPrimary)
            Switch(
              checked = userSettings.soundEnabled,
              onCheckedChange = { viewModel.toggleSound(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = InkTeal, checkedTrackColor = BronzeGoldLight)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Multi-Mode Account Setup Dialog (Google, Mobile Number, Email)
  if (showAuthDialog) {
    AlertDialog(
      onDismissRequest = { showAuthDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = InkTeal)
          Spacer(modifier = Modifier.width(8.dp))
          Text("اکاؤنٹ سیٹ اپ (Connect Account)", fontFamily = FontFamily.Serif, color = InkTeal)
        }
      },
      text = {
        Column {
          TabRow(selectedTabIndex = selectedAuthTab) {
            Tab(selected = selectedAuthTab == 0, onClick = { selectedAuthTab = 0 }, text = { Text("Google", fontSize = 11.sp) })
            Tab(selected = selectedAuthTab == 1, onClick = { selectedAuthTab = 1 }, text = { Text("موبائل نمبر", fontSize = 11.sp) })
            Tab(selected = selectedAuthTab == 2, onClick = { selectedAuthTab = 2 }, text = { Text("Email", fontSize = 11.sp) })
          }

          Spacer(modifier = Modifier.height(14.dp))

          when (selectedAuthTab) {
            0 -> {
              Text(
                text = "اپنے گوگل اکاؤنٹ سے سائن ان کریں تاکہ آپ کا اسکور، محفوظ آیات اور ہسٹری محفوظ رہیں:",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.5.sp,
                color = TextSoft
              )
              Spacer(modifier = Modifier.height(10.dp))
              OutlinedTextField(
                value = tempName,
                onValueChange = { tempName = it },
                label = { Text("آپ کا نام (Your Name)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(8.dp))
              OutlinedTextField(
                value = tempIdentifier,
                onValueChange = { tempIdentifier = it },
                label = { Text("Google Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
            }
            1 -> {
              Text(
                text = "اپنا موبائل نمبر درج کریں (SMS تصدیق کے ساتھ لاگ ان کریں):",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.5.sp,
                color = TextSoft
              )
              Spacer(modifier = Modifier.height(10.dp))
              OutlinedTextField(
                value = tempName,
                onValueChange = { tempName = it },
                label = { Text("آپ کا نام") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(8.dp))
              OutlinedTextField(
                value = tempIdentifier,
                onValueChange = { tempIdentifier = it },
                label = { Text("موبائل نمبر (e.g. +923001234567)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
            }
            2 -> {
              Text(
                text = "اپنا ای میل پتہ درج کریں:",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.5.sp,
                color = TextSoft
              )
              Spacer(modifier = Modifier.height(10.dp))
              OutlinedTextField(
                value = tempName,
                onValueChange = { tempName = it },
                label = { Text("آپ کا نام") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              Spacer(modifier = Modifier.height(8.dp))
              OutlinedTextField(
                value = tempIdentifier,
                onValueChange = { tempIdentifier = it },
                label = { Text("ای میل پتہ (Email Address)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = {
          val type = when (selectedAuthTab) {
            0 -> "Google"
            1 -> "Phone"
            else -> "Email"
          }
          val id = if (tempIdentifier.isNotBlank()) tempIdentifier else "user@15secondsforallah.com"
          val name = if (tempName.isNotBlank()) tempName else "ذاکرِ الٰہی"
          viewModel.updateAccountProfile(id, name, type)
          showAuthDialog = false
        }) {
          Text("محفوظ اور منسلک کریں", color = BronzeGold, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAuthDialog = false }) {
          Text("منسوخ", color = TextSoft)
        }
      }
    )
  }

  // Name Dialog
  if (showNameDialog) {
    AlertDialog(
      onDismissRequest = { showNameDialog = false },
      title = { Text("نام تبدیل کریں", fontFamily = FontFamily.Serif, color = InkTeal) },
      text = {
        OutlinedTextField(
          value = tempName,
          onValueChange = { tempName = it },
          label = { Text("آپ کا نام") },
          singleLine = true
        )
      },
      confirmButton = {
        TextButton(onClick = {
          if (tempName.isNotBlank()) {
            val id = if (userSettings.userPhone.isNotBlank()) userSettings.userPhone else userSettings.userEmail
            viewModel.updateAccountProfile(id, tempName, userSettings.authProvider)
          }
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

  // Interval Dialog
  if (showIntervalDialog) {
    val intervals = listOf("Every 1 hour (1 گھنٹہ بعد)", "Every 30 min (30 منٹ بعد)", "Every 15 min (15 منٹ بعد)", "Every 2 hours (2 گھنٹے بعد)")
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
              Text(interval, fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = TextPrimary)
              if (userSettings.reminderInterval == interval) {
                Text("✓", color = BronzeGold, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showIntervalDialog = false }) { Text("ٹھیک ہے", color = BronzeGold) }
      }
    )
  }

  // Goal Dialog
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
              Text("$g moments per day (بار روزانہ)", fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = TextPrimary)
              if (userSettings.dailyGoal == g) {
                Text("✓", color = BronzeGold, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showGoalDialog = false }) { Text("ٹھیک ہے", color = BronzeGold) }
      }
    )
  }

  // Timezone Dialog
  if (showTimezoneDialog) {
    TimezoneSelectionDialog(
      selectedTimezoneId = userSettings.selectedTimezone,
      onDismissRequest = { showTimezoneDialog = false },
      onSelectTimezone = { tzId ->
        viewModel.updateSelectedTimezone(tzId)
      }
    )
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
      Text(title, fontFamily = FontFamily.SansSerif, fontSize = 9.5.sp, color = TextSoft, textAlign = TextAlign.Center)
      Spacer(modifier = Modifier.height(3.dp))
      Text(value, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = InkTeal, textAlign = TextAlign.Center)
      Spacer(modifier = Modifier.height(2.dp))
      Text(subtitle, fontFamily = FontFamily.SansSerif, fontSize = 8.5.sp, color = BronzeGold, textAlign = TextAlign.Center)
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
    Text(title, fontFamily = FontFamily.SansSerif, fontSize = 13.5.sp, color = TextPrimary)
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(value, fontFamily = FontFamily.SansSerif, fontSize = 12.5.sp, color = BronzeGold)
      Spacer(modifier = Modifier.width(6.dp))
      Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = TextSoft, modifier = Modifier.size(12.dp))
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
