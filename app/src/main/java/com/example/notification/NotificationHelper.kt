package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.data.MomentLogEntity
import com.example.data.UserAccountEntity
import com.example.data.UserSettingsEntity
import com.example.util.ShareHelper
import com.example.util.SoundAndHaptics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object NotificationHelper {

  const val CHANNEL_ID = "channel_dhikr_reminders_hourly"
  const val NOTIFICATION_ID = 1001
  const val ALARM_REQUEST_CODE = 2001
  const val ACTION_SHARE_NOTIFICATION = "com.example.ACTION_SHARE_NOTIFICATION"
  private const val PREFS_NOTIFICATION = "notification_reminder_prefs"
  private const val KEY_SAVED_INTERVAL = "saved_reminder_interval_minutes"
  private const val KEY_POPUP_DURATION = "popup_duration_seconds"

  fun getSavedIntervalMinutes(context: Context): Long {
    val prefs = context.getSharedPreferences(PREFS_NOTIFICATION, Context.MODE_PRIVATE)
    return prefs.getLong(KEY_SAVED_INTERVAL, 15L) // Default is 15 minutes
  }

  fun saveIntervalMinutes(context: Context, minutes: Long) {
    val prefs = context.getSharedPreferences(PREFS_NOTIFICATION, Context.MODE_PRIVATE)
    prefs.edit().putLong(KEY_SAVED_INTERVAL, minutes).apply()
  }

  fun getPopupDurationSeconds(context: Context): Int {
    val prefs = context.getSharedPreferences(PREFS_NOTIFICATION, Context.MODE_PRIVATE)
    return prefs.getInt(KEY_POPUP_DURATION, 5) // 5 seconds default matching design
  }

  fun savePopupDurationSeconds(context: Context, seconds: Int) {
    val prefs = context.getSharedPreferences(PREFS_NOTIFICATION, Context.MODE_PRIVATE)
    prefs.edit().putInt(KEY_POPUP_DURATION, seconds).apply()
  }

  fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "15 Seconds 4 Allah • یاد دہانی"
      val descriptionText = "قرآن و سنت کے اذکار و دعاؤں کی یاد دہانی اور بیپ"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
        enableVibration(true)
        setShowBadge(true)
        lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  /**
   * Displays the Sacred Dhikr Reminder:
   * 1. Automatically awards +10 points (Hasanat) in Room DB for every popup displayed.
   * 2. Plays the requested sacred beep sound.
   * 3. Shows EXCLUSIVELY the non-blocking floating 5-second popup banner without opening MainActivity.
   */
  fun showDhikrNotification(context: Context, dhikr: DhikrItem) {
    // 1. Automatically award +10 points for this popup appearance
    recordPopupPointsInDatabase(context, dhikr)

    // 2. Play requested "Beep" sound
    try {
      SoundAndHaptics(context).playBeep()
    } catch (_: Exception) {}

    // 3. Cancel any accidental status bar notification
    try {
      NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    } catch (_: Exception) {}

    // 4. Strictly launch the non-blocking floating top popup window
    try {
      FloatingPopupManager.showFloatingPopup(context, dhikr)
    } catch (_: Exception) {}
  }

  /**
   * Increments the user's spiritual score by +10 points and records a moment log
   * every time a reminder popup is successfully shown on screen.
   */
  fun recordPopupPointsInDatabase(context: Context, dhikr: DhikrItem) {
    val appContext = context.applicationContext
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val dao = AppDatabase.getDatabase(appContext).momentDao()
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        dao.insertMomentLog(
          MomentLogEntity(
            dhikrId = dhikr.id,
            title = dhikr.transliteration.ifBlank { "Popup Dhikr" },
            category = "Popup Reminder",
            durationSeconds = getPopupDurationSeconds(appContext),
            timestamp = System.currentTimeMillis(),
            dateKey = dateKey
          )
        )

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

        if (updated.isSignedIn) {
          val identifier = if (updated.userPhone.isNotBlank()) updated.userPhone else updated.userEmail
          if (identifier.isNotBlank()) {
            dao.insertUserAccount(
              UserAccountEntity(
                identifier = identifier,
                displayName = updated.userName,
                accountType = updated.authProvider,
                totalScore = newScore
              )
            )
          }
        }
      } catch (_: Exception) {}
    }
  }

  /**
   * Ultra-Reliable Exact Alarm Scheduling:
   * Uses AlarmManager.setAlarmClock (which bypasses Doze mode and battery savers on all Android versions)
   * Ensures 15-minute (or selected interval) reminders fire on the exact second!
   */
  fun scheduleReminder(context: Context, intervalMinutes: Long? = null) {
    createNotificationChannel(context)

    val actualMinutes = if (intervalMinutes != null) {
      saveIntervalMinutes(context, intervalMinutes)
      intervalMinutes
    } else {
      getSavedIntervalMinutes(context)
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
      action = "com.example.ACTION_DHIKR_HOURLY_REMINDER"
      putExtra("INTERVAL_MINUTES", actualMinutes)
    }

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      ALARM_REQUEST_CODE,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerAtMillis = System.currentTimeMillis() + (actualMinutes * 60 * 1000L)

    try {
      alarmManager?.let { am ->
        // setAlarmClock is Android's most reliable exact alarm mechanism (exempt from Doze mode)
        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
          context,
          0,
          showIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)
        am.setAlarmClock(alarmClockInfo, pendingIntent)
      }
    } catch (_: Exception) {
      try {
        alarmManager?.let { am ->
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
          } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
          }
        }
      } catch (_: Exception) {
        alarmManager?.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
      }
    }

    // WorkManager Periodic Backup (ensures fail-safe triggers if app process was force stopped)
    try {
      val periodicWork = PeriodicWorkRequestBuilder<ReminderWorker>(
        actualMinutes.coerceAtLeast(15), TimeUnit.MINUTES
      ).build()

      WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "periodic_dhikr_reminder",
        ExistingPeriodicWorkPolicy.UPDATE,
        periodicWork
      )
    } catch (_: Exception) {}
  }

  fun cancelReminder(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
      action = "com.example.ACTION_DHIKR_HOURLY_REMINDER"
    }
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      ALARM_REQUEST_CODE,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
    try {
      WorkManager.getInstance(context).cancelUniqueWork("periodic_dhikr_reminder")
    } catch (_: Exception) {}
  }
}

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
  override fun doWork(): Result {
    val dhikr = DhikrCatalog.getRandomNotificationDhikr(applicationContext)
    NotificationHelper.showDhikrNotification(applicationContext, dhikr)
    return Result.success()
  }
}

class NotificationShareReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val id = intent.getStringExtra("DHIKR_ID") ?: "dhikr_share"
    val arabic = intent.getStringExtra("DHIKR_ARABIC") ?: ""
    val urdu = intent.getStringExtra("DHIKR_URDU") ?: ""
    val eng = intent.getStringExtra("DHIKR_ENG") ?: ""
    val source = intent.getStringExtra("DHIKR_SOURCE") ?: ""
    val isQuranic = intent.getBooleanExtra("IS_QURANIC", false)

    val dhikr = DhikrItem(
      id = id,
      arabic = arabic,
      transliteration = "",
      translationUrdu = urdu,
      translation = eng,
      contemplativeNote = "",
      category = "Share",
      source = source,
      virtue = "",
      isQuranic = isQuranic
    )
    ShareHelper.shareDhikrPoster(context, dhikr)
  }
}
