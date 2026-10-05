package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

typealias ViralPosterGenerator = PosterGenerator

object PosterGenerator {

    private const val POSTER_WIDTH = 1080
    private const val MIN_POSTER_HEIGHT = 1080
    private const val MAX_POSTER_HEIGHT = 6500

    // Viral Color Palette
    private val COLOR_BG_CENTER = Color.parseColor("#113026") // Deep Emerald
    private val COLOR_BG_EDGE = Color.parseColor("#05100D")   // Near Black
    private val COLOR_GOLD_ACCENT = Color.parseColor("#E5C07B") // Premium Gold
    private val COLOR_GOLD_BRIGHT = Color.parseColor("#FDE68A")
    private val COLOR_TEXT_WHITE = Color.parseColor("#F8F9FA")
    private val COLOR_TEXT_MUTED = Color.parseColor("#CBD5E1")
    private val COLOR_GLASS_CARD = Color.parseColor("#22FFFFFF")
    private val COLOR_HOOK_BG = Color.parseColor("#142E26")

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
                ?: Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        } catch (_: Exception) {
            Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
    }

    /**
     * Auto-Sizing Dynamic Poster Generator:
     * Automatically adjusts font size for readability AND dynamically expands poster height
     * from 1080px up to 3600px so even the longest Sahih Bukhari / Muslim Hadiths never cut off
     * or overlap with the footer!
     */
    fun generateDhikrPosterBitmap(context: Context, dhikr: DhikrItem): Bitmap {
        val cx = POSTER_WIDTH / 2f
        val contentWidth = (POSTER_WIDTH - 124).coerceAtLeast(800)
        val layoutLeft = cx - (contentWidth / 2f)

        val arabicText = dhikr.arabic.trim()
        val urduText = dhikr.translationUrdu.trim().ifBlank { dhikr.translation.trim() }

        // 1. Smart Auto-Font Size Selection based on character length (Never too tiny, never cut off)
        val totalChars = arabicText.length + urduText.length
        var arabicSize = when {
            arabicText.length < 70 -> 64f
            arabicText.length < 160 -> 52f
            arabicText.length < 320 -> 42f
            arabicText.length < 600 -> 35f
            arabicText.length < 1000 -> 30f
            else -> 26f
        }

        var urduSize = when {
            urduText.length < 90 -> 34f
            urduText.length < 200 -> 30f
            urduText.length < 400 -> 26f
            urduText.length < 800 -> 23f
            else -> 20.5f
        }

        val arabicPaint = TextPaint().apply {
            color = COLOR_GOLD_ACCENT
            typeface = getArabicTypeface(context)
            textAlign = Paint.Align.LEFT
            isFakeBoldText = true
            isAntiAlias = true
            setShadowLayer(8f, 0f, 4f, Color.parseColor("#55000000"))
        }

        val urduPaint = TextPaint().apply {
            color = COLOR_TEXT_WHITE
            typeface = getUrduTypeface(context)
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
            setShadowLayer(4f, 0f, 2f, Color.parseColor("#60000000"))
        }

        var arabicLayout: StaticLayout
        var urduLayout: StaticLayout

        // Fine-tune font size if total height is slightly over 1080px so short/medium items fit cleanly,
        // while allowing long Hadiths to expand the poster height naturally with readable text!
        while (true) {
            arabicPaint.textSize = arabicSize
            urduPaint.textSize = urduSize
            arabicLayout = createCenteredLayout(arabicText, arabicPaint, contentWidth, 1.32f)
            urduLayout = createCenteredLayout(urduText, urduPaint, contentWidth, 1.40f)

            val bismillahExtra = if (dhikr.isQuranic) 56 else 0
            val estimatedTotalHeight = 205 + bismillahExtra + arabicLayout.height + 32 + urduLayout.height + 34 + 62 + 36 + 260

            // If it's a medium text that almost fits in 1080 or 1350, shrink slightly down to readable min sizes
            val minReadableArabic = if (totalChars > 500) 26f else 32f
            val minReadableUrdu = if (totalChars > 500) 20f else 23f

            if (estimatedTotalHeight <= MIN_POSTER_HEIGHT || (arabicSize <= minReadableArabic && urduSize <= minReadableUrdu)) {
                break
            }
            if (arabicSize > minReadableArabic) arabicSize -= 2f
            if (urduSize > minReadableUrdu) urduSize -= 1f
        }

        // 2. Calculate Exact Required Dynamic Poster Height (Auto-Expand!)
        val bismillahSpace = if (dhikr.isQuranic) 56 else 0
        val requiredHeight = (
            205 +
            bismillahSpace +
            arabicLayout.height +
            32 +
            urduLayout.height +
            34 +
            62 + // Source reference pill height
            38 + // Margin before footer
            262  // Footer height + bottom frame padding
        ).coerceIn(MIN_POSTER_HEIGHT, MAX_POSTER_HEIGHT)

        val bitmap = Bitmap.createBitmap(POSTER_WIDTH, requiredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 3. Draw Background & Frame for Dynamic Height
        drawDarkPremiumBackground(canvas, POSTER_WIDTH, requiredHeight)

        // 4. Draw Top App Branding + Hook Header
        drawBrandedHookHeader(context, canvas, POSTER_WIDTH)

        // 5. Draw Full Uncut Central Content (Bismillah + Complete Arabic + Complete Urdu + Source Pill)
        var currentY = 205f

        if (dhikr.isQuranic) {
            val bismillahPaint = TextPaint().apply {
                color = COLOR_TEXT_MUTED
                textSize = 30f
                typeface = getArabicTypeface(context)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("۞ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۞", cx, currentY, bismillahPaint)
            currentY += 54f
        }

        // Draw Complete Arabic Layout
        canvas.save()
        canvas.translate(layoutLeft, currentY)
        arabicLayout.draw(canvas)
        canvas.restore()

        currentY += arabicLayout.height + 32f

        // Subtle Gold Divider Line between Arabic & Urdu
        val divPaint = Paint().apply {
            color = COLOR_GOLD_ACCENT
            strokeWidth = 1.5f
            alpha = 95
            isAntiAlias = true
        }
        canvas.drawLine(cx - 160f, currentY - 14f, cx + 160f, currentY - 14f, divPaint)

        // Draw Complete Urdu Layout
        canvas.save()
        canvas.translate(layoutLeft, currentY)
        urduLayout.draw(canvas)
        canvas.restore()

        currentY += urduLayout.height + 32f

        // Source Reference Pill (Positioned dynamically right below Urdu text, never overlapping!)
        val sourcePaint = TextPaint().apply {
            color = COLOR_BG_CENTER
            textSize = 22f
            typeface = getUrduTypeface(context)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val sourceText = "📖 حوالہ: ${dhikr.source}"
        val textWidth = sourcePaint.measureText(sourceText)
        val pillWidth = (textWidth + 68f).coerceAtMost(POSTER_WIDTH - 110f)

        val sourceBgPaint = Paint().apply {
            color = COLOR_GOLD_ACCENT
            isAntiAlias = true
        }
        val maxPillTop = requiredHeight - 262f - 62f
        val pillTop = currentY.coerceAtMost(maxPillTop)
        val sourceRect = RectF(cx - pillWidth / 2f, pillTop, cx + pillWidth / 2f, pillTop + 52f)
        canvas.drawRoundRect(sourceRect, 26f, 26f, sourceBgPaint)

        val textY = sourceRect.centerY() - ((sourcePaint.descent() + sourcePaint.ascent()) / 2f)
        canvas.drawText(sourceText, cx, textY, sourcePaint)

        // 6. Draw Bottom Viral Hook Box + QR Code at Dynamic Bottom
        drawViralDownloadHookFooter(context, canvas, POSTER_WIDTH, requiredHeight)

        return bitmap
    }

    fun generateDhikrPoster(context: Context, dhikr: DhikrItem): Uri? {
        return try {
            val bitmap = generateDhikrPosterBitmap(context, dhikr)
            saveBitmapToCache(context, bitmap, "viral_poster_${dhikr.id}")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateStreakPoster(
        context: Context,
        streakDays: Int,
        totalMoments: Int,
        score: Int,
        rank: String
    ): Uri? {
        return try {
            val bitmap = Bitmap.createBitmap(POSTER_WIDTH, MIN_POSTER_HEIGHT, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            drawDarkPremiumBackground(canvas, POSTER_WIDTH, MIN_POSTER_HEIGHT)
            drawBrandedHookHeader(context, canvas, POSTER_WIDTH)

            val cx = POSTER_WIDTH / 2f
            val pkrAmount = String.format(java.util.Locale.US, "%.2f", score * 0.01)

            // Glowing Gold Ring for Streak
            val circleCenterY = 340f
            val ringPaint = Paint().apply {
                color = COLOR_GOLD_ACCENT
                style = Paint.Style.STROKE
                strokeWidth = 4.5f
                isAntiAlias = true
            }
            canvas.drawCircle(cx, circleCenterY, 110f, ringPaint)

            val streakNumPaint = TextPaint().apply {
                color = COLOR_GOLD_ACCENT
                textSize = 84f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("$streakDays", cx, circleCenterY + 24f, streakNumPaint)

            val daysLabel = TextPaint().apply {
                color = COLOR_TEXT_WHITE
                textSize = 24f
                typeface = getUrduTypeface(context)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("مسلسل دن (Streak)", cx, circleCenterY + 70f, daysLabel)

            // Rank
            val rankPaint = TextPaint().apply {
                color = COLOR_GOLD_BRIGHT
                textSize = 30f
                typeface = getUrduTypeface(context)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("👑 $rank", cx, 515f, rankPaint)

            // Points & Hadya Balance Pill
            val statsText = "مکمل اذکار: $totalMoments  •  پوائنٹس: $score  •  ہدیہ والٹ: Rs. $pkrAmount"
            val statsPaint = TextPaint().apply {
                color = COLOR_BG_CENTER
                textSize = 24f
                typeface = getUrduTypeface(context)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val statsWidth = statsPaint.measureText(statsText)
            val pillW = (statsWidth + 70f).coerceAtMost(POSTER_WIDTH - 100f)
            val statsRect = RectF(cx - pillW / 2f, 565f, cx + pillW / 2f, 635f)
            val statsBgPaint = Paint().apply {
                color = COLOR_GOLD_ACCENT
                isAntiAlias = true
            }
            canvas.drawRoundRect(statsRect, 35f, 35f, statsBgPaint)
            val statsTextY = statsRect.centerY() - ((statsPaint.descent() + statsPaint.ascent()) / 2f)
            canvas.drawText(statsText, cx, statsTextY, statsPaint)

            drawViralDownloadHookFooter(context, canvas, POSTER_WIDTH, MIN_POSTER_HEIGHT)

            saveBitmapToCache(context, bitmap, "viral_streak_${streakDays}")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // =========================================================================
    // Canvas Rendering Methods
    // =========================================================================

    private fun drawDarkPremiumBackground(canvas: Canvas, width: Int, height: Int) {
        val bgPaint = Paint().apply {
            shader = RadialGradient(
                width / 2f, height / 2.4f, maxOf(width, height) * 0.78f,
                intArrayOf(COLOR_BG_CENTER, COLOR_BG_EDGE),
                null,
                Shader.TileMode.CLAMP
            )
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Subtle outer border frame
        val framePaint = Paint().apply {
            color = COLOR_GOLD_ACCENT
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = 90
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(28f, 28f, width - 28f, height - 28f), 28f, 28f, framePaint)

        // Top and bottom accent lines
        val linePaint = Paint().apply {
            color = COLOR_GOLD_ACCENT
            strokeWidth = 3f
            alpha = 180
            isAntiAlias = true
        }
        canvas.drawLine(70f, 44f, width - 70f, 44f, linePaint)
        canvas.drawLine(70f, height - 44f, width - 70f, height - 44f, linePaint)
    }

    private fun drawBrandedHookHeader(context: Context, canvas: Canvas, width: Int) {
        val cx = width / 2f

        // Glass pill for App Logo + Brand Name
        val pillRect = RectF(cx - 285f, 58f, cx + 285f, 134f)
        val pillPaint = Paint().apply {
            color = COLOR_GLASS_CARD
            isAntiAlias = true
        }
        val pillBorder = Paint().apply {
            color = COLOR_GOLD_ACCENT
            style = Paint.Style.STROKE
            strokeWidth = 1.8f
            alpha = 200
            isAntiAlias = true
        }
        canvas.drawRoundRect(pillRect, 38f, 38f, pillPaint)
        canvas.drawRoundRect(pillRect, 38f, 38f, pillBorder)

        // Draw App Brand Logo inside the left side of the pill
        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_fg_img)
            if (logoBitmap != null) {
                val logoSize = 66f
                val logoX = pillRect.left + 44f
                val logoY = pillRect.centerY() - logoSize / 2f
                val srcRect = android.graphics.Rect(0, 0, logoBitmap.width, logoBitmap.height)
                val dstRect = RectF(logoX - logoSize / 2f, logoY, logoX + logoSize / 2f, logoY + logoSize)
                canvas.drawBitmap(logoBitmap, srcRect, dstRect, Paint(Paint.FILTER_BITMAP_FLAG))
            }
        } catch (_: Exception) {}

        val headerText = TextPaint().apply {
            color = COLOR_GOLD_ACCENT
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.08f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("15 SECONDS 4 ALLAH", cx + 26f, 105f, headerText)

        // Top Slogan & Hook Line
        val hookSubPaint = TextPaint().apply {
            color = COLOR_GOLD_BRIGHT
            textSize = 22f
            typeface = getUrduTypeface(context)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(
            "✨ ثواب بھی ، Rewards بھی  •  ہر 15 منٹ بعد خودکار ذکر پاپ اپ ✨",
            cx,
            165f,
            hookSubPaint
        )
    }

    /**
     * Viral Download & Hadya Hook Box at the bottom of the poster:
     * Explains the Points System, 1 Point = 1 Paisa Hadya System, and GET REWARDS button
     */
    private fun drawViralDownloadHookFooter(context: Context, canvas: Canvas, width: Int, height: Int) {
        val boxTop = height - 256f
        val boxBottom = height - 56f
        val hookRect = RectF(52f, boxTop, width - 52f, boxBottom)

        val boxBg = Paint().apply {
            color = COLOR_HOOK_BG
            isAntiAlias = true
        }
        val boxBorder = Paint().apply {
            color = COLOR_GOLD_ACCENT
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawRoundRect(hookRect, 24f, 24f, boxBg)
        canvas.drawRoundRect(hookRect, 24f, 24f, boxBorder)

        // Draw Scannable QR Code on the Right Side of the Footer Box
        val qrSize = 142f
        val qrLeft = hookRect.right - qrSize - 26f
        val qrTop = boxTop + 18f
        val portalUrl = "https://toolyfi.com/15-Seconds-For-ALLAH/"
        QrCodeGenerator.drawQrCodeOnCanvas(canvas, portalUrl, qrLeft, qrTop, qrSize)

        val qrLabelPaint = TextPaint().apply {
            color = COLOR_GOLD_BRIGHT
            textSize = 15.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("📱 SCAN FOR REWARDS", qrLeft + qrSize / 2f, qrTop + qrSize + 22f, qrLabelPaint)

        // Left Content Area Center X
        val leftAreaRight = qrLeft - 20f
        val leftCx = (hookRect.left + 20f + leftAreaRight) / 2f

        // Line 1: Slogan + Points & Hadya Hook
        val rewardHookPaint = TextPaint().apply {
            color = COLOR_GOLD_BRIGHT
            textSize = 21f
            typeface = getUrduTypeface(context)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(
            "✨ ثواب بھی ، Rewards بھی • ہدیہ والٹ (1 Point = 1 Paisa)",
            leftCx,
            boxTop + 46f,
            rewardHookPaint
        )

        // Line 2: Prominent "GET REWARDS" Button Pill inside Left Area
        val ctaHalfW = ((leftAreaRight - hookRect.left - 44f) / 2f).coerceAtMost(365f)
        val ctaRect = RectF(leftCx - ctaHalfW, boxTop + 70f, leftCx + ctaHalfW, boxTop + 126f)
        val ctaBg = Paint().apply {
            color = COLOR_GOLD_ACCENT
            isAntiAlias = true
        }
        canvas.drawRoundRect(ctaRect, 28f, 28f, ctaBg)

        val ctaTextPaint = TextPaint().apply {
            color = Color.parseColor("#041E18")
            textSize = 20.5f
            typeface = getUrduTypeface(context)
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
        val ctaTextY = ctaRect.centerY() - ((ctaTextPaint.descent() + ctaTextPaint.ascent()) / 2f)
        canvas.drawText(
            "🎁 GET REWARDS  •  15s Timer (Toolyfi.com)",
            leftCx,
            ctaTextY,
            ctaTextPaint
        )

        // Line 3: Official Full URL Signature
        val urlText = TextPaint().apply {
            color = COLOR_TEXT_MUTED
            textSize = 17f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.02f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(
            "🌐 https://toolyfi.com/15-Seconds-For-ALLAH/",
            leftCx,
            boxTop + 168f,
            urlText
        )
    }

    private fun createCenteredLayout(
        text: String,
        paint: TextPaint,
        maxWidth: Int,
        spacingMult: Float
    ): StaticLayout {
        paint.textAlign = Paint.Align.LEFT
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
            val cacheDir = File(context.cacheDir, "viral_posters")
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
