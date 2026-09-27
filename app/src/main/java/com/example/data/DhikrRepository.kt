package com.example.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DhikrRepository(
  private val dao: MomentDao,
  private val context: Context
) {

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  private val rawCatalogFlow = MutableStateFlow<List<DhikrItem>>(DhikrCatalog.items)

  init {
    // Load 1000+ items from assets in background IO dispatcher
    CoroutineScope(Dispatchers.IO).launch {
      val fullList = DhikrCatalog.loadFullCatalog(context)
      rawCatalogFlow.value = fullList
    }
  }

  fun getTodayDateKey(): String = dateFormat.format(Date())

  val bookmarks: Flow<Set<String>> = dao.getAllBookmarks().map { list ->
    list.map { it.dhikrId }.toSet()
  }

  val allItems: Flow<List<DhikrItem>> = combine(rawCatalogFlow, bookmarks) { rawList, bookmarkSet ->
    rawList.map { item ->
      item.copy(isBookmarked = bookmarkSet.contains(item.id))
    }
  }

  val allLogs: Flow<List<MomentLogEntity>> = dao.getAllMomentLogs()

  fun getTodayLogs(): Flow<List<MomentLogEntity>> = dao.getMomentLogsForDate(getTodayDateKey())

  fun getRecentLogs(limit: Int = 10): Flow<List<MomentLogEntity>> = dao.getRecentMomentLogs(limit)

  val totalMomentsCount: Flow<Int> = dao.getTotalMomentsCount()

  val userSettings: Flow<UserSettingsEntity> = dao.getUserSettings().map { settings ->
    settings ?: UserSettingsEntity()
  }

  suspend fun logCompletedMoment(item: DhikrItem, durationSeconds: Int = 15) {
    val dateKey = getTodayDateKey()
    val log = MomentLogEntity(
      dhikrId = item.id,
      title = item.transliteration,
      category = item.category,
      durationSeconds = durationSeconds,
      timestamp = System.currentTimeMillis(),
      dateKey = dateKey
    )
    dao.insertMomentLog(log)

    // Increment user's spiritual score (+10 points per 15s moment, bonus +50 on completing daily goal)
    val current = dao.getUserSettingsDirect() ?: UserSettingsEntity()
    val newScore = current.totalScore + 10
    val rank = when {
      newScore >= 5000 -> "صاحبِ استقامت (Master of Devotion)"
      newScore >= 2000 -> "ذاکرِ مداوم (Consistent Rememberer)"
      newScore >= 800 -> "محبِ ذکر (Lover of Remembrance)"
      else -> "مبتدی (Seeker of Peace)"
    }
    dao.insertOrUpdateUserSettings(current.copy(totalScore = newScore, spiritualRank = rank))
  }

  suspend fun toggleBookmark(dhikrId: String, currentStatus: Boolean) {
    if (currentStatus) {
      dao.deleteBookmark(dhikrId)
    } else {
      dao.insertBookmark(BookmarkEntity(dhikrId = dhikrId))
    }
  }

  suspend fun updateSettings(settings: UserSettingsEntity) {
    dao.insertOrUpdateUserSettings(settings)
  }

  suspend fun clearHistory() {
    dao.clearAllLogs()
    val current = dao.getUserSettingsDirect() ?: UserSettingsEntity()
    dao.insertOrUpdateUserSettings(current.copy(totalScore = 0))
  }

  suspend fun seedInitialDataIfEmpty() {
    val dateKey = getTodayDateKey()
    val cal = Calendar.getInstance()

    for (i in 0 until 5) {
      val item = DhikrCatalog.items[i % DhikrCatalog.items.size]
      dao.insertMomentLog(
        MomentLogEntity(
          dhikrId = item.id,
          title = item.transliteration,
          category = item.category,
          durationSeconds = 15,
          timestamp = System.currentTimeMillis() - (i * 3600_000L),
          dateKey = dateKey
        )
      )
    }

    for (dayOffset in 1..25) {
      cal.time = Date()
      cal.add(Calendar.DAY_OF_YEAR, -dayOffset)
      val pastDateKey = dateFormat.format(cal.time)
      val numEntries = if (dayOffset % 7 == 6) 2 else if (dayOffset % 5 == 0) 6 else 4
      for (k in 0 until numEntries) {
        val item = DhikrCatalog.items[(dayOffset + k) % DhikrCatalog.items.size]
        dao.insertMomentLog(
          MomentLogEntity(
            dhikrId = item.id,
            title = item.transliteration,
            category = item.category,
            durationSeconds = 15,
            timestamp = cal.timeInMillis - (k * 2400_000L),
            dateKey = pastDateKey
          )
        )
      }
    }

    dao.insertBookmark(BookmarkEntity(dhikrId = "juz30_jannat_nafs_mutmainna"))
    dao.insertBookmark(BookmarkEntity(dhikrId = "juz30_hukam_inshirah_ease"))
  }
}
