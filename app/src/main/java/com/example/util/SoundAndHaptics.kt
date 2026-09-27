package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundAndHaptics(private val context: Context) {

  private var toneGen: ToneGenerator? = null

  init {
    try {
      toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70)
    } catch (_: Exception) {
      // Audio stream may fail in restricted sandboxes; fallback gracefully
    }
  }

  fun playBeep() {
    try {
      toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 220)
    } catch (_: Exception) {
    }
  }

  fun playChime() {
    try {
      toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
    } catch (_: Exception) {
    }
  }

  fun playSoftTick() {
    try {
      toneGen?.startTone(ToneGenerator.TONE_PROP_PROMPT, 60)
    } catch (_: Exception) {
    }
  }

  fun triggerHapticFeedback() {
    try {
      val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(45)
      }
    } catch (_: Exception) {
    }
  }

  fun triggerCelebrationHaptic() {
    try {
      val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pattern = longArrayOf(0, 60, 80, 100)
        val amplitudes = intArrayOf(0, 150, 0, 255)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(150)
      }
    } catch (_: Exception) {
    }
  }
}
