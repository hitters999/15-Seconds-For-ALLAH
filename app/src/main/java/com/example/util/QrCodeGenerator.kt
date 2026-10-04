package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

/**
 * Pure-Kotlin ISO/IEC 18004 Standard QR Code Generator (Byte Mode, ECC Level L).
 * Generates 100% camera-scannable QR Codes (Google Lens, Android Camera, iOS Camera, WhatsApp)
 * with zero external dependencies.
 */
object QrCodeGenerator {

  // Version capacities (Byte mode, ECC Level L):
  // Version 3 (29x29): total 70 codewords -> 55 data bytes, 15 ECC bytes (1 block)
  // Version 4 (33x33): total 100 codewords -> 80 data bytes, 20 ECC bytes (1 block)
  private data class QrVersionSpec(
    val version: Int,
    val size: Int,
    val dataCodewords: Int,
    val eccCodewords: Int,
    val alignCenter: Int
  )

  private val VERSION_3 = QrVersionSpec(version = 3, size = 29, dataCodewords = 55, eccCodewords = 15, alignCenter = 22)
  private val VERSION_4 = QrVersionSpec(version = 4, size = 33, dataCodewords = 80, eccCodewords = 20, alignCenter = 26)

  fun encodeToMatrix(text: String): Array<BooleanArray> {
    val utf8 = text.toByteArray(Charsets.UTF_8)
    val spec = if (utf8.size <= 53) VERSION_3 else VERSION_4
    val payload = utf8.take(spec.dataCodewords - 2).toByteArray()

    // 1. Build bit stream: Mode 0100 (4 bits) + Length (8 bits) + Payload + Terminator
    val bits = ArrayList<Int>(spec.dataCodewords * 8)
    fun appendBits(value: Int, count: Int) {
      for (i in count - 1 downTo 0) {
        bits.add((value ushr i) and 1)
      }
    }

    appendBits(0b0100, 4) // Byte mode
    appendBits(payload.size, 8)
    for (b in payload) {
      appendBits(b.toInt() and 0xFF, 8)
    }

    // Terminator up to 4 zeros
    val maxDataBits = spec.dataCodewords * 8
    val termCount = minOf(4, maxDataBits - bits.size)
    appendBits(0, termCount)

    // Pad to byte boundary
    while (bits.size % 8 != 0) {
      bits.add(0)
    }

    // Pad codewords 0xEC, 0x11
    var padToggle = true
    while (bits.size < maxDataBits) {
      appendBits(if (padToggle) 0xEC else 0x11, 8)
      padToggle = !padToggle
    }

    val dataBytes = IntArray(spec.dataCodewords) { idx ->
      var v = 0
      for (b in 0 until 8) {
        v = (v shl 1) or bits[idx * 8 + b]
      }
      v
    }

    // 2. Compute Reed-Solomon ECC bytes
    val eccBytes = computeReedSolomon(dataBytes, spec.eccCodewords)
    val allCodewords = IntArray(spec.dataCodewords + spec.eccCodewords)
    System.arraycopy(dataBytes, 0, allCodewords, 0, spec.dataCodewords)
    System.arraycopy(eccBytes, 0, allCodewords, spec.dataCodewords, spec.eccCodewords)

    // 3. Initialize grid and function patterns
    val n = spec.size
    val modules = Array(n) { BooleanArray(n) }
    val isFunction = Array(n) { BooleanArray(n) }

    fun setFunctionModule(r: Int, c: Int, dark: Boolean) {
      if (r in 0 until n && c in 0 until n) {
        modules[r][c] = dark
        isFunction[r][c] = true
      }
    }

    fun drawFinder(topR: Int, leftC: Int) {
      for (dr in -1..7) {
        for (dc in -1..7) {
          val r = topR + dr
          val c = leftC + dc
          if (r in 0 until n && c in 0 until n) {
            val inOuter = dr in 0..6 && dc in 0..6 && (dr == 0 || dr == 6 || dc == 0 || dc == 6)
            val inInner = dr in 2..4 && dc in 2..4
            setFunctionModule(r, c, inOuter || inInner)
          }
        }
      }
    }

    drawFinder(0, 0)
    drawFinder(0, n - 7)
    drawFinder(n - 7, 0)

    // Timing patterns
    for (i in 8 until n - 8) {
      setFunctionModule(6, i, i % 2 == 0)
      setFunctionModule(i, 6, i % 2 == 0)
    }

    // Alignment pattern (5x5 centered at alignCenter)
    val ac = spec.alignCenter
    for (dr in -2..2) {
      for (dc in -2..2) {
        val dist = maxOf(kotlin.math.abs(dr), kotlin.math.abs(dc))
        setFunctionModule(ac + dr, ac + dc, dist != 1)
      }
    }

    // Dark module
    setFunctionModule(n - 8, 8, true)

    // Reserve format information areas
    for (i in 0..8) {
      if (!isFunction[8][i]) isFunction[8][i] = true
      if (!isFunction[i][8]) isFunction[i][8] = true
    }
    for (i in 0..7) {
      if (!isFunction[8][n - 1 - i]) isFunction[8][n - 1 - i] = true
      if (!isFunction[n - 1 - i][8]) isFunction[n - 1 - i][8] = true
    }

    // 4. Place data + ECC bits in zigzag
    val totalBits = allCodewords.size * 8
    var bitIdx = 0
    var upward = true
    var col = n - 1
    while (col > 0) {
      if (col == 6) col-- // Skip vertical timing pattern
      for (step in 0 until n) {
        val row = if (upward) (n - 1 - step) else step
        for (dc in 0..1) {
          val c = col - dc
          if (!isFunction[row][c]) {
            val bit = if (bitIdx < totalBits) {
              ((allCodewords[bitIdx ushr 3] ushr (7 - (bitIdx and 7))) and 1) == 1
            } else {
              false
            }
            // Apply Mask 0: (row + c) % 2 == 0
            val masked = if ((row + c) % 2 == 0) !bit else bit
            modules[row][c] = masked
            bitIdx++
          }
        }
      }
      upward = !upward
      col -= 2
    }

    // 5. Write Format Information for ECC Level L (01) + Mask 0 (000) -> 0x77C4
    val formatBits = 0x77C4
    fun getFormatBit(i: Int): Boolean = ((formatBits ushr i) and 1) == 1

    // Top-left format bits
    for (i in 0..5) modules[8][i] = getFormatBit(14 - i)
    modules[8][7] = getFormatBit(8)
    modules[8][8] = getFormatBit(7)
    modules[7][8] = getFormatBit(6)
    for (i in 0..5) modules[5 - i][8] = getFormatBit(5 - i)

    // Top-right & Bottom-left format bits
    for (i in 0..7) modules[8][n - 1 - i] = getFormatBit(i)
    for (i in 0..6) modules[n - 7 + i][8] = getFormatBit(14 - i)

    return modules
  }

  /**
   * Draws the QR Code inside a crisp white rounded box on the given Canvas so cameras can scan it effortlessly.
   */
  fun drawQrCodeOnCanvas(
    canvas: Canvas,
    url: String,
    left: Float,
    top: Float,
    boxSize: Float
  ) {
    val matrix = encodeToMatrix(url)
    val n = matrix.size
    val quietZone = 3
    val totalModules = n + quietZone * 2
    val cellSize = boxSize / totalModules

    val bgPaint = Paint().apply {
      color = Color.WHITE
      isAntiAlias = true
    }
    val borderPaint = Paint().apply {
      color = Color.parseColor("#E5C07B")
      style = Paint.Style.STROKE
      strokeWidth = 3f
      isAntiAlias = true
    }
    val rect = RectF(left, top, left + boxSize, top + boxSize)
    canvas.drawRoundRect(rect, 16f, 16f, bgPaint)
    canvas.drawRoundRect(rect, 16f, 16f, borderPaint)

    val darkPaint = Paint().apply {
      color = Color.parseColor("#041E18")
      style = Paint.Style.FILL
      isAntiAlias = false
    }

    for (r in 0 until n) {
      for (c in 0 until n) {
        if (matrix[r][c]) {
          val x0 = left + (c + quietZone) * cellSize
          val y0 = top + (r + quietZone) * cellSize
          canvas.drawRect(x0, y0, x0 + cellSize + 0.4f, y0 + cellSize + 0.4f, darkPaint)
        }
      }
    }
  }

  private fun computeReedSolomon(data: IntArray, eccCount: Int): IntArray {
    // Build generator polynomial for eccCount roots: (x - 2^0)(x - 2^1)...(x - 2^(eccCount-1))
    var gen = intArrayOf(1)
    var root = 1
    for (i in 0 until eccCount) {
      val next = IntArray(gen.size + 1)
      for (j in gen.indices) {
        next[j] = next[j] xor gen[j]
        next[j + 1] = next[j + 1] xor gfMul(gen[j], root)
      }
      gen = next
      root = gfMul(root, 2)
    }

    val rem = IntArray(eccCount)
    for (b in data) {
      val factor = b xor rem[0]
      System.arraycopy(rem, 1, rem, 0, eccCount - 1)
      rem[eccCount - 1] = 0
      for (j in 0 until eccCount) {
        rem[j] = rem[j] xor gfMul(gen[j + 1], factor)
      }
    }
    return rem
  }

  private fun gfMul(x: Int, y: Int): Int {
    var a = x
    var b = y
    var res = 0
    while (b > 0) {
      if ((b and 1) != 0) res = res xor a
      b = b ushr 1
      a = a shl 1
      if ((a and 0x100) != 0) a = a xor 0x11D
    }
    return res
  }
}
