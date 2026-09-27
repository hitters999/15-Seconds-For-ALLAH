package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MomentDao {
  @Query("SELECT * FROM moment_logs ORDER BY timestamp DESC")
  fun getAllMomentLogs(): Flow<List<MomentLogEntity>>

  @Query("SELECT * FROM moment_logs WHERE dateKey = :dateKey ORDER BY timestamp DESC")
  fun getMomentLogsForDate(dateKey: String): Flow<List<MomentLogEntity>>

  @Query("SELECT * FROM moment_logs ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentMomentLogs(limit: Int): Flow<List<MomentLogEntity>>

  @Query("SELECT COUNT(*) FROM moment_logs")
  fun getTotalMomentsCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMomentLog(log: MomentLogEntity): Long

  @Query("DELETE FROM moment_logs")
  suspend fun clearAllLogs()

  // Bookmarks
  @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
  fun getAllBookmarks(): Flow<List<BookmarkEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity)

  @Query("DELETE FROM bookmarks WHERE dhikrId = :dhikrId")
  suspend fun deleteBookmark(dhikrId: String)

  // Settings
  @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
  fun getUserSettings(): Flow<UserSettingsEntity?>

  @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
  suspend fun getUserSettingsDirect(): UserSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateUserSettings(settings: UserSettingsEntity)

  // Registered Accounts / Viewers Database Directory
  @Query("SELECT * FROM registered_accounts ORDER BY joinedTimestamp DESC")
  fun getAllRegisteredAccounts(): Flow<List<UserAccountEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUserAccount(account: UserAccountEntity)
}
