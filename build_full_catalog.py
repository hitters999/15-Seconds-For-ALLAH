import urllib.request
import json
import time

def fetch_json(url):
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    with urllib.request.urlopen(req, timeout=15) as resp:
        return json.loads(resp.read().decode('utf-8'))

all_items = []

print("Fetching Juz 30 Arabic...")
ar_data = fetch_json("https://api.alquran.cloud/v1/juz/30/quran-uthmani")
time.sleep(1)

print("Fetching Juz 30 Urdu...")
ur_data = fetch_json("https://api.alquran.cloud/v1/juz/30/ur.jalandhry")
time.sleep(1)

print("Fetching Juz 30 English...")
en_data = fetch_json("https://api.alquran.cloud/v1/juz/30/en.sahih")

ar_ayahs = ar_data['data']['ayahs']
ur_ayahs = ur_data['data']['ayahs']
en_ayahs = en_data['data']['ayahs']

surah_urdu_names = {
    78: "سورۃ النبأ", 79: "سورۃ النازعات", 80: "سورۃ عبس", 81: "سورۃ التکویر",
    82: "سورۃ الانفطار", 83: "سورۃ المطففین", 84: "سورۃ الانشقاق", 85: "سورۃ البروج",
    86: "سورۃ الطارق", 87: "سورۃ الاعلیٰ", 88: "سورۃ الغاشیۃ", 89: "سورۃ الفجر",
    90: "سورۃ البلد", 91: "سورۃ الشمس", 92: "سورۃ اللیل", 93: "سورۃ الضحیٰ",
    94: "سورۃ الشرح", 95: "سورۃ التین", 96: "سورۃ العلق", 97: "سورۃ القدر",
    98: "سورۃ البینۃ", 99: "سورۃ الزلزال", 100: "سورۃ العادیات", 101: "سورۃ القارعۃ",
    102: "سورۃ التکاثر", 103: "سورۃ العصر", 104: "سورۃ الہمزۃ", 105: "سورۃ الفیل",
    106: "سورۃ قریش", 107: "سورۃ الماعون", 108: "سورۃ الکوثر", 109: "سورۃ الکافرون",
    110: "سورۃ النصر", 111: "سورۃ المسد", 112: "سورۃ الاخلاص", 113: "سورۃ الفلق",
    114: "سورۃ الناس"
}

# 1. Add Juz 30 Ayahs (564 items)
for i in range(len(ar_ayahs)):
    ar_item = ar_ayahs[i]
    ur_item = ur_ayahs[i]
    en_item = en_ayahs[i]

    s_num = ar_item['surah']['number']
    s_eng = ar_item['surah']['englishName']
    s_ur = surah_urdu_names.get(s_num, ar_item['surah']['name'])
    a_num = ar_item['numberInSurah']

    clean_ar = ar_item['text'].replace("بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", "").strip()
    if not clean_ar:
        clean_ar = ar_item['text']

    all_items.append({
        "id": f"quran_{s_num}_{a_num}",
        "arabic": clean_ar,
        "transliteration": f"{s_eng} [{s_num}:{a_num}]",
        "translationUrdu": ur_item['text'],
        "translation": en_item['text'],
        "contemplativeNote": f"{s_ur} کی مبارک آیت میں تدبر کریں اور اللہ کی رحمت کے طلبگار ہوں۔",
        "category": "Juz 30 (تیسواں پارہ)",
        "source": f"{s_ur} ({s_eng} {s_num}:{a_num})",
        "virtue": "قرآن مجید کا ایک حرف پڑھنے پر دس نیکیوں کا ثواب ملتا ہے۔",
        "defaultDurationSeconds": 15
    })

print(f"Juz 30 added: {len(all_items)} items")

# 2. Add 99 Names of Allah
import generate_part1
all_items.extend(generate_part1.items)
print(f"After Asma ul Husna: {len(all_items)} items")

# 3. Add Adhkar, Hadith, Duas to reach 1000+
with open("builder_adhkar_hadith.py") as f:
    code = f.read()
    exec(code, globals())

print(f"Total sacred items compiled: {len(all_items)}")

with open("app/src/main/assets/sacred_catalog.json", "w", encoding="utf-8") as out:
    json.dump(all_items, out, ensure_ascii=False, indent=2)

print("Saved successfully to app/src/main/assets/sacred_catalog.json!")
