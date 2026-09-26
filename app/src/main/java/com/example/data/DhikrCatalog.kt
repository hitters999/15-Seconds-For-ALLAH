package com.example.data

data class DhikrItem(
  val id: String,
  val arabic: String,
  val transliteration: String,
  val translation: String,
  val contemplativeNote: String,
  val category: String,
  val source: String,
  val virtue: String,
  val defaultDurationSeconds: Int = 15,
  val isBookmarked: Boolean = false
)

object DhikrCatalog {
  val items: List<DhikrItem> = listOf(
    // Gratitude & Praise
    DhikrItem(
      id = "subhanallah",
      arabic = "سُبْحَانَ اللَّٰهِ",
      transliteration = "Subhan-Allah",
      translation = "Glory be to Allah.",
      contemplativeNote = "Said with full presence, just for this moment. Freeing the heart of all distractions.",
      category = "Gratitude",
      source = "Sahih Muslim 2691",
      virtue = "Plants a palm tree in Jannah and lightens the heaviness of the soul."
    ),
    DhikrItem(
      id = "alhamdulillah",
      arabic = "الْحَمْدُ لِلَّٰهِ",
      transliteration = "Al-hamdulillah",
      translation = "All praise and gratitude belong to Allah alone.",
      contemplativeNote = "Fills the scale of good deeds. Acknowledge one specific blessing in your life right now.",
      category = "Gratitude",
      source = "Sahih Muslim 223",
      virtue = "Fills the scales (Mizan) on the Day of Judgment."
    ),
    DhikrItem(
      id = "subhan_wa_bihamdihi",
      arabic = "سُبْحَانَ اللَّٰهِ وَبِحَمْدِهِ",
      transliteration = "Subhan-Allahi wa bihamdihi",
      translation = "Glory be to Allah and all praise is His.",
      contemplativeNote = "A two-word key that erases mistakes like sea foam disappearing into the sand.",
      category = "Gratitude",
      source = "Sahih al-Bukhari 6405",
      virtue = "Whoever says this 100 times, their sins will be wiped away even if like sea foam."
    ),
    DhikrItem(
      id = "subhan_wa_bihamdihi_adada",
      arabic = "سُبْحَانَ اللَّٰهِ وَبِحَمْدِهِ عَدَدَ خَلْقِهِ وَرِضَا نَفْسِهِ وَزِنَةَ عَرْشِهِ وَمِدَادَ كَلِمَاتِهِ",
      transliteration = "Subhan-Allahi wa bihamdihi, 'adada khalqihi, wa rida nafsihi, wa zinata 'arshihi, wa midada kalimatihi",
      translation = "Glory be to Allah and praise Him, to the number of His creation, by His pleasure, by the weight of His Throne, and the ink of His words.",
      contemplativeNote = "A comprehensive remembrance heavier than hours of silent sitting.",
      category = "Morning Remembrance",
      source = "Sahih Muslim 2726",
      virtue = "Weighs more than hours of solitary remembrance in the scales of the Almighty."
    ),

    // Morning Remembrance
    DhikrItem(
      id = "asbahna_wa_asbaha",
      arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّٰهِ، وَالْحَمْدُ لِلَّٰهِ",
      transliteration = "Asbahna wa-asbahal-mulku lillah, wal-hamdu lillah",
      translation = "We have reached the morning and the whole dominion belongs to Allah, and all praise is for Allah.",
      contemplativeNote = "Entrusting this newborn dawn entirely into the hands of the Creator.",
      category = "Morning Remembrance",
      source = "Sahih Muslim 2723",
      virtue = "Brings contentment and guidance for the remainder of the day."
    ),
    DhikrItem(
      id = "radheetu_billah",
      arabic = "رَضِيتُ بِاللَّٰهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ ﷺ نَبِيًّا",
      transliteration = "Radheetu billahi Rabba, wa bil-Islami deena, wa bi-Muhammadin (sallallahu 'alayhi wa sallam) nabiyya",
      translation = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad ﷺ as my Prophet.",
      contemplativeNote = "Quiet acceptance. Peace in knowing who you are and where you belong.",
      category = "Morning Remembrance",
      source = "Abu Dawood 5072",
      virtue = "Allah has taken upon Himself to please whoever recites this three times."
    ),
    DhikrItem(
      id = "bismillah_alladhi",
      arabic = "بِسْمِ اللَّٰهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
      transliteration = "Bismillahi-lladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Aleem",
      translation = "In the Name of Allah, with Whose Name nothing on earth or in heaven can cause harm, and He is the All-Hearing, All-Knowing.",
      contemplativeNote = "A divine shield spoken with conviction over yourself and your loved ones.",
      category = "Morning Remembrance",
      source = "Sunan at-Tirmidhi 3388",
      virtue = "Protection against all unexpected harm throughout the day."
    ),

    // Evening Remembrance
    DhikrItem(
      id = "amsayna_wa_amsal",
      arabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّٰهِ، وَالْحَمْدُ لِلَّٰهِ",
      transliteration = "Amsayna wa-amsal-mulku lillah, wal-hamdu lillah",
      translation = "We have reached the evening and the entire dominion belongs to Allah, and praise is due to Allah.",
      contemplativeNote = "Releasing the stresses, meetings, and unfinished work of the day back to Allah.",
      category = "Evening Remembrance",
      source = "Sahih Muslim 2723",
      virtue = "Brings deep stillness as dusk falls upon the earth."
    ),
    DhikrItem(
      id = "sayyid_al_istighfar",
      arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَٰهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَىٰ عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ",
      transliteration = "Allahumma Anta Rabbi la ilaha illa Ant, khalaqtani wa ana 'abduk, wa ana 'ala 'ahdika wa wa'dika mastata't",
      translation = "O Allah, You are my Lord, none has the right to be worshiped but You. You created me and I am Your servant, keeping Your covenant as best I can.",
      contemplativeNote = "The Master supplication of forgiveness. The highest form of honest humility.",
      category = "Evening Remembrance",
      source = "Sahih al-Bukhari 6306",
      virtue = "The Prophet ﷺ called this the Chief of all Prayers for Forgiveness (Sayyid al-Istighfar)."
    ),

    // Forgiveness & Istighfar
    DhikrItem(
      id = "astaghfirullah",
      arabic = "أَسْتَغْفِرُ اللَّٰهَ وَأَتُوبُ إِلَيْهِ",
      transliteration = "Astaghfirullaha wa atoobu ilayh",
      translation = "I seek Allah's forgiveness and turn to Him in true repentance.",
      contemplativeNote = "A gentle return. No burden is too heavy for His vast ocean of mercy.",
      category = "Forgiveness",
      source = "Sahih al-Bukhari 6307",
      virtue = "The Messenger of Allah ﷺ sought forgiveness more than 70 times every day."
    ),
    DhikrItem(
      id = "rabbana_dhalamna",
      arabic = "رَبَّنَا ظَلَمْنَا أَنفُسَنَا وَإِن لَّمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
      transliteration = "Rabbana dhalamna anfusana wa il-lam taghfir lana wa tarhamna lana-koonanna minal-khasireen",
      translation = "Our Lord, we have wronged ourselves, and if You do not forgive us and have mercy upon us, we will surely be among the losers.",
      contemplativeNote = "The timeless prayer of Adam and Eve — humble, honest, and filled with hope.",
      category = "Forgiveness",
      source = "Surah Al-A'raf 7:23",
      virtue = "The prayer by which Allah accepted the repentance of mankind's parents."
    ),

    // Before Sleep
    DhikrItem(
      id = "bismika_rabbi_wada'tu",
      arabic = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي وَبِكَ أَرْفَعُهُ، إِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا",
      transliteration = "Bismika Rabbi wada'tu janbi wa bika arfa'uh, in amsakta nafsi far-hamha",
      translation = "In Your Name my Lord, I lay down my side, and by You I raise it up. If You take my soul, have mercy on it.",
      contemplativeNote = "Resting with a heart purified of grudges, ready to return or wake anew.",
      category = "Before Sleep",
      source = "Sahih al-Bukhari 6320",
      virtue = "Guarantees angelic guardianship through the hours of night."
    ),
    DhikrItem(
      id = "ayat_al_kursi_opening",
      arabic = "اللَّٰهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ",
      transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyum, la ta'khudhuhu sinatun wa la nawm",
      translation = "Allah — there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep.",
      contemplativeNote = "The greatest verse in the Qur'an. Sleep peacefully knowing the One watching you never sleeps.",
      category = "Before Sleep",
      source = "Surah Al-Baqarah 2:255",
      virtue = "Whoever recites it when sleeping will have a guardian from Allah over them until dawn."
    ),

    // Protection & Peace
    DhikrItem(
      id = "la_hawla",
      arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّٰهِ الْعَلِيِّ الْعَظِيمِ",
      transliteration = "La hawla wa la quwwata illa billahil-'Aliyyil-'Azeem",
      translation = "There is no power and no might except through Allah, the Most High, the Supreme.",
      contemplativeNote = "A treasure from beneath the Throne. Surrendering control brings absolute calm.",
      category = "Protection",
      source = "Sahih al-Bukhari 4205",
      virtue = "One of the treasures of Paradise (Kanz min kunooz al-Jannah)."
    ),
    DhikrItem(
      id = "hasbunallah",
      arabic = "حَسْبُنَا اللَّٰهُ وَنِعْمَ الْوَكِيلُ",
      transliteration = "Hasbunallahu wa ni'mal-Wakeel",
      translation = "Allah is sufficient for us, and He is the best Disposer of affairs.",
      contemplativeNote = "Said by Ibrahim (as) facing the fire, and by Muhammad ﷺ facing an army. Unshakeable trust.",
      category = "Protection",
      source = "Surah Ali 'Imran 3:173",
      virtue = "Transforms fear and uncertainty into quiet confidence and tranquility."
    ),

    // Hardship & Ease
    DhikrItem(
      id = "yunus_dua",
      arabic = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
      transliteration = "La ilaha illa Anta subhanaka inni kuntu minaz-zalimeen",
      translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
      contemplativeNote = "Dua of Prophet Yunus (Jonah) in the darkness of the whale. Darkness becomes light.",
      category = "Hardship",
      source = "Surah Al-Anbiya 21:87",
      virtue = "No distressed believer prays with this except that Allah relieves their hardship."
    ),
    DhikrItem(
      id = "inshirah_verse",
      arabic = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا • إِنَّ مَعَ الْعُسْرِ يُسْرًا",
      transliteration = "Fa-inna ma'al-'usri yusra. Inna ma'al-'usri yusra.",
      translation = "For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease.",
      contemplativeNote = "Not after hardship, but with it. The seed of relief is already planted inside the trial.",
      category = "Hardship",
      source = "Surah Ash-Sharh 94:5-6",
      virtue = "A divine promise that one hardship can never overcome twofold ease."
    ),

    // Quranic Gems
    DhikrItem(
      id = "rabbi_zidni_ilma",
      arabic = "رَّبِّ زِدْنِي عِلْمًا",
      transliteration = "Rabbi zidnee 'ilma",
      translation = "My Lord, increase me in knowledge.",
      contemplativeNote = "The only worldly matter the Prophet ﷺ was instructed to ask for more of.",
      category = "Quranic Gems",
      source = "Surah Ta-Ha 20:114",
      virtue = "Opens the chambers of understanding, wisdom, and spiritual clarity."
    ),
    DhikrItem(
      id = "rabbana_atina",
      arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
      transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar",
      translation = "Our Lord, grant us the good of this world and the good of the Hereafter, and save us from the torment of the Fire.",
      contemplativeNote = "The most frequent supplication of the Messenger of Allah ﷺ. Complete balance.",
      category = "Quranic Gems",
      source = "Surah Al-Baqarah 2:201",
      virtue = "Encompasses every form of goodness in this life and the next."
    ),
    DhikrItem(
      id = "salawat_nabawi",
      arabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ",
      transliteration = "Allahumma salli 'ala Muhammadin wa 'ala aali Muhammad",
      translation = "O Allah, send peace and blessings upon Muhammad and upon the family of Muhammad.",
      contemplativeNote = "When you send one prayer upon him, Allah sends ten prayers upon you.",
      category = "Gratitude",
      source = "Sahih Muslim 408",
      virtue = "Elevates ranks, erases sins, and brings immediate peace to the heart."
    )
  )

  val categories = listOf(
    "All",
    "Gratitude",
    "Morning Remembrance",
    "Evening Remembrance",
    "Forgiveness",
    "Before Sleep",
    "Protection",
    "Hardship",
    "Quranic Gems"
  )
}
