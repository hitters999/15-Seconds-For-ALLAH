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
   * Generates a magazine-grade, ultra-luxurious Islamic poster (1080 x 1920)
   * matching the aesthetic of majestic mosque silhouettes, glowing lanterns,
   * authentic Quranic Arabic calligraphy and graceful Urdu Nastaliq script.
   */
  fun generateDhikrPosterBitmap(context: Context, dhikr: DhikrItem): Bitmap {
    val bitmap = Bitmap.createBitmap(POSTER_WIDTH, POSTER_HEIGHT, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 1. Draw Atmospheric Backdrop Image
    drawBackdrop(context, canvas, POSTER_WIDTH, POSTER_HEIGHT)

    // 2. Ornate Golden Outer & Inner Frames
    drawOrnateGoldenBorders(canvas, POSTER_WIDTH, POSTER_HEIGHT)

    // 3. Top Header Branding: "15 Seconds for Allah • ۱۵ سیکنڈز اللہ کے لیے"
    drawHeaderBranding(context, canvas, POSTER_WIDTH)

    // 4. Sacred Card Container (Central Framed Area)
    val cardRect = RectF(60f, 260f, POSTER_WIDTH - 60f, 1720f)
    drawSacredCardFrame(canvas, cardRect)

    // 5. Card Category & Badge: "✨ TODAY'S MOMENT • آج کا مسنون ذکر ✨"
    drawTopMomentBadge(canvas, cardRect.centerX(), cardRect.top + 45f, dhikr.category)

    // 6. Bismillah Calligraphy Cartouche
    var currentY = cardRect.top + 105f
    currentY = drawBismillahCartouche(context, canvas, cardRect.centerX(), currentY)

    // 7. Arabic Sacred Calligraphy (Amiri Quran Font)
    currentY = drawArabicText(context, canvas, dhikr.arabic, cardRect, currentY)

    // 8. Ornate Divider with Star Motif
    currentY = drawStarDivider(canvas, cardRect.centerX(), currentY)

    // 9. Urdu Translation (Noto Nastaliq Font)
    currentY = drawUrduTranslation(context, canvas, dhikr.translationUrdu, cardRect, currentY)

    // 10. English Translation
    if (dhikr.translation.isNotBlank() && currentY < 1520f) {
      currentY = drawEnglishTranslation(canvas, dhikr.translation, cardRect, currentY)
    }

    // 11. Source / Citation Pill Badge (Hadith / Surah)
    if (currentY < 1620f) {
      drawSourcePill(context, canvas, dhikr.source, cardRect.centerX(), currentY + 25f)
    }

    // 12. Bottom Footer Branding
    drawFooterBranding(canvas, POSTER_WIDTH, POSTER_HEIGHT)

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
   * Generates a spiritual streak poster with gold laurels, flaming crescent, and stats.
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

      // 1. Draw Atmospheric Backdrop Image
      drawBackdrop(context, canvas, POSTER_WIDTH, POSTER_HEIGHT)

      // 2. Ornate Golden Borders
      drawOrnateGoldenBorders(canvas, POSTER_WIDTH, POSTER_HEIGHT)

      // 3. Header Branding
      drawHeaderBranding(context, canvas, POSTER_WIDTH)

      // 4. Central Sacred Card
      val cardRect = RectF(60f, 260f, POSTER_WIDTH - 60f, 1720f)
      drawSacredCardFrame(canvas, cardRect)

      // Top Streak Badge
      drawTopMomentBadge(canvas, cardRect.centerX(), cardRect.top + 45f, "روحانی استقامت • SPIRITUAL STREAK")

      // Glowing Golden Circle with Streak Days
      val circleCenterY = cardRect.top + 260f
      val outerCirclePaint = Paint().apply {
        color = Color.parseColor("#33D4AF37")
        style = Paint.Style.FILL
        isAntiAlias = true
      }
      val glowShader = RadialGradient(
        cardRect.centerX(), circleCenterY, 180f,
        intArrayOf(Color.parseColor("#66D4AF37"), Color.parseColor("#150A2620"), Color.TRANSPARENT),
        floatArrayOf(0f, 0.7f, 1f),
        Shader.TileMode.CLAMP
      )
      val glowPaint = Paint().apply {
        shader = glowShader
        isAntiAlias = true
      }
      canvas.drawCircle(cardRect.centerX(), circleCenterY, 180f, glowPaint)
      canvas.drawCircle(cardRect.centerX(), circleCenterY, 130f, outerCirclePaint)

      val circleBorder = Paint().apply {
        color = Color.parseColor("#D4AF37")
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
      }
      canvas.drawCircle(cardRect.centerX(), circleCenterY, 130f, circleBorder)

      // Streak number
      val numPaint = TextPaint().apply {
        color = Color.parseColor("#FFFFFF")
        textSize = 100f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        setShadowLayer(16f, 0f, 4f, Color.parseColor("#FFD700"))
      }
      canvas.drawText("$streakDays", cardRect.centerX(), circleCenterY + 25f, numPaint)

      val daysLabel = TextPaint().apply {
        color = Color.parseColor("#F5DEB3")
        textSize = 28f
        typeface = getUrduTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("دن کی مسلسل استقامت (Days Streak)", cardRect.centerX(), circleCenterY + 75f, daysLabel)

      // Rank Badge
      val rankRect = RectF(cardRect.centerX() - 320f, circleCenterY + 165f, cardRect.centerX() + 320f, circleCenterY + 250f)
      val rankBg = Paint().apply {
        shader = LinearGradient(
          rankRect.left, rankRect.top, rankRect.right, rankRect.bottom,
          intArrayOf(Color.parseColor("#B8863B"), Color.parseColor("#E5C388"), Color.parseColor("#996515")),
          null, Shader.TileMode.CLAMP
        )
        isAntiAlias = true
      }
      canvas.drawRoundRect(rankRect, 24f, 24f, rankBg)

      val rankBorder = Paint().apply {
        color = Color.parseColor("#FFF8DC")
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
      canvas.drawText("👑 $rank", cardRect.centerX(), circleCenterY + 218f, rankText)

      // Stats Cards Row: Moments & Score
      val statY = circleCenterY + 290f
      val stat1 = RectF(cardRect.left + 50f, statY, cardRect.centerX() - 20f, statY + 160f)
      val stat2 = RectF(cardRect.centerX() + 20f, statY, cardRect.right - 50f, statY + 160f)

      val statBg = Paint().apply {
        color = Color.parseColor("#CC082620")
        isAntiAlias = true
      }
      val statBorder = Paint().apply {
        color = Color.parseColor("#55D4AF37")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        isAntiAlias = true
      }
      canvas.drawRoundRect(stat1, 20f, 20f, statBg)
      canvas.drawRoundRect(stat1, 20f, 20f, statBorder)
      canvas.drawRoundRect(stat2, 20f, 20f, statBg)
      canvas.drawRoundRect(stat2, 20f, 20f, statBorder)

      val statValPaint = TextPaint().apply {
        color = Color.parseColor("#FFD700")
        textSize = 46f
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      val statSubPaint = TextPaint().apply {
        color = Color.parseColor("#E2E8F0")
        textSize = 24f
        typeface = getUrduTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }

      canvas.drawText("$totalMoments", stat1.centerX(), stat1.top + 70f, statValPaint)
      canvas.drawText("مکمل اذکار (Moments)", stat1.centerX(), stat1.top + 120f, statSubPaint)

      canvas.drawText("$score", stat2.centerX(), stat2.top + 70f, statValPaint)
      canvas.drawText("روحانی حسنات (Score)", stat2.centerX(), stat2.top + 120f, statSubPaint)

      // Sacred Prophetic Quote
      val quoteBoxY = statY + 200f
      val quoteRect = RectF(cardRect.left + 40f, quoteBoxY, cardRect.right - 40f, quoteBoxY + 220f)
      val quoteBg = Paint().apply {
        color = Color.parseColor("#990B2F28")
        isAntiAlias = true
      }
      canvas.drawRoundRect(quoteRect, 20f, 20f, quoteBg)
      canvas.drawRoundRect(quoteRect, 20f, 20f, statBorder)

      val arabicQuotePaint = TextPaint().apply {
        color = Color.parseColor("#FFFFFF")
        textSize = 38f
        typeface = getArabicTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ", cardRect.centerX(), quoteBoxY + 70f, arabicQuotePaint)

      val urduQuotePaint = TextPaint().apply {
        color = Color.parseColor("#F5DEB3")
        textSize = 30f
        typeface = getUrduTypeface(context)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
      }
      canvas.drawText("\"اللہ کے نزدیک سب سے پسندیدہ عمل وہ ہے جو ہمیشہ کیا جائے چاہے تھوڑا ہو۔\"", cardRect.centerX(), quoteBoxY + 145f, urduQuotePaint)

      // Footer
      drawFooterBranding(canvas, POSTER_WIDTH, POSTER_HEIGHT)

      saveBitmapToCache(context, bitmap, "streak_${streakDays}days")
    } catch (_: Exception) {
      null
    }
  }

  // -------------------------------------------------------------
  // DRAWING HELPER FUNCTIONS
  // -------------------------------------------------------------

  private fun drawBackdrop(context: Context, canvas: Canvas, width: Int, height: Int) {
    try {
      val options = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
      val bgBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_poster_bg, options)
      if (bgBitmap != null) {
        val src = Rect(0, 0, bgBitmap.width, bgBitmap.height)
        val dst = Rect(0, 0, width, height)
        val p = Paint().apply {
          isFilterBitmap = true
          isDither = true
        }
        canvas.drawBitmap(bgBitmap, src, dst, p)
        bgBitmap.recycle()
      } else {
        drawFallbackGradient(canvas, width, height)
      }
    } catch (_: Exception) {
      drawFallbackGradient(canvas, width, height)
    }

    // Gentle dark vignette over the central area for optimal text legibility
    val centerVignette = Paint().apply {
      shader = LinearGradient(
        0f, 240f, 0f, height.toFloat() - 180f,
        intArrayOf(
          Color.parseColor("#44061A16"),
          Color.parseColor("#DD051A15"),
          Color.parseColor("#E6051713"),
          Color.parseColor("#44061A16")
        ),
        floatArrayOf(0f, 0.25f, 0.8f, 1f),
        Shader.TileMode.CLAMP
      )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), centerVignette)
  }

  private fun drawFallbackGradient(canvas: Canvas, width: Int, height: Int) {
    val bgPaint = Paint().apply {
      shader = LinearGradient(
        0f, 0f, 0f, height.toFloat(),
        intArrayOf(Color.parseColor("#061C18"), Color.parseColor("#0F3831"), Color.parseColor("#041210")),
        floatArrayOf(0f, 0.5f, 1f),
        Shader.TileMode.CLAMP
      )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
  }

  private fun drawOrnateGoldenBorders(canvas: Canvas, width: Int, height: Int) {
    val outerMargin = 30f
    val innerMargin = 44f

    // Outer rich gold line
    val outerGoldPaint = Paint().apply {
      color = Color.parseColor("#D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 3.5f
      isAntiAlias = true
    }
    canvas.drawRoundRect(
      RectF(outerMargin, outerMargin, width - outerMargin, height - outerMargin),
      34f, 34f, outerGoldPaint
    )

    // Inner hairline gold line
    val innerGoldPaint = Paint().apply {
      color = Color.parseColor("#8C6D23")
      style = Paint.Style.STROKE
      strokeWidth = 1.5f
      isAntiAlias = true
    }
    canvas.drawRoundRect(
      RectF(innerMargin, innerMargin, width - innerMargin, height - innerMargin),
      26f, 26f, innerGoldPaint
    )

    // 4 Corner Filigree Diamonds / Stars
    drawCornerStar(canvas, outerMargin + 14f, outerMargin + 14f)
    drawCornerStar(canvas, width - outerMargin - 14f, outerMargin + 14f)
    drawCornerStar(canvas, outerMargin + 14f, height - outerMargin - 14f)
    drawCornerStar(canvas, width - outerMargin - 14f, height - outerMargin - 14f)
  }

  private fun drawCornerStar(canvas: Canvas, cx: Float, cy: Float) {
    val starPaint = Paint().apply {
      color = Color.parseColor("#E5C388")
      style = Paint.Style.FILL
      isAntiAlias = true
    }
    val path = Path().apply {
      moveTo(cx, cy - 10f)
      lineTo(cx + 3f, cy - 3f)
      lineTo(cx + 10f, cy)
      lineTo(cx + 3f, cy + 3f)
      lineTo(cx, cy + 10f)
      lineTo(cx - 3f, cy + 3f)
      lineTo(cx - 10f, cy)
      lineTo(cx - 3f, cy - 3f)
      close()
    }
    canvas.drawPath(path, starPaint)
  }

  private fun drawHeaderBranding(context: Context, canvas: Canvas, width: Int) {
    val cx = width / 2f

    // Top Golden Crescent / Emblem icon
    val iconPaint = Paint().apply {
      color = Color.parseColor("#F5DEB3")
      style = Paint.Style.FILL
      isAntiAlias = true
    }
    val iconBorder = Paint().apply {
      color = Color.parseColor("#D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 2f
      isAntiAlias = true
    }
    canvas.drawCircle(cx, 105f, 26f, iconPaint.apply { color = Color.parseColor("#33D4AF37") })
    canvas.drawCircle(cx, 105f, 26f, iconBorder)

    val crescentTextPaint = TextPaint().apply {
      color = Color.parseColor("#FFD700")
      textSize = 28f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("🌙", cx, 114f, crescentTextPaint)

    // Main App Title (Gold Embossed)
    val titlePaint = TextPaint().apply {
      color = Color.parseColor("#FFF4D6")
      textSize = 40f
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
      setShadowLayer(8f, 0f, 2f, Color.parseColor("#AA996515"))
    }
    canvas.drawText("15 Seconds for Allah • ۱۵ سیکنڈز اللہ کے لیے", cx, 175f, titlePaint)

    // Subtitle
    val subPaint = TextPaint().apply {
      color = Color.parseColor("#C2D4B6")
      textSize = 24f
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("A Moment of Divine Remembrance • ہر گھنٹے بعد یادِ الٰہی", cx, 218f, subPaint)
  }

  private fun drawSacredCardFrame(canvas: Canvas, rect: RectF) {
    // Card background fill with rich dark emerald depth
    val cardFill = Paint().apply {
      shader = LinearGradient(
        rect.left, rect.top, rect.left, rect.bottom,
        intArrayOf(
          Color.parseColor("#EB08241F"),
          Color.parseColor("#F5051B16"),
          Color.parseColor("#F5051914"),
          Color.parseColor("#E6092620")
        ),
        floatArrayOf(0f, 0.35f, 0.8f, 1f),
        Shader.TileMode.CLAMP
      )
      isAntiAlias = true
    }
    canvas.drawRoundRect(rect, 34f, 34f, cardFill)

    // Outer Card Gold Stroke
    val cardStroke = Paint().apply {
      color = Color.parseColor("#D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 2.5f
      isAntiAlias = true
    }
    canvas.drawRoundRect(rect, 34f, 34f, cardStroke)

    // Inner delicate hairline border
    val innerStroke = Paint().apply {
      color = Color.parseColor("#44D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 1f
      isAntiAlias = true
    }
    canvas.drawRoundRect(
      RectF(rect.left + 10f, rect.top + 10f, rect.right - 10f, rect.bottom - 10f),
      28f, 28f, innerStroke
    )
  }

  private fun drawTopMomentBadge(canvas: Canvas, cx: Float, cy: Float, category: String) {
    val badgeWidth = 460f
    val badgeHeight = 52f
    val badgeRect = RectF(cx - badgeWidth / 2f, cy - badgeHeight / 2f, cx + badgeWidth / 2f, cy + badgeHeight / 2f)

    val badgeBg = Paint().apply {
      color = Color.parseColor("#40D4AF37")
      isAntiAlias = true
    }
    val badgeBorder = Paint().apply {
      color = Color.parseColor("#D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 1.5f
      isAntiAlias = true
    }
    canvas.drawRoundRect(badgeRect, 26f, 26f, badgeBg)
    canvas.drawRoundRect(badgeRect, 26f, 26f, badgeBorder)

    val badgeTextPaint = TextPaint().apply {
      color = Color.parseColor("#FFE8B2")
      textSize = 22f
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    val label = if (category.isNotBlank() && category != "General") "✨ $category ✨" else "✨ TODAY'S MOMENT • آج کا مسنون ذکر ✨"
    canvas.drawText(label, cx, cy + 7f, badgeTextPaint)
  }

  private fun drawBismillahCartouche(context: Context, canvas: Canvas, cx: Float, topY: Float): Float {
    val bismillahText = "۞ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۞"
    val bismillahPaint = TextPaint().apply {
      color = Color.parseColor("#FFDF88")
      textSize = 38f
      typeface = getArabicTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
      setShadowLayer(6f, 0f, 2f, Color.parseColor("#80D4AF37"))
    }
    canvas.drawText(bismillahText, cx, topY + 45f, bismillahPaint)
    return topY + 70f
  }

  private fun drawArabicText(
    context: Context,
    canvas: Canvas,
    arabic: String,
    cardRect: RectF,
    startY: Float
  ): Float {
    // Dynamic text size based on length
    val textSize = when {
      arabic.length > 250 -> 44f
      arabic.length > 150 -> 52f
      arabic.length > 80 -> 60f
      else -> 68f
    }

    val arabicPaint = TextPaint().apply {
      color = Color.parseColor("#FFFFFF")
      this.textSize = textSize
      typeface = getArabicTypeface(context)
      isAntiAlias = true
      setShadowLayer(14f, 0f, 3f, Color.parseColor("#80D4AF37"))
    }

    val contentWidth = (cardRect.width() - 140f).toInt()
    val arabicLayout = StaticLayout.Builder.obtain(arabic, 0, arabic.length, arabicPaint, contentWidth)
      .setAlignment(Layout.Alignment.ALIGN_CENTER)
      .setLineSpacing(16f, 1.35f)
      .setIncludePad(false)
      .build()

    canvas.save()
    canvas.translate(cardRect.left + 70f, startY + 25f)
    arabicLayout.draw(canvas)
    canvas.restore()

    return startY + 25f + arabicLayout.height + 35f
  }

  private fun drawStarDivider(canvas: Canvas, cx: Float, y: Float): Float {
    val linePaint = Paint().apply {
      color = Color.parseColor("#88D4AF37")
      strokeWidth = 1.5f
      isAntiAlias = true
    }

    val halfSpan = 220f
    canvas.drawLine(cx - halfSpan, y, cx - 40f, y, linePaint)
    canvas.drawLine(cx + 40f, y, cx + halfSpan, y, linePaint)

    val starPaint = TextPaint().apply {
      color = Color.parseColor("#FFD700")
      textSize = 28f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("✦ ❖ ✦", cx, y + 9f, starPaint)

    return y + 40f
  }

  private fun drawUrduTranslation(
    context: Context,
    canvas: Canvas,
    urduText: String,
    cardRect: RectF,
    startY: Float
  ): Float {
    val formattedUrdu = "\"$urduText\""
    val contentWidth = (cardRect.width() - 180f).toInt()

    val urduPaint = TextPaint().apply {
      color = Color.parseColor("#FFEFC2")
      textSize = if (urduText.length > 200) 34f else 40f
      typeface = getUrduTypeface(context)
      isAntiAlias = true
      setShadowLayer(4f, 0f, 2f, Color.parseColor("#66000000"))
    }

    val urduLayout = StaticLayout.Builder.obtain(formattedUrdu, 0, formattedUrdu.length, urduPaint, contentWidth)
      .setAlignment(Layout.Alignment.ALIGN_CENTER)
      .setLineSpacing(20f, 1.45f)
      .setIncludePad(false)
      .build()

    // Draw frosted decorative background cartouche for Urdu text
    val boxTop = startY
    val boxBottom = boxTop + urduLayout.height + 40f
    val boxRect = RectF(cardRect.left + 50f, boxTop, cardRect.right - 50f, boxBottom)

    val boxBg = Paint().apply {
      color = Color.parseColor("#50072B24")
      isAntiAlias = true
    }
    val boxBorder = Paint().apply {
      color = Color.parseColor("#44D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 1.2f
      isAntiAlias = true
    }
    canvas.drawRoundRect(boxRect, 22f, 22f, boxBg)
    canvas.drawRoundRect(boxRect, 22f, 22f, boxBorder)

    canvas.save()
    canvas.translate(cardRect.left + 90f, boxTop + 20f)
    urduLayout.draw(canvas)
    canvas.restore()

    return boxBottom + 30f
  }

  private fun drawEnglishTranslation(
    canvas: Canvas,
    english: String,
    cardRect: RectF,
    startY: Float
  ): Float {
    val engPaint = TextPaint().apply {
      color = Color.parseColor("#D4DCE8")
      textSize = if (english.length > 180) 24f else 28f
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
      isAntiAlias = true
    }

    val contentWidth = (cardRect.width() - 160f).toInt()
    val engLayout = StaticLayout.Builder.obtain(english, 0, english.length, engPaint, contentWidth)
      .setAlignment(Layout.Alignment.ALIGN_CENTER)
      .setLineSpacing(8f, 1.25f)
      .setIncludePad(false)
      .build()

    canvas.save()
    canvas.translate(cardRect.left + 80f, startY)
    engLayout.draw(canvas)
    canvas.restore()

    return startY + engLayout.height + 25f
  }

  private fun drawSourcePill(context: Context, canvas: Canvas, source: String, cx: Float, y: Float) {
    val label = "📖 $source"
    val sourcePaint = TextPaint().apply {
      color = Color.parseColor("#E5C388")
      textSize = 26f
      typeface = getUrduTypeface(context)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }

    val pillWidth = (sourcePaint.measureText(label) + 60f).coerceAtLeast(240f)
    val pillHeight = 48f
    val pillRect = RectF(cx - pillWidth / 2f, y - pillHeight / 2f, cx + pillWidth / 2f, y + pillHeight / 2f)

    val pillBg = Paint().apply {
      color = Color.parseColor("#44D4AF37")
      isAntiAlias = true
    }
    val pillBorder = Paint().apply {
      color = Color.parseColor("#D4AF37")
      style = Paint.Style.STROKE
      strokeWidth = 1.5f
      isAntiAlias = true
    }
    canvas.drawRoundRect(pillRect, 24f, 24f, pillBg)
    canvas.drawRoundRect(pillRect, 24f, 24f, pillBorder)

    canvas.drawText(label, cx, y + 8f, sourcePaint)
  }

  private fun drawFooterBranding(canvas: Canvas, width: Int, height: Int) {
    val cx = width / 2f
    val footerTop = height - 160f

    // Footer hairline divider
    val divPaint = Paint().apply {
      color = Color.parseColor("#44D4AF37")
      strokeWidth = 1f
      isAntiAlias = true
    }
    canvas.drawLine(160f, footerTop, width - 160f, footerTop, divPaint)

    // Star in middle of line
    val starPaint = TextPaint().apply {
      color = Color.parseColor("#D4AF37")
      textSize = 18f
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("✦", cx, footerTop + 6f, starPaint)

    // Tagline
    val footerTagline = TextPaint().apply {
      color = Color.parseColor("#C2D4B6")
      textSize = 26f
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("15 Seconds for Allah • ہر گھنٹے بعد ذکر و دعا کا تحفہ", cx, footerTop + 45f, footerTagline)

    val hashPaint = TextPaint().apply {
      color = Color.parseColor("#B8863B")
      textSize = 22f
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      textAlign = Paint.Align.CENTER
      isAntiAlias = true
    }
    canvas.drawText("#15SecondsForAllah  #Quran  #Hadith  #Dhikr", cx, footerTop + 85f, hashPaint)
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
