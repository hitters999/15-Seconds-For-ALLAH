package com.example.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.MainActivity
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.ui.components.FloatingReminderBanner
import com.example.ui.theme.MyApplicationTheme
import com.example.util.ShareHelper
import kotlinx.coroutines.delay

/**
 * Transparent standalone Activity that displays the sacred Hero Popup Notification Window
 * over the user's screen whenever an hourly or periodic reminder triggers.
 * Ensures the popup window is always shown without requiring a separate status-bar tray notification.
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
        // Auto-dismiss smoothly after 5.5 seconds
        LaunchedEffect(Unit) {
          delay(5500L)
          finish()
        }

        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
              onClick = { finish() }
            ),
          contentAlignment = Alignment.TopCenter
        ) {
          Box(
            modifier = Modifier
              .statusBarsPadding()
              .padding(top = 20.dp)
          ) {
            FloatingReminderBanner(
              dhikr = dhikr,
              countdownSeconds = 5,
              onDismiss = { finish() },
              onBegin = {
                val mainIntent = Intent(this@PopupNotificationActivity, MainActivity::class.java).apply {
                  flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                  putExtra("TARGET_SCREEN", "MOMENT")
                  putExtra("DHIKR_ID", dhikr.id)
                }
                startActivity(mainIntent)
                finish()
              },
              onShare = {
                ShareHelper.shareDhikrPoster(this@PopupNotificationActivity, dhikr)
              },
              onWhatsAppShare = {
                ShareHelper.shareToWhatsApp(this@PopupNotificationActivity, dhikr)
              }
            )
          }
        }
      }
    }
  }
}
