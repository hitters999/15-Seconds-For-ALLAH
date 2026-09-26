package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.ui.MainViewModel
import com.example.ui.components.ParchmentBackground
import com.example.ui.theme.BronzeGold
import com.example.ui.theme.BronzeGoldLight
import com.example.ui.theme.InkTeal
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentSubtle
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft

@Composable
fun LibraryScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val allItems by viewModel.allItems.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()

  // Filter items according to search & category
  val filteredItems = allItems.filter { item ->
    val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
    val matchesSearch = searchQuery.isBlank() ||
      item.transliteration.contains(searchQuery, ignoreCase = true) ||
      item.translation.contains(searchQuery, ignoreCase = true) ||
      item.category.contains(searchQuery, ignoreCase = true) ||
      item.arabic.contains(searchQuery)
    matchesCategory && matchesSearch
  }

  // Precomputed category counts for the list view from the mockup
  val categoryCounts = mapOf(
    "Morning Remembrance" to allItems.count { it.category == "Morning Remembrance" },
    "Evening Remembrance" to allItems.count { it.category == "Evening Remembrance" },
    "Gratitude" to allItems.count { it.category == "Gratitude" },
    "Forgiveness" to allItems.count { it.category == "Forgiveness" },
    "Before Sleep" to allItems.count { it.category == "Before Sleep" },
    "Protection" to allItems.count { it.category == "Protection" },
    "Hardship" to allItems.count { it.category == "Hardship" },
    "Quranic Gems" to allItems.count { it.category == "Quranic Gems" }
  )

  ParchmentBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(bottom = 80.dp)
        .testTag("library_screen")
    ) {
      // Top Title
      Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp)) {
        Text(
          text = "Sacred Library",
          fontFamily = FontFamily.Serif,
          fontSize = 26.sp,
          fontWeight = FontWeight.Normal,
          color = InkTeal
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Every word chosen with intention",
          fontFamily = FontFamily.SansSerif,
          fontSize = 12.sp,
          color = TextSoft
        )
      }

      // Search Bar as in mockup: .search { background:#fff; border:1px solid var(--line); border-radius:12px; }
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = {
          Text(
            text = "Search dhikr, du'a, surah…",
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            color = TextSoft
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = BronzeGold
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextSoft)
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White,
          focusedBorderColor = BronzeGold,
          unfocusedBorderColor = ParchmentBorder,
          cursorColor = InkTeal
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 22.dp)
          .testTag("library_search_input")
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Category Horizontal Filter Pills
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
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                color = if (isSelected) ParchmentSurface else InkTeal
              )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = InkTeal,
              containerColor = ParchmentSurface
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = ParchmentBorder,
              selectedBorderColor = InkTeal
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Category Overview Row from Mockup (Screen 4 Library: .cat { b: category, small: count })
      if (searchQuery.isEmpty() && selectedCategory == "All") {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
        ) {
          item {
            Text(
              text = "CATEGORIES",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp,
              color = BronzeGold,
              letterSpacing = 1.2.sp,
              modifier = Modifier.padding(vertical = 8.dp)
            )
          }

          items(categoryCounts.entries.toList()) { entry ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { viewModel.setSelectedCategory(entry.key) }
                .padding(vertical = 12.dp, horizontal = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = entry.key,
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = InkTeal
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = ParchmentSubtle,
                modifier = Modifier.padding(start = 8.dp)
              ) {
                Text(
                  text = "${entry.value}",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  color = TextSoft,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ParchmentBorder.copy(alpha = 0.6f))
            )
          }

          item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
              text = "ALL REMEMBRANCES",
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp,
              color = BronzeGold,
              letterSpacing = 1.2.sp,
              modifier = Modifier.padding(vertical = 8.dp)
            )
          }

          items(allItems) { item ->
            DhikrItemCard(
              item = item,
              onSelect = { viewModel.selectDhikrForMoment(item, startImmediately = true) },
              onBookmark = { viewModel.toggleBookmark(item.id) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      } else {
        // Filtered List View
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
        ) {
          items(filteredItems) { item ->
            DhikrItemCard(
              item = item,
              onSelect = { viewModel.selectDhikrForMoment(item, startImmediately = true) },
              onBookmark = { viewModel.toggleBookmark(item.id) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun DhikrItemCard(
  item: DhikrItem,
  onSelect: () -> Unit,
  onBookmark: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = ParchmentCard),
    border = BorderStroke(1.dp, ParchmentBorder),
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
          color = ParchmentSubtle
        ) {
          Text(
            text = item.category,
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            color = BronzeGold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "15s",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            color = TextSoft,
            modifier = Modifier.padding(end = 8.dp)
          )
          IconButton(
            onClick = onBookmark,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = if (item.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = if (item.isBookmarked) BronzeGold else TextSoft,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = item.arabic,
        fontFamily = FontFamily.Serif,
        fontSize = 20.sp,
        color = InkTeal,
        lineHeight = 30.sp,
        textAlign = TextAlign.Right,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = item.transliteration,
        fontFamily = FontFamily.Serif,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = TextPrimary
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = item.translation,
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        color = TextSoft,
        lineHeight = 17.sp
      )
    }
  }
}
