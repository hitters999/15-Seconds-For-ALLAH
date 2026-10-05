package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.AppDatabase
import com.example.data.DhikrCatalog
import com.example.data.DhikrItem
import com.example.data.DhikrRepository
import com.example.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ShareHelper {

  private const val PREFS_DOWNLOAD_LINKS = "app_download_links_prefs"
  private const val KEY_APKPURE_URL = "apkpure_download_url"
  private const val KEY_WEB_PORTAL_URL = "web_portal_url"

  // Pending 15-Second Toolyfi Web Reward Session Keys
  private const val KEY_PENDING_REWARD_START_MS = "pending_reward_start_ms"
  private const val KEY_PENDING_REWARD_DHIKR_ID = "pending_reward_dhikr_id"

  // Official Uptodown & Toolyfi Download Links
  const val DEFAULT_UPTODOWN_URL = "https://15-seconds-for-allah.en.uptodown.com/android"
  const val DEFAULT_APKPURE_URL = DEFAULT_UPTODOWN_URL
  const val DEFAULT_WEB_PORTAL_URL = "https://toolyfi.com/15-Seconds-For-ALLAH/"
  const val GITHUB_APK_RELEASE_URL = "https://github.com/hitters999/15-Seconds-For-ALLAH/releases/latest"

  fun getApkPureUrl(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_DOWNLOAD_LINKS, Context.MODE_PRIVATE)
    val saved = prefs.getString(KEY_APKPURE_URL, DEFAULT_UPTODOWN_URL) ?: DEFAULT_UPTODOWN_URL
    return if (saved.contains("apkpure")) DEFAULT_UPTODOWN_URL else saved
  }

  fun setApkPureUrl(context: Context, url: String) {
    val prefs = context.getSharedPreferences(PREFS_DOWNLOAD_LINKS, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_APKPURE_URL, url.trim()).apply()
  }

  fun getWebPortalUrl(context: Context): String {
    return DEFAULT_WEB_PORTAL_URL
  }

  /**
   * Starts a 15-second reward session on the Toolyfi.com page AND passes the user's
   * exact current account email, name, points, and sessions so the Website & App stay 100% synced!
   */
  fun openWebTimerPage(context: Context, dhikr: DhikrItem, userIdentifier: String = "") {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val db = AppDatabase.getDatabase(context)
        val settings = db.momentDao().getUserSettingsDirect()
        val currentScore = settings?.totalScore ?: 0
        val currentName = settings?.userName ?: "Guest"
        val currentEmail = userIdentifier.ifBlank { settings?.userEmail ?: "" }
        val totalMoments = if (currentEmail.isNotBlank()) {
          db.momentDao().getUserAccountDirect(currentEmail)?.completedSessions ?: (currentScore / 10)
        } else {
          currentScore / 10
        }

        val prefs = context.getSharedPreferences(PREFS_DOWNLOAD_LINKS, Context.MODE_PRIVATE)
        prefs.edit()
          .putLong(KEY_PENDING_REWARD_START_MS, System.currentTimeMillis())
          .putString(KEY_PENDING_REWARD_DHIKR_ID, dhikr.id)
          .apply()

        CoroutineScope(Dispatchers.Main).launch {
          Toast.makeText(
            context,
            "Toolyfi پیج پر 15 سیکنڈ مکمل کریں تاکہ +10 پوائنٹس (10 پیسے) آپ کے اکاؤنٹ میں سنک ہو جائیں!",
            Toast.LENGTH_LONG
          ).show()
        }

        // Truncate URL query text if extremely long so browser URL never fails, while keeping full meaning
        val safeArabic = dhikr.arabic.take(600)
        val safeUrdu = dhikr.translationUrdu.take(600)

        val baseUrl = getWebPortalUrl(context)
        val uri = Uri.parse(baseUrl).buildUpon()
          .appendQueryParameter("id", dhikr.id)
          .appendQueryParameter("arabic", safeArabic)
          .appendQueryParameter("urdu", safeUrdu)
          .appendQueryParameter("source", dhikr.source)
          .appendQueryParameter("user", currentEmail.ifBlank { currentName })
          .appendQueryParameter("name", currentName)
          .appendQueryParameter("pts", currentScore.toString())
          .appendQueryParameter("sessions", totalMoments.toString())
          .build()

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
      } catch (_: Exception) {}
    }
  }

  /**
   * Handles Deep Link `allah15s://sync?user=...&name=...&pts=...&sessions=...`
   * when user clicks "Sync Wallet to Android App" on the Website!
   */
  fun handleIncomingWebSyncIntent(context: Context, intent: Intent?): Boolean {
    val data: Uri = intent?.data ?: return false
    if (data.scheme != "allah15s" && !data.host.orEmpty().contains("toolyfi.com")) return false

    val syncedEmail = data.getQueryParameter("user")?.trim().orEmpty()
    val syncedName = data.getQueryParameter("name")?.trim().orEmpty()
    val syncedPts = data.getQueryParameter("pts")?.toIntOrNull() ?: return false
    val syncedSessions = data.getQueryParameter("sessions")?.toIntOrNull() ?: (syncedPts / 10)

    // Clear any pending timer session so we don't double-credit on resume
    val prefs = context.getSharedPreferences(PREFS_DOWNLOAD_LINKS, Context.MODE_PRIVATE)
    prefs.edit()
      .remove(KEY_PENDING_REWARD_START_MS)
      .remove(KEY_PENDING_REWARD_DHIKR_ID)
      .apply()

    CoroutineScope(Dispatchers.IO).launch {
      val db = AppDatabase.getDatabase(context)
      val repo = DhikrRepository(db.momentDao(), context)
      repo.syncAccountFromWeb(
        email = syncedEmail,
        name = syncedName,
        webPoints = syncedPts,
        webSessions = syncedSessions
      )
    }

    val pkr = String.format(java.util.Locale.US, "%.2f", syncedPts * 0.01)
    Toast.makeText(
      context,
      "الحمد للہ! ویب سائٹ اور ایپ کا اکاؤنٹ سنک ہو گیا: $syncedPts پوائنٹس (Rs. $pkr PKR) ✓",
      Toast.LENGTH_LONG
    ).show()
    return true
  }

  /**
   * Called in MainActivity.onResume() when the user returns to the App from Toolyfi.com.
   * Verifies that at least 15 seconds were spent on the Toolyfi.com page before transferring points.
   */
  fun verifyAndCreditWebTimerSession(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_DOWNLOAD_LINKS, Context.MODE_PRIVATE)
    val startMs = prefs.getLong(KEY_PENDING_REWARD_START_MS, 0L)
    if (startMs <= 0L) return

    val dhikrId = prefs.getString(KEY_PENDING_REWARD_DHIKR_ID, "") ?: ""
    // Clear pending session immediately so it cannot be claimed twice
    prefs.edit()
      .remove(KEY_PENDING_REWARD_START_MS)
      .remove(KEY_PENDING_REWARD_DHIKR_ID)
      .apply()

    val elapsedSeconds = (System.currentTimeMillis() - startMs) / 1000L
    if (elapsedSeconds >= 15L) {
      val item = DhikrCatalog.items.find { it.id == dhikrId } ?: DhikrCatalog.items.first()
      NotificationHelper.recordPopupPointsInDatabase(context, item)
      Toast.makeText(
        context,
        "ماشاء اللہ! Toolyfi پیج پر 15 سیکنڈ مکمل کرنے پر +10 پوائنٹس (10 پیسے ہدیہ) آپ کے والٹ میں ٹرانسفر ہو گئے ✓",
        Toast.LENGTH_LONG
      ).show()
    } else {
      Toast.makeText(
        context,
        "ریوارڈ کے لیے Toolyfi پیج پر پورے 15 سیکنڈ گزارنا ضروری ہے! (آپ نے صرف ${elapsedSeconds}s گزارے)",
        Toast.LENGTH_LONG
      ).show()
    }
  }

  fun formatShareMessage(dhikr: DhikrItem, context: Context? = null): String {
    val bismillah = if (dhikr.isQuranic) "۞ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۞\n\n" else ""
    val uptodownLink = context?.let { getApkPureUrl(it) } ?: DEFAULT_UPTODOWN_URL
    val webLink = context?.let { getWebPortalUrl(it) } ?: DEFAULT_WEB_PORTAL_URL

    return """
✨ 15 Seconds for Allah • ثواب بھی ، Rewards بھی ✨
🪝 ہر 15 منٹ بعد صرف 5 سیکنڈ کا خودکار ذکر پاپ اپ — 15 سیکنڈ ٹائمر مکمل کریں اور ہر پوائنٹ پر حوصلہ افزائی ہدیہ (1 Point = 1 Paisa PKR) پائیں!

$bismillah${dhikr.arabic}

📖 اردو ترجمہ:
${dhikr.translationUrdu}

📍 حوالہ: ${dhikr.source}

🎁 اپنا اکاؤنٹ بنائیں، روزانہ ذکر پاپ اپ سے پوائنٹس اور ہدیہ جمع کریں:
📥 ڈاؤن لوڈ آفیشل ایپ (Uptodown Store):
$uptodownLink

🌐 آن لائن 15 سیکنڈ ٹائمر اور آفیشل پیج:
$webLink

#15Seconds4Allah #IslamicApp #SadqahJariyah #Toolyfi #Uptodown
    """.trimIndent()
  }

  fun shareDhikrPoster(context: Context, dhikr: DhikrItem, specificPackage: String? = null) {
    val posterUri: Uri? = PosterGenerator.generateDhikrPoster(context, dhikr)
    val caption = formatShareMessage(dhikr, context)

    if (posterUri != null) {
      val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, posterUri)
        putExtra(Intent.EXTRA_TEXT, caption)
        putExtra(Intent.EXTRA_SUBJECT, "15 Seconds for Allah: ${dhikr.transliteration}")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        specificPackage?.let { setPackage(it) }
      }

      try {
        if (specificPackage != null) {
          context.startActivity(sendIntent)
        } else {
          val chooser = Intent.createChooser(sendIntent, "پوسٹر شیئر کریں (Share Poster)")
          chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          context.startActivity(chooser)
        }
      } catch (_: Exception) {
        val fallback = Intent(Intent.ACTION_SEND).apply {
          type = "image/png"
          putExtra(Intent.EXTRA_STREAM, posterUri)
          putExtra(Intent.EXTRA_TEXT, caption)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooser = Intent.createChooser(fallback, "پوسٹر شیئر کریں (Share Poster)")
        chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooser)
      }
    } else {
      shareDhikrText(context, dhikr, specificPackage)
    }
  }

  fun shareDhikrText(context: Context, dhikr: DhikrItem, specificPackage: String? = null) {
    val shareText = formatShareMessage(dhikr, context)
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "15 Seconds for Allah: ${dhikr.transliteration}")
      putExtra(Intent.EXTRA_TEXT, shareText)
      specificPackage?.let { setPackage(it) }
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    try {
      if (specificPackage != null) {
        context.startActivity(sendIntent)
      } else {
        val chooser = Intent.createChooser(sendIntent, "شیئر کریں (Share via WhatsApp, X, Facebook)")
        chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooser)
      }
    } catch (_: Exception) {
      val fallback = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      val chooser = Intent.createChooser(fallback, "شیئر کریں (Share)")
      chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      context.startActivity(chooser)
    }
  }

  fun shareDhikr(context: Context, dhikr: DhikrItem, specificPackage: String? = null) {
    shareDhikrPoster(context, dhikr, specificPackage)
  }

  fun shareToWhatsApp(context: Context, dhikr: DhikrItem) {
    shareDhikrPoster(context, dhikr, "com.whatsapp")
  }

  fun shareToTwitter(context: Context, dhikr: DhikrItem) {
    shareDhikrPoster(context, dhikr, "com.twitter.android")
  }

  fun shareToFacebook(context: Context, dhikr: DhikrItem) {
    shareDhikrPoster(context, dhikr, "com.facebook.katana")
  }

  fun shareStreakPoster(context: Context, streak: Int, moments: Int, score: Int, rank: String) {
    val uri = PosterGenerator.generateStreakPoster(context, streak, moments, score, rank)
    val pkr = String.format(java.util.Locale.US, "%.2f", score * 0.01)
    val uptodownLink = getApkPureUrl(context)
    val webLink = getWebPortalUrl(context)
    val caption = """
الحمد للہ! 15 Seconds for Allah ایپ میں میری $streak دن کی مسلسل اسٹریک اور $score پوائنٹس (ہدیہ والٹ: Rs. $pkr PKR) مکمل ہوئے!
✨ ثواب بھی ، Rewards بھی! آپ بھی مفت ایپ ڈاؤن لوڈ کریں:
📥 Uptodown: $uptodownLink
🌐 Web: $webLink
#15Seconds4Allah #DhikrStreak #Toolyfi #Uptodown
    """.trimIndent()

    if (uri != null) {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, caption)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      val chooser = Intent.createChooser(intent, "اسٹریک پوسٹر شیئر کریں (Share Streak Poster)")
      chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      context.startActivity(chooser)
    }
  }
}
