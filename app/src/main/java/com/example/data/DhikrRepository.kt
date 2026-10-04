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

    // Increment user's score (+10 points = 10 Paisa per verified 15s Toolyfi session)
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

    // Update Google Account profile & calculations in registered_accounts database
    if (updated.isSignedIn && updated.userEmail.isNotBlank()) {
      val existingAcc = dao.getUserAccountDirect(updated.userEmail)
      val sessions = (existingAcc?.completedSessions ?: 0) + 1
      val joinedAt = existingAcc?.joinedTimestamp ?: System.currentTimeMillis()
      dao.insertUserAccount(
        UserAccountEntity(
          identifier = updated.userEmail,
          displayName = updated.userName,
          accountType = "Google",
          totalScore = newScore,
          completedSessions = sessions,
          pkrBalance = newScore * 0.01,
          joinedTimestamp = joinedAt
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
          userName = "Guest (سائن ان نہیں)",
          userEmail = "",
          totalScore = 0,
          spiritualRank = "مبتدی (Seeker of Peace)",
          isSignedIn = false,
          authProvider = "Guest"
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

  suspend fun registerOrSwitchUser(identifier: String, name: String, type: String = "Google") {
    val normalizedEmail = identifier.trim().lowercase(Locale.US)
    if (normalizedEmail.isBlank()) return

    val current = dao.getUserSettingsDirect() ?: UserSettingsEntity()
    val existingAccount = dao.getUserAccountDirect(normalizedEmail)

    // Combine or restore account score so every user has their own accurate calculations
    val mergedScore = maxOf(current.totalScore, existingAccount?.totalScore ?: 0)
    val sessions = existingAccount?.completedSessions ?: (mergedScore / 10)
    val joinedAt = existingAccount?.joinedTimestamp ?: System.currentTimeMillis()
    val resolvedName = name.ifBlank {
      existingAccount?.displayName?.ifBlank { normalizedEmail.substringBefore("@") }
        ?: normalizedEmail.substringBefore("@")
    }

    val updatedSettings = current.copy(
      userName = resolvedName,
      userEmail = normalizedEmail,
      userPhone = "",
      totalScore = mergedScore,
      isSignedIn = true,
      authProvider = "Google"
    )
    dao.insertOrUpdateUserSettings(updatedSettings)

    // Store Google Email & all calculations in registered_accounts database
    dao.insertUserAccount(
      UserAccountEntity(
        identifier = normalizedEmail,
        displayName = resolvedName,
        accountType = "Google",
        totalScore = mergedScore,
        completedSessions = sessions,
        pkrBalance = mergedScore * 0.01,
        joinedTimestamp = joinedAt
      )
    )
  }

  suspend fun clearHistory() {
    dao.clearAllLogs()
    val current = dao.getUserSettingsDirect() ?: UserSettingsEntity()
    dao.insertOrUpdateUserSettings(current.copy(totalScore = 0))
    if (current.isSignedIn && current.userEmail.isNotBlank()) {
      val existing = dao.getUserAccountDirect(current.userEmail)
      if (existing != null) {
        dao.insertUserAccount(
          existing.copy(totalScore = 0, completedSessions = 0, pkrBalance = 0.0)
        )
      }
    }
  }

  suspend fun seedInitialDataIfEmpty() {
    // New downloads start with 0 Points, 0 Rupees, and No Pre-Login!
    ensureUserSettingsInitialized()
  }
}
