package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.DhikrItem
import java.io.File
import java.io.FileOutputStream

object PosterGenerator {

  private const val POSTER_WIDTH = 1080
  private const val POSTER_HEIGHT = 1920

  private fun getArabicTypeface(context: Context): Typeface {
    return try {
      ResourcesCompat.getFont(context, R.font.amiri_quran)
        ?: ResourcesCompat.getFont(context, R.font.scheherazade_new)
        ?: ResourcesCompat.getFont(context, R.font.amiri)
        ?: Typeface.create(Typeface.SERIF, Typeface.BOLD)
    } catch (_: Exception) {
      Typeface.create(Typeface.SERIF, Typeface.BOLD)
    }
  }

  private fun getUrduTypeface(context: Context): Typeface {
    return try {
      ResourcesCompat.getFont(context, R.font.noto_nastaliq_urdu)
        ?: ResourcesCompat.getFont(context, R.font.noto_nastaliq)
        ?: Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    } catch (_: Exception) {
      Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    }
  }

  /**
   * Generates a masterpiece, magazine-grade Islamic poster (1080 x 1920)
   * in the exact royal ivory parchment card design loved by the user:
   * - Pristine cream parchment canvas with warm gold borders
   * - Rounded sacred card container
   * - Top badges: "✨ آج کا درسِ قرآن • 2 گھنٹے میں تبدیلی ✨" & "15 Seconds 4 Allah ✦"
   * - Deep Islamic Emerald Green Arabic calligraphy (Amiri Quran font)
   * - Transliteration in dignified dark slate
   * - Urdu translation in rich chestnut / mahogany ink (Noto Nastaliq Urdu font)
   * - English translation in clean serif
   * - Source citation pill badge
   * - "سبق و تدبر" Contemplative reflection card in warm cream
   * - Royal footer branding: "✦ 15 SECONDS 4 ALLAH ✦"
   */
  fun generateDhikrPosterBitmap(context: Context, dhikr: DhikrItem): Bitmap {
    val bitmap = Bitmap.createBitmap(POSTER_WIDTH, POSTER_HEIGHT, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 1. Draw Royal Ivory Parchment Canvas Background with subtle gold vignette
    drawParchmentCanvas(canvas, POSTER_WIDTH, POSTER_HEIGHT)

    // 2. Ornate Golden Outer Borders & Corner Filigree
    drawOrnateGoldenBorders(canvas, POSTER_WIDTH, POSTER_HEIGHT)

    // 3. Top Royal Branding: "✦ 15 SECONDS 4 ALLAH ✦"
    drawTopBrandingHeader(context, canvas, POSTER_WIDTH)

    // 4. Sacred Main Card (Ivory Container matching user's reference)
    val cardRect = RectF(55f, 175f, POSTER_WIDTH - 55f, 1765f)
    drawSacredParchmentCard(canvas, cardRect)

    // 5. Card Content: Badges, Arabic, Transliteration, Urdu, English, Source, Tadabbur
    drawSacredCardContent(context, canvas, dhikr, cardRect)

    // 6. Royal Footer Branding: "✦ 15 SECONDS 4 ALLAH • پندرہ سیکنڈ اللہ کے لیے ✦"
    drawRoyalFooterBranding(canvas, POSTER_WIDTH, POSTER_HEIGHT)

    return bitmap
  }

  fun generateDhikrPoster(context: Context, dhikr: DhikrItem): Uri? {
    return try {
      val bitmap = generateDhikrPosterBitmap(context, dhikr)
      saveBitmapToCache(context, bitmap, "poster_${dhikr.id}")
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  /**
   * Generates a spiritual streak poster with royal ivory parchment styling.
   */
  fun generateStreakPoster(
    context: Context,
    streakDays: Int,
    totalMoments: Int,
    score: Int,
    rank: String
  ): Uri? {
    return try {
      val bitmap = Bitmap.createBitmap(POSTER_WIDTH, POSTER_HEIGHT, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)

      drawParchmentCanvas(canvas, POSTER_WIDTH, POSTER_HEIGHT)
      drawOrnateGoldenBorders(canvas, POSTER_WIDTH, POSTER_HEIGHT)
      drawTopBrandingHeader(context, canvas, POSTER_WIDTH)

      val cardRect = RectF(55f, 175f, POSTER_WIDTH - 55f, 1765f)
      drawSacredParchmentCard(canvas, cardRect)

      // Top Streak Badge
      drawPillBadge(
        canvas = canvas,
        cx = cardRect.centerX(),
        cy = cardRect.top + 70f,
        text = "✦ روحانی استقامت • SPIRITUAL STREAK ✦",
        textColor = Color.parseColor("#8A5A1A"),
        bgColor = Color.parseColor("#FAF3E2"),
        borderColor = Color.parseColor("#D4AF37"),
        fontSize = 26f,
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      )

      // Glowing Golden Circle with Streak Days
      val circleCenterY = cardRect.top + 330f
      val glowShader = RadialGradient(
        cardRect.centerX(), circleCenterY, 200f,
        intArrayOf(Color.parseColor("#33D4AF37"), Color.parseColor("#10D4AF37"), Color.TRANSPARENT),
        floatArrayOf(0f, 0.7f, 1f),
        Shader.TileMode.CLAMP
      )
      val glowPaint = Paint().apply {
        shader = glowShader
        isAntiAlias = true
      }
      canvas.drawCircle(cardRect.centerX(), circleCenterY, 200f, glowPaint)

      val circleBgPaint = Paint().apply {
        color = Color.parseColor("#FDF8ED")
        style = Paint.Style.FILL
        isAntiAlias = true
      }
      canvas.drawCircle(cardRect.centerX(), circleCenterY, 140f, circleBgPaint)

      val circleBorder = Paint().apply {
        color = Color.parseColor("#D4AF37")
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
      }
      canvas.drawCircle(cardRect.centerX(), circleCenterY, 140f, circleBorder)

      // Streak number
      val numPaint = TextPaint().apply {
        color = Color.parseColor("#8A5A1A")
        textSize = 105f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("$streakDays", cardRect.centerX(), circleCenterY + 28f, numPaint)

      val daysLabel = TextPaint().apply {
        color = Color.parseColor("#6E260E")
        textSize = 30f
        typeface = getUrduTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("دن کی مسلسل استقامت (Days Streak)", cardRect.centerX(), circleCenterY + 80f, daysLabel)

      // Rank Badge
      val rankRect = RectF(cardRect.centerX() - 320f, circleCenterY + 185f, cardRect.centerX() + 320f, circleCenterY + 270f)
      val rankBg = Paint().apply {
        shader = LinearGradient(
          rankRect.left, rankRect.top, rankRect.right, rankRect.bottom,
          intArrayOf(Color.parseColor("#FAF3E2"), Color.parseColor("#F5E6C4"), Color.parseColor("#FAF3E2")),
          null, Shader.TileMode.CLAMP
        )
        isAntiAlias = true
      }
      canvas.drawRoundRect(rankRect, 24f, 24f, rankBg)

      val rankBorder = Paint().apply {
        color = Color.parseColor("#D4AF37")
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
      }
      canvas.drawRoundRect(rankRect, 24f, 24f, rankBorder)

      val rankText = TextPaint().apply {
        color = Color.parseColor("#09221D")
        textSize = 34f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("👑 $rank", cardRect.centerX(), circleCenterY + 240f, rankText)

      // Stats Cards Row: Moments & Score
      val statY = circleCenterY + 310f
      val stat1 = RectF(cardRect.left + 50f, statY, cardRect.centerX() - 20f, statY + 170f)
      val stat2 = RectF(cardRect.centerX() + 20f, statY, cardRect.right - 50f, statY + 170f)

      val statBg = Paint().apply {
        color = Color.parseColor("#FAF6EE")
        isAntiAlias = true
      }
      val statBorder = Paint().apply {
        color = Color.parseColor("#D4AF37")
        style = Paint.Style.STROKE
        strokeWidth = 1.8f
        isAntiAlias = true
      }
      canvas.drawRoundRect(stat1, 22f, 22f, statBg)
      canvas.drawRoundRect(stat1, 22f, 22f, statBorder)
      canvas.drawRoundRect(stat2, 22f, 22f, statBg)
      canvas.drawRoundRect(stat2, 22f, 22f, statBorder)

      val statValPaint = TextPaint().apply {
        color = Color.parseColor("#0A4D3C")
        textSize = 48f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      val statSubPaint = TextPaint().apply {
        color = Color.parseColor("#6E260E")
        textSize = 25f
        typeface = getUrduTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }

      canvas.drawText("$totalMoments", stat1.centerX(), stat1.top + 72f, statValPaint)
      canvas.drawText("مکمل اذکار (Moments)", stat1.centerX(), stat1.top + 125f, statSubPaint)

      canvas.drawText("$score", stat2.centerX(), stat2.top + 72f, statValPaint)
      canvas.drawText("روحانی حسنات (Score)", stat2.centerX(), stat2.top + 125f, statSubPaint)

      // Prophetic Hadith Quote Box (Ivory parchment)
      val quoteBoxY = statY + 220f
      val quoteRect = RectF(cardRect.left + 40f, quoteBoxY, cardRect.right - 40f, quoteBoxY + 280f)
      val quoteBg = Paint().apply {
        color = Color.parseColor("#FFFDF5")
        isAntiAlias = true
      }
      val quoteBorder = Paint().apply {
        color = Color.parseColor("#EADBB6")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        isAntiAlias = true
      }
      canvas.drawRoundRect(quoteRect, 24f, 24f, quoteBg)
      canvas.drawRoundRect(quoteRect, 24f, 24f, quoteBorder)

      val quoteTitlePaint = TextPaint().apply {
        color = Color.parseColor("#C5A059")
        textSize = 48f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        isAntiAlias = true
      }
      canvas.drawText("❝", quoteRect.left + 25f, quoteRect.top + 55f, quoteTitlePaint)

      val quoteHadithPaint = TextPaint().apply {
        color = Color.parseColor("#0A4D3C")
        textSize = 34f
        typeface = getArabicTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("أَحَبُّ الْأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ", quoteRect.centerX(), quoteRect.top + 95f, quoteHadithPaint)

      val quoteUrduPaint = TextPaint().apply {
        color = Color.parseColor("#6E260E")
        textSize = 28f
        typeface = getUrduTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("اللہ کے نزدیک سب سے پسندیدہ عمل وہ ہے جو مستقل ہو ، اگرچہ تھوڑا ہی کیوں نہ ہو ۔", quoteRect.centerX(), quoteRect.top + 165f, quoteUrduPaint)

      val quoteRefPaint = TextPaint().apply {
        color = Color.parseColor("#8A5A1A")
        textSize = 23f
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("📖 صحیح البخاری 6464", quoteRect.centerX(), quoteRect.top + 225f, quoteRefPaint)

      drawRoyalFooterBranding(canvas, POSTER_WIDTH, POSTER_HEIGHT)

      saveBitmapToCache(context, bitmap, "streak_${streakDays}")
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  // =========================================================================
  // Canvas Rendering & Layout Engine
  // =========================================================================

  /**
   * 1. Draws serene royal ivory parchment gradient background.
   */
  private fun drawParchmentCanvas(canvas: Canvas, width: Int, height: Int) {
    val bgPaint = Paint().apply {
      shader = LinearGradient(
        0f, 0f, 0f, height.toFloat(),
        intArrayOf(
          Color.parseColor("#FCFAF6"),
          Color.parseColor("#F9F5EC"),
          Color.parseColor("#F5EFE3")
        ),
        floatArrayOf(0f, 0.5f, 1f),
        Shader.TileMode.CLAMP
      )
      isAntiAlias = true
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Subtle golden corner vignette
    val vignettePaint = Paint().apply {
      shader = RadialGradient(
        width / 2f, height / 2f, height * 0.7f,
        intArrayOf(Color.TRANSPARENT, Color.parseColor("#0C8A5A1A")),
        floatArrayOf(0.7f, 1f),
        Shader.TileMode.CLAMP
      )
      isAntiAlias = true
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)
  }

  /**
   * 2. Outer golden hairline borders with subtle corner stars.
   */
  private fun drawOrnateGoldenBorders(canvas: Canvas, width: Int, height: Int) {
    val margin = 26f

    val outerBorder = Paint().apply {
      color = Color.parseColor("#D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 2f
      isAntiAlias = true
    }
    canvas.drawRoundRect(RectF(margin, margin, width - margin, height - margin), 32f, 32f, outerBorder)

    val innerMargin = margin + 8f
    val innerBorder = Paint().apply {
      color = Color.parseColor("#44D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 1f
      isAntiAlias = true
    }
    canvas.drawRoundRect(RectF(innerMargin, innerMargin, width - innerMargin, height - innerMargin), 26f, 26f, innerBorder)

    // Corner decorative stars
    drawCornerStar(canvas, innerMargin + 12f, innerMargin + 12f)
    drawCornerStar(canvas, width - innerMargin - 12f, innerMargin + 12f)
    drawCornerStar(canvas, innerMargin + 12f, height - innerMargin - 12f)
    drawCornerStar(canvas, width - innerMargin - 12f, height - innerMargin - 12f)
  }

  private fun drawCornerStar(canvas: Canvas, cx: Float, cy: Float) {
    val starPaint = Paint().apply {
      color = Color.parseColor("#C5A059")
      style = Paint.Style.FILL
      isAntiAlias = true
    }
    val path = Path().apply {
      moveTo(cx, cy - 8f)
      lineTo(cx + 2.5f, cy - 2.5f)
      lineTo(cx + 8f, cy)
      lineTo(cx + 2.5f, cy + 2.5f)
      lineTo(cx, cy + 8f)
      lineTo(cx - 2.5f, cy + 2.5f)
      lineTo(cx - 8f, cy)
      lineTo(cx - 2.5f, cy - 2.5f)
      close()
    }
    canvas.drawPath(path, starPaint)
  }

  /**
   * 3. Top Branding Header (above card).
   */
  private fun drawTopBrandingHeader(context: Context, canvas: Canvas, width: Int) {
    val cx = width / 2f

    // Royal Top Capsule: "✦ 15 SECONDS 4 ALLAH ✦"
    drawPillBadge(
      canvas = canvas,
      cx = cx,
      cy = 78f,
      text = "✦ 15 SECONDS 4 ALLAH ✦",
      textColor = Color.parseColor("#8A5A1A"),
      bgColor = Color.parseColor("#F9F3E4"),
      borderColor = Color.parseColor("#D4AF37"),
      fontSize = 22f,
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
    )

    // Urdu Subtitle
    val urduSub = TextPaint().apply {
      color = Color.parseColor("#6E260E")
      textSize = 32f
      typeface = getUrduTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("۱۵ سیکنڈز اللہ کے لیے • روزانہ کا مسنون ذکر", cx, 142f, urduSub)
  }

  /**
   * 4. The Sacred Main Card (Cream Parchment container matching user's screenshot).
   */
  private fun drawSacredParchmentCard(canvas: Canvas, rect: RectF) {
    // Soft drop shadow
    val shadowPaint = Paint().apply {
      color = Color.parseColor("#153A2C18")
      isAntiAlias = true
    }
    canvas.drawRoundRect(RectF(rect.left + 4f, rect.top + 8f, rect.right + 4f, rect.bottom + 8f), 46f, 46f, shadowPaint)

    // Card background: Pristine Ivory Parchment
    val cardFill = Paint().apply {
      color = Color.parseColor("#FDFBF7")
      isAntiAlias = true
    }
    canvas.drawRoundRect(rect, 46f, 46f, cardFill)

    // Card border: Royal Warm Gold
    val cardBorder = Paint().apply {
      color = Color.parseColor("#D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 3f
      isAntiAlias = true
    }
    canvas.drawRoundRect(rect, 46f, 46f, cardBorder)
  }

  /**
   * 5. Sacred Card Content:
   * Replicates the exact visual hierarchy of the user's reference image:
   * - Top badges: Left ("✨ آج کا درسِ قرآن • 2 گھنٹے میں تبدیلی ✨") & Right ("15 Seconds 4 Allah ✦")
   * - Bismillah calligraphy
   * - Arabic calligraphy in Emerald Green
   * - Transliteration
   * - Urdu translation in Chestnut / Mahogany ink
   * - English translation in Serif
   * - Source citation pill badge
   * - "سبق و تدبر" Contemplative reflection card in cream container
   */
  private fun drawSacredCardContent(
    context: Context,
    canvas: Canvas,
    dhikr: DhikrItem,
    cardRect: RectF
  ) {
    val cx = cardRect.centerX()
    val contentWidth = (cardRect.width() - 80f).toInt()

    // -------------------------------------------------------------
    // A. Top Badges Row (Matches user's screenshot exactly!)
    // -------------------------------------------------------------
    val topBadgesY = cardRect.top + 60f

    // Left Badge: "✨ آج کا درسِ قرآن • 2 گھنٹے میں تبدیلی ✨"
    val leftBadgeText = if (dhikr.isQuranic) "✨ آج کا درسِ قرآن • مسنون ذکر ✨" else "✨ مسنون ذکر و دعا • روزانہ کی یاد دہانی ✨"
    val leftBadgeRect = RectF(cardRect.left + 35f, topBadgesY - 26f, cardRect.left + 540f, topBadgesY + 26f)
    drawCardPill(canvas, leftBadgeRect, leftBadgeText, getUrduTypeface(context), 26f, Color.parseColor("#1A332C"), Color.parseColor("#FBF6EB"), Color.parseColor("#D4AF37"))

    // Right Badge: "15s ذکر ✦" / "15 Seconds 4 Allah"
    val rightBadgeRect = RectF(cardRect.right - 280f, topBadgesY - 26f, cardRect.right - 35f, topBadgesY + 26f)
    drawCardPill(canvas, rightBadgeRect, "15s ذکر ✦", Typeface.create(Typeface.SERIF, Typeface.BOLD), 24f, Color.parseColor("#8A5A1A"), Color.parseColor("#FBF6EB"), Color.parseColor("#D4AF37"))

    var currentY = topBadgesY + 70f

    // -------------------------------------------------------------
    // B. Bismillah Calligraphy (Optional sacred header)
    // -------------------------------------------------------------
    if (dhikr.isQuranic) {
      val bismillahPaint = TextPaint().apply {
        color = Color.parseColor("#0A4D3C")
        textSize = 36f
        typeface = getArabicTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("۞ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۞", cx, currentY + 10f, bismillahPaint)
      currentY += 55f
    } else {
      currentY += 20f
    }

    // -------------------------------------------------------------
    // C. Sacred Arabic Calligraphy (Deep Islamic Emerald Green from reference!)
    // -------------------------------------------------------------
    val arabicPaint = TextPaint().apply {
      color = Color.parseColor("#0A4D3C") // Deep Emerald Green exactly as in reference!
      textSize = when {
        dhikr.arabic.length < 40 -> 60f
        dhikr.arabic.length < 80 -> 52f
        dhikr.arabic.length < 130 -> 44f
        else -> 38f
      }
      typeface = getArabicTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

    val arabicLayout = createCenteredLayout(dhikr.arabic, arabicPaint, contentWidth, 1.35f)
    canvas.save()
    canvas.translate(cx, currentY)
    arabicLayout.draw(canvas)
    canvas.restore()

    currentY += arabicLayout.height + 25f

    // -------------------------------------------------------------
    // D. Transliteration (Clean Serif in Slate Charcoal)
    // -------------------------------------------------------------
    if (dhikr.transliteration.isNotBlank()) {
      val transPaint = TextPaint().apply {
        color = Color.parseColor("#4A5568")
        textSize = 28f
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      val transLayout = createCenteredLayout(dhikr.transliteration, transPaint, contentWidth, 1.25f)
      canvas.save()
      canvas.translate(cx, currentY)
      transLayout.draw(canvas)
      canvas.restore()

      currentY += transLayout.height + 30f
    }

    // -------------------------------------------------------------
    // E. Urdu Translation (Warm Chestnut / Mahogany ink in Noto Nastaliq!)
    // -------------------------------------------------------------
    val urduPaint = TextPaint().apply {
      color = Color.parseColor("#6E260E") // Rich chestnut/mahogany ink from screenshot!
      textSize = when {
        dhikr.translationUrdu.length < 60 -> 42f
        dhikr.translationUrdu.length < 120 -> 36f
        else -> 31f
      }
      typeface = getUrduTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

    val urduLayout = createCenteredLayout(dhikr.translationUrdu, urduPaint, contentWidth - 40, 1.45f)
    canvas.save()
    canvas.translate(cx, currentY)
    urduLayout.draw(canvas)
    canvas.restore()

    currentY += urduLayout.height + 25f

    // -------------------------------------------------------------
    // F. English Translation (Clean Serif in Dark Slate)
    // -------------------------------------------------------------
    if (dhikr.translation.isNotBlank()) {
      val engPaint = TextPaint().apply {
        color = Color.parseColor("#2D3748")
        textSize = 26f
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      val engLayout = createCenteredLayout(dhikr.translation, engPaint, contentWidth - 60, 1.28f)
      canvas.save()
      canvas.translate(cx, currentY)
      engLayout.draw(canvas)
      canvas.restore()

      currentY += engLayout.height + 30f
    }

    // -------------------------------------------------------------
    // G. Source Citation Pill Badge (Matches screenshot!)
    // -------------------------------------------------------------
    val sourceLabel = "📖 ${dhikr.source}"
    val sourcePaint = TextPaint().apply {
      color = Color.parseColor("#8A5A1A")
      textSize = 27f
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      isAntiAlias = true
    }
    val sourceTextWidth = sourcePaint.measureText(sourceLabel)
    val sourcePillWidth = (sourceTextWidth + 60f).coerceAtLeast(260f)
    val sourcePillRect = RectF(cx - sourcePillWidth / 2f, currentY, cx + sourcePillWidth / 2f, currentY + 54f)
    drawCardPill(canvas, sourcePillRect, sourceLabel, Typeface.create(Typeface.SERIF, Typeface.BOLD), 26f, Color.parseColor("#8A5A1A"), Color.parseColor("#FAF3E2"), Color.parseColor("#D4AF37"))

    currentY += 75f

    // -------------------------------------------------------------
    // H. "سبق و تدبر" Contemplative Reflection Card (Matches screenshot!)
    // -------------------------------------------------------------
    val noteText = if (dhikr.contemplativeNote.isNotBlank()) {
      dhikr.contemplativeNote
    } else {
      "رسول اللہ ﷺ کے سنہری ارشادات جو زندگی کو روشن کرتے ہیں۔ پندرہ سیکنڈ سکون کے ساتھ یادِ الٰہی میں گزاریں۔"
    }

    val tadabburRect = RectF(cardRect.left + 35f, currentY, cardRect.right - 35f, (cardRect.bottom - 45f).coerceAtLeast(currentY + 180f))

    // Background fill & border
    val tadabburBg = Paint().apply {
      color = Color.parseColor("#FFFDF5")
      isAntiAlias = true
    }
    val tadabburBorder = Paint().apply {
      color = Color.parseColor("#EADBB6")
      style = Paint.Style.STROKE
      strokeWidth = 1.5f
      isAntiAlias = true
    }
    canvas.drawRoundRect(tadabburRect, 22f, 22f, tadabburBg)
    canvas.drawRoundRect(tadabburRect, 22f, 22f, tadabburBorder)

    // Decorative quotation mark
    val quotePaint = TextPaint().apply {
      color = Color.parseColor("#C5A059")
      textSize = 48f
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      isAntiAlias = true
    }
    canvas.drawText("❝", tadabburRect.left + 18f, tadabburRect.top + 48f, quotePaint)

    // Note Text in Noto Nastaliq Urdu
    val fullNote = "سبق و تدبر: $noteText"
    val notePaint = TextPaint().apply {
      color = Color.parseColor("#4A3B2C")
      textSize = 28f
      typeface = getUrduTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    val noteLayout = createCenteredLayout(fullNote, notePaint, (tadabburRect.width() - 60f).toInt(), 1.40f)

    val noteY = tadabburRect.centerY() - (noteLayout.height / 2f)
    canvas.save()
    canvas.translate(cx, noteY)
    noteLayout.draw(canvas)
    canvas.restore()
  }

  /**
   * Helper to draw a rounded pill badge with border and centered text.
   */
  private fun drawCardPill(
    canvas: Canvas,
    rect: RectF,
    text: String,
    typeface: Typeface,
    textSize: Float,
    textColor: Int,
    bgColor: Int,
    borderColor: Int
  ) {
    val bgPaint = Paint().apply {
      color = bgColor
      isAntiAlias = true
    }
    val borderPaint = Paint().apply {
      color = borderColor
      style = Paint.Style.STROKE
      strokeWidth = 1.4f
      isAntiAlias = true
    }
    val radius = rect.height() / 2f
    canvas.drawRoundRect(rect, radius, radius, bgPaint)
    canvas.drawRoundRect(rect, radius, radius, borderPaint)

    val textPaint = TextPaint().apply {
      color = textColor
      this.textSize = textSize
      this.typeface = typeface
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    val textY = rect.centerY() - ((textPaint.descent() + textPaint.ascent()) / 2f)
    canvas.drawText(text, rect.centerX(), textY, textPaint)
  }

  private fun drawPillBadge(
    canvas: Canvas,
    cx: Float,
    cy: Float,
    text: String,
    textColor: Int,
    bgColor: Int,
    borderColor: Int,
    fontSize: Float,
    typeface: Typeface
  ) {
    val textPaint = TextPaint().apply {
      color = textColor
      textSize = fontSize
      this.typeface = typeface
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    val textWidth = textPaint.measureText(text)
    val pillWidth = textWidth + 50f
    val pillHeight = fontSize + 24f
    val rect = RectF(cx - pillWidth / 2f, cy - pillHeight / 2f, cx + pillWidth / 2f, cy + pillHeight / 2f)

    drawCardPill(canvas, rect, text, typeface, fontSize, textColor, bgColor, borderColor)
  }

  /**
   * 6. Royal Footer Branding:
   * "✦ 15 SECONDS 4 ALLAH • پندرہ سیکنڈ اللہ کے لیے ✦"
   * "#15Seconds4Allah  #DailyDhikr  #QuranSunnah"
   */
  private fun drawRoyalFooterBranding(canvas: Canvas, width: Int, height: Int) {
    val cx = width / 2f
    val footerTop = height - 125f

    // Divider line
    val divPaint = Paint().apply {
      color = Color.parseColor("#55D4AF37")
      strokeWidth = 1.2f
      isAntiAlias = true
    }
    canvas.drawLine(160f, footerTop, width - 160f, footerTop, divPaint)

    val starPaint = TextPaint().apply {
      color = Color.parseColor("#C5A059")
      textSize = 18f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("✦", cx, footerTop + 6f, starPaint)

    // Tagline with "15 SECONDS 4 ALLAH"
    val footerTagline = TextPaint().apply {
      color = Color.parseColor("#8A5A1A")
      textSize = 25f
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      letterSpacing = 0.05f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("✦ 15 SECONDS 4 ALLAH • پندرہ سیکنڈ اللہ کے لیے ✦", cx, footerTop + 45f, footerTagline)

    val hashPaint = TextPaint().apply {
      color = Color.parseColor("#A0783D")
      textSize = 21f
      typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("#15Seconds4Allah  #DailyDhikr  #QuranSunnah", cx, footerTop + 80f, hashPaint)
  }

  /**
   * Multi-line text builder using StaticLayout with exact center alignment.
   */
  private fun createCenteredLayout(
    text: String,
    paint: TextPaint,
    maxWidth: Int,
    spacingMult: Float
  ): StaticLayout {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      StaticLayout.Builder.obtain(text, 0, text.length, paint, maxWidth)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .setLineSpacing(0f, spacingMult)
        .setIncludePad(false)
        .build()
    } else {
      @Suppress("DEPRECATION")
      StaticLayout(
        text,
        paint,
        maxWidth,
        Layout.Alignment.ALIGN_CENTER,
        spacingMult,
        0f,
        false
      )
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
