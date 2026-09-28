package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class TimezoneOption(
  val id: String,
  val urduName: String,
  val englishName: String,
  val offsetLabel: String,
  val flagEmoji: String,
  val city: String,
  val country: String
)

object AppTimeHelper {

  val supportedTimezones = listOf(
    TimezoneOption(
      id = "Asia/Karachi",
      urduName = "پاکستان کا معیاری وقت (PKT)",
      englishName = "Pakistan (Karachi, Lahore, Islamabad)",
      offsetLabel = "UTC+05:00",
      flagEmoji = "🇵🇰",
      city = "Karachi",
      country = "Pakistan"
    ),
    TimezoneOption(
      id = "Asia/Riyadh",
      urduName = "سعودی عرب / مکہ مکرمہ (AST)",
      englishName = "Saudi Arabia (Makkah, Madinah, Riyadh)",
      offsetLabel = "UTC+03:00",
      flagEmoji = "🇸🇦",
      city = "Riyadh",
      country = "Saudi Arabia"
    ),
    TimezoneOption(
      id = "Asia/Dubai",
      urduName = "متحدہ عرب امارات / خلیج (GST)",
      englishName = "UAE / Gulf (Dubai, Abu Dhabi)",
      offsetLabel = "UTC+04:00",
      flagEmoji = "🇦🇪",
      city = "Dubai",
      country = "United Arab Emirates"
    ),
    TimezoneOption(
      id = "Europe/London",
      urduName = "برطانیہ (GMT / BST)",
      englishName = "United Kingdom (London)",
      offsetLabel = "UTC+00:00 / +01:00",
      flagEmoji = "🇬🇧",
      city = "London",
      country = "United Kingdom"
    ),
    TimezoneOption(
      id = "America/New_York",
      urduName = "امریکہ مشرقی (Eastern Time)",
      englishName = "USA / Canada Eastern (New York, Toronto)",
      offsetLabel = "UTC-05:00",
      flagEmoji = "🇺🇸",
      city = "New York",
      country = "United States"
    ),
    TimezoneOption(
      id = "America/Los_Angeles",
      urduName = "امریکہ مغربی (Pacific Time)",
      englishName = "USA Pacific (Los Angeles, SF)",
      offsetLabel = "UTC-08:00 / -07:00",
      flagEmoji = "🇺🇸",
      city = "Los Angeles",
      country = "United States"
    ),
    TimezoneOption(
      id = "SYSTEM_DEFAULT",
      urduName = "موبائل سسٹم کا وقت (Auto Device)",
      englishName = "Device System Timezone",
      offsetLabel = "Auto",
      flagEmoji = "📱",
      city = "Karachi",
      country = "Pakistan"
    )
  )

  /**
   * Resolves the effective java.util.TimeZone.
   * Defaults to Asia/Karachi (Pakistan) if unset or invalid,
   * guaranteeing the app is not distorted by US cloud emulator default clocks.
   */
  fun getEffectiveTimeZone(timezoneId: String?): TimeZone {
    if (timezoneId.isNullOrEmpty()) {
      return TimeZone.getTimeZone("Asia/Karachi")
    }
    if (timezoneId == "SYSTEM_DEFAULT") {
      return TimeZone.getDefault()
    }
    return try {
      TimeZone.getTimeZone(timezoneId)
    } catch (e: Exception) {
      TimeZone.getTimeZone("Asia/Karachi")
    }
  }

  fun getTimezoneOption(timezoneId: String?): TimezoneOption {
    val effectiveId = timezoneId ?: "Asia/Karachi"
    return supportedTimezones.find { it.id == effectiveId }
      ?: TimezoneOption(
        id = effectiveId,
        urduName = effectiveId,
        englishName = effectiveId,
        offsetLabel = "",
        flagEmoji = "🌐",
        city = "Karachi",
        country = "Pakistan"
      )
  }

  /**
   * Returns Dynamic Greeting based on the user's selected TimeZone.
   * Hours:
   * 04:00 - 11:59 -> Good morning • صبح بخیر
   * 12:00 - 16:29 -> Good afternoon • دوپہر بخیر
   * 16:30 - 19:59 -> Good evening • شام بخیر
   * 20:00 - 03:59 -> Good night • شب بخیر
   */
  fun getDynamicGreeting(timeZone: TimeZone): Pair<String, String> {
    val cal = Calendar.getInstance(timeZone)
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    val totalMinutes = hour * 60 + minute

    return when {
      // 4:00 AM (240 min) to 11:59 AM (719 min)
      totalMinutes in 240..719 -> Pair("صبح بخیر • Good morning", "السلام علیکم")
      // 12:00 PM (720 min) to 4:29 PM (989 min)
      totalMinutes in 720..989 -> Pair("دوپہر بخیر • Good afternoon", "السلام علیکم")
      // 4:30 PM (990 min) to 7:59 PM (1199 min)
      totalMinutes in 990..1199 -> Pair("شام بخیر • Good evening", "السلام علیکم")
      // 8:00 PM (1200 min) to 3:59 AM (239 min)
      else -> Pair("شب بخیر • Good night", "السلام علیکم")
    }
  }

  fun formatCurrentTime(timeZone: TimeZone): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
    sdf.timeZone = timeZone
    return sdf.format(Date())
  }

  fun formatCurrentDate(timeZone: TimeZone): String {
    val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH)
    sdf.timeZone = timeZone
    return sdf.format(Date())
  }

  fun getTodayDateKey(timeZone: TimeZone): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    sdf.timeZone = timeZone
    return sdf.format(Date())
  }
}
