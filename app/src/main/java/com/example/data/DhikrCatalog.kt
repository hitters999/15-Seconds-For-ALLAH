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

  val categories = listOf(
    "All (تمام 1000+)",
    "Saved (محفوظ آیات)",
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

  // Essential lessons from Juz 30 and Sunnah selected specifically for hourly notifications:
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
      id = "juz30_jannat_muttaqeen",
      arabic = "إِنَّ لِلْمُتَّقِينَ مَفَازًا • حَدَائِقَ وَأَعْنَابًا • وَكَوَاعِبَ أَتْرَابًا • وَكَأْسًا دِهَاقًا",
      transliteration = "Inna lil-muttaqeena mafaza. Hada'iqa wa a'naba. Wa kawa'iba atraba. Wa ka'san dihaqa.",
      translationUrdu = "بے شک پرہیزگاروں کے لیے کامیابی کی جگہ ہے، باغات اور انگور، اور چھلکتے ہوئے خوشگوار جام۔",
      translation = "Indeed, for the righteous is attainment - gardens and grapevines and full cups.",
      contemplativeNote = "تقویٰ اختیار کرنے والوں کے لیے جنت کی لازوال نعمتوں کا تذکرہ۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ النبأ 78:31-34",
      virtue = "تقویٰ اختیار کرنے والا دنیا و آخرت دونوں میں سرخرو ہے۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_jannat_abrar",
      arabic = "إِنَّ الْأَبْرَارَ لَفِي نَعِيمٍ • عَلَى الْأَرَائِكِ يَنظُرُونَ • تَعْرِفُ فِي وُجُوهِهِمْ نَضْرَةَ النَّعِيمِ",
      transliteration = "Innal-abrara lafee na'eem. 'Alal-ara'iki yandhuroon. Ta'rifu fee wujoohihim nadratan-na'eem.",
      translationUrdu = "بے شک نیک لوگ بڑی نعمتوں میں ہوں گے، مسندوں پر بیٹھے نظارہ کر رہے ہوں گے، تم ان کے چہروں پر رونق اور خوشی پہچان لو گے۔",
      translation = "Indeed, the righteous will be in pleasure, on adorned couches observing. You will recognize in their faces the radiance of pleasure.",
      contemplativeNote = "نیک اعمال کا انجام: دائمی خوشی اور چہروں کا نور۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ المطففین 83:22-24",
      virtue = "نیکی دل کو سکون اور چہرے کو نور بخشتی ہے۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_jannat_radhiyallahu",
      arabic = "جَزَاؤُهُمْ عِندَ رَبِّهِمْ جَنَّاتُ عَدْنٍ تَجْرِي مِن تَحْتِهَا الْأَنْهَارُ خَالِدِينَ فِيهَا أَبَدًا ۖ رَّضِيَ اللَّهُ عَنْهُمْ وَرَضُوا عَنْهُ ۚ ذَٰلِكَ لِمَنْ خَشِيَ رَبَّهُ",
      transliteration = "Jaza'uhum 'inda Rabbihim jannatu 'adnin tajree min tahtihal-anharu khalideena feeha abada. Radiyallahu 'anhum wa radoo 'anhu. Dhalika liman khashiya Rabbah.",
      translationUrdu = "ان کا بدلہ ان کے رب کے پاس سدا بہار جنتیں ہیں جن کے نیچے نہریں بہتی ہیں، اللہ ان سے راضی ہوا اور وہ اللہ سے راضی ہوئے، یہ اس کے لیے ہے جو اپنے رب سے ڈرا۔",
      translation = "Their reward with their Lord is gardens of perpetual residence beneath which rivers flow. Allah is pleased with them and they are pleased with Him. That is for whoever feared his Lord.",
      contemplativeNote = "سب سے بڑی نعمت اللہ کی رضا ہے جو خشیت الٰہی سے نصیب ہوتی ہے۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ البینۃ 98:8",
      virtue = "اللہ کی خوشنودی پانے کا نسخہ: دل میں اپنے پیدا کرنے والے کا خوف اور محبت رکھنا۔",
      isQuranic = true
    ),

    // 2. Jahannam ka darr aur azaab
    DhikrItem(
      id = "juz30_khauf_maqama_rabbihi",
      arabic = "وَأَمَّا مَنْ خَافَ مَقَامَ رَبِّهِ وَنَهَى النَّفْسَ عَنِ الْهَوَىٰ • فَإِنَّ الْجَنَّةَ هِيَ الْمَأْوَىٰ",
      transliteration = "Wa amma man khafa maqama Rabbihi wa nahan-nafsa 'anil-hawa. Fa-innal-jannata hiyal-ma'wa.",
      translationUrdu = "اور جو شخص اپنے رب کے سامنے پیش ہونے سے ڈرا اور اپنے نفس کو بری خواہشات سے روکا، پس یقیناً جنت ہی اس کا ٹھکانا ہے۔",
      translation = "But as for he who feared the position of his Lord and prevented the soul from [unlawful] inclination, then indeed, Paradise will be his refuge.",
      contemplativeNote = "خواہشِ نفس کو اللہ کے خوف سے چھوڑ دینا ہی جنت کا سب سے سیدھا راستہ ہے۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ النازعات 79:40-41",
      virtue = "نفس کو قابو میں رکھنے والا قیامت کے دن اللہ کے سایہ رحمت میں ہوگا۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_darr_al_hutamah",
      arabic = "كَلَّا ۖ لَيُنبَذَنَّ فِي الْحُطَمَةِ • وَمَا أَدْرَاكَ مَا الْحُطَمَةُ • نَارُ اللَّهِ الْمُوقَدَةُ • الَّتِي تَطَّلِعُ عَلَى الْأَفْئِدَةِ",
      transliteration = "Kalla layumbadhanna fil-hutamah. Wa ma adraka mal-hutamah. Narullahil-mooqadah. Allatee tattali'u 'alal-af'idah.",
      translationUrdu = "ہرگز نہیں! وہ ضرور حطمہ (توڑ پھوڑ دینے والی آگ) میں پھینکا جائے گا، اور تم کیا جانو حطمہ کیا ہے؟ وہ اللہ کی دہکائی ہوئی آگ ہے جو دلوں تک چڑھ جائے گی۔",
      translation = "No! He will surely be thrown into the Crusher. And what can make you know what is the Crusher? It is the fire of Allah, [eternally] fueled, which mounts directed at the hearts.",
      contemplativeNote = "غیبت، بدزبانی اور تکبر سے بچیں، جہنم کا عذاب روح کو لرزا دینے والا ہے۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ الہمزۃ 104:4-7",
      virtue = "اللہ تعالیٰ سے دوزخ کے عذاب سے پناہ مانگتے رہنا مسنون ہے۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_darr_mawazeenuhu",
      arabic = "فَأَمَّا مَن ثَقُلَتْ مَوَازِينُهُ • فَهُوَ فِي عِيشَةٍ رَّاضِيَةٍ • وَأَمَّا مَنْ خَفَّتْ مَوَازِينُهُ • فَأُمُّهُ هَاوِيَةٌ • وَمَا أَدْرَاكَ مَا هِيَهْ • نَارٌ حَامِيَةٌ",
      transliteration = "Fa-amma man thaqulat mawazeenuhu fa-huwa fee 'eeshatir-radiyah. Wa amma man khaffat mawazeenuhu fa-ummuhu hawiyah. Wa ma adraka ma hiyah. Narun hamiyah.",
      translationUrdu = "پس جس کے نیکیوں کے پلڑے بھاری ہوں گے وہ من پسند زندگی میں ہوگا، اور جس کے پلڑے ہلکے ہوں گے اس کا ٹھکانا گہری کھائی ہے، اور تمہیں کیا معلوم وہ کیا ہے؟ وہ دہکتی ہوئی آگ ہے۔",
      translation = "Then as for one whose scales are heavy [with good deeds], he will be in a pleasant life. But as for one whose scales are light, his refuge will be an abyss. And what can make you know what that is? It is a Fire, intensely hot.",
      contemplativeNote = "ہر نیک عمل اور ہر ذکر میزان کو بھاری کر رہا ہے، کوئی نیکی چھوٹی نہ سمجھیں۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ القارعۃ 101:6-11",
      virtue = "سبحان اللہ وبحمدہ میزان کو سب سے زیادہ وزنی کرتا ہے۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_darr_takathur",
      arabic = "أَلْهَاكُمُ التَّكَاثُرُ • حَتَّىٰ زُرْتُمُ الْمَقَابِرَ • كَلَّا سَوْفَ تَعْلَمُونَ • ثُمَّ كَلَّا سَوْفَ تَعْلَمُونَ",
      transliteration = "Alhakumut-takathur. Hatta zurtumul-maqabir. Kalla sawfa ta'lamoon. Thumma kalla sawfa ta'lamoon.",
      translationUrdu = "تمہیں مال اور دنیا کی کثرت کی ہوس نے غافل کر دیا، یہاں تک کہ تم نے قبریں جا دیکھیں، ہرگز نہیں! عنقریب تم حقیقت جان لو گے۔",
      translation = "Competition in increase diverts you until you visit the graveyards. No! You are going to know. Then no! You are going to know.",
      contemplativeNote = "دنیا عارضی ہے۔ 15 سیکنڈ نکال کر اپنی آخرت کی تیاری کی فکر کریں۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ التکاثر 102:1-4",
      virtue = "موت کو کثرت سے یاد کرنا دل کو دنیا کی محبت سے آزاد کرتا ہے۔",
      isQuranic = true
    ),

    // 3. ALLAH ka Hukam, Adal aur Hidayat
    DhikrItem(
      id = "juz30_hukam_zarrah",
      arabic = "فَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ خَيْرًا يَرَهُ • وَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ شَرًّا يَرَهُ",
      transliteration = "Faman ya'mal mithqala dharratin khayran yarah. Wa man ya'mal mithqala dharratin sharran yarah.",
      translationUrdu = "پس جو ذرہ برابر نیکی کرے گا وہ اسے دیکھ لے گا، اور جو ذرہ برابر برائی کرے گا وہ اسے بھی دیکھ لے گا۔",
      translation = "So whoever does an atom's weight of good will see it, and whoever does an atom's weight of evil will see it.",
      contemplativeNote = "آپ کا 15 سیکنڈ کا یہ چھوٹا سا ذکر بھی کل قیامت کے دن سامنے آئے گا۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ الزلزلۃ 99:7-8",
      virtue = "اللہ تعالیٰ کے ہاں کوئی ادنیٰ نیکی بھی ضائع نہیں ہوتی۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_hukam_asr",
      arabic = "وَالْعَصْرِ • إِنَّ الْإِنسَانَ لَفِي خُسْرٍ • إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ",
      transliteration = "Wal-'asr. Innal-insana lafee khusr. Illal-ladheena amanoo wa 'amilus-salihati wa tawwasaw bil-haqqi wa tawwasaw bis-sabr.",
      translationUrdu = "زمانے کی قسم! انسان یقیناً خسارے میں ہے، سوائے ان کے جو ایمان لائے اور نیک عمل کیے اور ایک دوسرے کو حق اور صبر کی تلقین کی۔",
      translation = "By time, indeed mankind is in loss, except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.",
      contemplativeNote = "اگر پورا قرآن نہ اترتا تو صرف سورۃ العصر انسانیت کی ہدایت کے لیے کافی تھی۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ العصر 103:1-3",
      virtue = "امام شافعی رحمہ اللہ کا ارشاد: سورۃ العصر تمام حکمت کا خلاصہ ہے۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_hukam_ma_gharraka",
      arabic = "يَا أَيُّهَا الْإِنسَانُ مَا غَرَّكَ بِرَبِّكَ الْكَرِيمِ • الَّذِي خَلَقَكَ فَسَوَّاكَ فَعَدَلَكَ",
      transliteration = "Ya ayyuhal-insanu ma gharraka bi-Rabbikal-kareem. Alladhee khalaqaka fa-sawwaka fa-'adalak.",
      translationUrdu = "اے انسان! تجھے اپنے رب کریم کے بارے میں کس چیز نے دھوکے میں ڈال دیا؟ جس نے تجھے پیدا کیا، پھر تیرے اعضاء کو سنوارا اور تجھے متناسب بنایا۔",
      translation = "O mankind, what has deceived you concerning your Lord, the Generous, who created you, proportioned you, and balanced you?",
      contemplativeNote = "رب کے احسانات کو یاد کر کے شرم اور عاجزی کے آنسو بہائیں۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ الانفطار 82:6-7",
      virtue = "اللہ کی رحمت اور نعمتوں کو پہچاننا ہی اصل شکر گزاری ہے۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_hukam_inshirah_ease",
      arabic = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا • إِنَّ مَعَ الْعُسْرِ يُسْرًا • فَإِذَا فَرَغْتَ فَانصَبْ • وَإِلَىٰ رَبِّكَ فَارْغَب",
      transliteration = "Fa-inna ma'al-'usri yusra. Inna ma'al-'usri yusra. Fa-idha faraghta fansab. Wa ila Rabbika farghab.",
      translationUrdu = "پس بے شک تنگی کے ساتھ آسانی ہے، یقیناً تنگی کے ساتھ آسانی ہے۔ پس جب آپ فارغ ہوں تو عبادت میں محنت کریں اور اپنے رب ہی کی طرف لو لگائیں۔",
      translation = "For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease. So when you have finished, stand up [for worship], and to your Lord direct [your] longing.",
      contemplativeNote = "مشکل جتنی بھی بڑی ہو، اللہ کی آسانی اس کے ساتھ ہی لگی ہوئی ہے۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ الشرح 94:5-8",
      virtue = "اللہ کا سچا وعدہ کہ تنگی کے بعد وسعت اور کشادگی یقینی ہے۔",
      isQuranic = true
    ),

    // 4. Nabi ﷺ ki Shan aur Muhabbat
    DhikrItem(
      id = "juz30_nabi_waddaaqa",
      arabic = "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ • وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ • وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ",
      transliteration = "Ma wadda'aka Rabbuka wa ma qala. Wa lal-akhiratu khayrul-laka minal-oola. Wa lasawfa yu'teeka Rabbuka fatarda.",
      translationUrdu = "آپ کے رب نے نہ آپ کو چھوڑا ہے اور نہ وہ بیزار ہوا ہے، اور آخرت آپ کے لیے دنیا سے کہیں بہتر ہے، اور عنقریب آپ کا رب آپ کو اتنا عطا فرمائے گا کہ آپ راضی ہو جائیں گے۔",
      translation = "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you]. And the Hereafter is better for you than the first [life]. And your Lord is going to give you, and you will be satisfied.",
      contemplativeNote = "رسول اللہ ﷺ سے رب کی بے پایاں محبت اور شفاعت کبریٰ کی نوید۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ الضحیٰ 93:3-5",
      virtue = "حضرت علی رضی اللہ عنہ: قرآن کریم کی سب سے زیادہ امید افزا آیت ہے۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_nabi_rafaana_dhikrak",
      arabic = "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ • وَوَضَعْنَا عَنكَ وِزْرَكَ • الَّذِي أَنقَضَ ظَهَرَكَ • وَرَفَعْنَا لَكَ ذِكْرَكَ",
      transliteration = "Alam nashrah laka sadrak. Wa wada'na 'anka wizrak. Alladhee anqada dhahrak. Wa rafa'na laka dhikrak.",
      translationUrdu = "کیا ہم نے آپ کی خاطر آپ کا سینہ کشادہ نہیں فرما دیا؟ اور آپ سے آپ کا بوجھ اتار دیا، جس نے آپ کی کمر جھکا رکھی تھی، اور ہم نے آپ کے ذکر کو بلند کر دیا۔",
      translation = "Did We not expand for you, [O Muhammad], your breast? And We removed from you your burden which had weighed upon your back, and raised high for you your repute.",
      contemplativeNote = "جہاں جہاں اللہ کا نام آتا ہے، وہاں وہاں رسول اللہ ﷺ کا نام بلند ہوتا ہے۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ الشرح 94:1-4",
      virtue = "نبی اکرم ﷺ کے ذکر کی بلندی اللہ کا ابدی اور لاجواب فیصلہ ہے۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "juz30_nabi_al_kawthar",
      arabic = "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ • فَصَلِّ لِرَبِّكَ وَانْحَرْ • إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ",
      transliteration = "Inna a'taynkal-kawthar. Fa-salli li-Rabbika wanhar. Inna shani'aka huwal-abtar.",
      translationUrdu = "بے شک ہم نے آپ کو کوثر (بے انتہا بھلائی) عطا فرمائی، پس آپ اپنے رب کے لیے نماز پڑھیں اور قربانی کریں، یقیناً آپ کا دشمن ہی بے نام و نشان رہے گا۔",
      translation = "Indeed, We have granted you, [O Muhammad], al-Kawthar. So pray to your Lord and sacrifice. Indeed, your enemy is the one cut off.",
      contemplativeNote = "حوضِ کوثر کا دیدار اور رسول اللہ ﷺ کے مبارک ہاتھوں سے جام پینا۔",
      category = "Juz 30 (تیسواں پارہ)",
      source = "سورۃ الکوثر 108:1-3",
      virtue = "حوض کوثر پر نبی کریم ﷺ کے دست مبارک سے پینے کے بعد کبھی پیاس نہیں لگے گی۔",
      isQuranic = true
    ),
    DhikrItem(
      id = "durood_sharif_ibrahimi",
      arabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
      transliteration = "Allahumma salli 'ala Muhammadin wa 'ala aali Muhammadin kama sallayta 'ala Ibraheema wa 'ala aali Ibraheema innaka Hameedum-Majeed",
      translationUrdu = "اے اللہ! رحمتیں نازل فرما حضرت محمد ﷺ پر اور آپ ﷺ کی آل پر، جیسا کہ تو نے رحمتیں نازل فرمائیں حضرت ابراہیم علیہ السلام پر اور ان کی آل پر، بے شک تو قابل تعریف اور بڑی بزرگی والا ہے۔",
      translation = "O Allah, send peace and blessings upon Muhammad and upon the family of Muhammad, as You sent blessings upon Ibrahim and upon the family of Ibrahim. Indeed, You are Praiseworthy and Glorious.",
      contemplativeNote = "جب آپ ایک بار درود بھیجتے ہیں تو اللہ تعالیٰ آپ پر دس رحمتیں نازل فرماتا ہے۔",
      category = "نبی کریم ﷺ پر درود و سلام",
      source = "صحیح بخاری 3370",
      virtue = "جو مجھ پر ایک بار درود بھیجے، اللہ اس پر دس رحمتیں نازل فرماتا ہے اور دس گناہ مٹا دیتا ہے۔",
      isQuranic = false
    )
  )

  // Primary list for fast UI initialization (combines essential notification ayat + foundational dhikr)
  val items: List<DhikrItem> = essentialNotificationAyat

  fun getRandomNotificationDhikr(context: Context): DhikrItem {
    return essentialNotificationAyat.random()
  }

  fun loadFullCatalog(context: Context): List<DhikrItem> {
    return try {
      val jsonString = context.assets.open("sacred_catalog.json").bufferedReader().use { it.readText() }
      val jsonArray = JSONArray(jsonString)
      val list = ArrayList<DhikrItem>(jsonArray.length() + essentialNotificationAyat.size)

      // Always put the essential notification ayat at the very top of catalog
      list.addAll(essentialNotificationAyat)

      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        val id = obj.getString("id")
        // Don't duplicate if already in essential
        if (essentialNotificationAyat.none { it.id == id }) {
          val cat = obj.getString("category")
          val isQ = cat.contains("Juz", ignoreCase = true) || cat.contains("Quran", ignoreCase = true)
          list.add(
            DhikrItem(
              id = id,
              arabic = obj.getString("arabic"),
              transliteration = obj.getString("transliteration"),
              translationUrdu = obj.getString("translationUrdu"),
              translation = obj.getString("translation"),
              contemplativeNote = obj.optString("contemplativeNote", ""),
              category = cat,
              source = obj.getString("source"),
              virtue = obj.getString("virtue"),
              defaultDurationSeconds = obj.optInt("defaultDurationSeconds", 15),
              isQuranic = isQ
            )
          )
        }
      }
      if (list.isNotEmpty()) list else items
    } catch (_: Exception) {
      items
    }
  }
}
