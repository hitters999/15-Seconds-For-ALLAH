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

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMomentLog(log: MomentLogEntity): Long

  @Query("SELECT COUNT(*) FROM moment_logs")
  fun getTotalMomentsCount(): Flow<Int>

  @Query("SELECT COUNT(DISTINCT dateKey) FROM moment_logs")
  fun getTotalActiveDays(): Flow<Int>

  @Query("SELECT * FROM user_settings WHERE id = 1")
  fun getUserSettings(): Flow<UserSettingsEntity?>

  @Query("SELECT * FROM user_settings WHERE id = 1")
  suspend fun getUserSettingsDirect(): UserSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateUserSettings(settings: UserSettingsEntity)

  @Query("SELECT * FROM bookmarked_dhikr ORDER BY timestamp DESC")
  fun getAllBookmarks(): Flow<List<BookmarkEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity)

  @Query("DELETE FROM bookmarked_dhikr WHERE dhikrId = :dhikrId")
  suspend fun deleteBookmark(dhikrId: String)

  @Query("DELETE FROM moment_logs")
  suspend fun clearAllLogs()
}
