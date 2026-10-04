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
  val userName: String = "Guest (سائن ان نہیں)",
  val userEmail: String = "",
  val userPhone: String = "",
  val reminderInterval: String = "Every 1 hour (1 گھنٹہ بعد)",
  val dailyGoal: Int = 8,
  val hapticsEnabled: Boolean = true,
  val soundEnabled: Boolean = true,
  val isDarkMode: Boolean? = null,
  val totalScore: Int = 0,
  val spiritualRank: String = "مبتدی (Seeker of Peace)",
  val isSignedIn: Boolean = false,
  val authProvider: String = "Guest", // "Google" or "Guest"
  val selectedTimezone: String = "Asia/Karachi"
)

@Entity(tableName = "registered_accounts")
data class UserAccountEntity(
  @PrimaryKey
  val identifier: String, // Google Email
  val displayName: String,
  val accountType: String = "Google",
  val totalScore: Int = 0,
  val completedSessions: Int = 0,
  val pkrBalance: Double = 0.0,
  val joinedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "hadith_cache", primaryKeys = ["bookKey", "hadithNumber"])
data class HadithEntity(
  val bookKey: String,
  val hadithNumber: Int,
  val bookNameUrdu: String,
  val chapterName: String,
  val arabicText: String,
  val urduText: String,
  val englishText: String = "",
  val isBookmarked: Boolean = false,
  val cachedAt: Long = System.currentTimeMillis()
)
