package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InsightsScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val streak by viewModel.streakCount.collectAsState()
  val totalMoments by viewModel.totalMomentsCount.collectAsState()
  val activityGrid by viewModel.activityGrid.collectAsState()
  val recentLogs by viewModel.recentLogs.collectAsState()
  val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

  ParchmentBackground(modifier = modifier) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(horizontal = 22.dp)
        .padding(bottom = 90.dp)
        .testTag("insights_screen")
    ) {
      item {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "Spiritual Insights",
          fontFamily = FontFamily.Serif,
          fontSize = 26.sp,
          fontWeight = FontWeight.Normal,
          color = InkTeal
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Consistency in small deeds loved most by Allah",
          fontFamily = FontFamily.SansSerif,
          fontSize = 12.sp,
          color = TextSoft
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stat Row from Mockup Screen 5 (.stat-row { 21 DAY STREAK, 340 MOMENTS, 92% THIS WEEK })
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          StatItem(
            value = "$streak",
            label = "DAY STREAK",
            modifier = Modifier.weight(1f)
          )
          StatItem(
            value = "$totalMoments",
            label = "MOMENTS",
            modifier = Modifier.weight(1f)
          )
          StatItem(
            value = "92%",
            label = "THIS WEEK",
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Habit Heat-Map Grid from Mockup Screen 5: .grid { repeat(7, 1fr) }
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = ParchmentCard),
          border = BorderStroke(1.dp, ParchmentBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "PRESENCE GRID",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = BronzeGold,
                letterSpacing = 1.2.sp
              )
              Text(
                text = "Last 5 Weeks",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = TextSoft
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day labels header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                  text = day,
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 10.sp,
                  color = TextSoft,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.weight(1f)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5 rows of 7 days (35 days)
            for (row in 0 until 5) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 2.5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
              ) {
                for (col in 0 until 7) {
                  val index = row * 7 + col
                  val dayData = activityGrid.getOrNull(index)
                  val isHit = (dayData?.count ?: 0) > 0
                  val isToday = dayData?.isToday == true

                  val cellColor = when {
                    isHit && (dayData?.count ?: 0) >= 4 -> BronzeGold
                    isHit -> BronzeGoldLight
                    else -> ParchmentBorder.copy(alpha = 0.5f)
                  }

                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .aspectRatio(1f)
                      .clip(RoundedCornerShape(4.dp))
                      .background(cellColor)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Calmest Hour Card from Mockup Screen 5 (.pad { .card { "Your calmest hour is usually Fajr." } })
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
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(InkTeal)
            ) {
              Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = BronzeGoldLight,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = "Your calmest hour is usually Fajr.",
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = InkTeal
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Morning remembrance creates peace that lasts all day.",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = TextSoft
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
          text = "RECENT MOMENTS",
          fontFamily = FontFamily.SansSerif,
          fontWeight = FontWeight.SemiBold,
          fontSize = 11.sp,
          color = BronzeGold,
          letterSpacing = 1.2.sp,
          modifier = Modifier.padding(vertical = 4.dp)
        )
      }

      items(recentLogs) { log ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, ParchmentBorder),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = BronzeGold,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = log.title,
                  fontFamily = FontFamily.Serif,
                  fontSize = 14.sp,
                  color = InkTeal
                )
                Text(
                  text = "${log.category} • ${log.durationSeconds}s",
                  fontFamily = FontFamily.SansSerif,
                  fontSize = 11.sp,
                  color = TextSoft
                )
              }
            }

            Text(
              text = timeFormat.format(Date(log.timestamp)),
              fontFamily = FontFamily.SansSerif,
              fontSize = 11.sp,
              color = TextSoft
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StatItem(
  value: String,
  label: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = value,
      fontFamily = FontFamily.Serif,
      fontSize = 24.sp,
      fontWeight = FontWeight.Normal,
      color = InkTeal
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = label,
      fontFamily = FontFamily.SansSerif,
      fontSize = 9.sp,
      fontWeight = FontWeight.Medium,
      color = TextSoft,
      letterSpacing = 1.sp
    )
  }
}
