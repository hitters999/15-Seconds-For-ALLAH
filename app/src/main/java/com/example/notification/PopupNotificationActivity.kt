package com.example.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.MainActivity
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.ui.components.HeroPopupNotificationCard
import com.example.ui.theme.MyApplicationTheme

/**
 * Non-blocking Top Strip Activity fallback (with taskAffinity="" and singleInstance).
 * Never opens MainActivity, never occupies the full screen, and passes all outside
 * touches directly to the underlying application (FLAG_NOT_TOUCH_MODAL | FLAG_NOT_FOCUSABLE).
 */
class PopupNotificationActivity : ComponentActivity() {

  companion object {
    private const val EXTRA_DHIKR_ID = "EXTRA_DHIKR_ID"

    fun start(context: Context, dhikr: DhikrItem) {
      val intent = Intent(context, PopupNotificationActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
          Intent.FLAG_ACTIVITY_NO_ANIMATION or
          Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        putExtra(EXTRA_DHIKR_ID, dhikr.id)
      }
      context.startActivity(intent)
    }

    fun getPendingIntent(context: Context, dhikr: DhikrItem): PendingIntent {
      val intent = Intent(context, PopupNotificationActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
          Intent.FLAG_ACTIVITY_NO_ANIMATION or
          Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
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
    @Suppress("DEPRECATION")
    overridePendingTransition(0, 0)

    // Configure strictly as a top-only non-modal strip so ongoing user work never stops
    window.setLayout(
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.WRAP_CONTENT
    )
    window.setGravity(Gravity.TOP)
    window.setDimAmount(0f)
    window.addFlags(
      WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    )

    val dhikrId = intent.getStringExtra(EXTRA_DHIKR_ID)
    val dhikr = DhikrCatalog.loadFullCatalog(this).find { it.id == dhikrId }
      ?: DhikrCatalog.getTwoHourRotatedDhikr(this)

    setContent {
      MyApplicationTheme {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
          contentAlignment = Alignment.TopCenter
        ) {
          HeroPopupNotificationCard(
            dhikr = dhikr,
            countdownSeconds = NotificationHelper.getPopupDurationSeconds(this@PopupNotificationActivity),
            onStartMoment = {
              finish()
              @Suppress("DEPRECATION")
              overridePendingTransition(0, 0)
            },
            onDismiss = {
              finish()
              @Suppress("DEPRECATION")
              overridePendingTransition(0, 0)
            }
          )
        }
      }
    }
  }

  override fun finish() {
    super.finish()
    @Suppress("DEPRECATION")
    overridePendingTransition(0, 0)
  }
}
