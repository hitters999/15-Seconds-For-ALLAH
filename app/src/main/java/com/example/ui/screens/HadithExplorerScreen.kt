package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DhikrItem
import com.example.data.HadithCollections
import com.example.data.HadithItemDetail
import com.example.ui.MainViewModel
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
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.Emerald
import com.example.ui.theme.Gold
import com.example.ui.theme.InkTeal
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSubtle
import com.example.ui.theme.SoftGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft
import com.example.ui.theme.UrduFontFamily
import com.example.ui.theme.UrduNastaliqFontFamily
import com.example.util.ShareHelper

/**
 * Full Authentic Hadith Explorer Screen (36,000+ Hadiths across 7 Classical Books)
 * Supports:
 * - Sahih al-Bukhari (7,563)
 * - Sahih Muslim (7,500)
 * - Jami` at-Tirmidhi (3,956)
 * - Sunan Abi Dawud (5,274)
 * - Sunan an-Nasa'i (5,758)
 * - Sunan Ibn Majah (4,341)
 * - Muwatta Imam Malik (1,858)
 * Direct number search, random discovery, Arabic with Tashkeel, flowing Urdu, and luxury posters.
 */
@Composable
fun HadithExplorerScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isDark = isSystemInDarkTheme()
  val keyboardController = LocalSoftwareKeyboardController.current

  val selectedBook by viewModel.selectedHadithBook.collectAsState()
  val currentNumber by viewModel.currentHadithNumber.collectAsState()
  val hadithDetail by viewModel.currentHadithDetail.collectAsState()
  val isLoading by viewModel.hadithLoading.collectAsState()
  val errorMessage by viewModel.hadithErrorMessage.collectAsState()
  val userSettings by viewModel.userSettings.collectAsState()

  var inputNumberText by remember(currentNumber) { mutableStateOf(currentNumber.toString()) }
  var previewPosterItem by remember { mutableStateOf<DhikrItem?>(null) }

  // Theme Colors
  val cardBg = if (isDark) DarkCardSurface else ParchmentCard
  val cardBorder = if (isDark) DarkCardBorder else ParchmentBorder
  val titleColor = if (isDark) DarkTextPrimary else InkTeal
  val subtitleColor = if (isDark) DarkTextSoft else TextSoft
  val goldColor = if (isDark) DarkAccentGold else BronzeGold
  val arabicColor = if (isDark) DarkArabicText else InkTeal
  val urduColor = if (isDark) DarkUrduText else Color(0xFF8A5A1A)

  BackHandler {
    viewModel.navigateTo(Screen.Library)
  }

  ParchmentBackground(modifier = modifier, showMadinahBackdrop = false) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(bottom = 75.dp)
        .testTag("hadith_explorer_screen")
    ) {
      // 1. Top Header Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.navigateTo(Screen.Library) },
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = titleColor
          )
        }

        Image(
          painter = painterResource(id = R.drawable.app_brand_logo),
          contentDescription = "15 Seconds for Allah Logo",
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .border(1.2.dp, goldColor, CircleShape),
          contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "صحاح ستہ و کتبِ سبعہ (36,000+ Hadiths)",
            fontFamily = UrduFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
          )
          Text(
            text = "تمام ۷ مستند کتب کی مکمل احادیث • عربی و اردو ترجمہ",
            fontFamily = UrduFontFamily,
            fontSize = 11.5.sp,
            color = subtitleColor
          )
        }

        // Random Hadith Button
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = goldColor.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, goldColor.copy(alpha = 0.4f)),
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable {
              keyboardController?.hide()
              viewModel.loadRandomHadith()
            }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = goldColor, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "بے ترتیب",
              fontFamily = UrduFontFamily,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = goldColor
            )
          }
        }
      }

      // 2. 7 Books Horizontal Tabs
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(HadithCollections.books) { book ->
          val isSelected = book.id == selectedBook.id
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) goldColor else cardBg,
            border = BorderStroke(1.dp, if (isSelected) goldColor else cardBorder),
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                keyboardController?.hide()
                viewModel.selectHadithBook(book)
              }
              .testTag("book_tab_${book.id}")
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = book.nameUrdu,
                fontFamily = UrduFontFamily,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else titleColor
              )
              Text(
                text = "${book.totalHadiths} احادیث",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.85f) else subtitleColor
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Hadith Number Quick Jump Bar & Pagination
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Previous Hadith Button
          IconButton(
            onClick = {
              keyboardController?.hide()
              viewModel.loadPreviousHadith()
            },
            enabled = currentNumber > 1 && !isLoading,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Previous Hadith",
              tint = if (currentNumber > 1) goldColor else subtitleColor.copy(alpha = 0.4f)
            )
          }

          // Number Input and Jump Button
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "حدیث نمبر:",
              fontFamily = UrduFontFamily,
              fontSize = 12.sp,
              color = subtitleColor
            )
            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
              value = inputNumberText,
              onValueChange = { newText ->
                if (newText.all { it.isDigit() } && newText.length <= 5) {
                  inputNumberText = newText
                }
              },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Search),
              keyboardActions = KeyboardActions(
                onSearch = {
                  keyboardController?.hide()
                  val num = inputNumberText.toIntOrNull() ?: 1
                  viewModel.fetchCurrentHadith(number = num)
                }
              ),
              shape = RoundedCornerShape(8.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = goldColor,
                unfocusedBorderColor = cardBorder
              ),
              modifier = Modifier
                .width(76.dp)
                .height(44.dp)
                .testTag("hadith_number_input")
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
              text = "/ ${selectedBook.totalHadiths}",
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = subtitleColor
            )

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = goldColor,
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  keyboardController?.hide()
                  val num = inputNumberText.toIntOrNull() ?: 1
                  viewModel.fetchCurrentHadith(number = num)
                }
            ) {
              Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.padding(8.dp).size(16.dp)
              )
            }
          }

          // Next Hadith Button
          IconButton(
            onClick = {
              keyboardController?.hide()
              viewModel.loadNextHadith()
            },
            enabled = currentNumber < selectedBook.totalHadiths && !isLoading,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Next Hadith",
              tint = if (currentNumber < selectedBook.totalHadiths) goldColor else subtitleColor.copy(alpha = 0.4f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4. Main Hadith Content View
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp)
      ) {
        item {
          if (isLoading) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = goldColor, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  text = "حدیث مبارکہ لوڈ ہو رہی ہے...",
                  fontFamily = UrduFontFamily,
                  fontSize = 13.sp,
                  color = subtitleColor
                )
              }
            }
          } else if (errorMessage != null) {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = cardBg),
              border = BorderStroke(1.dp, cardBorder),
              modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "⚠️ $errorMessage",
                  fontFamily = UrduFontFamily,
                  fontSize = 13.5.sp,
                  color = Color(0xFFD32F2F),
                  textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                  onClick = { viewModel.fetchCurrentHadith(number = currentNumber) },
                  colors = ButtonDefaults.buttonColors(containerColor = goldColor)
                ) {
                  Text("دوبارہ کوشش کریں (Retry)", fontFamily = UrduFontFamily, color = Color.White)
                }
              }
            }
          } else {
            val hadith = hadithDetail
            if (hadith != null) {
              Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.2.dp, goldColor.copy(alpha = 0.4f)),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 16.dp)
              ) {
              Column(modifier = Modifier.padding(18.dp)) {
                // Book badge + Chapter Name
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = goldColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, goldColor.copy(alpha = 0.3f))
                  ) {
                    Text(
                      text = "📖 ${hadith.bookNameUrdu} • حدیث ${hadith.hadithNumber}",
                      fontFamily = UrduFontFamily,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = goldColor,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                  }

                  IconButton(
                    onClick = { viewModel.toggleHadithBookmark(hadith) },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(
                      imageVector = if (hadith.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                      contentDescription = "Bookmark",
                      tint = if (hadith.isBookmarked) goldColor else subtitleColor
                    )
                  }
                }

                if (hadith.chapterName.isNotBlank()) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = hadith.chapterName,
                    fontFamily = ArabicFontFamily,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Arabic Text
                Text(
                  text = hadith.arabicText,
                  fontFamily = ArabicFontFamily,
                  fontSize = 21.sp,
                  fontWeight = FontWeight.Bold,
                  color = arabicColor,
                  lineHeight = 36.sp,
                  textAlign = TextAlign.Right,
                  modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Urdu Translation
                Text(
                  text = "📖 سلیس اردو ترجمہ:",
                  fontFamily = UrduFontFamily,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = goldColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = hadith.urduText,
                  fontFamily = UrduNastaliqFontFamily,
                  fontSize = 14.5.sp,
                  color = urduColor,
                  lineHeight = 25.sp,
                  textAlign = TextAlign.Right,
                  modifier = Modifier.fillMaxWidth()
                )

                if (hadith.englishText.isNotBlank()) {
                  Spacer(modifier = Modifier.height(12.dp))
                  Text(
                    text = "English Translation:",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = subtitleColor
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = hadith.englishText,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = subtitleColor,
                    lineHeight = 18.sp
                  )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons: 15s Moment, WhatsApp Poster, Copy Text
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  // 15s Dhikr Moment
                  Button(
                    onClick = {
                      val dhikrItem = DhikrItem(
                        id = "hadith_${hadith.bookId}_${hadith.hadithNumber}",
                        arabic = hadith.arabicText.take(180),
                        transliteration = "${hadith.bookNameEnglish} #${hadith.hadithNumber}",
                        translationUrdu = hadith.urduText.take(220),
                        translation = hadith.englishText.take(180),
                        contemplativeNote = "حدیث مبارکہ پر غور و فکر اور عمل کی نیت۔",
                        category = hadith.bookNameUrdu,
                        source = "${hadith.bookNameUrdu} ${hadith.hadithNumber}",
                        virtue = "سنت نبوی پر عمل اور دینی بصیرت کا حصول۔",
                        defaultDurationSeconds = 15,
                        isQuranic = false
                      )
                      viewModel.selectDhikrForMoment(dhikrItem, startImmediately = true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InkTeal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.2f).height(42.dp)
                  ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("۱۵ سیکنڈ غور", fontFamily = UrduFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }

                  // WhatsApp Poster Preview & Share
                  Button(
                    onClick = {
                      val dhikrItem = DhikrItem(
                        id = "hadith_${hadith.bookId}_${hadith.hadithNumber}",
                        arabic = hadith.arabicText.take(160),
                        transliteration = "${hadith.bookNameEnglish} #${hadith.hadithNumber}",
                        translationUrdu = hadith.urduText.take(180),
                        translation = hadith.englishText.take(160),
                        contemplativeNote = "سنت نبویﷺ",
                        category = hadith.bookNameUrdu,
                        source = "${hadith.bookNameUrdu} ${hadith.hadithNumber}",
                        virtue = "حدیث نبوی کی اشاعت اور صدقہ جاریہ۔",
                        defaultDurationSeconds = 15,
                        isQuranic = false
                      )
                      previewPosterItem = dhikrItem
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E5A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.3f).height(42.dp)
                  ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("پوسٹر شیئر", fontFamily = UrduFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }

                  // Copy Hadith
                  OutlinedButton(
                    onClick = {
                      val fullText = """
✨ ${hadith.bookNameUrdu} - حدیث نمبر: ${hadith.hadithNumber} ✨

${hadith.arabicText}

📖 اردو ترجمہ:
${hadith.urduText}

📍 حوالہ: ${hadith.bookNameUrdu}: ${hadith.hadithNumber}
📱 15 Seconds for Allah App
                      """.trimIndent()
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      clipboard.setPrimaryClip(ClipData.newPlainText("Hadith Text", fullText))
                      Toast.makeText(context, "حدیث مبارکہ کاپی ہو گئی ✓", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.height(42.dp)
                  ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = titleColor, modifier = Modifier.size(16.dp))
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // GET REWARDS (15s Toolyfi Timer) for Hadith Recitation
                Button(
                  onClick = {
                    val dhikrItem = DhikrItem(
                      id = "hadith_${hadith.bookId}_${hadith.hadithNumber}",
                      arabic = hadith.arabicText.take(180),
                      transliteration = "${hadith.bookNameEnglish} #${hadith.hadithNumber}",
                      translationUrdu = hadith.urduText.take(220),
                      translation = hadith.englishText.take(180),
                      contemplativeNote = "حدیث مبارکہ کا مطالعہ اور سنت پر عمل۔",
                      category = hadith.bookNameUrdu,
                      source = "${hadith.bookNameUrdu} ${hadith.hadithNumber}",
                      virtue = "سنت نبویﷺ کا علم اور حسنات کا حصول۔",
                      defaultDurationSeconds = 15,
                      isQuranic = false
                    )
                    val id = userSettings.userEmail.ifBlank { userSettings.userName }
                    ShareHelper.openWebTimerPage(context, dhikrItem, id)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color(0xFF1B1305)),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("hadith_claim_points_button")
                ) {
                  Text(
                    text = "🎁 GET REWARDS • ثواب بھی ، Rewards بھی (15s Web Timer)",
                    fontFamily = UrduFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1305)
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  if (previewPosterItem != null) {
    PosterPreviewDialog(
      dhikr = previewPosterItem!!,
      onDismiss = { previewPosterItem = null }
    )
  }
}
}
