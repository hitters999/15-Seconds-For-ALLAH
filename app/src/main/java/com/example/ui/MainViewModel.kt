package com.example.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.data.DhikrRepository
import com.example.data.HadithBookInfo
import com.example.data.HadithCollections
import com.example.data.HadithItemDetail
import com.example.data.HadithRepository
import com.example.data.MomentLogEntity
import com.example.data.PrayerTimesService
import com.example.data.PrayerTimesState
import com.example.data.UserAccountEntity
import com.example.data.UserSettingsEntity
import com.example.notification.NotificationHelper
import com.example.util.AppTimeHelper
import com.example.util.LocationHelper
import com.example.util.SoundAndHaptics
import java.util.TimeZone
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class Screen(val route: String) {
  object Home : Screen("home")
  object Moment : Screen("moment")
  object Library : Screen("library")
  object HadithExplorer : Screen("hadith_explorer")
  object Insights : Screen("insights")
  object Profile : Screen("profile")
}

data class DayActivity(
  val dateKey: String,
  val dayOfMonth: Int,
  val dayOfWeek: Int,
  val count: Int,
  val isToday: Boolean
)

data class MomentTimerUiState(
  val totalSeconds: Int = 15,
  val remainingSeconds: Float = 15f,
  val isRunning: Boolean = false,
  val isCompleted: Boolean = false,
  val repeatTarget: Int = 1,
  val currentCycle: Int = 1
)

data class PointsEarnedEvent(
  val pointsAwarded: Int = 10,
  val totalScore: Int,
  val spiritualRank: String,
  val dhikrTitle: String,
  val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  private val repository = DhikrRepository(database.momentDao(), application)
  private val soundAndHaptics = SoundAndHaptics(application)

  private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  val userSettings: StateFlow<UserSettingsEntity> = repository.userSettings
    .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettingsEntity())

  val registeredAccounts: StateFlow<List<UserAccountEntity>> = repository.registeredAccounts
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val allItems: StateFlow<List<DhikrItem>> = repository.allItems
    .stateIn(viewModelScope, SharingStarted.Eagerly, DhikrCatalog.items)

  val bookmarkedItems: StateFlow<List<DhikrItem>> = allItems.map { list ->
    list.filter { it.isBookmarked }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val todayLogs: StateFlow<List<MomentLogEntity>> = repository.getTodayLogs()
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val allLogs: StateFlow<List<MomentLogEntity>> = repository.allLogs
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val recentLogs: StateFlow<List<MomentLogEntity>> = repository.getRecentLogs(10)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dynamic 2-Hour Auto-Rotating Featured Item for Landing Page
  private val _manualRotationOffset = MutableStateFlow(0)
  val rotatingFeaturedDhikr: StateFlow<DhikrItem> = combine(allItems, _manualRotationOffset) { _, offset ->
    DhikrCatalog.getTwoHourRotatedDhikr(getApplication(), offset)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DhikrCatalog.items.first())

  // Active Moment State
  private val _selectedDhikr = MutableStateFlow(DhikrCatalog.items.first())
  val selectedDhikr: StateFlow<DhikrItem> = _selectedDhikr.asStateFlow()

  private val _timerState = MutableStateFlow(MomentTimerUiState())
  val timerState: StateFlow<MomentTimerUiState> = _timerState.asStateFlow()

  private var timerJob: Job? = null

  // Search & Filters in Library
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow("All (تمام 1000+)")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  // Sacred Popup Reminder Dialog State (Single unified popup - No WhatsApp-like double banner)
  private val _showReminderPopup = MutableStateFlow(false)
  val showReminderPopup: StateFlow<Boolean> = _showReminderPopup.asStateFlow()

  private val _reminderPopupDhikr = MutableStateFlow(DhikrCatalog.items.first())
  val reminderPopupDhikr: StateFlow<DhikrItem> = _reminderPopupDhikr.asStateFlow()

  // Backward compatibility aliases
  val showFloatingBanner: StateFlow<Boolean> get() = _showReminderPopup.asStateFlow()
  val floatingBannerDhikr: StateFlow<DhikrItem> get() = _reminderPopupDhikr.asStateFlow()
  val currentStreak: StateFlow<Int> get() = streakCount
  val momentTimerState: StateFlow<MomentTimerUiState> get() = timerState
  val featuredDhikrItem: StateFlow<DhikrItem> get() = rotatingFeaturedDhikr
  val reminderInterval: StateFlow<String> = userSettings.map { it.reminderInterval }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Every 1 hour (1 گھنٹہ بعد)")

  // Points & Spiritual Score State
  val userTotalScore: StateFlow<Int> = userSettings.map { it.totalScore }
    .stateIn(viewModelScope, SharingStarted.Eagerly, 340)

  val userSpiritualRank: StateFlow<String> = userSettings.map { it.spiritualRank }
    .stateIn(viewModelScope, SharingStarted.Eagerly, "مبتدی (Seeker of Peace)")

  private val _pointsCelebration = MutableStateFlow<PointsEarnedEvent?>(null)
  val pointsCelebration: StateFlow<PointsEarnedEvent?> = _pointsCelebration.asStateFlow()

  fun dismissPointsCelebration() {
    _pointsCelebration.value = null
  }

  fun startCurrentMoment() {
    val currentItem = rotatingFeaturedDhikr.value
    selectDhikrForMoment(currentItem, startImmediately = true)
  }

  fun claimMomentPoints(dhikr: DhikrItem? = null) {
    viewModelScope.launch {
      val item = dhikr ?: _selectedDhikr.value
      timerJob?.cancel()
      _timerState.value = _timerState.value.copy(
        isRunning = false,
        isCompleted = true,
        remainingSeconds = 0f
      )
      val updated = repository.logCompletedMoment(item, 15)
      soundAndHaptics.triggerCelebrationHaptic()
      soundAndHaptics.playChime()
      _pointsCelebration.value = PointsEarnedEvent(
        pointsAwarded = 10,
        totalScore = updated.totalScore,
        spiritualRank = updated.spiritualRank,
        dhikrTitle = item.transliteration
      )
    }
  }

  // Streak & Statistics calculation
  val streakCount: StateFlow<Int> = allLogs.combine(todayLogs) { logs, _ ->
    calculateStreak(logs)
  }.stateIn(viewModelScope, SharingStarted.Eagerly, 21)

  val totalMomentsCount: StateFlow<Int> = repository.totalMomentsCount
    .stateIn(viewModelScope, SharingStarted.Eagerly, 340)

  // 35-day activity grid
  val activityGrid: StateFlow<List<DayActivity>> = allLogs.combine(todayLogs) { logs, _ ->
    calculateActivityGrid(logs)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Clock ticker updated every 30 seconds
  private val _clockTicker = MutableStateFlow(System.currentTimeMillis())

  val activeTimeZone: StateFlow<TimeZone> = userSettings.map { settings ->
    AppTimeHelper.getEffectiveTimeZone(settings.selectedTimezone)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTimeHelper.getEffectiveTimeZone("Asia/Karachi"))

  val currentTimeFormatted: StateFlow<String> = combine(activeTimeZone, _clockTicker) { tz, _ ->
    AppTimeHelper.formatCurrentTime(tz)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTimeHelper.formatCurrentTime(AppTimeHelper.getEffectiveTimeZone("Asia/Karachi")))

  val currentDateFormatted: StateFlow<String> = combine(activeTimeZone, _clockTicker) { tz, _ ->
    AppTimeHelper.formatCurrentDate(tz)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTimeHelper.formatCurrentDate(AppTimeHelper.getEffectiveTimeZone("Asia/Karachi")))

  val dynamicGreeting: StateFlow<Pair<String, String>> = combine(activeTimeZone, _clockTicker) { tz, _ ->
    AppTimeHelper.getDynamicGreeting(tz)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTimeHelper.getDynamicGreeting(AppTimeHelper.getEffectiveTimeZone("Asia/Karachi")))

  // Prayer Times State based on Public API & Geolocation
  private val _prayerTimesState = MutableStateFlow(
    PrayerTimesService.computeDefaultPrayerState("پاکستان کا معیاری وقت (PKT)", AppTimeHelper.getEffectiveTimeZone("Asia/Karachi"))
  )
  val prayerTimesState: StateFlow<PrayerTimesState> = _prayerTimesState.asStateFlow()

  // ==========================================
  // 36,000+ HADITH EXPLORER (7 AUTHENTIC BOOKS)
  // ==========================================
  private val _selectedHadithBook = MutableStateFlow<HadithBookInfo>(HadithCollections.books[0])
  val selectedHadithBook: StateFlow<HadithBookInfo> = _selectedHadithBook.asStateFlow()

  private val _currentHadithNumber = MutableStateFlow<Int>(1)
  val currentHadithNumber: StateFlow<Int> = _currentHadithNumber.asStateFlow()

  private val _hadithLoading = MutableStateFlow<Boolean>(false)
  val hadithLoading: StateFlow<Boolean> = _hadithLoading.asStateFlow()

  private val _currentHadithDetail = MutableStateFlow<HadithItemDetail?>(null)
  val currentHadithDetail: StateFlow<HadithItemDetail?> = _currentHadithDetail.asStateFlow()

  private val _hadithErrorMessage = MutableStateFlow<String?>(null)
  val hadithErrorMessage: StateFlow<String?> = _hadithErrorMessage.asStateFlow()

  fun selectHadithBook(book: HadithBookInfo) {
    _selectedHadithBook.value = book
    _currentHadithNumber.value = 1
    fetchCurrentHadith(book.id, 1)
  }

  fun fetchCurrentHadith(bookId: String = _selectedHadithBook.value.id, number: Int = _currentHadithNumber.value) {
    val maxNumber = _selectedHadithBook.value.totalHadiths
    val safeNumber = number.coerceIn(1, maxNumber)
    _currentHadithNumber.value = safeNumber
    _hadithLoading.value = true
    _hadithErrorMessage.value = null

    viewModelScope.launch {
      val result = HadithRepository.getHadith(getApplication(), bookId, safeNumber)
      _hadithLoading.value = false
      result.onSuccess { detail ->
        _currentHadithDetail.value = detail
      }.onFailure { err ->
        _hadithErrorMessage.value = err.message ?: "حدیث لوڈ نہ ہو سکی۔ انٹرنیٹ چیک کریں۔"
      }
    }
  }

  fun loadNextHadith() {
    val next = _currentHadithNumber.value + 1
    if (next <= _selectedHadithBook.value.totalHadiths) {
      fetchCurrentHadith(number = next)
    }
  }

  fun loadPreviousHadith() {
    val prev = _currentHadithNumber.value - 1
    if (prev >= 1) {
      fetchCurrentHadith(number = prev)
    }
  }

  fun loadRandomHadith() {
    val randomNum = (1.._selectedHadithBook.value.totalHadiths).random()
    fetchCurrentHadith(number = randomNum)
  }

  fun toggleHadithBookmark(hadith: HadithItemDetail) {
    viewModelScope.launch {
      HadithRepository.toggleBookmark(
        getApplication(),
        hadith.bookId,
        hadith.hadithNumber,
        hadith.isBookmarked
      )
      _currentHadithDetail.value = hadith.copy(isBookmarked = !hadith.isBookmarked)
    }
  }

  init {
    viewModelScope.launch {
      val existingLogs = repository.allLogs.first()
      if (existingLogs.isEmpty()) {
        repository.seedInitialDataIfEmpty()
      }
      repository.ensureUserSettingsInitialized()
    }

    // Preload Hadith 1 of Sahih al-Bukhari
    fetchCurrentHadith("bukhari", 1)

    // Initialize Notification System (AlarmManager + WorkManager)
    NotificationHelper.createNotificationChannel(application)
    NotificationHelper.scheduleReminder(application)

    // Initial Prayer Times Fetch (tries GPS location or fallback)
    fetchPrayerTimes(useGps = true)

    // Periodic 30-second loop: updates clock ticker, dynamic greeting, and prayer countdown
    viewModelScope.launch {
      while (true) {
        val remaining = DhikrCatalog.getRemainingTimeInTwoHourWindowMillis()
        delay(30_000L)
        _clockTicker.value = System.currentTimeMillis()
        // Refresh prayer countdown & active prayer item dynamically
        refreshPrayerCountdownOnly()
        // Trigger recomposition of rotated item if 2 hours elapsed
        if (remaining <= 30_000L) {
          _manualRotationOffset.value = _manualRotationOffset.value
        }
      }
    }
  }

  fun updateSelectedTimezone(timezoneId: String) {
    viewModelScope.launch {
      val current = userSettings.value
      val updated = current.copy(selectedTimezone = timezoneId)
      repository.updateSettings(updated)
      _clockTicker.value = System.currentTimeMillis()
      fetchPrayerTimes(useGps = false)
    }
  }

  fun fetchPrayerTimes(useGps: Boolean = true) {
    viewModelScope.launch {
      _prayerTimesState.value = _prayerTimesState.value.copy(isLoading = true)
      var lat: Double? = null
      var lng: Double? = null
      var locationName: String? = null

      if (useGps) {
        val locResult = LocationHelper.getCurrentLocation(getApplication())
        if (locResult != null) {
          lat = locResult.latitude
          lng = locResult.longitude
          locationName = locResult.cityName
        }
      }

      val tzId = userSettings.value.selectedTimezone
      val result = PrayerTimesService.fetchPrayerTimes(lat, lng, locationName, tzId)
      _prayerTimesState.value = result
    }
  }

  private fun refreshPrayerCountdownOnly() {
    val current = _prayerTimesState.value
    if (current.items.isEmpty()) return
    val fajr = current.items.find { it.id == "fajr" }?.time24 ?: "05:08"
    val sunrise = current.items.find { it.id == "sunrise" }?.time24 ?: "06:22"
    val dhuhr = current.items.find { it.id == "dhuhr" }?.time24 ?: "12:20"
    val asr = current.items.find { it.id == "asr" }?.time24 ?: "15:42"
    val maghrib = current.items.find { it.id == "maghrib" }?.time24 ?: "18:18"
    val isha = current.items.find { it.id == "isha" }?.time24 ?: "19:32"

    val tz = AppTimeHelper.getEffectiveTimeZone(userSettings.value.selectedTimezone)
    val updated = PrayerTimesService.computePrayerState(
      fajr = fajr,
      sunrise = sunrise,
      dhuhr = dhuhr,
      asr = asr,
      maghrib = maghrib,
      isha = isha,
      locationName = current.locationName,
      hijriDate = current.hijriDate,
      isGps = current.isGpsEnabled,
      timeZone = tz
    )
    _prayerTimesState.value = updated
  }

  fun navigateTo(screen: Screen) {
    _currentScreen.value = screen
  }

  fun rotateFeaturedDhikrNow() {
    _manualRotationOffset.value = _manualRotationOffset.value + 1
    soundAndHaptics.triggerHapticFeedback()
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSelectedCategory(cat: String) {
    _selectedCategory.value = cat
  }

  fun selectDhikrForMoment(item: DhikrItem, startImmediately: Boolean = true) {
    _selectedDhikr.value = item
    _timerState.value = MomentTimerUiState(
      totalSeconds = item.defaultDurationSeconds,
      remainingSeconds = item.defaultDurationSeconds.toFloat(),
      isRunning = false,
      isCompleted = false,
      repeatTarget = 1,
      currentCycle = 1
    )
    navigateTo(Screen.Moment)
    if (startImmediately) {
      startTimer()
    }
  }

  fun triggerReminderPopup(dhikr: DhikrItem? = null) {
    val targetDhikr = dhikr ?: rotatingFeaturedDhikr.value
    _reminderPopupDhikr.value = targetDhikr
    _showReminderPopup.value = true
    soundAndHaptics.playBeep()
  }

  fun dismissReminderPopup() {
    _showReminderPopup.value = false
  }

  fun triggerFloatingBanner(dhikr: DhikrItem? = null) {
    triggerReminderPopup(dhikr)
  }

  fun dismissFloatingBanner() {
    dismissReminderPopup()
  }

  fun sendTestNotificationNow(dhikr: DhikrItem? = null) {
    val target = dhikr ?: rotatingFeaturedDhikr.value
    triggerReminderPopup(target)
    NotificationHelper.showDhikrNotification(getApplication(), target)
  }

  fun toggleTimer() {
    if (_timerState.value.isRunning) {
      pauseTimer()
    } else {
      if (_timerState.value.isCompleted) {
        resetTimer()
      }
      startTimer()
    }
  }

  fun startTimer() {
    timerJob?.cancel()
    _timerState.value = _timerState.value.copy(isRunning = true)

    timerJob = viewModelScope.launch {
      val stepMs = 100L
      var tickCounter = 0
      while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
        delay(stepMs)
        val newRemaining = (_timerState.value.remainingSeconds - (stepMs / 1000f)).coerceAtLeast(0f)
        _timerState.value = _timerState.value.copy(remainingSeconds = newRemaining)

        tickCounter++
        if (tickCounter % 10 == 0 && userSettings.value.soundEnabled) {
          soundAndHaptics.playSoftTick()
        }
      }

      if (_timerState.value.remainingSeconds <= 0f) {
        onTimerComplete()
      }
    }
  }

  fun pauseTimer() {
    timerJob?.cancel()
    _timerState.value = _timerState.value.copy(isRunning = false)
  }

  fun resetTimer() {
    timerJob?.cancel()
    val total = _selectedDhikr.value.defaultDurationSeconds
    _timerState.value = MomentTimerUiState(
      totalSeconds = total,
      remainingSeconds = total.toFloat(),
      isRunning = false,
      isCompleted = false,
      repeatTarget = _timerState.value.repeatTarget,
      currentCycle = 1
    )
  }

  private fun onTimerComplete() {
    _timerState.value = _timerState.value.copy(
      isRunning = false,
      isCompleted = true,
      remainingSeconds = 0f
    )

    if (userSettings.value.hapticsEnabled) {
      soundAndHaptics.triggerCelebrationHaptic()
    }
    if (userSettings.value.soundEnabled) {
      soundAndHaptics.playChime()
    }

    viewModelScope.launch {
      val updated = repository.logCompletedMoment(_selectedDhikr.value, _timerState.value.totalSeconds)
      _pointsCelebration.value = PointsEarnedEvent(
        pointsAwarded = 10,
        totalScore = updated.totalScore,
        spiritualRank = updated.spiritualRank,
        dhikrTitle = _selectedDhikr.value.transliteration
      )
    }
  }

  fun nextDhikr() {
    val items = allItems.value
    val currentIndex = items.indexOfFirst { it.id == _selectedDhikr.value.id }
    val nextIndex = if (currentIndex >= 0 && currentIndex < items.size - 1) currentIndex + 1 else 0
    selectDhikrForMoment(items[nextIndex], startImmediately = false)
  }

  fun previousDhikr() {
    val items = allItems.value
    val currentIndex = items.indexOfFirst { it.id == _selectedDhikr.value.id }
    val prevIndex = if (currentIndex > 0) currentIndex - 1 else items.size - 1
    selectDhikrForMoment(items[prevIndex], startImmediately = false)
  }

  fun toggleBookmark(dhikrId: String) {
    viewModelScope.launch {
      val item = allItems.value.firstOrNull { it.id == dhikrId } ?: return@launch
      repository.toggleBookmark(dhikrId, item.isBookmarked)
    }
  }

  fun updateAccountProfile(identifier: String, name: String, type: String) {
    viewModelScope.launch {
      repository.registerOrSwitchUser(identifier.trim(), name.trim(), type)
    }
  }

  fun signOutAccount() {
    viewModelScope.launch {
      val current = userSettings.value
      repository.updateSettings(
        current.copy(
          userName = "مہمان کاربر (Guest Seeker)",
          userEmail = "guest@15secondsforallah.com",
          userPhone = "",
          isSignedIn = false,
          authProvider = "Guest"
        )
      )
    }
  }

  fun updateDailyGoal(goal: Int) {
    viewModelScope.launch {
      val current = userSettings.value
      repository.updateSettings(current.copy(dailyGoal = goal))
    }
  }

  fun updateReminderInterval(interval: String) {
    viewModelScope.launch {
      val current = userSettings.value
      repository.updateSettings(current.copy(reminderInterval = interval))

      val minutes = when {
        interval.contains("15") -> 15L
        interval.contains("30") -> 30L
        interval.contains("2") -> 120L
        else -> 60L
      }
      NotificationHelper.scheduleReminder(getApplication(), minutes)
    }
  }

  fun toggleHaptics(enabled: Boolean) {
    viewModelScope.launch {
      val current = userSettings.value
      repository.updateSettings(current.copy(hapticsEnabled = enabled))
      if (enabled) soundAndHaptics.triggerHapticFeedback()
    }
  }

  fun toggleSound(enabled: Boolean) {
    viewModelScope.launch {
      val current = userSettings.value
      repository.updateSettings(current.copy(soundEnabled = enabled))
      if (enabled) soundAndHaptics.playSoftTick()
    }
  }

  fun setDarkModePreference(isDark: Boolean?) {
    viewModelScope.launch {
      val current = userSettings.value
      repository.updateSettings(current.copy(isDarkMode = isDark))
    }
  }

  fun clearAllHistory() {
    viewModelScope.launch {
      repository.clearHistory()
    }
  }

  fun handleIntent(intent: Intent?) {
    val target = intent?.getStringExtra("TARGET_SCREEN")
    val dhikrId = intent?.getStringExtra("DHIKR_ID")
    if (target == "MOMENT" && dhikrId != null) {
      val item = allItems.value.firstOrNull { it.id == dhikrId } ?: rotatingFeaturedDhikr.value
      selectDhikrForMoment(item, startImmediately = true)
    }
  }

  private fun calculateStreak(logs: List<MomentLogEntity>): Int {
    if (logs.isEmpty()) return 0
    val tz = AppTimeHelper.getEffectiveTimeZone(userSettings.value.selectedTimezone)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
      timeZone = tz
    }
    val datesSet = logs.map { it.dateKey }.toSet()

    val cal = Calendar.getInstance(tz)
    var streak = 0
    val todayKey = dateFormat.format(cal.time)

    if (!datesSet.contains(todayKey)) {
      cal.add(Calendar.DAY_OF_YEAR, -1)
      val yesterdayKey = dateFormat.format(cal.time)
      if (!datesSet.contains(yesterdayKey)) {
        return 0
      }
    }

    while (true) {
      val key = dateFormat.format(cal.time)
      if (datesSet.contains(key)) {
        streak++
        cal.add(Calendar.DAY_OF_YEAR, -1)
      } else {
        break
      }
    }
    return streak.coerceAtLeast(1)
  }

  private fun calculateActivityGrid(logs: List<MomentLogEntity>): List<DayActivity> {
    val tz = AppTimeHelper.getEffectiveTimeZone(userSettings.value.selectedTimezone)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
      timeZone = tz
    }
    val countsByDate = logs.groupingBy { it.dateKey }.eachCount()

    val cal = Calendar.getInstance(tz)
    val todayKey = dateFormat.format(cal.time)

    cal.add(Calendar.DAY_OF_YEAR, -34)

    val grid = mutableListOf<DayActivity>()
    for (i in 0 until 35) {
      val key = dateFormat.format(cal.time)
      val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
      val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
      val count = countsByDate[key] ?: 0
      grid.add(
        DayActivity(
          dateKey = key,
          dayOfMonth = dayOfMonth,
          dayOfWeek = dayOfWeek,
          count = count,
          isToday = (key == todayKey)
        )
      )
      cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    return grid
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
  }
}
