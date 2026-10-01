package com.example.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.MainActivity
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.ui.components.HeroPopupNotificationCard
import com.example.ui.theme.MyApplicationTheme

/**
 * Transparent standalone Activity that displays the wide top-floating Hero Popup Notification.
 * Tailored to user's specification:
 * - NOT a full-screen blocking modal!
 * - Floats gracefully at the top of the screen ("WhatsApp notification se thora zyda choora, bara")
 * - Completely transparent background, tap outside to dismiss immediately.
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

    fun getPendingIntent(context: Context, dhikr: DhikrItem): PendingIntent {
      val intent = Intent(context, PopupNotificationActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
          Intent.FLAG_ACTIVITY_CLEAR_TOP or
          Intent.FLAG_ACTIVITY_SINGLE_TOP
        putExtra(EXTRA_DHIKR_ID, dhikr.id)
      }
      return PendingIntent.getActivity(
        context,
        dhikr.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Position window at top without dim scrim
    window.setGravity(Gravity.TOP)
    window.setDimAmount(0f)
    window.addFlags(
      WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    )

    val dhikrId = intent.getStringExtra(EXTRA_DHIKR_ID)
    val dhikr = DhikrCatalog.loadFullCatalog(this).find { it.id == dhikrId }
      ?: DhikrCatalog.getTwoHourRotatedDhikr(this)

    setContent {
      MyApplicationTheme {
        // Full screen transparent clickable backdrop: taps outside dismiss the popup
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) {
              finish()
            },
          contentAlignment = Alignment.TopCenter
        ) {
          // Inner card container that does NOT bubble clicks to the dismissal backdrop
          Box(
            modifier = Modifier
              .padding(top = 8.dp)
              .clickable(enabled = false) {}
          ) {
            HeroPopupNotificationCard(
              dhikr = dhikr,
              countdownSeconds = NotificationHelper.getPopupDurationSeconds(this@PopupNotificationActivity),
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
}
