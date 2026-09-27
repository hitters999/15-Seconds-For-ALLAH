package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.util.ShareHelper
import com.example.util.SoundAndHaptics

object NotificationHelper {

  const val CHANNEL_ID = "fifteen_seconds_hourly_reminder"
  private const val CHANNEL_NAME = "15 Seconds for Allah • یاد دہانی"
  private const val NOTIFICATION_ID = 1001
  private const val ALARM_REQUEST_CODE = 2001
  const val ACTION_SHARE_NOTIFICATION = "com.example.ACTION_SHARE_NOTIFICATION"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Hourly reminders for mindful dhikr and contemplation"
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 200, 100, 200)
        lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
      }
      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      manager.createNotificationChannel(channel)
    }
  }

  fun showDhikrNotification(context: Context, dhikr: DhikrItem) {
    createNotificationChannel(context)

    // Play requested "Beep" sound
    try {
      SoundAndHaptics(context).playBeep()
    } catch (_: Exception) {
    }

    // If overlay permission is enabled, pop up the 5s floating window directly!
    if (FloatingPopupManager.canDrawOverlays(context)) {
      FloatingPopupManager.showFloatingPopup(context, dhikr)
    }

    // Intent to open app directly on Moment screen with this dhikr
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra("TARGET_SCREEN", "MOMENT")
      putExtra("DHIKR_ID", dhikr.id)
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      dhikr.id.hashCode(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Share action Intent for WhatsApp, Facebook, X
    val shareIntent = Intent(context, NotificationShareReceiver::class.java).apply {
      action = ACTION_SHARE_NOTIFICATION
      putExtra("DHIKR_ID", dhikr.id)
      putExtra("DHIKR_ARABIC", dhikr.arabic)
      putExtra("DHIKR_URDU", dhikr.translationUrdu)
      putExtra("DHIKR_ENG", dhikr.translation)
      putExtra("DHIKR_SOURCE", dhikr.source)
      putExtra("IS_QURANIC", dhikr.isQuranic)
    }

    val sharePendingIntent = PendingIntent.getBroadcast(
      context,
      dhikr.id.hashCode() + 10,
      shareIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Large icon using the official brand logo
    val largeLogoBitmap = try {
      BitmapFactory.decodeResource(context.resources, R.drawable.app_brand_logo)
    } catch (_: Exception) {
      null
    }

    val bigText = buildString {
      append(dhikr.arabic)
      append("\n\n")
      append("اردو: ")
      append(dhikr.translationUrdu)
      append("\n\n")
      append("English: ")
      append(dhikr.translation)
      append("\n\n")
      append(dhikr.source)
    }

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.app_brand_logo)
      .setLargeIcon(largeLogoBitmap)
      .setContentTitle("15 Seconds for Allah • یاد دہانی")
      .setContentText("${dhikr.arabic} — ${dhikr.translationUrdu}")
      .setStyle(
        NotificationCompat.BigTextStyle()
          .bigText(bigText)
          .setSummaryText("15 سیکنڈ اللہ کے لیے")
      )
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setCategory(NotificationCompat.CATEGORY_REMINDER)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .addAction(
        R.drawable.app_brand_logo,
        "ذکر شروع کریں (Begin)",
        pendingIntent
      )
      .addAction(
        android.R.drawable.ic_menu_share,
        "شیئر کریں (Share WhatsApp/X)",
        sharePendingIntent
      )
      .build()

    try {
      val notificationManager = NotificationManagerCompat.from(context)
      notificationManager.notify(NOTIFICATION_ID, notification)
    } catch (_: SecurityException) {
      // Permission not granted yet
    }
  }

  fun scheduleReminder(context: Context, intervalMinutes: Long = 60) {
    createNotificationChannel(context)
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

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
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          triggerAtMillis,
          pendingIntent
        )
      } else {
        alarmManager.setExact(
          AlarmManager.RTC_WAKEUP,
          triggerAtMillis,
          pendingIntent
        )
      }
    } catch (_: Exception) {
      alarmManager.set(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
      )
    }
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
    ShareHelper.shareDhikr(context, dhikr)
  }
}
