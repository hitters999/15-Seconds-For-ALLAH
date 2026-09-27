package com.example.notification

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.data.DhikrItem
import com.example.ui.components.FloatingReminderBanner
import com.example.ui.theme.MyApplicationTheme
import com.example.util.ShareHelper
import com.example.util.SoundAndHaptics

object FloatingPopupManager {

  private var activeOverlayView: ComposeView? = null
  private var windowManager: WindowManager? = null
  private val mainHandler = Handler(Looper.getMainLooper())
  private var dismissRunnable: Runnable? = null

  fun canDrawOverlays(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Settings.canDrawOverlays(context)
    } else {
      true
    }
  }

  fun requestOverlayPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
      val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
      ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
    }
  }

  fun showFloatingPopup(context: Context, dhikr: DhikrItem) {
    if (!canDrawOverlays(context)) return

    mainHandler.post {
      dismissExistingOverlay()

      try {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
          @Suppress("DEPRECATION")
          WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
          WindowManager.LayoutParams.MATCH_PARENT,
          WindowManager.LayoutParams.WRAP_CONTENT,
          layoutType,
          WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
          PixelFormat.TRANSLUCENT
        ).apply {
          gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
          y = 50
        }

        // Play gentle beep
        SoundAndHaptics(context).playBeep()

        val lifecycleOwner = OverlayLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val composeView = ComposeView(context).apply {
          setViewTreeLifecycleOwner(lifecycleOwner)
          setViewTreeSavedStateRegistryOwner(lifecycleOwner)
          setContent {
            MyApplicationTheme {
              FloatingReminderBanner(
                dhikr = dhikr,
                countdownSeconds = 5,
                onDismiss = { dismissExistingOverlay() },
                onBegin = {
                  dismissExistingOverlay()
                  val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("TARGET_SCREEN", "MOMENT")
                    putExtra("DHIKR_ID", dhikr.id)
                  }
                  context.startActivity(intent)
                },
                onShare = {
                  ShareHelper.shareDhikrPoster(context, dhikr)
                },
                onWhatsAppShare = {
                  ShareHelper.shareToWhatsApp(context, dhikr)
                }
              )
            }
          }
        }

        windowManager?.addView(composeView, params)
        activeOverlayView = composeView

        // Auto dismiss after 5.5 seconds smoothly
        dismissRunnable = Runnable {
          dismissExistingOverlay()
        }
        mainHandler.postDelayed(dismissRunnable!!, 5500)

      } catch (_: Exception) {}
    }
  }

  fun dismissExistingOverlay() {
    dismissRunnable?.let { mainHandler.removeCallbacks(it) }
    dismissRunnable = null
    activeOverlayView?.let { view ->
      try {
        windowManager?.removeView(view)
      } catch (_: Exception) {}
    }
    activeOverlayView = null
  }

  private class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    fun handleLifecycleEvent(event: Lifecycle.Event) = lifecycleRegistry.handleLifecycleEvent(event)
    fun performRestore(savedState: android.os.Bundle?) = savedStateRegistryController.performRestore(savedState)
  }
}
