package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.example.data.DhikrCatalog

class ReminderBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    // Acquire a short WakeLock so device stays awake to sound beep and show popup
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    val wakeLock = powerManager?.newWakeLock(
      PowerManager.PARTIAL_WAKE_LOCK,
      "MyApp:ReminderWakeLock"
    )
    wakeLock?.acquire(5000L)

    try {
      // Pick the next non-repeating Dhikr from the 1000+ pool
      // Guarantees zero repetitions until all 1000+ items have cycled!
      val dhikr = DhikrCatalog.getNextNonRepeatingDhikr(context)

      // Trigger Beep sound, wide top popup window, and high-priority heads-up banner
      NotificationHelper.showDhikrNotification(context, dhikr)

      // Re-schedule for next interval (honors user's saved 15m/30m/60m setting)
      NotificationHelper.scheduleReminder(context)
    } finally {
      if (wakeLock?.isHeld == true) {
        try { wakeLock.release() } catch (_: Exception) {}
      }
    }
  }
}
