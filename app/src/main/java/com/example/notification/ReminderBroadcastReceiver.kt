package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.DhikrCatalog

class ReminderBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val intervalMinutes = intent.getLongExtra("INTERVAL_MINUTES", 60L)

    // Select specifically from the curated essential Juz 30 lessons:
    // Jannat, Jahannam ka darr, Allah ka hukam, Nabi ﷺ ki muhabbat
    val dhikr = DhikrCatalog.getRandomNotificationDhikr(context)

    // Show system notification & floating overlay with Beep sound
    NotificationHelper.showDhikrNotification(context, dhikr)

    // Reschedule for next interval (1 hour)
    NotificationHelper.scheduleReminder(context, intervalMinutes)
  }
}
