package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.DhikrCatalog

class ReminderBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    // Pick the sacred ayah according to 2-hour rotation or curated lessons
    val dhikr = DhikrCatalog.getTwoHourRotatedDhikr(context)

    // Trigger Notification & Floating popup if allowed
    NotificationHelper.showDhikrNotification(context, dhikr)

    // Re-schedule for next period
    val intervalMinutes = intent.getLongExtra("INTERVAL_MINUTES", 60L)
    NotificationHelper.scheduleReminder(context, intervalMinutes)
  }
}
