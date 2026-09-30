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
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.util.ShareHelper
import com.example.util.SoundAndHaptics
import java.util.concurrent.TimeUnit

object NotificationHelper {

  const val CHANNEL_ID = "channel_dhikr_reminders_hourly"
  const val NOTIFICATION_ID = 1001
  const val ALARM_REQUEST_CODE = 2001
  const val ACTION_SHARE_NOTIFICATION = "com.example.ACTION_SHARE_NOTIFICATION"

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
      val name = "15 Seconds for Allah • یاد دہانی"
      val descriptionText = "ہر گھنٹے بعد قرآن و سنت کے اذکار کی یاد دہانی اور بیپ"
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

  fun showDhikrNotification(context: Context, dhikr: DhikrItem) {
    createNotificationChannel(context)

    // Play requested "Beep" sound
    SoundAndHaptics(context).playBeep()

    // Strictly show ONLY the sacred Hero Popup Notification window (no duplicate WhatsApp-like banner or drawer line)
    PopupNotificationActivity.start(context, dhikr)
  }

  // Reliable Dual Scheduling: Uses AlarmManager for exact time + WorkManager as fail-safe backup!
  fun scheduleReminder(context: Context, intervalMinutes: Long = 60) {
    createNotificationChannel(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
      action = "com.example.ACTION_DHIKR_HOURLY_REMINDER"
      putExtra("INTERVAL_MINUTES", intervalMinutes)
    }

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      ALARM_REQUEST_CODE,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerAtMillis = System.currentTimeMillis() + (intervalMinutes * 60 * 1000L)

    try {
      alarmManager?.let { am ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
          am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
      }
    } catch (_: Exception) {
      // Fallback if exact alarm not permitted
      alarmManager?.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    // WorkManager Periodic Backup (ensures notifications survive deep sleep/doze)
    try {
      val periodicWork = PeriodicWorkRequestBuilder<ReminderWorker>(
        intervalMinutes.coerceAtLeast(15), TimeUnit.MINUTES
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
