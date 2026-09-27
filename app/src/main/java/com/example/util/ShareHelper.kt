package com.example.util

import android.content.Context
import android.content.Intent
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

  fun shareDhikr(context: Context, dhikr: DhikrItem, specificPackage: String? = null) {
    val shareText = formatShareMessage(dhikr)
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "15 Seconds for Allah: ${dhikr.transliteration}")
      putExtra(Intent.EXTRA_TEXT, shareText)
      specificPackage?.let {
        setPackage(it)
      }
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
      // If specific app is not installed, open standard chooser
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

  fun shareToWhatsApp(context: Context, dhikr: DhikrItem) {
    shareDhikr(context, dhikr, "com.whatsapp")
  }

  fun shareToTwitter(context: Context, dhikr: DhikrItem) {
    shareDhikr(context, dhikr, "com.twitter.android")
  }

  fun shareToFacebook(context: Context, dhikr: DhikrItem) {
    shareDhikr(context, dhikr, "com.facebook.katana")
  }
}
