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

  val registeredAccounts: Flow<List<UserAccountEntity>> = dao.getAllRegisteredAccounts()

  val allCachedHadiths: Flow<List<HadithEntity>> = dao.getAllCachedHadiths()

  suspend fun deleteUserAccount(identifier: String) {
    dao.deleteUserAccount(identifier)
  }

  suspend fun addOrUpdateHadith(hadith: HadithEntity) {
    dao.insertHadithToCache(hadith)
  }

  suspend fun logCompletedMoment(item: DhikrItem, durationSeconds: Int = 15): UserSettingsEntity {
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

    // Increment user's spiritual score (+10 points per 15s moment)
    val current = dao.getUserSettingsDirect() ?: UserSettingsEntity()
    val newScore = current.totalScore + 10
    val rank = when {
      newScore >= 5000 -> "صاحبِ استقامت (Master League)"
      newScore >= 2000 -> "ذاکرِ مداوم (Diamond League)"
      newScore >= 800 -> "محبِ ذکر (Gold League)"
      else -> "مبتدی (Seeker of Peace)"
    }
    val updated = current.copy(totalScore = newScore, spiritualRank = rank)
    dao.insertOrUpdateUserSettings(updated)

    // Update account profile in registered viewers database directory
    if (updated.isSignedIn) {
      val identifier = if (updated.userPhone.isNotBlank()) updated.userPhone else updated.userEmail
      dao.insertUserAccount(
        UserAccountEntity(
          identifier = identifier,
          displayName = updated.userName,
          accountType = updated.authProvider,
          totalScore = newScore
        )
      )
    }
    return updated
  }

  suspend fun ensureUserSettingsInitialized() {
    val current = dao.getUserSettingsDirect()
    if (current == null) {
      dao.insertOrUpdateUserSettings(
        UserSettingsEntity(
          id = 1,
          userName = "خادمِ ذکر (Servant of Allah)",
          totalScore = 340,
          spiritualRank = "مبتدی (Seeker of Peace)"
        )
      )
    }
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

  suspend fun registerOrSwitchUser(identifier: String, name: String, type: String) {
    val current = dao.getUserSettingsDirect() ?: UserSettingsEntity()
    val isPhone = type == "Phone" || identifier.matches(Regex("^[+0-9\\s-]+$"))

    val updatedSettings = current.copy(
      userName = name.ifBlank { if (isPhone) "موبائل یوزر ($identifier)" else identifier.substringBefore("@") },
      userEmail = if (isPhone) "" else identifier,
      userPhone = if (isPhone) identifier else "",
      isSignedIn = true,
      authProvider = type
    )
    dao.insertOrUpdateUserSettings(updatedSettings)

    // Record in registered viewers directory
    dao.insertUserAccount(
      UserAccountEntity(
        identifier = identifier,
        displayName = updatedSettings.userName,
        accountType = type,
        totalScore = current.totalScore
      )
    )
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

    // Initial community viewers in database
    dao.insertUserAccount(UserAccountEntity("qari.abdullah@gmail.com", "قاری عبد اللہ", "Google", 1420))
    dao.insertUserAccount(UserAccountEntity("+923001234567", "حافظ محمد عمر", "Phone", 2850))
    dao.insertUserAccount(UserAccountEntity("tariq.masood@outlook.com", "طارق مسعود", "Email", 950))
  }
}
