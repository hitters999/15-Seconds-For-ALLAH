package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moment_logs")
data class MomentLogEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val dhikrId: String,
  val title: String,
  val category: String,
  val durationSeconds: Int,
  val timestamp: Long,
  val dateKey: String
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
  @PrimaryKey
  val dhikrId: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
  @PrimaryKey
  val id: Int = 1,
  val userName: String = "خادمِ ذکر (Servant of Allah)",
  val userEmail: String = "guest@15secondsforallah.com",
  val userPhone: String = "",
  val reminderInterval: String = "Every 1 hour (1 گھنٹہ بعد)",
  val dailyGoal: Int = 8,
  val hapticsEnabled: Boolean = true,
  val soundEnabled: Boolean = true,
  val isDarkMode: Boolean? = null,
  val totalScore: Int = 340,
  val spiritualRank: String = "صاحبِ استقامت (Master of Devotion)",
  val isSignedIn: Boolean = false,
  val authProvider: String = "Guest", // "Google", "Email", "Phone", "Guest"
  val selectedTimezone: String = "Asia/Karachi"
)

@Entity(tableName = "registered_accounts")
data class UserAccountEntity(
  @PrimaryKey
  val identifier: String, // email or phone
  val displayName: String,
  val accountType: String, // "Google", "Email", "Phone"
  val totalScore: Int = 0,
  val joinedTimestamp: Long = System.currentTimeMillis()
)
