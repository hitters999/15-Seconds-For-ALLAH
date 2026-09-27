package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.DhikrItem

object ShareHelper {

  fun formatShareMessage(dhikr: DhikrItem): String {
    val bismillah = if (dhikr.isQuranic) "۞ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۞\n\n" else ""
    return """
✨ 15 Seconds for Allah • ۱۵ سیکنڈز اللہ کے لیے ✨

$bismillah${dhikr.arabic}

📖 اردو ترجمہ:
${dhikr.translationUrdu}

English:
${dhikr.translation}

📍 حوالہ: ${dhikr.source}
💫 فضیلت: ${dhikr.virtue}

📱 اپنے دن کا ہر ایک گھنٹہ یاد الٰہی کے 15 سیکنڈز سے روشن کریں۔
#15SecondsForAllah #Quran #Dhikr #Hadith
    """.trimIndent()
  }

  // Shares a high-resolution visual poster image for WhatsApp Status, Facebook, X, etc.
  fun shareDhikrPoster(context: Context, dhikr: DhikrItem, specificPackage: String? = null) {
    val posterUri: Uri? = PosterGenerator.generateDhikrPoster(context, dhikr)
    val caption = formatShareMessage(dhikr)

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
          val chooser = Intent.createChooser(sendIntent, "پوسٹر شیئر کریں (WhatsApp Status / Social Poster)")
          chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          context.startActivity(chooser)
        }
      } catch (_: Exception) {
        // Fallback to general chooser without package restriction
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
      // Fallback to text share
      shareDhikrText(context, dhikr, specificPackage)
    }
  }

  fun shareDhikrText(context: Context, dhikr: DhikrItem, specificPackage: String? = null) {
    val shareText = formatShareMessage(dhikr)
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
    val caption = "الحمد للہ! 15 Seconds for Allah ایپ میں میری $streak دن کی مسلسل اسٹریک مکمل ہوئی۔ آپ بھی شامل ہوں!\n#15SecondsForAllah #DhikrStreak"

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
