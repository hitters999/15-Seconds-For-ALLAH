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
  val durationSeconds: Int = 15,
  val timestamp: Long = System.currentTimeMillis(),
  val dateKey: String // Format: YYYY-MM-DD
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
  @PrimaryKey
  val id: Int = 1,
  val userName: String = "Abdur Rahim",
  val reminderInterval: String = "Every 30 min",
  val dailyGoal: Int = 8,
  val hapticsEnabled: Boolean = true,
  val soundEnabled: Boolean = true,
  val isDarkMode: Boolean? = null // null means system default
)

@Entity(tableName = "bookmarked_dhikr")
data class BookmarkEntity(
  @PrimaryKey
  val dhikrId: String,
  val timestamp: Long = System.currentTimeMillis()
)
