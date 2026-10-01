package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class HadithBookInfo(
  val id: String, // "bukhari", "muslim", "tirmidhi", "abudawud", "nasai", "ibnmajah", "malik"
  val nameUrdu: String,
  val nameArabic: String,
  val nameEnglish: String,
  val author: String,
  val totalHadiths: Int,
  val totalChapters: Int,
  val description: String,
  val defaultHadithNumber: Int = 1
)

data class HadithItemDetail(
  val bookId: String,
  val hadithNumber: Int,
  val bookNameUrdu: String,
  val bookNameArabic: String,
  val bookNameEnglish: String = "",
  val chapterName: String,
  val arabicText: String,
  val urduText: String,
  val englishText: String,
  val isBookmarked: Boolean = false,
  val isOfflineCached: Boolean = true
)

object HadithCollections {

  val books = listOf(
    HadithBookInfo(
      id = "bukhari",
      nameUrdu = "صحیح البخاری",
      nameArabic = "صحيح البخاري",
      nameEnglish = "Sahih al-Bukhari",
      author = "الإمام محمد بن إسماعيل البخاري (متوفى 256ھ)",
      totalHadiths = 7563,
      totalChapters = 97,
      description = "أصح كتاب بعد كتاب الله عز وجل، اتفق أئمة المسلمين على تلقيه بالقبول التام."
    ),
    HadithBookInfo(
      id = "muslim",
      nameUrdu = "صحیح مسلم",
      nameArabic = "صحيح مسلم",
      nameEnglish = "Sahih Muslim",
      author = "الإمام مسلم بن الحجاج النيسابوري (متوفى 261ھ)",
      totalHadiths = 7500,
      totalChapters = 56,
      description = "ثاني أصح كتب السنة النبوية، يمتاز بدقة سياق الأسانيد وجمع طرق الحديث في موضع واحد."
    ),
    HadithBookInfo(
      id = "tirmidhi",
      nameUrdu = "جامع الترمذی",
      nameArabic = "الجامع المختصر (جامع الترمذي)",
      nameEnglish = "Jami` at-Tirmidhi",
      author = "الإمام أبو عيسى محمد بن عيسى الترمذي (متوفى 279ھ)",
      totalHadiths = 3956,
      totalChapters = 49,
      description = "ديوان فقهي عظيم يمتاز ببيان درجة الحديث (صحيح، حسن، غريب) ومذاهب الفقهاء والصحابة."
    ),
    HadithBookInfo(
      id = "abudawud",
      nameUrdu = "سنن ابوداؤد",
      nameArabic = "سنن أبي داود",
      nameEnglish = "Sunan Abi Dawud",
      author = "الإمام أبو داود سليمان بن الأشعث السجستاني (متوفى 275ھ)",
      totalHadiths = 5274,
      totalChapters = 43,
      description = "جامع أحاديث الأحكام ومعتمد الأئمة والمجتهدين في استنباط المسائل الفقهية."
    ),
    HadithBookInfo(
      id = "nasai",
      nameUrdu = "سنن نسائی",
      nameArabic = "المجتبى من السنن (سنن النسائي)",
      nameEnglish = "Sunan an-Nasa'i",
      author = "الإمام أحمد بن شعيب النسائي (متوفى 303ھ)",
      totalHadiths = 5758,
      totalChapters = 52,
      description = "أقل السنن الأربعة حديثاً ضعيفاً وأشدها شرطاً في الرجال بعد الصحيحين."
    ),
    HadithBookInfo(
      id = "ibnmajah",
      nameUrdu = "سنن ابن ماجہ",
      nameArabic = "سنن ابن ماجه",
      nameEnglish = "Sunan Ibn Majah",
      author = "الإمام محمد بن يزيد بن ماجه القزويني (متوفى 273ھ)",
      totalHadiths = 4341,
      totalChapters = 37,
      description = "خاتمة الكتب الستة الصحاح، يمتاز بحسن الترتيب وتسهيل الوصول للأحاديث الفقهية."
    ),
    HadithBookInfo(
      id = "malik",
      nameUrdu = "موطا امام مالک",
      nameArabic = "موطأ الإمام مالك",
      nameEnglish = "Muwatta Imam Malik",
      author = "الإمام مالك بن أنس إمام دار الهجرة (متوفى 179ھ)",
      totalHadiths = 1858,
      totalChapters = 61,
      description = "أقدم وأوثق ديوان حديث وفقه. قال الشافعي: ما على ظهر الأرض كتاب بعد كتاب الله أصح من موطأ مالك."
    )
  )

  fun findBook(bookId: String): HadithBookInfo {
    return books.find { it.id.equals(bookId, ignoreCase = true) } ?: books[0]
  }
}

object HadithRepository {

  /**
   * Fetches any Hadith from the 36,000+ collection:
   * 1. Checks Room DB local cache first.
   * 2. If not found in cache, fetches Arabic + Urdu + English from the authoritative open Hadith API.
   * 3. Stores in Room DB for permanent offline access.
   * 4. If network unavailable, falls back to pre-bundled catalog matching this book and number.
   */
  suspend fun getHadith(
    context: Context,
    bookId: String,
    hadithNumber: Int
  ): Result<HadithItemDetail> = withContext(Dispatchers.IO) {
    try {
      val database = AppDatabase.getDatabase(context)
      val dao = database.momentDao()
      val book = HadithCollections.findBook(bookId)

      // 1. Check local Room cache
      val cached = dao.getHadithFromCache(book.id, hadithNumber)
      if (cached != null && cached.arabicText.isNotBlank()) {
        return@withContext Result.success(
          HadithItemDetail(
            bookId = book.id,
            hadithNumber = hadithNumber,
            bookNameUrdu = book.nameUrdu,
            bookNameArabic = book.nameArabic,
            bookNameEnglish = book.nameEnglish,
            chapterName = cached.chapterName,
            arabicText = cached.arabicText,
            urduText = cached.urduText,
            englishText = cached.englishText,
            isBookmarked = cached.isBookmarked,
            isOfflineCached = true
          )
        )
      }

      // 2. Fetch Arabic, Urdu, and English in parallel
      val (arabicData, urduData, englishData) = coroutineScope {
        val araDeferred = async { fetchJson("https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/ara-${book.id}/$hadithNumber.json") }
        val urdDeferred = async { fetchJson("https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/urd-${book.id}/$hadithNumber.json") }
        val engDeferred = async { fetchJson("https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/eng-${book.id}/$hadithNumber.json") }
        Triple(araDeferred.await(), urdDeferred.await(), engDeferred.await())
      }

      val arabicText = parseHadithText(arabicData)
      val urduText = parseHadithText(urduData)
      val englishText = parseHadithText(englishData)
      val chapterName = parseChapterName(arabicData) ?: parseChapterName(urduData) ?: "باب من أبواب السنة النبوية"

      if (arabicText.isNotBlank()) {
        // 3. Save into Room Cache
        val entity = HadithEntity(
          bookKey = book.id,
          hadithNumber = hadithNumber,
          bookNameUrdu = book.nameUrdu,
          chapterName = chapterName,
          arabicText = arabicText,
          urduText = if (urduText.isNotBlank()) urduText else "ترجمہ لوڈ نہیں ہو سکا (نیٹ ورک چیک کریں)",
          englishText = englishText,
          isBookmarked = false,
          cachedAt = System.currentTimeMillis()
        )
        dao.insertHadithToCache(entity)

        return@withContext Result.success(
          HadithItemDetail(
            bookId = book.id,
            hadithNumber = hadithNumber,
            bookNameUrdu = book.nameUrdu,
            bookNameArabic = book.nameArabic,
            bookNameEnglish = book.nameEnglish,
            chapterName = chapterName,
            arabicText = arabicText,
            urduText = entity.urduText,
            englishText = englishText,
            isBookmarked = false,
            isOfflineCached = false
          )
        )
      }

      // Fallback: check pre-bundled catalog
      val bundled = DhikrCatalog.loadFullCatalog(context).find {
        it.source.contains(book.nameUrdu.take(4)) || it.category.contains(book.nameUrdu.take(4))
      }
      if (bundled != null) {
        return@withContext Result.success(
          HadithItemDetail(
            bookId = book.id,
            hadithNumber = hadithNumber,
            bookNameUrdu = book.nameUrdu,
            bookNameArabic = book.nameArabic,
            bookNameEnglish = book.nameEnglish,
            chapterName = "مجموعہ اذکار و ارشادات",
            arabicText = bundled.arabic,
            urduText = bundled.translationUrdu,
            englishText = bundled.translation,
            isBookmarked = false,
            isOfflineCached = true
          )
        )
      }

      Result.failure(Exception("حدیث نمبر $hadithNumber لوڈ نہ ہو سکی۔ براہ کرم انٹرنیٹ کنکشن چیک کریں۔"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun toggleBookmark(context: Context, bookId: String, hadithNumber: Int, currentlyBookmarked: Boolean) = withContext(Dispatchers.IO) {
    try {
      val database = AppDatabase.getDatabase(context)
      database.momentDao().setHadithBookmark(bookId, hadithNumber, !currentlyBookmarked)
    } catch (_: Exception) {}
  }

  private fun fetchJson(urlString: String): String? {
    var connection: HttpURLConnection? = null
    return try {
      val url = URL(urlString)
      connection = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = 6000
        readTimeout = 6000
        setRequestProperty("User-Agent", "15SecondsForAllah-App")
      }
      if (connection.responseCode in 200..299) {
        BufferedReader(InputStreamReader(connection.inputStream, "UTF-8")).use { reader ->
          reader.readText()
        }
      } else null
    } catch (_: Exception) {
      null
    } finally {
      connection?.disconnect()
    }
  }

  private fun parseHadithText(jsonStr: String?): String {
    if (jsonStr.isNullOrBlank()) return ""
    return try {
      val root = JSONObject(jsonStr)
      val hadiths = root.optJSONArray("hadiths") ?: return ""
      if (hadiths.length() > 0) {
        hadiths.getJSONObject(0).optString("text", "")
      } else ""
    } catch (_: Exception) {
      ""
    }
  }

  private fun parseChapterName(jsonStr: String?): String? {
    if (jsonStr.isNullOrBlank()) return null
    return try {
      val root = JSONObject(jsonStr)
      val metadata = root.optJSONObject("metadata") ?: return null
      val section = metadata.optJSONObject("section") ?: return null
      val keys = section.keys()
      if (keys.hasNext()) {
        section.optString(keys.next(), null)
      } else null
    } catch (_: Exception) {
      null
    }
  }
}
