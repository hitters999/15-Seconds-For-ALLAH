package com.example.notification

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.MainActivity
import com.example.R
import com.example.data.DhikrItem
import com.example.util.ShareHelper

object FloatingPopupManager {

  private var currentOverlayView: View? = null
  private var currentAnimator: ValueAnimator? = null
  private val mainHandler = Handler(Looper.getMainLooper())
  private var autoDismissRunnable: Runnable? = null

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

  /**
   * Displays a strictly non-blocking 5-second top floating banner:
   * - Never opens MainActivity or interrupts the user's current app/keyboard/video.
   * - Uses WindowManager TYPE_APPLICATION_OVERLAY with FLAG_NOT_FOCUSABLE & FLAG_NOT_TOUCH_MODAL
   *   when overlay permission is granted.
   * - Falls back to isolated singleInstance top-strip PopupNotificationActivity (taskAffinity="")
   *   if overlay permission is not yet granted.
   */
  fun showFloatingPopup(context: Context, dhikr: DhikrItem) {
    val appContext = context.applicationContext
    if (canDrawOverlays(appContext)) {
      mainHandler.post {
        try {
          showSystemWindowOverlay(appContext, dhikr)
        } catch (_: Exception) {
          PopupNotificationActivity.start(appContext, dhikr)
        }
      }
    } else {
      PopupNotificationActivity.start(appContext, dhikr)
    }
  }

  private fun showSystemWindowOverlay(context: Context, dhikr: DhikrItem) {
    dismissExistingOverlay(context)

    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return
    val durationSec = NotificationHelper.getPopupDurationSeconds(context).coerceIn(3, 15)

    val outerContainer = FrameLayout(context).apply {
      setPadding(dp(context, 12), dp(context, 8), dp(context, 12), dp(context, 8))
    }

    val cardBg = GradientDrawable(
      GradientDrawable.Orientation.TOP_BOTTOM,
      intArrayOf(
        Color.parseColor("#09292F"),
        Color.parseColor("#051B20"),
        Color.parseColor("#021014")
      )
    ).apply {
      cornerRadius = dp(context, 24).toFloat()
      setStroke(dp(context, 2), Color.parseColor("#4FD1C5"))
    }

    val cardLayout = LinearLayout(context).apply {
      orientation = LinearLayout.VERTICAL
      background = cardBg
      setPadding(dp(context, 16), dp(context, 13), dp(context, 16), dp(context, 13))
      elevation = dp(context, 14).toFloat()
    }

    // 1. Top Row: App Logo + Title + "+10 حسنات ✨" Badge
    val topRow = LinearLayout(context).apply {
      orientation = LinearLayout.HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
    }

    val logoView = ImageView(context).apply {
      setImageResource(R.drawable.app_brand_logo)
      scaleType = ImageView.ScaleType.FIT_CENTER
      val bg = GradientDrawable().apply {
        setColor(Color.parseColor("#0D3337"))
        cornerRadius = dp(context, 9).toFloat()
        setStroke(dp(context, 1), Color.parseColor("#319795"))
      }
      background = bg
      setPadding(dp(context, 4), dp(context, 4), dp(context, 4), dp(context, 4))
    }
    topRow.addView(logoView, LinearLayout.LayoutParams(dp(context, 34), dp(context, 34)))

    val titleCol = LinearLayout(context).apply {
      orientation = LinearLayout.VERTICAL
      setPadding(dp(context, 10), 0, dp(context, 8), 0)
    }
    val appTitle = TextView(context).apply {
      text = "15 Seconds for Allah"
      setTextColor(Color.WHITE)
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    val appSub = TextView(context).apply {
      text = "✨ ثواب بھی ، Rewards بھی • 1 Pt = 1 Paisa"
      setTextColor(Color.parseColor("#FDE68A"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
      typeface = getUrduTypeface(context)
    }
    titleCol.addView(appTitle)
    titleCol.addView(appSub)
    topRow.addView(titleCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

    // +10 Points / 10 Paisa Hadya Earned Pill Badge
    val pointsBadge = TextView(context).apply {
      text = "+10 Pts (10 پیسے) 🪙"
      setTextColor(Color.parseColor("#041F1A"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f)
      typeface = getUrduTypeface(context)
      setPadding(dp(context, 10), dp(context, 3), dp(context, 10), dp(context, 3))
      background = GradientDrawable().apply {
        setColor(Color.parseColor("#E5C07B"))
        cornerRadius = dp(context, 12).toFloat()
      }
    }
    topRow.addView(pointsBadge, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))

    cardLayout.addView(topRow, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

    // 2. Middle Row: Crescent Icon + Arabic & Urdu/English Text
    val middleRow = LinearLayout(context).apply {
      orientation = LinearLayout.HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
      setPadding(0, dp(context, 10), 0, dp(context, 10))
    }

    val crescentView = ImageView(context).apply {
      setImageResource(R.drawable.golden_crescent_ornate)
      scaleType = ImageView.ScaleType.FIT_CENTER
    }
    middleRow.addView(crescentView, LinearLayout.LayoutParams(dp(context, 62), dp(context, 62)))

    val textCol = LinearLayout(context).apply {
      orientation = LinearLayout.VERTICAL
      gravity = Gravity.CENTER_HORIZONTAL
      setPadding(dp(context, 10), 0, 0, 0)
    }

    val arabicTv = TextView(context).apply {
      text = dhikr.arabic
      setTextColor(Color.parseColor("#FDE68A"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
      typeface = getArabicTypeface(context)
      gravity = Gravity.CENTER
      maxLines = 2
      ellipsize = TextUtils.TruncateAt.END
    }
    textCol.addView(arabicTv, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

    if (dhikr.translationUrdu.isNotBlank()) {
      val urduTv = TextView(context).apply {
        text = dhikr.translationUrdu
        setTextColor(Color.parseColor("#F1F5F9"))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.5f)
        typeface = getUrduTypeface(context)
        gravity = Gravity.CENTER
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
        setPadding(0, dp(context, 2), 0, 0)
      }
      textCol.addView(urduTv, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
    }

    val sourceTv = TextView(context).apply {
      text = if (dhikr.isQuranic) "Qur’an — ${dhikr.source}" else dhikr.source
      setTextColor(Color.parseColor("#7FA3A9"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5f)
      gravity = Gravity.CENTER
      maxLines = 1
      ellipsize = TextUtils.TruncateAt.END
      setPadding(0, dp(context, 3), 0, 0)
    }
    textCol.addView(sourceTv, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

    middleRow.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
    cardLayout.addView(middleRow, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

    // 3. Bottom Row: 5s Progress Bar + "Later" Dismiss Pill
    val bottomRow = LinearLayout(context).apply {
      orientation = LinearLayout.HORIZONTAL
      gravity = Gravity.CENTER_VERTICAL
    }

    val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
      max = 1000
      progress = 1000
      val track = GradientDrawable().apply {
        setColor(Color.parseColor("#13383E"))
        cornerRadius = dp(context, 3).toFloat()
      }
      val fill = GradientDrawable().apply {
        setColor(Color.parseColor("#5CE1E6"))
        cornerRadius = dp(context, 3).toFloat()
      }
      val clipFill = ClipDrawable(fill, Gravity.START, ClipDrawable.HORIZONTAL)
      progressDrawable = LayerDrawable(arrayOf(track, clipFill)).apply {
        setId(0, android.R.id.background)
        setId(1, android.R.id.progress)
      }
    }
    bottomRow.addView(progressBar, LinearLayout.LayoutParams(0, dp(context, 5), 1f))

    val secTv = TextView(context).apply {
      text = "${durationSec}s"
      setTextColor(Color.parseColor("#5CE1E6"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f)
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      setPadding(dp(context, 6), 0, dp(context, 8), 0)
    }
    bottomRow.addView(secTv)

    // Prominent "GET REWARDS" Button connected directly to Toolyfi 15s Timer Page
    val getRewardsBtn = TextView(context).apply {
      text = "🎁 GET REWARDS"
      setTextColor(Color.parseColor("#041F1A"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.5f)
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      setPadding(dp(context, 12), dp(context, 6), dp(context, 12), dp(context, 6))
      background = GradientDrawable().apply {
        setColor(Color.parseColor("#E5C07B"))
        cornerRadius = dp(context, 14).toFloat()
      }
      setOnClickListener {
        dismissExistingOverlay(context)
        ShareHelper.openWebTimerPage(context, dhikr)
      }
    }
    val getRewardsParams = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.WRAP_CONTENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
      marginEnd = dp(context, 6)
    }
    bottomRow.addView(getRewardsBtn, getRewardsParams)

    val laterBtn = TextView(context).apply {
      text = "Later ✕"
      setTextColor(Color.parseColor("#E2E8F0"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      setPadding(dp(context, 10), dp(context, 5), dp(context, 10), dp(context, 5))
      background = GradientDrawable().apply {
        setColor(Color.parseColor("#174340"))
        cornerRadius = dp(context, 14).toFloat()
        setStroke(dp(context, 1), Color.parseColor("#2C6861"))
      }
      setOnClickListener {
        dismissExistingOverlay(context)
      }
    }
    bottomRow.addView(laterBtn)

    cardLayout.addView(bottomRow, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
    outerContainer.addView(cardLayout, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT))

    val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    // Critical non-blocking flags:
    // FLAG_NOT_FOCUSABLE + FLAG_NOT_TOUCH_MODAL ensure the underlying app NEVER pauses,
    // keyboard stays open, and all touches outside the banner pass directly to the user's app!
    val params = WindowManager.LayoutParams(
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      layoutType,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
      y = dp(context, 24)
    }

    windowManager.addView(outerContainer, params)
    currentOverlayView = outerContainer

    // Animate progress bar smoothly over durationSec
    currentAnimator = ValueAnimator.ofInt(1000, 0).apply {
      duration = durationSec * 1000L
      interpolator = LinearInterpolator()
      addUpdateListener { anim ->
        progressBar.progress = anim.animatedValue as Int
      }
      start()
    }

    val dismissTask = Runnable {
      dismissExistingOverlay(context)
    }
    autoDismissRunnable = dismissTask
    mainHandler.postDelayed(dismissTask, durationSec * 1000L)
  }

  fun dismissExistingOverlay(context: Context? = null) {
    autoDismissRunnable?.let { mainHandler.removeCallbacks(it) }
    autoDismissRunnable = null
    currentAnimator?.cancel()
    currentAnimator = null

    val view = currentOverlayView ?: return
    currentOverlayView = null
    try {
      val wm = (context ?: view.context).getSystemService(Context.WINDOW_SERVICE) as? WindowManager
      wm?.removeViewImmediate(view)
    } catch (_: Exception) {}
  }

  private fun dp(context: Context, value: Int): Int {
    return TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      value.toFloat(),
      context.resources.displayMetrics
    ).toInt()
  }

  private fun getArabicTypeface(context: Context): Typeface {
    return try {
      ResourcesCompat.getFont(context, R.font.amiri_quran)
        ?: ResourcesCompat.getFont(context, R.font.scheherazade_new)
        ?: Typeface.create(Typeface.SERIF, Typeface.BOLD)
    } catch (_: Exception) {
      Typeface.create(Typeface.SERIF, Typeface.BOLD)
    }
  }

  private fun getUrduTypeface(context: Context): Typeface {
    return try {
      ResourcesCompat.getFont(context, R.font.noto_nastaliq_urdu)
        ?: Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    } catch (_: Exception) {
      Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    }
  }
}
