package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundAndHaptics(private val context: Context) {

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  private var toneGenerator: ToneGenerator? = try {
    ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
  } catch (_: Exception) {
    null
  }

  fun playBeep() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 180)
    } catch (_: Exception) {}
  }

  fun playSoftTick() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 45)
    } catch (_: Exception) {}
  }

  fun playChime() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 350)
    } catch (_: Exception) {}
  }

  fun triggerHapticFeedback() {
    try {
      if (vibrator?.hasVibrator() == true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(35)
        }
      }
    } catch (_: Exception) {}
  }

  fun triggerCelebrationHaptic() {
    try {
      if (vibrator?.hasVibrator() == true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          val timings = longArrayOf(0, 70, 60, 100)
          val amplitudes = intArrayOf(0, 180, 0, 255)
          vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(180)
        }
      }
    } catch (_: Exception) {}
  }
}
