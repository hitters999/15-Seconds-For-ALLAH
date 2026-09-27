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
import com.example.util.SoundAndHaptics

object FloatingPopupManager {

  private var activeComposeView: ComposeView? = null
  private val handler = Handler(Looper.getMainLooper())

  fun canDrawOverlays(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Settings.canDrawOverlays(context)
    } else {
      true
    }
  }

  fun requestOverlayPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
      ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      try {
        context.startActivity(intent)
      } catch (_: Exception) {
      }
    }
  }

  fun showFloatingPopup(context: Context, dhikr: DhikrItem) {
    handler.post {
      // Play the requested "Beep" sound
      try {
        val soundAndHaptics = SoundAndHaptics(context)
        soundAndHaptics.playBeep()
      } catch (_: Exception) {
      }

      if (!canDrawOverlays(context)) {
        // Fallback to high priority heads up system notification
        NotificationHelper.showDhikrNotification(context, dhikr)
        return@post
      }

      val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return@post
      dismissFloatingPopup(context)

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
          WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
          WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
      ).apply {
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        y = 50
      }

      class WindowLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)
        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        init {
          savedStateRegistryController.performRestore(null)
          lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
          lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
          lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        fun destroy() {
          lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
          lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
          lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }

        override val lifecycle: Lifecycle get() = lifecycleRegistry
        override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
      }

      val windowOwner = WindowLifecycleOwner()

      val composeView = ComposeView(context).apply {
        setViewTreeLifecycleOwner(windowOwner)
        setViewTreeSavedStateRegistryOwner(windowOwner)
        setContent {
          FloatingReminderBanner(
            dhikr = dhikr,
            durationSeconds = 5,
            onDismiss = {
              dismissFloatingPopup(context)
            },
            onStartMoment = {
              dismissFloatingPopup(context)
              val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("TARGET_SCREEN", "MOMENT")
                putExtra("DHIKR_ID", dhikr.id)
              }
              context.startActivity(intent)
            }
          )
        }
      }

      try {
        windowManager.addView(composeView, params)
        activeComposeView = composeView

        // Auto dismiss safely after 5.5 seconds
        handler.postDelayed({
          dismissFloatingPopup(context)
          windowOwner.destroy()
        }, 5500L)
      } catch (_: Exception) {
        // If window manager failed, show heads up notification
        NotificationHelper.showDhikrNotification(context, dhikr)
      }
    }
  }

  fun dismissFloatingPopup(context: Context) {
    activeComposeView?.let { view ->
      try {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        windowManager?.removeViewImmediate(view)
      } catch (_: Exception) {
      }
      activeComposeView = null
    }
  }
}
