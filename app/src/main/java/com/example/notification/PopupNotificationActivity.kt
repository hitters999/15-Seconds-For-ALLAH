package com.example.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.MainActivity
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.ui.components.DhikrReminderPopupDialog
import com.example.ui.theme.MyApplicationTheme

/**
 * Transparent standalone Activity that displays the sacred Hero Popup Notification Window
 * over the user's screen whenever an hourly or periodic reminder triggers.
 * Displays exclusively the popup dialog window without any WhatsApp-like top banner or double notification.
 */
class PopupNotificationActivity : ComponentActivity() {

  companion object {
    private const val EXTRA_DHIKR_ID = "EXTRA_DHIKR_ID"

    fun start(context: Context, dhikr: DhikrItem) {
      val intent = Intent(context, PopupNotificationActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
          Intent.FLAG_ACTIVITY_CLEAR_TOP or
          Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra(EXTRA_DHIKR_ID, dhikr.id)
      }
      context.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Turn screen on and show over lockscreen if device is asleep
    window.addFlags(
      WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    )

    val dhikrId = intent.getStringExtra(EXTRA_DHIKR_ID)
    val dhikr = DhikrCatalog.items.find { it.id == dhikrId }
      ?: DhikrCatalog.getTwoHourRotatedDhikr(this)

    setContent {
      MyApplicationTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
        ) {
          DhikrReminderPopupDialog(
            dhikr = dhikr,
            onStartMoment = {
              val mainIntent = Intent(this@PopupNotificationActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("TARGET_SCREEN", "MOMENT")
                putExtra("DHIKR_ID", dhikr.id)
              }
              startActivity(mainIntent)
              finish()
            },
            onDismiss = {
              finish()
            }
          )
        }
      }
    }
  }
}
