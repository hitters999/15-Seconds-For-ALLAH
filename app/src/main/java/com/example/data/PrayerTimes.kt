package com.example.data

data class PrayerTimeItem(
  val id: String,
  val nameEn: String,
  val nameAr: String,
  val nameUr: String,
  val time24: String, // "05:12"
  val time12: String, // "5:12 AM"
  val isNext: Boolean = false,
  val isPassed: Boolean = false
) {
  val nameUrdu: String get() = nameUr
  val nameEnglish: String get() = nameEn
}

data class PrayerTimesState(
  val items: List<PrayerTimeItem> = defaultPrayerItems(),
  val nextPrayer: PrayerTimeItem? = null,
  val countdownText: String = "",
  val locationName: String = "کراچی، پاکستان (طے شدہ)",
  val hijriDate: String = "16 ربیع الثانی 1448ھ",
  val gregorianDate: String = "",
  val isLoading: Boolean = false,
  val isGpsEnabled: Boolean = false,
  val errorMessage: String? = null
) {
  companion object {
    fun defaultPrayerItems(): List<PrayerTimeItem> = listOf(
      PrayerTimeItem("fajr", "Fajr", "الفجر", "فجر", "05:08", "5:08 AM"),
      PrayerTimeItem("sunrise", "Sunrise", "الشروق", "طلوعِ آفتاب", "06:22", "6:22 AM"),
      PrayerTimeItem("dhuhr", "Dhuhr", "الظهر", "ظہر", "12:20", "12:20 PM"),
      PrayerTimeItem("asr", "Asr", "العصر", "عصر", "15:42", "3:42 PM"),
      PrayerTimeItem("maghrib", "Maghrib", "المغرب", "مغرب", "18:18", "6:18 PM"),
      PrayerTimeItem("isha", "Isha", "العشاء", "عشاء", "19:32", "7:32 PM")
    )
  }
}
