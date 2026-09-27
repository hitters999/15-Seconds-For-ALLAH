package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.data.DhikrItem
import java.io.File
import java.io.FileOutputStream

object PosterGenerator {

  fun generateDhikrPoster(context: Context, dhikr: DhikrItem): Uri? {
    return try {
      val width = 1080
      val height = 1440
      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      // 1. Rich Islamic Gradient Background (Deep Teal to Midnight)
      val bgPaint = Paint().apply {
        shader = LinearGradient(
          0f, 0f, 0f, height.toFloat(),
          intArrayOf(Color.parseColor("#0F302C"), Color.parseColor("#1B4E48"), Color.parseColor("#0A1C1A")),
          floatArrayOf(0f, 0.5f, 1f),
          Shader.TileMode.CLAMP
        )
      }
      canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

      // 2. Ornate Golden Borders
      val goldBorder = Paint().apply {
        color = Color.parseColor("#E5C388")
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
      }
      val innerBorder = Paint().apply {
        color = Color.parseColor("#B8863B")
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
      }
      canvas.drawRoundRect(RectF(40f, 40f, width - 40f, height - 40f), 32f, 32f, goldBorder)
      canvas.drawRoundRect(RectF(55f, 55f, width - 55f, height - 55f), 24f, 24f, innerBorder)

      // 3. Header Branding
      val headerPaint = TextPaint().apply {
        color = Color.parseColor("#E5C388")
        textSize = 38f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("✨ 15 Seconds for Allah • ۱۵ سیکنڈز اللہ کے لیے ✨", width / 2f, 120f, headerPaint)

      val subHeaderPaint = TextPaint().apply {
        color = Color.parseColor("#A3BE8C")
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("یادِ الٰہی کی روزانہ گھڑی • Daily Sacred Reminder", width / 2f, 165f, subHeaderPaint)

      // 4. Bismillah Calligraphy Frame
      val bismillahBox = RectF(140f, 210f, width - 140f, 280f)
      val boxPaint = Paint().apply {
        color = Color.parseColor("#22B8863B")
        style = Paint.Style.FILL
      }
      val boxBorder = Paint().apply {
        color = Color.parseColor("#B8863B")
        style = Paint.Style.STROKE
        strokeWidth = 2f
      }
      canvas.drawRoundRect(bismillahBox, 20f, 20f, boxPaint)
      canvas.drawRoundRect(bismillahBox, 20f, 20f, boxBorder)

      val bismillahPaint = TextPaint().apply {
        color = Color.parseColor("#FFE8B2")
        textSize = 34f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("۞ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۞", width / 2f, 258f, bismillahPaint)

      // 5. Arabic Text (Large and Centered)
      val arabicPaint = TextPaint().apply {
        color = Color.parseColor("#FFFFFF")
        textSize = 52f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        isAntiAlias = true
      }
      val arabicLayout = StaticLayout.Builder.obtain(
        dhikr.arabic, 0, dhikr.arabic.length, arabicPaint, width - 200
      ).setAlignment(Layout.Alignment.ALIGN_CENTER).build()

      canvas.save()
      canvas.translate(100f, 330f)
      arabicLayout.draw(canvas)
      canvas.restore()

      var currentY = 330f + arabicLayout.height + 40f

      // 6. Urdu Translation Card
      val urduBoxTop = currentY
      val urduPaint = TextPaint().apply {
        color = Color.parseColor("#FFE8B2")
        textSize = 38f
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        isAntiAlias = true
      }
      val urduLayout = StaticLayout.Builder.obtain(
        "\"${dhikr.translationUrdu}\"", 0, dhikr.translationUrdu.length + 2, urduPaint, width - 220
      ).setAlignment(Layout.Alignment.ALIGN_CENTER).build()

      val urduBoxHeight = urduLayout.height + 50f
      val urduRect = RectF(90f, urduBoxTop, width - 90f, urduBoxTop + urduBoxHeight)
      val urduBgPaint = Paint().apply {
        color = Color.parseColor("#152C28")
        style = Paint.Style.FILL
      }
      canvas.drawRoundRect(urduRect, 20f, 20f, urduBgPaint)
      canvas.drawRoundRect(urduRect, 20f, 20f, innerBorder)

      canvas.save()
      canvas.translate(110f, urduBoxTop + 25f)
      urduLayout.draw(canvas)
      canvas.restore()

      currentY = urduBoxTop + urduBoxHeight + 35f

      // 7. English Translation
      if (dhikr.translation.isNotBlank() && currentY < height - 250f) {
        val engPaint = TextPaint().apply {
          color = Color.parseColor("#D8DEE9")
          textSize = 30f
          typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
          isAntiAlias = true
        }
        val engLayout = StaticLayout.Builder.obtain(
          dhikr.translation, 0, dhikr.translation.length, engPaint, width - 240
        ).setAlignment(Layout.Alignment.ALIGN_CENTER).build()

        canvas.save()
        canvas.translate(120f, currentY)
        engLayout.draw(canvas)
        canvas.restore()

        currentY += engLayout.height + 30f
      }

      // 8. Citation & Virtue
      val sourcePaint = TextPaint().apply {
        color = Color.parseColor("#E5C388")
        textSize = 32f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("📍 ${dhikr.source}", width / 2f, currentY + 30f, sourcePaint)

      // 9. Footer Call to Action
      val footerPaint = TextPaint().apply {
        color = Color.parseColor("#A3BE8C")
        textSize = 28f
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("15 Seconds for Allah • ہر گھنٹے بعد ذکر و دعا کا تحفہ", width / 2f, height - 90f, footerPaint)

      val tagPaint = TextPaint().apply {
        color = Color.parseColor("#B8863B")
        textSize = 24f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("#15SecondsForAllah  #Quran  #Hadith  #Dhikr", width / 2f, height - 60f, tagPaint)

      // Save to cache file and return FileProvider content Uri
      saveBitmapToCache(context, bitmap, "poster_${dhikr.id}")
    } catch (_: Exception) {
      null
    }
  }

  fun generateStreakPoster(context: Context, streakDays: Int, totalMoments: Int, score: Int, rank: String): Uri? {
    return try {
      val width = 1080
      val height = 1440
      val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      // Gradient background
      val bgPaint = Paint().apply {
        shader = LinearGradient(
          0f, 0f, 0f, height.toFloat(),
          intArrayOf(Color.parseColor("#0C2320"), Color.parseColor("#1B4E48"), Color.parseColor("#122A26")),
          floatArrayOf(0f, 0.5f, 1f),
          Shader.TileMode.CLAMP
        )
      }
      canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

      // Golden Borders
      val goldBorder = Paint().apply {
        color = Color.parseColor("#E5C388")
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
      }
      canvas.drawRoundRect(RectF(40f, 40f, width - 40f, height - 40f), 32f, 32f, goldBorder)

      // Title
      val titlePaint = TextPaint().apply {
        color = Color.parseColor("#E5C388")
        textSize = 42f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("✨ 15 Seconds for Allah • میری روحانی اسٹریک ✨", width / 2f, 150f, titlePaint)

      // Big Streak Crescent / Flame Circle
      val circlePaint = Paint().apply {
        color = Color.parseColor("#22B8863B")
        style = Paint.Style.FILL
      }
      val circleBorder = Paint().apply {
        color = Color.parseColor("#B8863B")
        style = Paint.Style.STROKE
        strokeWidth = 8f
      }
      canvas.drawCircle(width / 2f, 420f, 180f, circlePaint)
      canvas.drawCircle(width / 2f, 420f, 180f, circleBorder)

      // Streak number
      val numPaint = TextPaint().apply {
        color = Color.parseColor("#FFFFFF")
        textSize = 120f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("$streakDays", width / 2f, 440f, numPaint)

      val labelPaint = TextPaint().apply {
        color = Color.parseColor("#FFE8B2")
        textSize = 36f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("دن کی مسلسل استقامت (Days Streak)", width / 2f, 510f, labelPaint)

      // Rank Badge
      val rankRect = RectF(140f, 660f, width - 140f, 750f)
      val rankBg = Paint().apply {
        color = Color.parseColor("#B8863B")
        style = Paint.Style.FILL
      }
      canvas.drawRoundRect(rankRect, 25f, 25f, rankBg)

      val rankText = TextPaint().apply {
        color = Color.parseColor("#1B4E48")
        textSize = 38f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("👑 $rank", width / 2f, 720f, rankText)

      // Stats row: Moments and Points
      val statBox1 = RectF(120f, 810f, 500f, 960f)
      val statBox2 = RectF(580f, 810f, 960f, 960f)
      val boxPaint = Paint().apply {
        color = Color.parseColor("#152C28")
        style = Paint.Style.FILL
      }
      canvas.drawRoundRect(statBox1, 20f, 20f, boxPaint)
      canvas.drawRoundRect(statBox2, 20f, 20f, boxPaint)

      val statNumPaint = TextPaint().apply {
        color = Color.parseColor("#E5C388")
        textSize = 48f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      val statLabelPaint = TextPaint().apply {
        color = Color.parseColor("#D8DEE9")
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("$totalMoments", 310f, 880f, statNumPaint)
      canvas.drawText("مکمل اذکار (Moments)", 310f, 930f, statLabelPaint)

      canvas.drawText("$score pts", 770f, 880f, statNumPaint)
      canvas.drawText("روحانی حسنات (Score)", 770f, 930f, statLabelPaint)

      // Motivational Quote
      val quotePaint = TextPaint().apply {
        color = Color.parseColor("#FFFFFF")
        textSize = 36f
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ", width / 2f, 1070f, quotePaint)

      val quoteUrdu = TextPaint().apply {
        color = Color.parseColor("#FFE8B2")
        textSize = 32f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("\"اللہ کے نزدیک سب سے پسندیدہ عمل وہ ہے جو ہمیشہ کیا جائے چاہے تھوڑا ہو۔\"", width / 2f, 1130f, quoteUrdu)

      // Footer
      val footerPaint = TextPaint().apply {
        color = Color.parseColor("#A3BE8C")
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("15 Seconds for Allah • آپ بھی روزانہ ذکر کا حصہ بنیں", width / 2f, height - 90f, footerPaint)

      saveBitmapToCache(context, bitmap, "streak_${streakDays}days")
    } catch (_: Exception) {
      null
    }
  }

  private fun saveBitmapToCache(context: Context, bitmap: Bitmap, prefix: String): Uri? {
    return try {
      val cacheDir = File(context.cacheDir, "shared_posters")
      if (!cacheDir.exists()) cacheDir.mkdirs()

      val file = File(cacheDir, "${prefix}_${System.currentTimeMillis()}.png")
      FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
      }

      FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
    } catch (_: Exception) {
      null
    }
  }
}
