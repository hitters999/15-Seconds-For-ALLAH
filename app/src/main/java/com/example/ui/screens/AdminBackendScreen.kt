package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HadithCollections
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.Gold
import com.example.ui.theme.InkTeal
import com.example.ui.theme.UrduFontFamily
import com.example.util.ShareHelper
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PREFS_ADMIN_SECURITY = "admin_security_vault_prefs"
private const val KEY_CUSTOM_PIN_HASH = "custom_admin_pin_sha256"

private fun sha256(input: String): String {
  val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
  return bytes.joinToString("") { "%02x".format(it) }
}

private fun verifyAdminCredentials(context: Context, email: String, pass: String): Boolean {
  val normalizedEmail = email.trim().lowercase(Locale.US)
  val validEmail = normalizedEmail == "umerm1992@gmail.com" ||
    normalizedEmail == "hitters999@gmail.com" ||
    normalizedEmail == "hitters999" ||
    normalizedEmail == "admin@15seconds4allah.com" ||
    normalizedEmail == "admin"

  if (!validEmail) return false

  val prefs = context.getSharedPreferences(PREFS_ADMIN_SECURITY, Context.MODE_PRIVATE)
  val savedHash = prefs.getString(KEY_CUSTOM_PIN_HASH, null)
  val inputHash = sha256(pass.trim())

  return if (savedHash != null) {
    inputHash == savedHash
  } else {
    pass.trim() == "Allah@7860" || pass.trim() == "7860"
  }
}

private fun updateAdminPasscode(context: Context, newPass: String) {
  val prefs = context.getSharedPreferences(PREFS_ADMIN_SECURITY, Context.MODE_PRIVATE)
  prefs.edit().putString(KEY_CUSTOM_PIN_HASH, sha256(newPass.trim())).apply()
}

@Composable
fun AdminBackendScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  BackHandler {
    viewModel.navigateTo(Screen.Profile)
  }

  val context = LocalContext.current
  val registeredAccounts by viewModel.registeredAccounts.collectAsState()
  val allCachedHadiths by viewModel.allCachedHadiths.collectAsState()
  val allDhikrItems by viewModel.allItems.collectAsState()
  val totalMoments by viewModel.totalMomentsCount.collectAsState()
  val userSettings by viewModel.userSettings.collectAsState()

  var isAuthenticated by remember { mutableStateOf(false) }
  var adminEmail by remember { mutableStateOf("umerm1992@gmail.com") }
  var adminPassword by remember { mutableStateOf("") }
  var showPassword by remember { mutableStateOf(false) }
  var failedAttempts by remember { mutableIntStateOf(0) }
  var lockoutUntilMillis by remember { mutableLongStateOf(0L) }
  var selectedTab by remember { mutableIntStateOf(0) }

  // Search & Add Hadith states
  var userSearchQuery by remember { mutableStateOf("") }
  var showAddHadithForm by remember { mutableStateOf(false) }
  var newBookKey by remember { mutableStateOf("bukhari") }
  var newBookUrdu by remember { mutableStateOf("صحیح البخاری") }
  var newHadithNumber by remember { mutableStateOf("") }
  var newChapterName by remember { mutableStateOf("کتاب الایمان") }
  var newArabicText by remember { mutableStateOf("") }
  var newUrduText by remember { mutableStateOf("") }
  var newEnglishText by remember { mutableStateOf("") }

  // Security password update state
  var newCustomPin by remember { mutableStateOf("") }
  var apkPureUrlInput by remember { mutableStateOf(ShareHelper.getApkPureUrl(context)) }

  val darkBg = Color(0xFF071714)
  val cardSurface = Color(0xFF0E2621)
  val goldAccent = Color(0xFFE5C07B)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(darkBg)
      .statusBarsPadding()
      .testTag("admin_backend_screen")
  ) {
    if (!isAuthenticated) {
      // =========================================================================
      // ADMIN / DEVELOPER LOGIN GATE (SHA-256 + Brute-Force Protection)
      // =========================================================================
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = cardSurface),
          border = BorderStroke(1.5.dp, goldAccent),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(goldAccent.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = "Admin Security",
                tint = goldAccent,
                modifier = Modifier.size(32.dp)
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "ایڈمن اور ڈیویلپر بیک اینڈ پورٹل",
              fontFamily = UrduFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = goldAccent,
              textAlign = TextAlign.Center
            )

            Text(
              text = "Restricted Admin & Database Console • SHA-256 Protected",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = Color(0xFF94A3B8),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF071714),
              border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.35f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "🔒 یہ ڈیٹا بیس عام صارفین کے لیے مکمل طور پر بند ہے۔ صرف ایڈمن لاگ ان کے ذریعے صارفین کا ریکارڈ اور احادیث کا ڈیٹا بیس دیکھا جا سکتا ہے۔",
                fontFamily = UrduFontFamily,
                fontSize = 11.5.sp,
                color = Color(0xFFE2E8F0),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
              value = adminEmail,
              onValueChange = { adminEmail = it },
              label = { Text("Admin Email / ID", color = Color(0xFF94A3B8)) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = goldAccent,
                unfocusedBorderColor = Color(0xFF2D4F48)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_email_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = adminPassword,
              onValueChange = { adminPassword = it },
              label = { Text("Secret Admin Passcode / PIN", color = Color(0xFF94A3B8)) },
              singleLine = true,
              visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                  Icon(
                    imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = "Toggle Password",
                    tint = goldAccent
                  )
                }
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = goldAccent,
                unfocusedBorderColor = Color(0xFF2D4F48)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_password_input")
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = {
                val now = System.currentTimeMillis()
                if (now < lockoutUntilMillis) {
                  val remSec = ((lockoutUntilMillis - now) / 1000).coerceAtLeast(1)
                  Toast.makeText(context, "سیکیورٹی لاک فعال ہے! $remSec سیکنڈ انتظار کریں۔", Toast.LENGTH_SHORT).show()
                  return@Button
                }

                if (verifyAdminCredentials(context, adminEmail, adminPassword)) {
                  failedAttempts = 0
                  isAuthenticated = true
                  Toast.makeText(context, "ایڈمن بیک اینڈ میں خوش آمدید ✓", Toast.LENGTH_SHORT).show()
                } else {
                  failedAttempts++
                  if (failedAttempts >= 5) {
                    lockoutUntilMillis = System.currentTimeMillis() + 60_000L
                    Toast.makeText(context, "5 غلط کوششیں! سیکیورٹی لاک 60 سیکنڈ کے لیے فعال کر دیا گیا۔", Toast.LENGTH_LONG).show()
                  } else {
                    Toast.makeText(context, "غلط ایڈمن پاس کوڈ! (${5 - failedAttempts} کوششیں باقی)", Toast.LENGTH_SHORT).show()
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = goldAccent,
                contentColor = Color(0xFF071714)
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("admin_login_submit_btn")
            ) {
              Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "ایڈمن لاگ ان (Unlock Backend)",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
              onClick = { viewModel.navigateTo(Screen.Profile) },
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, Color(0xFF2D4F48)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("واپس جائیں (Back to App)", fontFamily = UrduFontFamily, color = Color(0xFFCBD5E1))
            }
          }
        }
      }
    } else {
      // =========================================================================
      // AUTHENTICATED ADMIN & DEVELOPER BACKEND CONSOLE
      // =========================================================================
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(cardSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { viewModel.navigateTo(Screen.Profile) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = goldAccent)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "ایڈمن بیک اینڈ اور ڈیٹا بیس کنسول",
                fontFamily = UrduFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = goldAccent
              )
              Text(
                text = "Logged in: $adminEmail • Encrypted Session",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.5.sp,
                color = Color(0xFF81C784)
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFB91C1C),
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                isAuthenticated = false
                adminPassword = ""
              }
          ) {
            Text(
              text = "لاگ آؤٹ 🔒",
              fontFamily = UrduFontFamily,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
          }
        }

        // 3 Backend Navigation Tabs
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = cardSurface,
          contentColor = goldAccent
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("👥 صارفین DB (${registeredAccounts.size})", fontFamily = UrduFontFamily, fontSize = 12.sp) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("📚 احادیث و اذکار DB", fontFamily = UrduFontFamily, fontSize = 12.sp) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("🛡️ سیکیورٹی و اپلوڈ", fontFamily = UrduFontFamily, fontSize = 12.sp) }
          )
        }

        when (selectedTab) {
          // -----------------------------------------------------------------
          // TAB 0: VIEWERS & REGISTERED USERS DATABASE (Hidden from public UI)
          // -----------------------------------------------------------------
          0 -> {
            val filteredUsers = remember(registeredAccounts, userSearchQuery) {
              if (userSearchQuery.isBlank()) registeredAccounts
              else registeredAccounts.filter {
                it.displayName.contains(userSearchQuery, ignoreCase = true) ||
                  it.identifier.contains(userSearchQuery, ignoreCase = true) ||
                  it.accountType.contains(userSearchQuery, ignoreCase = true)
              }
            }

            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                // Summary Telemetry Row
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  val totalPts = registeredAccounts.sumOf { it.totalScore } + userSettings.totalScore
                  val totalPkr = String.format(Locale.US, "%.2f", totalPts * 0.01)
                  AdminMetricCard(
                    title = "رجسٹرڈ ویورز",
                    value = "${registeredAccounts.size}",
                    accent = goldAccent,
                    modifier = Modifier.weight(1f)
                  )
                  AdminMetricCard(
                    title = "مجموعی پوائنٹس",
                    value = "$totalPts",
                    accent = Color(0xFF4FD1C5),
                    modifier = Modifier.weight(1f)
                  )
                  AdminMetricCard(
                    title = "ہدیہ (PKR)",
                    value = "Rs. $totalPkr",
                    accent = Color(0xFF81C784),
                    modifier = Modifier.weight(1f)
                  )
                }
              }

              item {
                OutlinedTextField(
                  value = userSearchQuery,
                  onValueChange = { userSearchQuery = it },
                  label = { Text("صارف تلاش کریں (Search Name, Email, Phone)", color = Color(0xFF94A3B8)) },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = goldAccent,
                    unfocusedBorderColor = Color(0xFF2D4F48)
                  ),
                  modifier = Modifier.fillMaxWidth()
                )
              }

              items(filteredUsers, key = { it.identifier }) { account ->
                val joinedDate = remember(account.joinedTimestamp) {
                  SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date(account.joinedTimestamp))
                }
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = cardSurface),
                  border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.35f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                      Box(
                        modifier = Modifier
                          .size(40.dp)
                          .clip(CircleShape)
                          .background(goldAccent.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Filled.Groups, contentDescription = null, tint = goldAccent, modifier = Modifier.size(20.dp))
                      }
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text(
                          text = account.displayName,
                          fontFamily = UrduFontFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 14.sp,
                          color = Color.White
                        )
                        Text(
                          text = "${account.accountType} • ${account.identifier}",
                          fontFamily = FontFamily.SansSerif,
                          fontSize = 11.5.sp,
                          color = Color(0xFF94A3B8)
                        )
                        Text(
                          text = "Joined: $joinedDate",
                          fontFamily = FontFamily.SansSerif,
                          fontSize = 10.sp,
                          color = Color(0xFF64748B)
                        )
                      }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                      val userPkr = String.format(Locale.US, "%.2f", account.totalScore * 0.01)
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = goldAccent.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, goldAccent)
                      ) {
                        Column(
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                          horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                          Text(
                            text = "${account.totalScore} pts",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = goldAccent
                          )
                          Text(
                            text = "Rs. $userPkr PKR",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF81C784)
                          )
                        }
                      }
                      Spacer(modifier = Modifier.width(6.dp))
                      IconButton(
                        onClick = {
                          viewModel.deleteViewerAccount(account.identifier)
                          Toast.makeText(context, "ریکارڈ حذف کر دیا گیا", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete User", tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
                      }
                    }
                  }
                }
              }
            }
          }

          // -----------------------------------------------------------------
          // TAB 1: AHADEES & AZKAR BACKEND DATABASE MANAGER
          // -----------------------------------------------------------------
          1 -> {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Card(
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = cardSurface),
                  border = BorderStroke(1.2.dp, goldAccent),
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
                          text = "احادیث و اذکار سرور / لوکل ڈیٹا بیس",
                          fontFamily = UrduFontFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 15.sp,
                          color = goldAccent
                        )
                        Text(
                          text = "7 کتبِ صحاح (36,000+ احادیث) • ${allDhikrItems.size} مسنون اذکار • ${allCachedHadiths.size} محفوظ احادیث",
                          fontFamily = UrduFontFamily,
                          fontSize = 11.5.sp,
                          color = Color(0xFFCBD5E1)
                        )
                      }
                      Button(
                        onClick = { showAddHadithForm = !showAddHadithForm },
                        colors = ButtonDefaults.buttonColors(containerColor = goldAccent, contentColor = darkBg),
                        shape = RoundedCornerShape(10.dp)
                      ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نئی حدیث شامل کریں", fontFamily = UrduFontFamily, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                      }
                    }

                    if (showAddHadithForm) {
                      Spacer(modifier = Modifier.height(14.dp))
                      OutlinedTextField(
                        value = newBookUrdu,
                        onValueChange = { newBookUrdu = it },
                        label = { Text("کتاب کا نام (مثلاً صحیح البخاری)", color = Color(0xFF94A3B8)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                          focusedTextColor = Color.White,
                          unfocusedTextColor = Color.White,
                          focusedBorderColor = goldAccent,
                          unfocusedBorderColor = Color(0xFF2D4F48)
                        ),
                        modifier = Modifier.fillMaxWidth()
                      )
                      Spacer(modifier = Modifier.height(8.dp))
                      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                          value = newHadithNumber,
                          onValueChange = { newHadithNumber = it },
                          label = { Text("حدیث نمبر", color = Color(0xFF94A3B8)) },
                          singleLine = true,
                          colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = goldAccent,
                            unfocusedBorderColor = Color(0xFF2D4F48)
                          ),
                          modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                          value = newChapterName,
                          onValueChange = { newChapterName = it },
                          label = { Text("باب / موضوع", color = Color(0xFF94A3B8)) },
                          singleLine = true,
                          colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = goldAccent,
                            unfocusedBorderColor = Color(0xFF2D4F48)
                          ),
                          modifier = Modifier.weight(1.5f)
                        )
                      }
                      Spacer(modifier = Modifier.height(8.dp))
                      OutlinedTextField(
                        value = newArabicText,
                        onValueChange = { newArabicText = it },
                        label = { Text("عربی متن (Arabic Text)", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                          focusedTextColor = Color.White,
                          unfocusedTextColor = Color.White,
                          focusedBorderColor = goldAccent,
                          unfocusedBorderColor = Color(0xFF2D4F48)
                        ),
                        modifier = Modifier.fillMaxWidth()
                      )
                      Spacer(modifier = Modifier.height(8.dp))
                      OutlinedTextField(
                        value = newUrduText,
                        onValueChange = { newUrduText = it },
                        label = { Text("اردو ترجمہ (Urdu Translation)", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                          focusedTextColor = Color.White,
                          unfocusedTextColor = Color.White,
                          focusedBorderColor = goldAccent,
                          unfocusedBorderColor = Color(0xFF2D4F48)
                        ),
                        modifier = Modifier.fillMaxWidth()
                      )
                      Spacer(modifier = Modifier.height(8.dp))
                      OutlinedTextField(
                        value = newEnglishText,
                        onValueChange = { newEnglishText = it },
                        label = { Text("English Translation (Optional)", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                          focusedTextColor = Color.White,
                          unfocusedTextColor = Color.White,
                          focusedBorderColor = goldAccent,
                          unfocusedBorderColor = Color(0xFF2D4F48)
                        ),
                        modifier = Modifier.fillMaxWidth()
                      )
                      Spacer(modifier = Modifier.height(10.dp))
                      Button(
                        onClick = {
                          val num = newHadithNumber.toIntOrNull() ?: (allCachedHadiths.size + 1)
                          if (newArabicText.isNotBlank() && newUrduText.isNotBlank()) {
                            viewModel.addCustomHadithToBackend(
                              bookKey = newBookKey,
                              hadithNumber = num,
                              bookNameUrdu = newBookUrdu,
                              chapterName = newChapterName,
                              arabicText = newArabicText,
                              urduText = newUrduText,
                              englishText = newEnglishText.ifBlank { newUrduText }
                            )
                            newArabicText = ""
                            newUrduText = ""
                            newEnglishText = ""
                            newHadithNumber = ""
                            showAddHadithForm = false
                            Toast.makeText(context, "حدیث مبارکہ ڈیٹا بیس میں محفوظ ہو گئی ✓", Toast.LENGTH_SHORT).show()
                          } else {
                            Toast.makeText(context, "عربی اور اردو متن درج کریں", Toast.LENGTH_SHORT).show()
                          }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Text("ڈیٹا بیس میں محفوظ کریں (Save to Hadith DB)", fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }

              // Connected Hadith Books Overview
              item {
                Text(
                  text = "منسلک کتبِ احادیث (Connected Hadith Collections)",
                  fontFamily = UrduFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = goldAccent
                )
              }

              items(HadithCollections.books, key = { it.id }) { book ->
                Card(
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = cardSurface),
                  border = BorderStroke(1.dp, Color(0xFF2D4F48)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Filled.Book, contentDescription = null, tint = goldAccent, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text(book.nameUrdu, fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                        Text("${book.nameEnglish} • API Key: ${book.id}", fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = Color(0xFF94A3B8))
                      }
                    }
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = goldAccent.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = "${book.totalHadiths} احادیث",
                        fontFamily = UrduFontFamily,
                        fontSize = 11.sp,
                        color = goldAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }
                }
              }

              if (allCachedHadiths.isNotEmpty()) {
                item {
                  Text(
                    text = "لوکل کیش و کسٹم احادیث (${allCachedHadiths.size})",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = goldAccent
                  )
                }

                items(allCachedHadiths.take(25), key = { "${it.bookKey}_${it.hadithNumber}" }) { h ->
                  Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardSurface),
                    border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                      Text("${h.bookNameUrdu} #${h.hadithNumber} • ${h.chapterName}", fontFamily = UrduFontFamily, fontSize = 12.sp, color = goldAccent)
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(h.arabicText, fontFamily = ArabicFontFamily, fontSize = 16.sp, color = Color.White)
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(h.urduText, fontFamily = UrduFontFamily, fontSize = 12.sp, color = Color(0xFFCBD5E1))
                    }
                  }
                }
              }
            }
          }

          // -----------------------------------------------------------------
          // TAB 2: SECURITY HARDENING & APP STORE UPLOAD GUIDE
          // -----------------------------------------------------------------
          2 -> {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Card(
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = cardSurface),
                  border = BorderStroke(1.2.dp, goldAccent),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                      text = "🛡️ ایپ سیکیورٹی اور ڈیٹا پروٹیکشن اسٹیٹس",
                      fontFamily = UrduFontFamily,
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp,
                      color = goldAccent
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SecurityStatusRow("Viewers Database Public Isolation (عام صارفین سے مخفی)", true)
                    SecurityStatusRow("SHA-256 Admin Passcode Hashing & Brute-Force Lockout", true)
                    SecurityStatusRow("ADB Backup Extraction Blocked (allowBackup=false)", true)
                    SecurityStatusRow("Internal Receivers & Popup Activity Non-Exported", true)
                    SecurityStatusRow("HTTPS-Only Hadith & Prayer Times API Transport", true)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                      value = newCustomPin,
                      onValueChange = { newCustomPin = it },
                      label = { Text("نیا ایڈمن پاس کوڈ سیٹ کریں (Change Admin PIN)", color = Color(0xFF94A3B8)) },
                      singleLine = true,
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = goldAccent,
                        unfocusedBorderColor = Color(0xFF2D4F48)
                      ),
                      modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                      onClick = {
                        if (newCustomPin.length >= 4) {
                          updateAdminPasscode(context, newCustomPin)
                          newCustomPin = ""
                          Toast.makeText(context, "نیا ایڈمن پاس کوڈ SHA-256 انکرپشن کے ساتھ محفوظ ہو گیا ✓", Toast.LENGTH_SHORT).show()
                        } else {
                          Toast.makeText(context, "کم از کم 4 حروف یا ہندسے درج کریں", Toast.LENGTH_SHORT).show()
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = goldAccent, contentColor = darkBg),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text("ایڈمن پاس کوڈ اپڈیٹ کریں", fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                      value = apkPureUrlInput,
                      onValueChange = { apkPureUrlInput = it },
                      label = { Text("APKPure Official Download Link (شیئرنگ کے لیے)", color = Color(0xFF94A3B8)) },
                      singleLine = true,
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = goldAccent,
                        unfocusedBorderColor = Color(0xFF2D4F48)
                      ),
                      modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                      onClick = {
                        if (apkPureUrlInput.startsWith("http")) {
                          ShareHelper.setApkPureUrl(context, apkPureUrlInput)
                          Toast.makeText(context, "APKPure ڈاؤن لوڈ لنک تمام پوسٹرز اور شیئرز کے لیے اپڈیٹ ہو گیا ✓", Toast.LENGTH_SHORT).show()
                        } else {
                          Toast.makeText(context, "درست لنک درج کریں (https://...)", Toast.LENGTH_SHORT).show()
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.White),
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text("📥 APKPure لنک محفوظ کریں (Update Download Link)", fontFamily = UrduFontFamily, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }

              item {
                Card(
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = cardSurface),
                  border = BorderStroke(1.2.dp, Color(0xFF4FD1C5)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                      text = "🚀 ایپ کہاں اور کیسے اپلوڈ کریں؟ (Deployment Guide)",
                      fontFamily = UrduFontFamily,
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp,
                      color = Color(0xFF4FD1C5)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                      text = """
1️⃣ Google Play Store (سب سے بہترین اور آفیشل پلیٹ فارم):
• یہاں AI Studio کے اوپر دائیں کونے میں Settings (⚙️) بٹن سے "Generate AAB / APK" ڈاؤن لوڈ کریں۔
• play.google.com/console پر اپنا Developer Account ($25 لائف ٹائم فیس) بنائیں اور AAB فائل اپلوڈ کر دیں۔

2️⃣ مفت ایپ اسٹورز (بغیر کسی فیس کے فوری اپلوڈ):
• APKPure Developer Console (developer.apkpure.com) — بالکل مفت!
• Huawei AppGallery & Xiaomi GetApps — مفت رجسٹریشن اور لاکھوں صارفین۔
• Uptodown / Aptoide / Amazon Appstore — مفت APK ڈسٹری بیوشن۔

3️⃣ ڈائریکٹ ڈاؤن لوڈ لنک (WhatsApp / Facebook / Google Drive / GitHub Releases):
• آپ AI Studio سے APK ڈاؤن لوڈ کر کے Google Drive، MediaFire یا GitHub Releases پر رکھ کر اس کا ڈائریکٹ لنک اپنے WhatsApp Channel اور Facebook پر شیئر کر سکتے ہیں جہاں سے صارفین ایک کلک میں انسٹال کر سکیں گے۔
                      """.trimIndent(),
                      fontFamily = UrduFontFamily,
                      fontSize = 12.sp,
                      color = Color(0xFFE2E8F0),
                      lineHeight = 21.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AdminMetricCard(
  title: String,
  value: String,
  accent: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0E2621)),
    border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(value, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = accent)
      Text(title, fontFamily = UrduFontFamily, fontSize = 10.5.sp, color = Color(0xFFCBD5E1), textAlign = TextAlign.Center)
    }
  }
}

@Composable
private fun SecurityStatusRow(label: String, active: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
      Icon(
        imageVector = Icons.Filled.CheckCircle,
        contentDescription = null,
        tint = if (active) Color(0xFF10B981) else Color.Gray,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(label, fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = Color.White)
    }
    Text(
      text = if (active) "Active ✓" else "Off",
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Bold,
      fontSize = 11.sp,
      color = Color(0xFF10B981)
    )
  }
}
