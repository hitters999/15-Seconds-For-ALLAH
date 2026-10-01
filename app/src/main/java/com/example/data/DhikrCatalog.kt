package com.example.data

import android.content.Context
import org.json.JSONArray

data class DhikrItem(
  val id: String,
  val arabic: String,
  val transliteration: String,
  val translationUrdu: String,
  val translation: String,
  val contemplativeNote: String,
  val category: String,
  val source: String,
  val virtue: String,
  val defaultDurationSeconds: Int = 15,
  val isBookmarked: Boolean = false,
  val isQuranic: Boolean = false
)

object DhikrCatalog {

  val hadithBooks = listOf(
    "صحیح البخاری (Sahih Bukhari)",
    "صحیح مسلم (Sahih Muslim)",
    "جامع ترمذی (Jami Tirmidhi)",
    "سنن ابو داؤد (Sunan Abu Dawud)",
    "سنن نسائی (Sunan an-Nasa'i)",
    "سنن ابن ماجہ (Sunan Ibn Majah)",
    "موطا امام مالک (Muwatta Malik)"
  )

  val categories = listOf(
    "All (تمام 1000+)",
    "Saved (محفوظ آیات)",
    "صحیح البخاری (Sahih Bukhari)",
    "صحیح مسلم (Sahih Muslim)",
    "جامع ترمذی (Jami Tirmidhi)",
    "سنن ابو داؤد (Sunan Abu Dawud)",
    "سنن نسائی (Sunan an-Nasa'i)",
    "سنن ابن ماجہ (Sunan Ibn Majah)",
    "موطا امام مالک (Muwatta Malik)",
    "Juz 30 (تیسواں پارہ)",
    "Asma ul Husna",
    "Quranic Duas (قرآنی دعائیں)",
    "Morning & Evening (صبح و شام)",
    "After Salah (نماز کے بعد)",
    "Forgiveness (توبہ و استغفار)",
    "Gratitude (حمد و شکر)",
    "Protection (حفاظت و پناہ)",
    "Hadith Nabawi (احادیث مبارکہ)"
  )

  // Essential lessons from Juz 30 and Sunnah selected specifically for reminders:
  // Jannat ki basharat, Jahannam ka darr, Khauf-e-Khuda, Allah ka hukam, Nabi ﷺ ki muhabbat
  val essentialNotificationAyat: List<DhikrItem> = listOf(
    // 1. Jannat ki basharat
    DhikrItem(
      id = "juz30_jannat_nafs_mutmainna",
      arabic = "يَا أَيَّتُهَا النَّفْسُ الْمُطْمَئِنَّةُ • ارْجِعِي إِلَىٰ رَبِّكِ رَاضِيَةً مَّرْضِيَّةً • فَادْخُلِي فِي عِبَادِي • وَادْخُلِي جَنَّتِي",
      transliteration = "Ya ayyatuhan-nafsul-mutma'innah. Irji'ee ila Rabbiki radiyatan mardiyyah. Fadkhulee fee 'ibadee. Wadkhulee jannatee.",
      translationUrdu = "اے اطمینان والی روح! اپنے رب کی طرف لوٹ آ، تو اس سے راضی وہ تجھ سے راضی، پس میرے نیک بندوں میں داخل ہو جا اور میری جنت میں داخل ہو جا۔",
      translation = "O soul at rest and satisfaction! Return to your Lord, well-pleased and well-pleasing. Enter among My righteous servants, and enter My Paradise.",
      contemplativeNote = "موت کے وقت مومن کو ملنے والی سب سے پیاری بشارت۔ دل کو مطمئن رکھیں۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ الفجر 89:27-30",
      virtue = "اہل ایمان کے لیے جنت اور اللہ کے دیدار کی سب سے عظیم تسلی۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_abasa_beaming_faces",
      arabic = "وُجُوهٌ يَوْمَئِذٍ مُّسْفِرَةٌ • ضَاحِكَةٌ مُّسْتَبْشِرَةٌ",
      transliteration = "Wujoohun yawma'idhin musfirah. Dahikatun mustabshirah.",
      translationUrdu = "اس دن بہت سے چہرے روشن اور چمکتے ہوئے ہوں گے، ہنستے مسکراتے اور خوش و خرم!",
      translation = "Some faces, that Day, will be bright - laughing, rejoicing at good news.",
      contemplativeNote = "اہلِ ایمان کے چہرے قیامت کے دن نورِ الٰہی اور نیک اعمال کی برکت سے دمک رہے ہوں گے۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ عبس (Abasa 80:38-39)",
      virtue = "آخرت میں مسرت، سرخروئی اور اللہ کا فضل پانے کی پر امید آیت۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_jannat_muttaqeen",
      arabic = "إِنَّ لِلْمُتَّقِينَ مَفَازًا • حَدَائِقَ وَأَعْنَابًا • وَكَوَاعِبَ أَتْرَابًا • وَكَأْسًا دِهَاقًا",
      transliteration = "Inna lil-muttaqeena mafaza. Hada'iqa wa a'naba. Wa kawa'iba atraba. Wa ka'san dihaqa.",
      translationUrdu = "بے شک پرہیزگاروں کے لیے کامیابی کی جگہ ہے، باغات اور انگور، اور چھلکتے ہوئے خوشگوار جام۔",
      translation = "Indeed, for the righteous is attainment - gardens and grapevines and full cups.",
      contemplativeNote = "تقویٰ اختیار کرنے والوں کے لیے جنت کی لازوال نعمتوں کا تذکرہ۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ النبأ 78:31-34",
      virtue = "جنت کا پختہ یقین اور نیک اعمال کا شوق پیدا کرنے والی آیت۔",
      isQuranic = true
    ),
    // 2. Jahannam ka darr aur azaab se panah
    DhikrItem(
      id = "juz30_khauf_jahannam_naran_talazza",
      arabic = "فَأَنذَرْتُكُمْ نَارًا تَلَظَّىٰ • لَا يَصْلَاهَا إِلَّا الْأَشْقَى • الَّذِي كَذَّبَ وَتَوَلَّىٰ",
      transliteration = "Fa-andhartukum naran talazza. La yaslaha illa al-ashqa. Alladhee kadhdhaba wa tawalla.",
      translationUrdu = "پس میں نے تمہیں بھڑکتی ہوئی آگ سے خبردار کر دیا، جس میں بدبخت ترین شخص کے سوا کوئی داخل نہ ہوگا، جس نے جھٹلایا اور منہ پھیرا۔",
      translation = "So I have warned you of a Fire which is blazing. None will enter it except the most wretched, who denied and turned away.",
      contemplativeNote = "جہنم کی آگ کا خوف انسان کو گناہوں سے بچاتا ہے اور توبہ کی طرف لاتا ہے۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ اللیل 92:14-16",
      virtue = "دوزخ کی آگ سے پناہ مانگنے اور گناہوں سے باز رہنے کا پر زور حکم۔",
      isQuranic = true
    ),
    // 3. Allah ka hukam aur mushkil mein aasani
    DhikrItem(
      id = "juz30_hukam_inshirah_ease",
      arabic = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا • إِنَّ مَعَ الْعُسْرِ يُسْرًا • فَإِذَا فَرَغْتَ فَانصَبْ • وَإِلَىٰ رَبِّكَ فَارْغَب",
      transliteration = "Fa-inna ma'al-'usri yusra. Inna ma'al-'usri yusra. Fa-idha faraghta fansab. Wa ila Rabbika farghab.",
      translationUrdu = "پس بے شک ہر تنگی کے ساتھ آسانی ہے، بے شک تنگی کے ساتھ آسانی ہے! پس جب آپ فارغ ہوں تو محنت فرمائیں، اور اپنے رب ہی کی طرف رغبت رکھیں۔",
      translation = "For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease. So when you have finished, labor hard, and to your Lord direct your longing.",
      contemplativeNote = "ربِ کائنات کا وعدہ ہے کہ پریشانی کبھی ہمیشہ نہیں رہتی، ہر تنگی کے بعد دوہری آسانی ہے۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ الشرح 94:5-8",
      virtue = "مایوسی اور ڈپریشن کا سب سے بڑا قرآنی علاج اور مستقل محنت کا حکم۔",
      isQuranic = true
    ),
    // 4. Nabi ﷺ ki muhabbat aur darood shareef
    DhikrItem(
      id = "nabi_muhabbat_durood_ibrahimi",
      arabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
      transliteration = "Allahumma salli 'ala Muhammadin wa 'ala aali Muhammadin kama sallayta 'ala Ibrahima wa 'ala aali Ibrahima innaka Hamidum-Majeed.",
      translationUrdu = "اے اللہ! رحمتیں نازل فرما محمد ﷺ پر اور ان کی آل پر جیسا کہ تو نے رحمتیں نازل فرمائیں ابراہیم علیہ السلام پر اور ان کی آل پر، بے شک تو قابل تعریف اور بزرگی والا ہے۔",
      translation = "O Allah, send blessings upon Muhammad and the family of Muhammad, as You sent blessings upon Ibrahim and the family of Ibrahim. Indeed, You are Praiseworthy and Majestic.",
      contemplativeNote = "رسول اللہ ﷺ کی محبت ایمان کی روح ہے۔ ایک بار درود پڑھنے پر اللہ تعالیٰ دس رحمتیں نازل فرماتا ہے۔",
      category = "Hadith Nabawi (احادیث مبارکہ)",
      source = "صحیح بخاری 3370",
      virtue = "ایک بار درود سے 10 گناہ معاف، 10 درجات بلند اور 10 رحمتیں نازل ہوتی ہیں۔",
      isQuranic = false
    )
  )

  val items: List<DhikrItem> = essentialNotificationAyat

  private var cachedCatalog: List<DhikrItem>? = null

  private const val PREFS_NAME = "dhikr_rotation_prefs"
  private const val KEY_SHOWN_IDS = "shown_dhikr_ids_set"
  private const val KEY_CURRENT_FEATURED_ID = "current_featured_dhikr_id"
  private const val KEY_FEATURED_TIMESTAMP = "current_featured_timestamp"

  fun loadFullCatalog(context: Context): List<DhikrItem> {
    cachedCatalog?.let { return it }

    val loadedList = mutableListOf<DhikrItem>()
    try {
      val jsonString = context.assets.open("sacred_catalog.json").bufferedReader().use { it.readText() }
      val jsonArray = JSONArray(jsonString)

      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        loadedList.add(
          DhikrItem(
            id = obj.optString("id", "dhikr_$i"),
            arabic = obj.optString("arabic", ""),
            transliteration = obj.optString("transliteration", ""),
            translationUrdu = obj.optString("translationUrdu", ""),
            translation = obj.optString("translation", ""),
            contemplativeNote = obj.optString("contemplativeNote", ""),
            category = obj.optString("category", "Juz 30 (تیسواں پارہ)"),
            source = obj.optString("source", ""),
            virtue = obj.optString("virtue", ""),
            defaultDurationSeconds = obj.optInt("defaultDurationSeconds", 15),
            isQuranic = obj.optBoolean("isQuranic", false)
          )
        )
      }
    } catch (_: Exception) {
      // Fallback to essential items
      loadedList.addAll(essentialNotificationAyat)
    }

    if (loadedList.isEmpty()) {
      loadedList.addAll(essentialNotificationAyat)
    }

    cachedCatalog = loadedList
    return loadedList
  }

  // -------------------------------------------------------------------------
  // Strict Anti-Repetition Rotation Engine:
  // "Ak din agar ak Dhikr ya Ayat nazar aajy to dobara nazar na aay until baqi 1000+ use na hojain"
  // Guarantees zero duplicates until all 1000+ items have cycled completely!
  // -------------------------------------------------------------------------

  private fun getShownIds(context: Context): MutableSet<String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getStringSet(KEY_SHOWN_IDS, emptySet())?.toMutableSet() ?: mutableSetOf()
  }

  private fun saveShownIds(context: Context, ids: Set<String>) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putStringSet(KEY_SHOWN_IDS, ids).apply()
  }

  /**
   * Returns the progress of the 1000+ cycle: (shownCount, totalCount)
   */
  fun getRotationCycleProgress(context: Context): Pair<Int, Int> {
    val fullList = loadFullCatalog(context)
    val shownCount = getShownIds(context).size
    return Pair(shownCount, fullList.size)
  }

  /**
   * Retrieves the next distinct, unshown Dhikr or Ayah from the 1000+ pool.
   * If all 1000+ items have been shown, the pool resets cleanly and starts the cycle over.
   */
  @Synchronized
  fun getNextNonRepeatingDhikr(context: Context): DhikrItem {
    val fullList = loadFullCatalog(context)
    val shownIds = getShownIds(context)

    // Filter out all already shown dhikrs
    val unshownList = fullList.filter { it.id !in shownIds }

    val nextItem: DhikrItem = if (unshownList.isNotEmpty()) {
      // Pick next unshown item (balanced pseudo-random selection)
      unshownList.random()
    } else {
      // All 1000+ items have been displayed! Reset shown set and restart pool
      shownIds.clear()
      fullList.random()
    }

    // Mark as shown persistently
    shownIds.add(nextItem.id)
    saveShownIds(context, shownIds)

    return nextItem
  }

  // 2-Hour Auto-Rotation Algorithm for Landing Page (Home Screen):
  // Keeps the same Dhikr for 2 hours, then strictly advances to an unshown item from the 1000+ pool!
  fun getTwoHourRotatedDhikr(context: Context, offsetHours: Int = 0): DhikrItem {
    val fullList = loadFullCatalog(context)
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    if (offsetHours != 0) {
      val totalHours = (System.currentTimeMillis() / (1000L * 60L * 60L)) + offsetHours
      val index = Math.floorMod((totalHours / 2L).toInt() * 31 + 7, fullList.size)
      return fullList[index]
    }

    val currentFeaturedId = prefs.getString(KEY_CURRENT_FEATURED_ID, null)
    val timestamp = prefs.getLong(KEY_FEATURED_TIMESTAMP, 0L)
    val now = System.currentTimeMillis()
    val twoHoursMillis = 2L * 3600L * 1000L

    if (currentFeaturedId != null && (now - timestamp) in 0 until twoHoursMillis) {
      val existing = fullList.find { it.id == currentFeaturedId }
      if (existing != null) return existing
    }

    // Window expired or no item featured: pull next unshown dhikr
    val newItem = getNextNonRepeatingDhikr(context)
    prefs.edit()
      .putString(KEY_CURRENT_FEATURED_ID, newItem.id)
      .putLong(KEY_FEATURED_TIMESTAMP, now)
      .apply()

    return newItem
  }

  // Time remaining in current 2-hour window (in milliseconds)
  fun getRemainingTimeInTwoHourWindowMillis(): Long {
    val currentMillis = System.currentTimeMillis()
    val slotDuration = 2L * 3600L * 1000L
    val elapsedInSlot = currentMillis % slotDuration
    return (slotDuration - elapsedInSlot).coerceAtLeast(0L)
  }

  fun getRandomNotificationDhikr(context: Context): DhikrItem {
    // Strictly uses the non-repeating queue so notifications never repeat any Dhikr
    // until all 1000+ have been delivered!
    return getNextNonRepeatingDhikr(context)
  }
}
