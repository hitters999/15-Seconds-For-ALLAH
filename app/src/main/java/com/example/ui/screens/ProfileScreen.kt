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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.ParchmentSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSoft

@Composable
fun ProfileScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val userSettings by viewModel.userSettings.collectAsState()
  val scrollState = rememberScrollState()

  var showNameDialog by remember { mutableStateOf(false) }
  var showIntervalDialog by remember { mutableStateOf(false) }
  var showGoalDialog by remember { mutableStateOf(false) }
  var showClearDialog by remember { mutableStateOf(false) }
  var tempName by remember { mutableStateOf("") }

  ParchmentBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 22.dp)
        .padding(bottom = 90.dp)
        .testTag("profile_screen"),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      // Avatar circle from Mockup Screen 6: .avatar { width:52px; height:52px; background:var(--bronze-light); }
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(BronzeGoldLight)
          .clickable {
            tempName = userSettings.userName
            showNameDialog = true
          }
          .testTag("profile_avatar_large"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = userSettings.userName.take(1).uppercase(),
          fontFamily = FontFamily.Serif,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          color = InkTeal
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // User name with quick edit affordance
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable {
            tempName = userSettings.userName
            showNameDialog = true
          }
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = userSettings.userName,
          fontFamily = FontFamily.Serif,
          fontSize = 20.sp,
          fontWeight = FontWeight.Medium,
          color = InkTeal
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
          imageVector = Icons.Filled.Edit,
          contentDescription = "Edit name",
          tint = BronzeGold,
          modifier = Modifier.size(16.dp)
        )
      }

      Text(
        text = "Soul committed to daily remembrance",
        fontFamily = FontFamily.SansSerif,
        fontSize = 11.5.sp,
        color = TextSoft
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Profile settings list as shown in Mockup Screen 6:
      // .plist div { display:flex; justify-content:space-between; padding:11px 18px; border-bottom:1px solid var(--line); }
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ParchmentCard),
        border = BorderStroke(1.dp, ParchmentBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
          SettingRow(
            title = "Reminder Interval",
            value = userSettings.reminderInterval,
            onClick = { showIntervalDialog = true }
          )

          SettingRow(
            title = "Daily Presence Goal",
            value = "${userSettings.dailyGoal} moments",
            onClick = { showGoalDialog = true }
          )

          SettingRow(
            title = "Sacred Language",
            value = "Arabic & English",
            onClick = {}
          )

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
                text = "Gentle Vibration",
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.5.sp,
                color = TextPrimary
              )
              Text(
                text = "Tactile pulse on completion",
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
                text = "Completion Chime",
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.5.sp,
                color = TextPrimary
              )
              Text(
                text = "Harmonic bell when 15s ends",
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
                text = "Night Ink Theme",
                fontFamily = FontFamily.SansSerif,
                fontSize = 13.5.sp,
                color = TextPrimary
              )
              Text(
                text = "Deep teal parchment sanctuary",
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

      // Philosophy Card from Design Mockup: "A quieter kind of premium"
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ParchmentSurface),
        border = BorderStroke(1.dp, ParchmentBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "A quieter kind of premium",
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = InkTeal
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Parchment, ink-teal and bronze — replacing the generic green/white app look with something that feels handwritten and calm, not corporate.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = TextSoft,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Reset Data Option
      Text(
        text = "Reset Presence Logs",
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

  // Dialog: Edit Name
  if (showNameDialog) {
    AlertDialog(
      onDismissRequest = { showNameDialog = false },
      title = {
        Text("Your Name", fontFamily = FontFamily.Serif, color = InkTeal)
      },
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
          Text("Save", color = BronzeGold, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showNameDialog = false }) {
          Text("Cancel", color = TextSoft)
        }
      }
    )
  }

  // Dialog: Reminder Interval
  if (showIntervalDialog) {
    val intervals = listOf("Every 15 min", "Every 30 min", "Every 1 hour", "Every 2 hours", "At Prayer Times")
    AlertDialog(
      onDismissRequest = { showIntervalDialog = false },
      title = {
        Text("Reminder Interval", fontFamily = FontFamily.Serif, color = InkTeal)
      },
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
              Text(interval, fontFamily = FontFamily.SansSerif, fontSize = 14.sp, color = TextPrimary)
              if (userSettings.reminderInterval == interval) {
                Text("✓", color = BronzeGold, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showIntervalDialog = false }) {
          Text("Done", color = BronzeGold)
        }
      }
    )
  }

  // Dialog: Daily Goal
  if (showGoalDialog) {
    val goals = listOf(3, 5, 8, 12, 15, 20)
    AlertDialog(
      onDismissRequest = { showGoalDialog = false },
      title = {
        Text("Daily Moments Target", fontFamily = FontFamily.Serif, color = InkTeal)
      },
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
              Text("$g moments per day", fontFamily = FontFamily.SansSerif, fontSize = 14.sp, color = TextPrimary)
              if (userSettings.dailyGoal == g) {
                Text("✓", color = BronzeGold, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showGoalDialog = false }) {
          Text("Done", color = BronzeGold)
        }
      }
    )
  }

  // Dialog: Clear Confirmation
  if (showClearDialog) {
    AlertDialog(
      onDismissRequest = { showClearDialog = false },
      title = { Text("Reset History?", fontFamily = FontFamily.Serif, color = Color(0xFFA83232)) },
      text = { Text("This will clear your completed moments history. Your custom profile settings will be kept.") },
      confirmButton = {
        TextButton(onClick = {
          viewModel.clearAllHistory()
          showClearDialog = false
        }) {
          Text("Reset", color = Color(0xFFA83232))
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearDialog = false }) {
          Text("Cancel", color = TextSoft)
        }
      }
    )
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
