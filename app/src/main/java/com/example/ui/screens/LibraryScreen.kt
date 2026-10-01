package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.BismillahCalligraphyHeader
import com.example.ui.components.ParchmentBackground
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
import com.example.ui.theme.ParchmentSubtle
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft
import com.example.ui.theme.UrduFontFamily
import com.example.util.ShareHelper

@Composable
fun LibraryScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val allItems by viewModel.allItems.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val context = LocalContext.current
  val isDark = isSystemInDarkTheme()

  val cardBg = if (isDark) DarkCardSurface else ParchmentCard
  val cardBorder = if (isDark) DarkCardBorder else ParchmentBorder
  val titleColor = if (isDark) DarkTextPrimary else InkTeal
  val subtitleColor = if (isDark) DarkTextSoft else TextSoft
  val goldColor = if (isDark) DarkAccentGold else BronzeGold
  val arabicColor = if (isDark) DarkArabicText else InkTeal
  val urduColor = if (isDark) DarkUrduText else Color(0xFF8A5A1A)

  val filteredItems = allItems.filter { item ->
    val matchesCategory = when {
      selectedCategory.startsWith("All") -> true
      selectedCategory.contains("Saved") || selectedCategory.contains("محفوظ") -> item.isBookmarked
      selectedCategory.contains("بخاری") -> item.source.contains("بخاری") || item.category.contains("بخاری")
      selectedCategory.contains("مسلم") -> item.source.contains("مسلم") || item.category.contains("مسلم")
      selectedCategory.contains("ترمذی") -> item.source.contains("ترمذی") || item.category.contains("ترمذی")
      selectedCategory.contains("داؤد") -> item.source.contains("داؤد") || item.category.contains("داؤد")
      selectedCategory.contains("نسائی") -> item.source.contains("نسائی") || item.category.contains("نسائی")
      selectedCategory.contains("ماجہ") -> item.source.contains("ماجہ") || item.category.contains("ماجہ")
      selectedCategory.contains("مالک") -> item.source.contains("مالک") || item.category.contains("مالک")
      else -> item.category.contains(selectedCategory.take(10), ignoreCase = true) ||
        selectedCategory.contains(item.category.take(10), ignoreCase = true)
    }
    val matchesSearch = searchQuery.isBlank() ||
      item.transliteration.contains(searchQuery, ignoreCase = true) ||
      item.translationUrdu.contains(searchQuery, ignoreCase = true) ||
      item.translation.contains(searchQuery, ignoreCase = true) ||
      item.category.contains(searchQuery, ignoreCase = true) ||
      item.source.contains(searchQuery, ignoreCase = true) ||
      item.arabic.contains(searchQuery)
    matchesCategory && matchesSearch
  }

  ParchmentBackground(modifier = modifier, showMadinahBackdrop = false) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(bottom = 80.dp)
        .testTag("library_screen")
    ) {
      Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp)) {
        Text(
          text = "Sacred Library • مقدس ذخیرہ",
          fontFamily = FontFamily.Serif,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = titleColor
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = "${allItems.size}+ تمام اذکار، دعائیں اور 30ویں سپارے کی آیات مع اردو ترجمہ",
          fontFamily = UrduFontFamily,
          fontSize = 13.sp,
          color = subtitleColor
        )
      }

      // 36,000+ Complete Hadith Explorer Banner
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF09292F) else Color(0xFFEAF5F3)),
        border = BorderStroke(1.2.dp, goldColor.copy(alpha = 0.5f)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 22.dp)
          .clip(RoundedCornerShape(16.dp))
          .clickable { viewModel.navigateTo(Screen.HadithExplorer) }
          .testTag("open_hadith_explorer_banner")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = goldColor.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, goldColor.copy(alpha = 0.4f)),
              modifier = Modifier.size(42.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text("📚", fontSize = 20.sp)
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "مکمل صحاح ستہ و کتبِ سبعہ",
                  fontFamily = UrduFontFamily,
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = titleColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                ) {
                  Text(
                    text = "36,000+ احادیث",
                    fontFamily = UrduFontFamily,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "بخاری، مسلم، ترمذی وغیرہ کی ہر حدیث نمبر وار پڑھیں ➔",
                fontFamily = UrduFontFamily,
                fontSize = 11.sp,
                color = subtitleColor
              )
            }
          }

          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open Hadith Explorer",
            tint = goldColor,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = {
          Text(
            text = "تلاش کریں (Search dhikr, du'a, urdu…)",
            fontFamily = UrduFontFamily,
            fontSize = 12.5.sp,
            color = subtitleColor
          )
        },
        leadingIcon = {
          Icon(Icons.Filled.Search, contentDescription = "Search", tint = goldColor)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = subtitleColor)
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = cardBg,
          unfocusedContainerColor = cardBg,
          focusedBorderColor = goldColor,
          unfocusedBorderColor = cardBorder,
          cursorColor = goldColor,
          focusedTextColor = titleColor,
          unfocusedTextColor = titleColor
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 22.dp)
          .testTag("library_search_input")
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 22.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "مجموعہ کتب و ابواب (Hadith Books & Categories)",
          fontFamily = UrduFontFamily,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = subtitleColor
        )
        Text(
          text = "${filteredItems.size} اذکار و احادیث",
          fontFamily = UrduFontFamily,
          fontSize = 11.5.sp,
          color = goldColor
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Category Filter Chips
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(DhikrCatalog.categories) { category ->
          val isSelected = selectedCategory.equals(category, ignoreCase = true)
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.setSelectedCategory(category) },
            label = {
              Text(
                text = category,
                fontFamily = UrduFontFamily,
                fontSize = 12.sp,
                color = if (isSelected) Color.White else titleColor
              )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = goldColor,
              containerColor = cardBg
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = cardBorder,
              selectedBorderColor = goldColor
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // List of Items
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 22.dp)
      ) {
        items(filteredItems) { item ->
          DhikrItemCard(
            item = item,
            isDark = isDark,
            cardBg = cardBg,
            cardBorder = cardBorder,
            titleColor = titleColor,
            subtitleColor = subtitleColor,
            arabicColor = arabicColor,
            urduColor = urduColor,
            goldColor = goldColor,
            onSelect = { viewModel.selectDhikrForMoment(item, startImmediately = true) },
            onBookmark = { viewModel.toggleBookmark(item.id) },
            onSharePoster = { ShareHelper.shareDhikrPoster(context, item) }
          )
          Spacer(modifier = Modifier.height(10.dp))
        }
      }
    }
  }
}

@Composable
private fun DhikrItemCard(
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
  onBookmark: () -> Unit,
  onSharePoster: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    border = BorderStroke(1.dp, cardBorder),
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onSelect)
      .testTag("dhikr_card_${item.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = goldColor.copy(alpha = 0.12f),
          border = BorderStroke(0.8.dp, goldColor.copy(alpha = 0.25f))
        ) {
          Text(
            text = item.category,
            fontFamily = UrduFontFamily,
            fontSize = 11.sp,
            color = goldColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "15s",
            fontFamily = FontFamily.Serif,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = subtitleColor,
            modifier = Modifier.padding(end = 4.dp)
          )
          IconButton(
            onClick = onSharePoster,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.Share,
              contentDescription = "Share Poster",
              tint = goldColor,
              modifier = Modifier.size(16.dp)
            )
          }
          IconButton(
            onClick = onBookmark,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = if (item.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = if (item.isBookmarked) goldColor else subtitleColor,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Distinct Bismillah Header if Quranic Ayah
      if (item.isQuranic) {
        BismillahCalligraphyHeader(isDarkTheme = isDark)
        Spacer(modifier = Modifier.height(8.dp))
      }

      // Arabic Calligraphy with Amiri font
      Text(
        text = item.arabic,
        fontFamily = ArabicFontFamily,
        fontSize = 22.sp,
        color = arabicColor,
        lineHeight = 34.sp,
        textAlign = TextAlign.Right,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Transliteration
      Text(
        text = item.transliteration,
        fontFamily = FontFamily.Serif,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = titleColor
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Urdu Translation in Noto Nastaliq Urdu font
      Text(
        text = item.translationUrdu,
        fontFamily = UrduFontFamily,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Normal,
        color = urduColor,
        lineHeight = 22.sp
      )

      Spacer(modifier = Modifier.height(3.dp))

      // English Translation
      Text(
        text = item.translation,
        fontFamily = FontFamily.Serif,
        fontSize = 11.5.sp,
        color = subtitleColor,
        lineHeight = 17.sp
      )

      if (item.source.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = goldColor.copy(alpha = 0.1f),
          border = BorderStroke(0.6.dp, goldColor.copy(alpha = 0.3f))
        ) {
          Text(
            text = "📖 ${item.source}",
            fontFamily = UrduFontFamily,
            fontSize = 11.sp,
            color = goldColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}

