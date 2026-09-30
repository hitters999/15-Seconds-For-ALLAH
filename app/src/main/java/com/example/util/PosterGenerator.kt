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

  // Universal 1:1 Social Media Standard (1080 x 1080)
  // Perfectly optimized for WhatsApp Channels, Facebook Feed, Instagram, and Group Chats
  // 100% CROP-FREE: Zero cutting at top or bottom in Facebook and WhatsApp channel feeds!
  private const val POSTER_WIDTH = 1080
  private const val POSTER_HEIGHT = 1080

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
   * Generates a world-class, luxury Islamic poster (1080 x 1080 - 1:1 Square)
   * specifically crafted to solve social media cutting issues:
   * - 100% full view on WhatsApp Channels & Facebook feed with ZERO top/bottom cropping!
   * - Giant, magnified, zoomed-in Arabic calligraphy readable from far away
   * - Royal Islamic branding seal with real app logo and gold foil embellishments
   * - Flowing Noto Nastaliq Urdu in rich Persian walnut ink
   * - Gold-embossed citation ribbon and "سبق و تدبر" spiritual wisdom card
   * - High-converting viral CTA footer for viewers and followers
   */
  fun generateDhikrPosterBitmap(context: Context, dhikr: DhikrItem): Bitmap {
    val bitmap = Bitmap.createBitmap(POSTER_WIDTH, POSTER_HEIGHT, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 1. Royal Parchment Canvas with subtle golden vignette
    drawParchmentCanvas(canvas, POSTER_WIDTH, POSTER_HEIGHT)

    // 2. Ornate 24K Golden Borders & 8-Point Islamic Corner Stars
    drawOrnateGoldenBorders(canvas, POSTER_WIDTH, POSTER_HEIGHT)

    // 3. Top Royal Branding Header with App Logo & Gold Medal
    drawTopBrandingHeader(context, canvas, POSTER_WIDTH)

    // 4. Sacred Main Card: Framed comfortably within safe boundaries (110f to 965f)
    val cardRect = RectF(48f, 115f, POSTER_WIDTH - 48f, 968f)
    drawSacredParchmentCard(canvas, cardRect)

    // 5. Card Content: Zoomed Arabic, Badges, Nastaliq Urdu, Citation, Reflection
    drawSacredCardContent(context, canvas, dhikr, cardRect)

    // 6. Royal Footer Social Branding (safe within 980f to 1055f)
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
   * Generates a spiritual streak poster in the exact same 1080x1080 crop-free format.
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

      val cardRect = RectF(48f, 115f, POSTER_WIDTH - 48f, 968f)
      drawSacredParchmentCard(canvas, cardRect)

      // Top Streak Badge
      drawPillBadge(
        canvas = canvas,
        cx = cardRect.centerX(),
        cy = cardRect.top + 46f,
        text = "✦ روحانی استقامت • SPIRITUAL STREAK ✦",
        textColor = Color.parseColor("#8A5A1A"),
        bgColor = Color.parseColor("#FAF3E2"),
        borderColor = Color.parseColor("#D4AF37"),
        fontSize = 22f,
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      )

      // Glowing Golden Circle with Streak Days
      val circleCenterY = cardRect.top + 225f
      val glowShader = RadialGradient(
        cardRect.centerX(), circleCenterY, 150f,
        intArrayOf(Color.parseColor("#33D4AF37"), Color.parseColor("#10D4AF37"), Color.TRANSPARENT),
        floatArrayOf(0f, 0.7f, 1f),
        Shader.TileMode.CLAMP
      )
      val glowPaint = Paint().apply {
        shader = glowShader
        isAntiAlias = true
      }
      canvas.drawCircle(cardRect.centerX(), circleCenterY, 150f, glowPaint)

      val ringPaint = Paint().apply {
        color = Color.parseColor("#D4AF37")
        style = Paint.Style.STROKE
        strokeWidth = 3.5f
        isAntiAlias = true
      }
      canvas.drawCircle(cardRect.centerX(), circleCenterY, 105f, ringPaint)

      val innerRingPaint = Paint().apply {
        color = Color.parseColor("#55D4AF37")
        style = Paint.Style.STROKE
        strokeWidth = 1.2f
        isAntiAlias = true
      }
      canvas.drawCircle(cardRect.centerX(), circleCenterY, 97f, innerRingPaint)

      val streakNumPaint = TextPaint().apply {
        color = Color.parseColor("#063A2F")
        textSize = 80f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("$streakDays", cardRect.centerX(), circleCenterY + 22f, streakNumPaint)

      val daysLabel = TextPaint().apply {
        color = Color.parseColor("#8A5A1A")
        textSize = 21f
        typeface = getUrduTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("مسلسل دن (Days)", cardRect.centerX(), circleCenterY + 62f, daysLabel)

      // Rank Title
      val rankText = TextPaint().apply {
        color = Color.parseColor("#996515")
        textSize = 26f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("👑 $rank", cardRect.centerX(), circleCenterY + 160f, rankText)

      // Stats Cards Row: Moments & Score
      val statY = circleCenterY + 205f
      val stat1 = RectF(cardRect.left + 45f, statY, cardRect.centerX() - 15f, statY + 115f)
      val stat2 = RectF(cardRect.centerX() + 15f, statY, cardRect.right - 45f, statY + 115f)

      val statBg = Paint().apply {
        color = Color.parseColor("#FAF6EE")
        isAntiAlias = true
      }
      val statBorderPaint = Paint().apply {
        color = Color.parseColor("#D4AF37")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        isAntiAlias = true
      }
      canvas.drawRoundRect(stat1, 16f, 16f, statBg)
      canvas.drawRoundRect(stat1, 16f, 16f, statBorderPaint)
      canvas.drawRoundRect(stat2, 16f, 16f, statBg)
      canvas.drawRoundRect(stat2, 16f, 16f, statBorderPaint)

      val statValPaint = TextPaint().apply {
        color = Color.parseColor("#0A4D3C")
        textSize = 38f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      val statSubPaint = TextPaint().apply {
        color = Color.parseColor("#6E260E")
        textSize = 21f
        typeface = getUrduTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }

      canvas.drawText("$totalMoments", stat1.centerX(), stat1.top + 50f, statValPaint)
      canvas.drawText("مکمل اذکار (Moments)", stat1.centerX(), stat1.top + 92f, statSubPaint)

      canvas.drawText("$score", stat2.centerX(), stat2.top + 50f, statValPaint)
      canvas.drawText("روحانی حسنات (Score)", stat2.centerX(), stat2.top + 92f, statSubPaint)

      // Hadith Box
      val quoteBoxY = statY + 135f
      val quoteRect = RectF(cardRect.left + 35f, quoteBoxY, cardRect.right - 35f, quoteBoxY + 140f)
      val quoteBg = Paint().apply {
        color = Color.parseColor("#FFFDF5")
        isAntiAlias = true
      }
      val quoteBorder = Paint().apply {
        color = Color.parseColor("#EADBB6")
        style = Paint.Style.STROKE
        strokeWidth = 1.3f
        isAntiAlias = true
      }
      canvas.drawRoundRect(quoteRect, 18f, 18f, quoteBg)
      canvas.drawRoundRect(quoteRect, 18f, 18f, quoteBorder)

      val quoteHadithPaint = TextPaint().apply {
        color = Color.parseColor("#0A4D3C")
        textSize = 27f
        typeface = getArabicTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("أَحَبُّ الْأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ", quoteRect.centerX(), quoteRect.top + 45f, quoteHadithPaint)

      val quoteUrduPaint = TextPaint().apply {
        color = Color.parseColor("#6E260E")
        textSize = 22f
        typeface = getUrduTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("اللہ کے نزدیک سب سے پسندیدہ عمل وہ ہے جو مستقل ہو ، اگرچہ تھوڑا ہی ہو ۔", quoteRect.centerX(), quoteRect.top + 92f, quoteUrduPaint)

      val quoteRefPaint = TextPaint().apply {
        color = Color.parseColor("#8A5A1A")
        textSize = 19f
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("📖 صحیح البخاری 6464", quoteRect.centerX(), quoteRect.top + 126f, quoteRefPaint)

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

  private fun drawParchmentCanvas(canvas: Canvas, width: Int, height: Int) {
    val bgPaint = Paint().apply {
      shader = LinearGradient(
        0f, 0f, 0f, height.toFloat(),
        intArrayOf(
          Color.parseColor("#FEFCF8"),
          Color.parseColor("#F9F4EB"),
          Color.parseColor("#F4EADC")
        ),
        floatArrayOf(0f, 0.5f, 1f),
        Shader.TileMode.CLAMP
      )
      isAntiAlias = true
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Soft warm golden radial vignette
    val vignettePaint = Paint().apply {
      shader = RadialGradient(
        width / 2f, height / 2f, width * 0.72f,
        intArrayOf(Color.TRANSPARENT, Color.parseColor("#128A5A1A")),
        floatArrayOf(0.68f, 1f),
        Shader.TileMode.CLAMP
      )
      isAntiAlias = true
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)
  }

  private fun drawOrnateGoldenBorders(canvas: Canvas, width: Int, height: Int) {
    val margin = 20f

    val outerBorder = Paint().apply {
      color = Color.parseColor("#D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 2.2f
      isAntiAlias = true
    }
    canvas.drawRoundRect(RectF(margin, margin, width - margin, height - margin), 26f, 26f, outerBorder)

    val innerMargin = margin + 6f
    val innerBorder = Paint().apply {
      color = Color.parseColor("#44D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 1f
      isAntiAlias = true
    }
    canvas.drawRoundRect(RectF(innerMargin, innerMargin, width - innerMargin, height - innerMargin), 22f, 22f, innerBorder)

    // Corner decorative Islamic 8-point stars
    drawCornerStar(canvas, innerMargin + 10f, innerMargin + 10f)
    drawCornerStar(canvas, width - innerMargin - 10f, innerMargin + 10f)
    drawCornerStar(canvas, innerMargin + 10f, height - innerMargin - 10f)
    drawCornerStar(canvas, width - innerMargin - 10f, height - innerMargin - 10f)
  }

  private fun drawCornerStar(canvas: Canvas, cx: Float, cy: Float) {
    val starPaint = Paint().apply {
      color = Color.parseColor("#C5A059")
      style = Paint.Style.FILL
      isAntiAlias = true
    }
    val path = Path().apply {
      moveTo(cx, cy - 7f)
      lineTo(cx + 2.2f, cy - 2.2f)
      lineTo(cx + 7f, cy)
      lineTo(cx + 2.2f, cy + 2.2f)
      lineTo(cx, cy + 7f)
      lineTo(cx - 2.2f, cy + 2.2f)
      lineTo(cx - 7f, cy)
      lineTo(cx - 2.2f, cy - 2.2f)
      close()
    }
    canvas.drawPath(path, starPaint)
  }

  private fun drawTopBrandingHeader(context: Context, canvas: Canvas, width: Int) {
    val cx = width / 2f

    // 1. Draw App Brand Logo Medallion
    try {
      val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.app_brand_logo)
      if (logoBitmap != null) {
        val logoSize = 46f
        val logoX = cx - 210f
        val logoY = 32f

        val ringPaint = Paint().apply {
          color = Color.parseColor("#D4AF37")
          style = Paint.Style.STROKE
          strokeWidth = 1.8f
          isAntiAlias = true
        }
        val bgPaint = Paint().apply {
          color = Color.WHITE
          style = Paint.Style.FILL
          isAntiAlias = true
        }
        canvas.drawCircle(logoX, logoY + logoSize / 2f, logoSize / 2f + 2f, bgPaint)
        canvas.drawCircle(logoX, logoY + logoSize / 2f, logoSize / 2f + 2f, ringPaint)

        val srcRect = android.graphics.Rect(0, 0, logoBitmap.width, logoBitmap.height)
        val dstRect = RectF(logoX - logoSize / 2f, logoY, logoX + logoSize / 2f, logoY + logoSize)
        canvas.drawBitmap(logoBitmap, srcRect, dstRect, Paint(Paint.FILTER_BITMAP_FLAG))
      }
    } catch (_: Exception) {}

    // 2. Royal Pill Header Badge: "✦ 15 SECONDS 4 ALLAH ✦"
    drawPillBadge(
      canvas = canvas,
      cx = cx + 18f,
      cy = 52f,
      text = "✦ 15 SECONDS 4 ALLAH ✦",
      textColor = Color.parseColor("#8A5A1A"),
      bgColor = Color.parseColor("#FAF3E4"),
      borderColor = Color.parseColor("#D4AF37"),
      fontSize = 20f,
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
    )

    // 3. Urdu Subtitle
    val urduSub = TextPaint().apply {
      color = Color.parseColor("#6E260E")
      textSize = 25f
      typeface = getUrduTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("۱۵ سیکنڈز اللہ کے لیے • روزانہ کا مسنون ذکر و دعا", cx, 96f, urduSub)
  }

  private fun drawSacredParchmentCard(canvas: Canvas, rect: RectF) {
    // Soft ambient shadow
    val shadowPaint = Paint().apply {
      color = Color.parseColor("#123A2C18")
      isAntiAlias = true
    }
    canvas.drawRoundRect(RectF(rect.left + 2f, rect.top + 4f, rect.right + 2f, rect.bottom + 4f), 34f, 34f, shadowPaint)

    // Pristine Ivory Parchment Card
    val cardFill = Paint().apply {
      color = Color.parseColor("#FDFBF7")
      isAntiAlias = true
    }
    canvas.drawRoundRect(rect, 34f, 34f, cardFill)

    // Warm Gold Card Border
    val cardBorder = Paint().apply {
      color = Color.parseColor("#D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 2.4f
      isAntiAlias = true
    }
    canvas.drawRoundRect(rect, 34f, 34f, cardBorder)
  }

  /**
   * Main Content Layout in 1:1 format:
   * - Giant, Zoomed-In Arabic Calligraphy (Visible and readable from far away!)
   * - Beautiful Urdu Nastaliq in warm Persian ink
   * - Crisp citation and contemplative wisdom
   * - Fully balanced to never cut off at top or bottom
   */
  private fun drawSacredCardContent(
    context: Context,
    canvas: Canvas,
    dhikr: DhikrItem,
    cardRect: RectF
  ) {
    val cx = cardRect.centerX()
    val contentWidth = (cardRect.width() - 64f).toInt()

    // -------------------------------------------------------------
    // A. Top Badges Row
    // -------------------------------------------------------------
    val topBadgesY = cardRect.top + 42f

    val leftBadgeText = if (dhikr.isQuranic) "✨ درسِ قرآن • مسنون ذکر ✨" else "✨ مسنون ذکر و دعا ✨"
    val leftBadgeRect = RectF(cardRect.left + 26f, topBadgesY - 20f, cardRect.left + 440f, topBadgesY + 20f)
    drawCardPill(canvas, leftBadgeRect, leftBadgeText, getUrduTypeface(context), 22f, Color.parseColor("#1A332C"), Color.parseColor("#FBF6EB"), Color.parseColor("#D4AF37"))

    val rightBadgeRect = RectF(cardRect.right - 210f, topBadgesY - 20f, cardRect.right - 26f, topBadgesY + 20f)
    drawCardPill(canvas, rightBadgeRect, "15s ذکر ✦", Typeface.create(Typeface.SERIF, Typeface.BOLD), 19.5f, Color.parseColor("#8A5A1A"), Color.parseColor("#FBF6EB"), Color.parseColor("#D4AF37"))

    var currentY = topBadgesY + 44f

    // B. Bismillah Calligraphy (Cartouche)
    if (dhikr.isQuranic) {
      val bismillahPaint = TextPaint().apply {
        color = Color.parseColor("#0A4D3C")
        textSize = 28f
        typeface = getArabicTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("۞ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۞", cx, currentY + 4f, bismillahPaint)
      currentY += 40f
    } else {
      currentY += 8f
    }

    // -------------------------------------------------------------
    // C. MAGNIFIED, ZOOMED-IN ARABIC CALLIGRAPHY (Visible from afar!)
    // -------------------------------------------------------------
    val arabicPaint = TextPaint().apply {
      color = Color.parseColor("#043B2E") // Deep Royal Islamic Emerald Green
      textSize = when {
        dhikr.arabic.length < 35 -> 88f // Giant & Majestic! Readable across the room!
        dhikr.arabic.length < 65 -> 74f
        dhikr.arabic.length < 105 -> 62f
        else -> 50f
      }
      typeface = getArabicTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
      // Subtle gold drop shadow to give 3D calligraphy depth
      setShadowLayer(3.5f, 1f, 1.5f, Color.parseColor("#33D4AF37"))
    }

    val arabicLayout = createCenteredLayout(dhikr.arabic, arabicPaint, contentWidth, 1.34f)
    canvas.save()
    canvas.translate(cx, currentY)
    arabicLayout.draw(canvas)
    canvas.restore()

    currentY += arabicLayout.height + 16f

    // -------------------------------------------------------------
    // D. Transliteration (Serif in Slate Charcoal)
    // -------------------------------------------------------------
    if (dhikr.transliteration.isNotBlank()) {
      val transPaint = TextPaint().apply {
        color = Color.parseColor("#4A5568")
        textSize = 22f
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      val transLayout = createCenteredLayout(dhikr.transliteration, transPaint, contentWidth, 1.2f)
      canvas.save()
      canvas.translate(cx, currentY)
      transLayout.draw(canvas)
      canvas.restore()

      currentY += transLayout.height + 14f
    }

    // -------------------------------------------------------------
    // E. Urdu Translation (Warm Persian Walnut Ink in Noto Nastaliq!)
    // -------------------------------------------------------------
    val urduPaint = TextPaint().apply {
      color = Color.parseColor("#6E200A") // Deep warm walnut ink
      textSize = when {
        dhikr.translationUrdu.length < 50 -> 36f
        dhikr.translationUrdu.length < 100 -> 31f
        else -> 27f
      }
      typeface = getUrduTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

    val urduLayout = createCenteredLayout(dhikr.translationUrdu, urduPaint, contentWidth - 20, 1.36f)
    canvas.save()
    canvas.translate(cx, currentY)
    urduLayout.draw(canvas)
    canvas.restore()

    currentY += urduLayout.height + 14f

    // -------------------------------------------------------------
    // F. English Translation (Clean Serif in Slate)
    // -------------------------------------------------------------
    if (dhikr.translation.isNotBlank()) {
      val engPaint = TextPaint().apply {
        color = Color.parseColor("#334155")
        textSize = 20f
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      val engLayout = createCenteredLayout(dhikr.translation, engPaint, contentWidth - 40, 1.2f)
      canvas.save()
      canvas.translate(cx, currentY)
      engLayout.draw(canvas)
      canvas.restore()

      currentY += engLayout.height + 16f
    }

    // -------------------------------------------------------------
    // G. Source Citation Pill Ribbon
    // -------------------------------------------------------------
    val sourceLabel = "📖 ${dhikr.source}"
    val sourcePaint = TextPaint().apply {
      color = Color.parseColor("#8A5A1A")
      textSize = 22f
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      isAntiAlias = true
    }
    val sourceTextWidth = sourcePaint.measureText(sourceLabel)
    val sourcePillWidth = (sourceTextWidth + 44f).coerceAtLeast(220f)
    val sourcePillRect = RectF(cx - sourcePillWidth / 2f, currentY, cx + sourcePillWidth / 2f, currentY + 40f)
    drawCardPill(canvas, sourcePillRect, sourceLabel, Typeface.create(Typeface.SERIF, Typeface.BOLD), 21f, Color.parseColor("#8A5A1A"), Color.parseColor("#FAF3E2"), Color.parseColor("#D4AF37"))

    currentY += 52f

    // -------------------------------------------------------------
    // H. "سبق و تدبر" Contemplative Wisdom Card (Auto-fitted, proportional)
    // -------------------------------------------------------------
    val noteText = if (dhikr.contemplativeNote.isNotBlank()) {
      dhikr.contemplativeNote
    } else {
      "رسول اللہ ﷺ کے سنہری ارشادات جو زندگی کو روشن کرتے ہیں۔ پندرہ سیکنڈ سکون کے ساتھ یادِ الٰہی میں گزاریں۔"
    }

    val notePaint = TextPaint().apply {
      color = Color.parseColor("#4A3B2C")
      textSize = 23f
      typeface = getUrduTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

    val fullNote = "سبق و تدبر: $noteText"
    val tadabburWidth = cardRect.width() - 50f
    val noteLayout = createCenteredLayout(fullNote, notePaint, (tadabburWidth - 40f).toInt(), 1.35f)

    val maxTadabburHeight = (cardRect.bottom - currentY - 14f).coerceAtLeast(80f)
    val tadabburHeight = (noteLayout.height + 40f).coerceIn(80f, maxTadabburHeight)
    val tadabburRect = RectF(cardRect.left + 25f, currentY, cardRect.right - 25f, currentY + tadabburHeight)

    val tadabburBg = Paint().apply {
      color = Color.parseColor("#FFFDF5")
      isAntiAlias = true
    }
    val tadabburBorder = Paint().apply {
      color = Color.parseColor("#EADBB6")
      style = Paint.Style.STROKE
      strokeWidth = 1.3f
      isAntiAlias = true
    }
    canvas.drawRoundRect(tadabburRect, 16f, 16f, tadabburBg)
    canvas.drawRoundRect(tadabburRect, 16f, 16f, tadabburBorder)

    // Quotation mark
    val quotePaint = TextPaint().apply {
      color = Color.parseColor("#C5A059")
      textSize = 34f
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      isAntiAlias = true
    }
    canvas.drawText("❝", tadabburRect.left + 14f, tadabburRect.top + 34f, quotePaint)

    val noteY = tadabburRect.centerY() - (noteLayout.height / 2f)
    canvas.save()
    canvas.translate(cx, noteY)
    noteLayout.draw(canvas)
    canvas.restore()
  }

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
      strokeWidth = 1.2f
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
    val pillWidth = textWidth + 38f
    val pillHeight = fontSize + 18f
    val rect = RectF(cx - pillWidth / 2f, cy - pillHeight / 2f, cx + pillWidth / 2f, cy + pillHeight / 2f)

    drawCardPill(canvas, rect, text, typeface, fontSize, textColor, bgColor, borderColor)
  }

  private fun drawRoyalFooterBranding(canvas: Canvas, width: Int, height: Int) {
    val cx = width / 2f
    val footerTop = height - 98f

    // Divider line with gold fade
    val divPaint = Paint().apply {
      color = Color.parseColor("#55D4AF37")
      strokeWidth = 1.2f
      isAntiAlias = true
    }
    canvas.drawLine(100f, footerTop, width - 100f, footerTop, divPaint)

    val starPaint = TextPaint().apply {
      color = Color.parseColor("#C5A059")
      textSize = 15f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("✦", cx, footerTop + 4f, starPaint)

    // Tagline with "15 SECONDS 4 ALLAH"
    val footerTagline = TextPaint().apply {
      color = Color.parseColor("#8A5A1A")
      textSize = 21f
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      letterSpacing = 0.04f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("✦ 15 SECONDS 4 ALLAH • پندرہ سیکنڈ اللہ کے لیے ✦", cx, footerTop + 30f, footerTagline)

    val hashPaint = TextPaint().apply {
      color = Color.parseColor("#996515")
      textSize = 18f
      typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("Facebook & WhatsApp Channel Verified • #15Seconds4Allah", cx, footerTop + 56f, hashPaint)
  }

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
