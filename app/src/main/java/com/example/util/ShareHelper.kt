package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.DhikrItem
import com.example.notification.NotificationHelper

object ShareHelper {

  private const val PREFS_DOWNLOAD_LINKS = "app_download_links_prefs"
  private const val KEY_APKPURE_URL = "apkpure_download_url"
  private const val KEY_WEB_PORTAL_URL = "web_portal_url"

  const val DEFAULT_APKPURE_URL = "https://apkpure.com/p/com.aistudio.fifteenseconds.allah"
  const val DEFAULT_WEB_PORTAL_URL = "https://toolyfi.com/15-Seconds-For-ALLAH/"
  const val GITHUB_APK_RELEASE_URL = "https://github.com/hitters999/15-Seconds-For-ALLAH/releases/latest"

  fun getApkPureUrl(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_DOWNLOAD_LINKS, Context.MODE_PRIVATE)
    return prefs.getString(KEY_APKPURE_URL, DEFAULT_APKPURE_URL) ?: DEFAULT_APKPURE_URL
  }

  fun setApkPureUrl(context: Context, url: String) {
    val prefs = context.getSharedPreferences(PREFS_DOWNLOAD_LINKS, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_APKPURE_URL, url.trim()).apply()
  }

  fun getWebPortalUrl(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_DOWNLOAD_LINKS, Context.MODE_PRIVATE)
    return prefs.getString(KEY_WEB_PORTAL_URL, DEFAULT_WEB_PORTAL_URL) ?: DEFAULT_WEB_PORTAL_URL
  }

  fun openWebTimerPage(context: Context, dhikr: DhikrItem, userIdentifier: String = "") {
    try {
      val baseUrl = getWebPortalUrl(context)
      val uri = Uri.parse(baseUrl).buildUpon()
        .appendQueryParameter("id", dhikr.id)
        .appendQueryParameter("arabic", dhikr.arabic)
        .appendQueryParameter("urdu", dhikr.translationUrdu)
        .appendQueryParameter("source", dhikr.source)
        .appendQueryParameter("user", userIdentifier)
        .build()
      val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
    } catch (_: Exception) {}
  }

  fun formatShareMessage(dhikr: DhikrItem, context: Context? = null): String {
    val bismillah = if (dhikr.isQuranic) "۞ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۞\n\n" else ""
    val apkPureLink = context?.let { getApkPureUrl(it) } ?: DEFAULT_APKPURE_URL
    val webLink = context?.let { getWebPortalUrl(it) } ?: DEFAULT_WEB_PORTAL_URL

    return """
✨ 15 Seconds for Allah • ۱۵ سیکنڈز اللہ کے لیے ✨
🪝 دنیا کے کاموں میں ہر 15 منٹ بعد صرف 5 سیکنڈ کا خودکار ذکر پاپ اپ — روحانی سکون بھی اور ہر پوائنٹ پر حوصلہ افزائی ہدیہ (1 Point = 1 Paisa PKR) بھی!

$bismillah${dhikr.arabic}

📖 اردو ترجمہ:
${dhikr.translationUrdu}

📍 حوالہ: ${dhikr.source}

🎁 اپنا اکاؤنٹ بنائیں، روزانہ ذکر پاپ اپ سے پوائنٹس اور ہدیہ جمع کریں:
📥 ڈاؤن لوڈ لنک (APKPure Official):
$apkPureLink

🌐 آن لائن 15 سیکنڈ ٹائمر اور آفیشل پیج:
$webLink

#15Seconds4Allah #IslamicApp #SadqahJariyah #Toolyfi #VortexShorts
    """.trimIndent()
  }

  // Shares a high-resolution visual poster image and awards bonus points for spreading Dhikr
  fun shareDhikrPoster(context: Context, dhikr: DhikrItem, specificPackage: String? = null) {
    // Award +10 points for sharing Sadqah Jariyah poster
    NotificationHelper.recordPopupPointsInDatabase(context, dhikr)

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
          val chooser = Intent.createChooser(sendIntent, "پوسٹر شیئر کریں (+10 پوائنٹس ہدیہ)")
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
    val apkPureLink = getApkPureUrl(context)
    val webLink = getWebPortalUrl(context)
    val caption = """
الحمد للہ! 15 Seconds for Allah ایپ میں میری $streak دن کی مسلسل اسٹریک اور $score پوائنٹس (ہدیہ والٹ: Rs. $pkr PKR) مکمل ہوئے!
🪝 آپ بھی مفت ایپ ڈاؤن لوڈ کریں، ہر ذکر پاپ اپ پر پوائنٹس اور حوصلہ افزائی ہدیہ پائیں:
📥 APKPure: $apkPureLink
🌐 Web: $webLink
#15Seconds4Allah #DhikrStreak #Toolyfi
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
