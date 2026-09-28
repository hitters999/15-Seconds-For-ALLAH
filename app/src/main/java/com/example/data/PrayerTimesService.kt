package com.example.data

import android.content.Context
import android.util.Log
import com.example.util.AppTimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object PrayerTimesService {

  private const val TAG = "PrayerTimesService"

  suspend fun fetchPrayerTimes(
    latitude: Double?,
    longitude: Double?,
    locationNameOverride: String? = null,
    timezoneId: String? = null
  ): PrayerTimesState = withContext(Dispatchers.IO) {
    val targetTz = AppTimeHelper.getEffectiveTimeZone(timezoneId)
    val tzOption = AppTimeHelper.getTimezoneOption(timezoneId)

    try {
      val now = System.currentTimeMillis() / 1000
      val urlString = if (latitude != null && longitude != null) {
        "https://api.aladhan.com/v1/timings/$now?latitude=$latitude&longitude=$longitude&method=1"
      } else {
        val city = tzOption.city
        val country = tzOption.country
        "https://api.aladhan.com/v1/timingsByCity?city=$city&country=$country&method=1"
      }

      val url = URL(urlString)
      val connection = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = 8000
        readTimeout = 8000
        setRequestProperty("Accept", "application/json")
        setRequestProperty("User-Agent", "15SecondsForAllah/1.0")
      }

      if (connection.responseCode == HttpURLConnection.HTTP_OK) {
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.readText()
        reader.close()

        val json = JSONObject(response)
        val data = json.optJSONObject("data")
        if (data != null) {
          val timings = data.getJSONObject("timings")
          val dateObj = data.optJSONObject("date")
          val hijriObj = dateObj?.optJSONObject("hijri")

          val fajr = cleanTimeString(timings.getString("Fajr"))
          val sunrise = cleanTimeString(timings.getString("Sunrise"))
          val dhuhr = cleanTimeString(timings.getString("Dhuhr"))
          val asr = cleanTimeString(timings.getString("Asr"))
          val maghrib = cleanTimeString(timings.getString("Maghrib"))
          val isha = cleanTimeString(timings.getString("Isha"))

          val hijriDay = hijriObj?.optString("day") ?: ""
          val hijriMonthAr = hijriObj?.optJSONObject("month")?.optString("ar")
            ?: hijriObj?.optJSONObject("month")?.optString("en") ?: ""
          val hijriYear = hijriObj?.optString("year") ?: ""
          val formattedHijri = if (hijriDay.isNotEmpty()) "$hijriDay $hijriMonthAr $hijriYear ھ" else "16 ربیع الثانی 1448ھ"

          val locationLabel = locationNameOverride
            ?: if (latitude != null) "موجودہ مقام (GPS)"
            else "${tzOption.urduName} (${tzOption.offsetLabel})"

          return@withContext computePrayerState(
            fajr = fajr,
            sunrise = sunrise,
            dhuhr = dhuhr,
            asr = asr,
            maghrib = maghrib,
            isha = isha,
            locationName = locationLabel,
            hijriDate = formattedHijri,
            isGps = latitude != null,
            timeZone = targetTz
          )
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error fetching prayer times: ${e.message}", e)
    }

    // Return sensible fallback if API network error
    val fallbackLabel = locationNameOverride ?: "${tzOption.urduName} (${tzOption.offsetLabel})"
    computeDefaultPrayerState(fallbackLabel, targetTz)
  }

  private fun cleanTimeString(raw: String): String {
    // raw might be "05:12 (PKT)" -> "05:12"
    val parts = raw.trim().split(" ")
    return parts[0]
  }

  fun formatTo12Hour(time24: String): String {
    return try {
      val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
      val sdf12 = SimpleDateFormat("h:mm a", Locale.getDefault())
      val date = sdf24.parse(time24)
      if (date != null) sdf12.format(date) else time24
    } catch (e: Exception) {
      time24
    }
  }

  fun computePrayerState(
    fajr: String,
    sunrise: String,
    dhuhr: String,
    asr: String,
    maghrib: String,
    isha: String,
    locationName: String,
    hijriDate: String,
    isGps: Boolean,
    timeZone: TimeZone = AppTimeHelper.getEffectiveTimeZone(null)
  ): PrayerTimesState {
    val calendar = Calendar.getInstance(timeZone)
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)
    val currentTotalMinutes = currentHour * 60 + currentMinute

    val rawItems = listOf(
      PrayerTimeItem("fajr", "Fajr", "الفجر", "فجر", fajr, formatTo12Hour(fajr)),
      PrayerTimeItem("sunrise", "Sunrise", "الشروق", "طلوعِ آفتاب", sunrise, formatTo12Hour(sunrise)),
      PrayerTimeItem("dhuhr", "Dhuhr", "الظهر", "ظہر", dhuhr, formatTo12Hour(dhuhr)),
      PrayerTimeItem("asr", "Asr", "العصر", "عصر", asr, formatTo12Hour(asr)),
      PrayerTimeItem("maghrib", "Maghrib", "المغرب", "مغرب", maghrib, formatTo12Hour(maghrib)),
      PrayerTimeItem("isha", "Isha", "العشاء", "عشاء", isha, formatTo12Hour(isha))
    )

    // Calculate next prayer
    var nextItem: PrayerTimeItem? = null
    var minutesDiff = Int.MAX_VALUE

    // Exclude sunrise from next prayer determination or include as reference
    val prayerOnlyItems = rawItems.filter { it.id != "sunrise" }

    for (item in prayerOnlyItems) {
      val parsedMinutes = parseMinutes(item.time24)
      if (parsedMinutes > currentTotalMinutes) {
        val diff = parsedMinutes - currentTotalMinutes
        if (diff < minutesDiff) {
          minutesDiff = diff
          nextItem = item
        }
      }
    }

    // If all prayers today passed, next is tomorrow's Fajr
    if (nextItem == null) {
      val fajrMinutes = parseMinutes(fajr)
      minutesDiff = (24 * 60 - currentTotalMinutes) + fajrMinutes
      nextItem = rawItems.first { it.id == "fajr" }
    }

    val countdownText = if (minutesDiff < 60) {
      "$minutesDiff منٹ باقی"
    } else {
      val h = minutesDiff / 60
      val m = minutesDiff % 60
      "${h}گھنٹے ${m}منٹ باقی"
    }

    val finalItems = rawItems.map { item ->
      val itemMin = parseMinutes(item.time24)
      item.copy(
        isNext = item.id == nextItem?.id,
        isPassed = itemMin <= currentTotalMinutes
      )
    }

    val gregorianFmt = SimpleDateFormat("dd MMMM yyyy", Locale("ur", "PK")).apply {
      this.timeZone = timeZone
    }
    val gregorianDate = gregorianFmt.format(Date())

    return PrayerTimesState(
      items = finalItems,
      nextPrayer = nextItem,
      countdownText = countdownText,
      locationName = locationName,
      hijriDate = hijriDate,
      gregorianDate = gregorianDate,
      isLoading = false,
      isGpsEnabled = isGps,
      errorMessage = null
    )
  }

  private fun parseMinutes(time24: String): Int {
    return try {
      val parts = time24.split(":")
      val h = parts[0].toInt()
      val m = parts[1].toInt()
      h * 60 + m
    } catch (e: Exception) {
      0
    }
  }

  fun computeDefaultPrayerState(
    locationName: String,
    timeZone: TimeZone = AppTimeHelper.getEffectiveTimeZone(null)
  ): PrayerTimesState {
    return computePrayerState(
      fajr = "05:08",
      sunrise = "06:22",
      dhuhr = "12:20",
      asr = "15:42",
      maghrib = "18:18",
      isha = "19:32",
      locationName = locationName,
      hijriDate = "16 ربیع الثانی 1448ھ",
      isGps = false,
      timeZone = timeZone
    )
  }
}

