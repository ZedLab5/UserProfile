package com.example.data.quran

import com.example.data.model.DailyMoodWisdom
import com.example.data.model.DuaItem

object DuaData {

    val dailyAyah = DuaItem(
        id = "ayah_today",
        category = "Daily Ayah",
        title = "Divine Proximity & Reassurance",
        arabicText = "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ",
        transliteration = "Wa-iḏhā sa'alaka ʿibādī ʿannī fa'innī qarīb, ujību daʿwatad-dāʿi iḏhā daʿān",
        translation = "And when My servants ask you concerning Me - indeed I am near. I respond to the invocation of the supplicant when he calls upon Me.",
        reference = "Surah Al-Baqarah (2:186)",
        occasion = "Daily Spiritual Reflection",
        categoryAr = "آية اليوم",
        referenceAr = "سورة البقرة: ١٨٦"
    )

    val dailyDua = DuaItem(
        id = "dua_today",
        category = "Daily Dua",
        title = "Supplication for Light & Rectification",
        arabicText = "اللَّهُمَّ اجْعَلْ فِي قَلْبِي نُورًا، وَفِي بَصَرِي نُورًا، وَفِي سَمْعِي نُورًا، وَعَنْ يَمِينِي نُورًا، وَعَنْ يَسَارِي نُورًا",
        transliteration = "Allāhumma-jʿal fī qalbī nūrā, wa fī baṣarī nūrā, wa fī samʿī nūrā, wa ʿan yamīnī nūrā, wa ʿan yasārī nūrā",
        translation = "O Allah, place light in my heart, light in my sight, light in my hearing, light on my right, and light on my left.",
        reference = "Sahih Muslim 763",
        occasion = "Morning & Before Prayer",
        categoryAr = "دعاء اليوم",
        referenceAr = "صحيح مسلم ٧٦٣"
    )

    val moodWisdomMap: Map<String, Pair<DailyMoodWisdom, DailyMoodWisdom>> = mapOf(
        "Anxious" to Pair(
            DailyMoodWisdom(
                mood = "Anxious",
                isIslamic = true,
                arabicText = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
                translation = "Allah is sufficient for us, and He is the best Disposer of affairs.",
                source = "Surah Ali 'Imran (3:173)",
                explanation = "A profound divine fortress spoken by Ibrahim (AS) when cast into fire and Prophet Muhammad (PBUH) during imminent danger, transforming distress into unshakeable tranquility.",
                sourceAr = "سورة آل عمران: ١٧٣",
                explanationAr = "حصن رباني عظيم قاله إبراهيم عليه السلام حين ألقي في النار، وقاله النبي ﷺ يوم الأحزاب، فتنقلب به المخاوف إلى سكينة وطمأنينة."
            ),
            DailyMoodWisdom(
                mood = "Anxious",
                isIslamic = false,
                arabicText = "كُن كَالْمَاءِ، لَا يَشُقُّ عَلَيْهِ أَيُّ مَسَارٍ",
                translation = "Present moment mindfulness: Breath is your anchor; peace is not the absence of storm, but stillness within.",
                source = "Mindful Contemplation",
                explanation = "Recognize that your current worry is a transient cognitive wave. Anchor your awareness to the slow rhythm of conscious breath.",
                sourceAr = "تأمل وهدوء النفس",
                explanationAr = "اعلم أن القلق عابر كالغيوم، واستحضر هدوء اللحظة الحاضرة والسكينة الداخلية."
            )
        ),
        "Grateful" to Pair(
            DailyMoodWisdom(
                mood = "Grateful",
                isIslamic = true,
                arabicText = "لَئِن شَكَرْتُمْ لَأَزِيدَنَّكُمْ",
                translation = "If you are grateful, I will surely increase you [in favor].",
                source = "Surah Ibrahim (14:7)",
                explanation = "Gratitude (Shukr) is an active spiritual multiplier. When the heart acknowledges divine blessings, divine abundance and inner peace expand.",
                sourceAr = "سورة إبراهيم: ٧",
                explanationAr = "الشكر مفتاح المزيد وقيد النعم، فحين يستشعر القلب فضل الله تتسع الأرزاق والسكينة."
            ),
            DailyMoodWisdom(
                mood = "Grateful",
                isIslamic = false,
                arabicText = "الامْتِنَانُ رَبِيعُ الرُّوحِ الدَّائِمُ",
                translation = "Gratitude turns what we have into enough, illuminating the hidden abundance of ordinary moments.",
                source = "Universal Wisdom",
                explanation = "Cultivating gratitude rewires neural pathways, fostering emotional resilience and profound contentment.",
                sourceAr = "حكمة الامتنان",
                explanationAr = "الامتنان يحول القليل إلى كفاية، وينير تفاصيل الحياة بالرضا والبهجة."
            )
        ),
        "Seeking Guidance" to Pair(
            DailyMoodWisdom(
                mood = "Seeking Guidance",
                isIslamic = true,
                arabicText = "رَبِّ إِنِّي لِمَا أَنزَلْتَ إِلَيَّ مِنْ خَيْرٍ فَقِيرٌ",
                translation = "My Lord, indeed I am, for whatever good You would send down to me, in need.",
                source = "Surah Al-Qasas (28:24)",
                explanation = "The supplication of Musa (AS) at his moment of absolute vulnerability, which opened immediate doors of sanctuary, family, and honor.",
                sourceAr = "سورة القصص: ٢٤",
                explanationAr = "دعاء موسى عليه السلام في ذروة الافتقار إلى الله، ففتحت له أبواب الخير والأمان والبركة."
            ),
            DailyMoodWisdom(
                mood = "Seeking Guidance",
                isIslamic = false,
                arabicText = "الصَّمْتُ يَفْتَحُ أَبْوَابَ الْبَصِيرَةِ",
                translation = "Clarity does not arrive from turbulent striving, but from quiet listening to your deepest moral compass.",
                source = "Philosophical Insight",
                explanation = "Allow your mind to settle like clear water; the path forward becomes evident when inner noise subsides.",
                sourceAr = "بصيرة وهداية",
                explanationAr = "الوضوح ينبع من سكون القلب والتأمل الصادق حين تهدأ ضوضاء التردد."
            )
        ),
        "Overwhelmed" to Pair(
            DailyMoodWisdom(
                mood = "Overwhelmed",
                isIslamic = true,
                arabicText = "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا",
                translation = "Allah does not burden a soul beyond that it can bear.",
                source = "Surah Al-Baqarah (2:286)",
                explanation = "A sacred guarantee that your soul possesses the exact resilience, capacity, and divine grace required for this trial.",
                sourceAr = "سورة البقرة: ٢٨٦",
                explanationAr = "وعد رباني بأن كل ابتداء في قدرتك وتيسيرك، وأن مع العسر يسراً ولطفاً خفياً."
            ),
            DailyMoodWisdom(
                mood = "Overwhelmed",
                isIslamic = false,
                arabicText = "خُطْوَةٌ وَاحِدَةٌ تَكْفِي لِبَدْءِ الْعَوْدَةِ",
                translation = "You don't have to carry the whole mountain today. Simply focus on this single breath, this single step.",
                source = "Mindful Living",
                explanation = "Deconstruct daunting obstacles into single present-moment actions to restore cognitive equilibrium.",
                sourceAr = "تيسير وتخفيف",
                explanationAr = "لا تحمل هم الأيام دفعة واحدة، ركز في خطوة اليوم وسيرعاك الله في كل خطوة."
            )
        ),
        "Peaceful" to Pair(
            DailyMoodWisdom(
                mood = "Peaceful",
                isIslamic = true,
                arabicText = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                translation = "Unquestionably, by the remembrance of Allah hearts are assured.",
                source = "Surah Ar-Ra'd (13:28)",
                explanation = "The highest state of inner tranquility (Sakinah) is experienced when the soul re-aligns with its eternal Creator.",
                sourceAr = "سورة الرعد: ٢٨",
                explanationAr = "أعظم طمأنينة وراحة ينالها القلب حين يتصل بربه ذكراً وشكراً وتسليماً."
            ),
            DailyMoodWisdom(
                mood = "Peaceful",
                isIslamic = false,
                arabicText = "السَّكِينَةُ هِيَ التَّنَاغُمُ مَعَ الْحَيَاةِ",
                translation = "Peace is an inside state that reflects harmony with reality as it unfolds.",
                source = "Inner Harmony",
                explanation = "Cherish serenity by savoring stillness and radiating kindness to those around you.",
                sourceAr = "سكينة وسلام",
                explanationAr = "السلام الداخلي ينبع من الرضا والتصالح مع أقدار الحياة."
            )
        ),
        "Seeking Forgiveness" to Pair(
            DailyMoodWisdom(
                mood = "Seeking Forgiveness",
                isIslamic = true,
                arabicText = "رَبَّنَا ظَلَمْنَا أَنفُسَنَا وَإِن لَّمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
                translation = "Our Lord, we have wronged ourselves, and if You do not forgive us and have mercy upon us, we will surely be among the losers.",
                source = "Surah Al-A'raf (7:23)",
                explanation = "The eternal prayer of Adam and Hawwa (peace be upon them) demonstrating that sincere humility and repentance instantly restore divine mercy.",
                sourceAr = "سورة الأعراف: ٢٣",
                explanationAr = "دعاء آدم وحواء عليهما السلام، يفيض بالانكسار والتوبة الصادقة المستوجبة للرحمة والمغفرة."
            ),
            DailyMoodWisdom(
                mood = "Seeking Forgiveness",
                isIslamic = false,
                arabicText = "التَّسَامُحُ مَعَ النَّفْسِ هُوَ بِدَايَةُ النُّمُوِّ",
                translation = "Forgiveness is freeing yourself from the weight of past missteps to step forward with renewed grace.",
                source = "Humanistic Reflection",
                explanation = "Mistakes are invitations to grow wiser. Release self-condemnation and commit to righteous action.",
                sourceAr = "توبة واستغفار",
                explanationAr = "الاستغفار يمحو الذنوب ويجدد العهد مع الله لتبدأ كل يوم بروح طاهرة."
            )
        )
    )

    val categories: List<com.example.data.model.DuaCategory> = listOf(
        com.example.data.model.DuaCategory(
            id = "Morning Azkar",
            titleEnglish = "Morning Azkar",
            titleArabic = "أذكار الصباح",
            description = "Divine light & spiritual fortress to start the day",
            itemCount = 6,
            iconType = "morning"
        ),
        com.example.data.model.DuaCategory(
            id = "Evening Azkar",
            titleEnglish = "Evening Azkar",
            titleArabic = "أذكار المساء",
            description = "Tranquility, sanctuary & night protection",
            itemCount = 6,
            iconType = "evening"
        ),
        com.example.data.model.DuaCategory(
            id = "After Salah",
            titleEnglish = "After Salah",
            titleArabic = "أذكار بعد الصلاة",
            description = "Prescribed remembrances following the 5 daily prayers",
            itemCount = 6,
            iconType = "salah"
        ),
        com.example.data.model.DuaCategory(
            id = "Sleep & Awakening",
            titleEnglish = "Sleep & Awakening",
            titleArabic = "أذكار النوم والاستيقاظ",
            description = "Prophetic supplications for rest and waking up",
            itemCount = 5,
            iconType = "sleep"
        ),
        com.example.data.model.DuaCategory(
            id = "Protection & Anxiety",
            titleEnglish = "Protection & Anxiety",
            titleArabic = "الحفظ والسكينة",
            description = "Relief from distress, sorrow, fear & evil eye",
            itemCount = 5,
            iconType = "shield"
        ),
        com.example.data.model.DuaCategory(
            id = "Health & Healing",
            titleEnglish = "Health & Healing",
            titleArabic = "الشفاء والعافية",
            description = "Prophetic Ruqyah and healing for pain and sickness",
            itemCount = 4,
            iconType = "health"
        ),
        com.example.data.model.DuaCategory(
            id = "Travel & Decisions",
            titleEnglish = "Travel & Decisions",
            titleArabic = "السفر والاستخارة",
            description = "Guidance in major life decisions & journey safety",
            itemCount = 4,
            iconType = "travel"
        ),
        com.example.data.model.DuaCategory(
            id = "Quranic Prophetic Duas",
            titleEnglish = "Quranic Duas",
            titleArabic = "أدعية القرآن الكريم",
            description = "Direct prayers of the Prophets (Rabbana) from the Quran",
            itemCount = 6,
            iconType = "quran"
        )
    )

    val categorizedDuas: List<DuaItem> = listOf(
        // --- MORNING AZKAR ---
        DuaItem(
            id = "m1",
            category = "Morning Azkar",
            title = "Master of Forgiveness (Sayyid al-Istighfar)",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            transliteration = "Allāhumma anta Rabbī lā ilāha illā anta, khalaqtanī wa anā ʿabduka, wa anā ʿalā ʿahdika wa waʿdika mastataʿtu, aʿūḏhu bika min sharri mā ṣanaʿtu, abū'u laka biniʿmatika ʿalayya, wa abū'u laka biḏhambī faghfir lī fa'innahū lā yaghfiruḏh-ḏhunūba illā ant",
            translation = "O Allah, You are my Lord; there is no deity except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me, and I acknowledge my sin, so forgive me, for indeed none forgives sins except You.",
            reference = "Sahih al-Bukhari 6306",
            occasion = "Recited every morning",
            repeatCount = 1,
            benefit = "Whoever recites it in the morning with firm faith and dies before evening will enter Paradise."
        ),
        DuaItem(
            id = "m2",
            category = "Morning Azkar",
            title = "Protection Against All Calamity",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillāhillaḏhī lā yaḍurru maʿasmihī shay'un fil-arḍi walā fis-samā'i wa huwas-Samīʿul-ʿAlīm",
            translation = "In the Name of Allah with Whose Name nothing can harm on earth or in the heavens, and He is the All-Hearing, the All-Knowing.",
            reference = "Sunan Abi Dawud 5088",
            occasion = "Morning (3 Times)",
            repeatCount = 3,
            benefit = "Nothing shall harm the believer throughout the entire day."
        ),
        DuaItem(
            id = "m3",
            category = "Morning Azkar",
            title = "Morning Declaration of Dominion & Gratitude",
            arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Aṣbaḥnā wa aṣbaḥal-mulku lillāh, wal-ḥamdu lillāh, lā ilāha illallāhu waḥdahū lā sharīka lah, lahul-mulku wa lahul-ḥamdu wa huwa ʿalā kulli shay'in qadīr",
            translation = "We have entered the morning and the entire dominion belongs to Allah, and all praise is for Allah. There is no deity except Allah alone without partner. To Him belongs sovereignty and praise, and He has power over all things.",
            reference = "Sahih Muslim 2723",
            occasion = "Morning upon awakening",
            repeatCount = 1,
            benefit = "Renews the believer's covenant of Tawhid and submission to Allah."
        ),
        DuaItem(
            id = "m4",
            category = "Morning Azkar",
            title = "Seeking Well-Being in Religion, Body & Life",
            arabicText = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، اللَّهُمَّ اسْتُرْ عَوْرَاتِي وَآمِنْ رَوْعَاتِي",
            transliteration = "Allāhumma innī as'alukal-ʿāfiyata fid-dunyā wal-ākhirah, Allāhumma innī as'alukal-ʿafwa wal-ʿāfiyata fī dīnī wa dunyāya wa ahlī wa mālī, Allāhummastur ʿawrātī wa āmin rawʿātī",
            translation = "O Allah, I ask You for well-being in this life and the Hereafter. O Allah, I ask You for pardon and well-being in my religion, worldly affairs, family, and wealth. O Allah, conceal my faults and reassure my fears.",
            reference = "Sunan Abi Dawud 5074",
            occasion = "Every morning",
            repeatCount = 1,
            benefit = "Complete prophetic shield covering health, faith, wealth, and family."
        ),
        DuaItem(
            id = "m5",
            category = "Morning Azkar",
            title = "Satisfaction with Allah, Islam, and the Prophet",
            arabicText = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
            transliteration = "Raḍītu billāhi Rabbā, wa bil-Islāmi dīnā, wa bi-Muḥammadin ṣallallāhu ʿalayhi wa sallama Nabiyyā",
            translation = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad (peace and blessings be upon him) as my Prophet.",
            reference = "Sunan Abi Dawud 5072",
            occasion = "Morning (3 Times)",
            repeatCount = 3,
            benefit = "Allah has promised to please the servant who says this on the Day of Judgment."
        ),
        DuaItem(
            id = "m6",
            category = "Morning Azkar",
            title = "Tasbih & Praise Equivalent to All Creation",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ: عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ",
            transliteration = "Subḥānallāhi wa biḥamdih: ʿadada khalqih, wa riḍā nafsih, wa zinata ʿarshih, wa midāda kalimātih",
            translation = "Glory is to Allah and praise is to Him: by the number of His creation, by His pleasure, by the weight of His Throne, and by the ink of His words.",
            reference = "Sahih Muslim 2726",
            occasion = "Morning (3 Times)",
            repeatCount = 3,
            benefit = "Weighs more heavily on the scales than hours of ordinary continuous remembrance."
        ),

        // --- EVENING AZKAR ---
        DuaItem(
            id = "e1",
            category = "Evening Azkar",
            title = "Evening Declaration of Kingdom & Praise",
            arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Amsaynā wa amsal-mulku lillāh, wal-ḥamdu lillāh, lā ilāha illallāhu waḥdahū lā sharīka lah, lahul-mulku wa lahul-ḥamdu wa huwa ʿalā kulli shay'in qadīr",
            translation = "We have entered the evening and sovereignty belongs to Allah, and all praise is for Allah. There is no deity except Allah alone without partner. To Him belongs the dominion and praise, and He is over all things capable.",
            reference = "Sahih Muslim 2723",
            occasion = "Recited at sunset / evening",
            repeatCount = 1,
            benefit = "Brings serenity and protection against the trials of the night."
        ),
        DuaItem(
            id = "e2",
            category = "Evening Azkar",
            title = "Seeking Refuge in the Perfect Words of Allah",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            transliteration = "Aʿūḏhu bi-kalimātillāhit-tāmmāti min sharri mā khalaq",
            translation = "I seek refuge in the perfect words of Allah from the evil of that which He has created.",
            reference = "Sahih Muslim 2709",
            occasion = "Evening (3 Times)",
            repeatCount = 3,
            benefit = "No venomous bite, harm, or fever will touch the supplicant that night."
        ),
        DuaItem(
            id = "e3",
            category = "Evening Azkar",
            title = "The 3 Protective Surahs (Al-Mu'awwidhat)",
            arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
            transliteration = "Surah Al-Ikhlas, Surah Al-Falaq, Surah An-Nas",
            translation = "Recite Surah Al-Ikhlas, Al-Falaq, and An-Nas three times in the morning and evening.",
            reference = "Sunan Abi Dawud 5082",
            occasion = "Evening (3 Times each)",
            repeatCount = 3,
            benefit = "They will suffice you against all evils and harm in creation."
        ),
        DuaItem(
            id = "e4",
            category = "Evening Azkar",
            title = "Evening Affirmation of Divine Protection",
            arabicText = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
            transliteration = "Allāhumma bika amsaynā, wa bika aṣbaḥnā, wa bika naḥyā, wa bika namūtu, wa ilaykal-maṣīr",
            translation = "O Allah, by Your grace we have reached the evening, by Your grace we reached the morning, by You we live, by You we die, and to You is our final return.",
            reference = "Sunan at-Tirmidhi 3391",
            occasion = "Evening",
            repeatCount = 1,
            benefit = "Deepens mindful awareness of human mortality and reliance on Allah."
        ),
        DuaItem(
            id = "e5",
            category = "Evening Azkar",
            title = "Supplication for Sufficiency (Hasbiyallahu)",
            arabicText = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            transliteration = "Ḥasbiyallāhu lā ilāha illā huwa ʿalayhi tawakkaltu wa huwa Rabbul-ʿArshil-ʿAẓīm",
            translation = "Allah is sufficient for me; there is no deity except Him. Upon Him I have relied, and He is the Lord of the Great Throne.",
            reference = "Sunan Abi Dawud 5081",
            occasion = "Evening (7 Times)",
            repeatCount = 7,
            benefit = "Allah will suffice the reciter against whatever worries them in this world and the Next."
        ),
        DuaItem(
            id = "e6",
            category = "Evening Azkar",
            title = "Seeking Divine Light & Protection From 4 Directions",
            arabicText = "اللَّهُمَّ احْفَظْنِي مِنْ بَيْنِ يَدَيَّ، وَمِنْ خَلْفِي، وَعَنْ يَمِينِي، وَعَنْ شِمَالِي، وَمِنْ فَوْقِي، وَأَعُوذُ بِعَظَمَتِكَ أَنْ أُغْتَالَ مِنْ تَحْتِي",
            transliteration = "Allāhummaḥfaẓnī mim bayni yadayya, wa min khalfī, wa ʿan yamīnī, wa ʿan shimālī, wa min fawqī, wa aʿūḏhu bi-ʿaẓamatika an ughtāla min taḥtī",
            translation = "O Allah, protect me from in front of me and behind me, from my right and my left, and from above me; and I seek refuge in Your greatness from being struck from beneath me.",
            reference = "Sunan Abi Dawud 5074",
            occasion = "Evening",
            repeatCount = 1,
            benefit = "A comprehensive 6-directional spiritual fortress of protection."
        ),

        // --- AFTER SALAH ---
        DuaItem(
            id = "s1",
            category = "After Salah",
            title = "Seeking Forgiveness & Supplication for Peace",
            arabicText = "أَسْتَغْفِرُ اللَّهَ (٣x)، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            transliteration = "Astaghfirullāh (3x), Allāhumma antas-Salāmu wa minkas-salām, tabārakta yā Ḏhal-Jalāli wal-Ikrām",
            translation = "I seek the forgiveness of Allah (3 times). O Allah, You are Peace and from You comes peace. Blessed are You, O Possessor of majesty and honor.",
            reference = "Sahih Muslim 591",
            occasion = "Immediately following the Salam of every obligatory prayer",
            repeatCount = 3,
            benefit = "Purifies any shortcomings during prayer and invokes divine serenity."
        ),
        DuaItem(
            id = "s2",
            category = "After Salah",
            title = "The Throne Verse (Ayat al-Kursi)",
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ",
            transliteration = "Allāhu lā ilāha illā huwal-ḥayyul-qayyūm...",
            translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence...",
            reference = "Sunan an-Nasa'i 9848",
            occasion = "After every obligatory prayer",
            repeatCount = 1,
            benefit = "Whoever recites it after every prescribed prayer, nothing stands between him and entering Paradise except death."
        ),
        DuaItem(
            id = "s3",
            category = "After Salah",
            title = "SubhanAllah (Glory be to Allah)",
            arabicText = "سُبْحَانَ اللَّهِ",
            transliteration = "Subḥānallāh",
            translation = "Glory be to Allah in His infinite perfection.",
            reference = "Sahih Muslim 597",
            occasion = "Post-Salah Tasbih (33 Times)",
            repeatCount = 33,
            benefit = "Part of the sacred 99-bead post-prayer remembrance."
        ),
        DuaItem(
            id = "s4",
            category = "After Salah",
            title = "Alhamdulillah (All Praise to Allah)",
            arabicText = "الْحَمْدُ لِلَّهِ",
            transliteration = "Al-ḥamdu lillāh",
            translation = "All praise and gratitude are due exclusively to Allah.",
            reference = "Sahih Muslim 597",
            occasion = "Post-Salah Tahmid (33 Times)",
            repeatCount = 33,
            benefit = "Fills the heavenly scales with radiant spiritual reward."
        ),
        DuaItem(
            id = "s5",
            category = "After Salah",
            title = "Allahu Akbar (Allah is the Greatest)",
            arabicText = "اللَّهُ أَكْبَرُ",
            transliteration = "Allāhu Akbar",
            translation = "Allah is Greater than all creation, worry, and difficulty.",
            reference = "Sahih Muslim 597",
            occasion = "Post-Salah Takbir (33 Times)",
            repeatCount = 33,
            benefit = "Elevates the soul and magnifies divine grandeur."
        ),
        DuaItem(
            id = "s6",
            category = "After Salah",
            title = "Seal of the 100 Post-Prayer Remembrances",
            arabicText = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Lā ilāha illallāhu waḥdahū lā sharīka lah, lahul-mulku wa lahul-ḥamdu, wa huwa ʿalā kulli shay'in qadīr",
            translation = "There is no deity except Allah alone without partner. To Him belongs all sovereignty and praise, and He has power over everything.",
            reference = "Sahih Muslim 597",
            occasion = "100th Tasbih Completion",
            repeatCount = 1,
            benefit = "Sins will be forgiven even if they are like the foam of the sea."
        ),

        // --- SLEEP & AWAKENING ---
        DuaItem(
            id = "sl1",
            category = "Sleep & Awakening",
            title = "Supplication Before Sleeping",
            arabicText = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
            transliteration = "Bismika Rabbī waḍaʿtu jambī wa bika arfaʿuh, fa'in amsakta nafsī farḥamhā, wa in arsaltahā faḥfaẓhā bimā taḥfaẓu bihī ʿibādakaṣ-ṣāliḥīn",
            translation = "In Your Name my Lord, I lay down my side and by You I raise it up. If You take my soul, have mercy upon it, and if You send it back, protect it as You protect Your righteous servants.",
            reference = "Sahih al-Bukhari 6320",
            occasion = "Before sleeping on the right side",
            repeatCount = 1,
            benefit = "Entrusts the sleeping soul directly into Allah's gentle custody."
        ),
        DuaItem(
            id = "sl2",
            category = "Sleep & Awakening",
            title = "Short Dua for Sleep with Divine Name",
            arabicText = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            transliteration = "Bismikallāhumma amūtu wa aḥyā",
            translation = "In Your Name, O Allah, I die and I live.",
            reference = "Sahih al-Bukhari 6312",
            occasion = "Immediately before closing eyes",
            repeatCount = 1,
            benefit = "Attunes the heart to the divine name as consciousness fades."
        ),
        DuaItem(
            id = "sl3",
            category = "Sleep & Awakening",
            title = "Dua Upon Awakening from Sleep",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Al-ḥamdu lillāhillaḏhī aḥyānā baʿda mā amātanā wa ilayhin-nushūr",
            translation = "All praise is for Allah who gave us life after having given us death, and unto Him is the resurrection.",
            reference = "Sahih al-Bukhari 6312",
            occasion = "Upon opening eyes in the morning",
            repeatCount = 1,
            benefit = "Starts the morning with instant gratitude for the gift of life."
        ),
        DuaItem(
            id = "sl4",
            category = "Sleep & Awakening",
            title = "Tasbih of Fatimah (RA) Before Bed",
            arabicText = "سُبْحَانَ اللَّهِ (٣٣x)، الْحَمْدُ لِلَّهِ (٣٣x)، اللَّهُ أَكْبَرُ (٣٤x)",
            transliteration = "SubhanAllah (33x), Alhamdulillah (33x), Allahu Akbar (34x)",
            translation = "Glorifying, praising, and magnifying Allah before sleep totaling 100.",
            reference = "Sahih al-Bukhari 5361",
            occasion = "Bedtime ritual",
            repeatCount = 3,
            benefit = "Gives spiritual vigor and strength greater than any servant or worldly helper."
        ),
        DuaItem(
            id = "sl5",
            category = "Sleep & Awakening",
            title = "Last 2 Verses of Surah Al-Baqarah (285-286)",
            arabicText = "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ...",
            transliteration = "Āmanar-Rasūlu bimā unzila ilayhi mir-Rabbihī wal-mu'minūn...",
            translation = "The Messenger has believed in what was revealed to him from his Lord, and so have the believers...",
            reference = "Sahih al-Bukhari 5009",
            occasion = "Night recitation before sleep",
            repeatCount = 1,
            benefit = "Whoever recites them at night, they will suffice him against every evil."
        ),

        // --- PROTECTION & ANXIETY ---
        DuaItem(
            id = "p1",
            category = "Protection & Anxiety",
            title = "Dua for Alleviating Anxiety, Sorrow & Heavy Debt",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
            transliteration = "Allāhumma innī aʿūḏhu bika minal-hammi wal-ḥazan, wal-ʿajzi wal-kasal, wal-bukhli wal-jubn, wa ḍalaʿid-dayni wa ghalabatir-rijāl",
            translation = "O Allah, I seek refuge in You from anxiety and sorrow, from weakness and laziness, from miserliness and cowardice, and from the burden of debt and being overpowered by men.",
            reference = "Sahih al-Bukhari 2893",
            occasion = "Times of psychological distress or financial strain",
            repeatCount = 1,
            benefit = "Dissolves emotional paralysis and breaks the spiritual weight of debt."
        ),
        DuaItem(
            id = "p2",
            category = "Protection & Anxiety",
            title = "Dua of Prophet Yunus (AS) in Deep Anguish",
            arabicText = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            transliteration = "Lā ilāha illā anta subḥānaka innī kuntu minaẓ-ẓālimīn",
            translation = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            reference = "Surah Al-Anbiya (21:87)",
            occasion = "Direct relief from deep darkness and crises",
            repeatCount = 1,
            benefit = "No Muslim supplicates with this in any distress except that Allah relieves him."
        ),
        DuaItem(
            id = "p3",
            category = "Protection & Anxiety",
            title = "Supplication of Absolute Reliance (Tawakkul)",
            arabicText = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            transliteration = "Ḥasbunallāhu wa niʿmal-wakīl",
            translation = "Allah is sufficient for us, and He is the best Disposer of affairs.",
            reference = "Surah Ali 'Imran (3:173)",
            occasion = "Times of danger or uncertainty",
            repeatCount = 7,
            benefit = "Spoken by Ibrahim (AS) in fire and Muhammad (PBUH) in battle."
        ),
        DuaItem(
            id = "p4",
            category = "Protection & Anxiety",
            title = "Relief from Difficult Affairs & Hardships",
            arabicText = "اللَّهُمَّ لَا سَهْلَ إِلَّا مَا جَعَلْتَهُ سَهْلًا، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلًا",
            transliteration = "Allāhumma lā sahla illā mā jaʿaltahū sahlā, wa anta tajʿalul-ḥazna iḏhā shi'ta sahlā",
            translation = "O Allah, there is no ease except that which You make easy, and You make hardship easy if You will.",
            reference = "Sahih Ibn Hibban 974",
            occasion = "Before exams, difficult tasks, or challenging interviews",
            repeatCount = 1,
            benefit = "Opens closed pathways and softens challenging circumstances."
        ),
        DuaItem(
            id = "p5",
            category = "Protection & Anxiety",
            title = "Seeking Protection from the Evil Eye and Devils",
            arabicText = "أُعِيذُكُمْ بِكَلِمَاتِ اللَّهِ التَّامَّةِ مِنْ كُلِّ شَيْطَانٍ وَهَامَّةٍ، وَمِنْ كُلِّ عَيْنٍ لَامَّةٍ",
            transliteration = "Uʿīḏhukum bi-kalimātillāhit-tāmmati min kulli shayṭānin wa hāmmah, wa min kulli ʿaynin lāmmah",
            translation = "I seek refuge for you in the perfect words of Allah from every devil and poisonous creature, and from every envious evil eye.",
            reference = "Sahih al-Bukhari 3371",
            occasion = "Protection for children and loved ones",
            repeatCount = 3,
            benefit = "The exact prayer the Prophet (PBUH) recited over Hasan and Husain."
        ),

        // --- HEALTH & HEALING ---
        DuaItem(
            id = "h1",
            category = "Health & Healing",
            title = "Prophetic Healing for Illness and Bodily Pain",
            arabicText = "اللَّهُمَّ رَبَّ النَّاسِ أَذْهِبِ الْبَاسَ، اشْفِهِ وَأَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
            transliteration = "Allāhumma Rabban-nās aḏhhibil-ba's, ishfi wa anta-Shāfī, lā shifā'a illā shifā'uk, shifā'an lā yughādiru saqamā",
            translation = "O Allah, Lord of mankind, remove the hardship, heal, for You are the Healer. There is no cure except Your cure, a cure that leaves behind no illness.",
            reference = "Sahih al-Bukhari 5743",
            occasion = "Visiting the sick or self-ruqyah",
            repeatCount = 3,
            benefit = "Invoke Allah's supreme attribute as As-Shafi (The Absolute Healer)."
        ),
        DuaItem(
            id = "h2",
            category = "Health & Healing",
            title = "7-Times Healing Supplication for the Sick",
            arabicText = "أَسْأَلُ اللَّهَ الْعَظِيمَ رَبَّ الْعَرْشِ الْعَظِيمِ أَنْ يَشْفِيَكَ",
            transliteration = "As'alullāhal-ʿAẓīma Rabbal-ʿArshil-ʿAẓīmi an yashfiyak",
            translation = "I ask Allah the Almighty, Lord of the Magnificent Throne, to cure you.",
            reference = "Sunan Abi Dawud 3106",
            occasion = "Recited 7 times over sick persons",
            repeatCount = 7,
            benefit = "Prophet (PBUH) promised healing for the sick person unless appointed death has come."
        ),
        DuaItem(
            id = "h3",
            category = "Health & Healing",
            title = "Hand on Pain Ruqyah (Bismillah 3x + A'udhu 7x)",
            arabicText = "بِسْمِ اللَّهِ (٣x)، أَعُوذُ بِاللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (٧x)",
            transliteration = "Bismillāh (3x), Aʿūḏhu billāhi wa qudratihī min sharri mā ajidu wa uḥāḏhir (7x)",
            translation = "In the Name of Allah (3 times), I seek refuge in Allah and His power from the evil of what I feel and fear (7 times).",
            reference = "Sahih Muslim 2202",
            occasion = "Place right hand on location of pain",
            repeatCount = 7,
            benefit = "Subdues acute physical pain and invites restorative divine grace."
        ),
        DuaItem(
            id = "h4",
            category = "Health & Healing",
            title = "Dua of Prophet Ayyub (AS) in Chronic Illness",
            arabicText = "أَنِّي مَسَّنِيَ الضُّرُّ وَأَنتَ أَرْحَمُ الرَّاحِمِينَ",
            transliteration = "Annī massaniyaḍ-ḍurru wa anta arḥamur-rāḥimīn",
            translation = "Indeed, adversity has touched me, and You are the Most Merciful of the merciful.",
            reference = "Surah Al-Anbiya (21:83)",
            occasion = "Prolonged illness or suffering",
            repeatCount = 1,
            benefit = "A masterclass in respectful humility that unlocked miraculous recovery."
        ),

        // --- TRAVEL & DECISIONS ---
        DuaItem(
            id = "t1",
            category = "Travel & Decisions",
            title = "Prophetic Supplication for Journey & Travel",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ، اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَٰذَا الْبِرَّ وَالتَّقْوَى",
            transliteration = "Subḥānallaḏhī sakhkhara lanā hāḏhā wa mā kunnā lahū muqrinīn, wa innā ilā Rabbinā lamunqalibūn. Allāhumma innā nas'aluka fī safarinā hāḏhal-birra wat-taqwā",
            translation = "Glory to Him who has subjected this to us, and we could never have accomplished it ourselves, and indeed to our Lord we will return. O Allah, we ask You in this journey of ours for righteousness and piety.",
            reference = "Sahih Muslim 1342",
            occasion = "Boarding vehicles, planes, or starting trips",
            repeatCount = 1,
            benefit = "Ensures angel accompaniment and divine safety throughout the voyage."
        ),
        DuaItem(
            id = "t2",
            category = "Travel & Decisions",
            title = "Supplication for Seeking Divine Counsel (Salat al-Istikhara)",
            arabicText = "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ، وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ، وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ، وَتَعْلَمُ وَلَا أَعْلَمُ، وَأَنْتَ عَلَّامُ الْغُيُوبِ",
            transliteration = "Allāhumma innī astakhīruka biʿilmika, wa astaqdiruka biqudratika, wa as'aluka min faḍlikal-ʿaẓīm, fa'innaka taqdiru walā aqdir, wa taʿlamu walā aʿlam, wa anta ʿAllāmul-ghuyūb",
            translation = "O Allah, I seek Your guidance by virtue of Your knowledge, and I seek ability by virtue of Your power, and I ask You of Your great bounty. For indeed You are capable and I am not, and You know and I do not, and You are the Knower of the unseen.",
            reference = "Sahih al-Bukhari 1162",
            occasion = "Major decisions (marriage, work, ventures)",
            repeatCount = 1,
            benefit = "Aligns life decisions with divine blessing and removes regret."
        ),
        DuaItem(
            id = "t3",
            category = "Travel & Decisions",
            title = "Dua When Leaving the Home",
            arabicText = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "Bismillāhi tawakkaltu ʿalallāhi wa lā ḥawla wa lā quwwata illā billāh",
            translation = "In the Name of Allah, I place my trust in Allah; there is no might nor power except with Allah.",
            reference = "Sunan Abi Dawud 5095",
            occasion = "Stepping out through the front door",
            repeatCount = 1,
            benefit = "It is said to him: You are guided, defended, and protected, and Satan retreats."
        ),
        DuaItem(
            id = "t4",
            category = "Travel & Decisions",
            title = "Dua Upon Entering the Home",
            arabicText = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا",
            transliteration = "Bismillāhi walajnā, wa bismillāhi kharajnā, wa ʿalallāhi Rabbinā tawakkalnā",
            translation = "In the Name of Allah we enter, and in the Name of Allah we leave, and upon Allah our Lord we place our trust.",
            reference = "Sunan Abi Dawud 5096",
            occasion = "Entering home with peace to family",
            repeatCount = 1,
            benefit = "Prevents Satan from lodging or partaking in food within the home."
        ),

        // --- QURANIC PROPHETIC DUAS ---
        DuaItem(
            id = "q1",
            category = "Quranic Prophetic Duas",
            title = "Dua for Goodness in Both Worlds (Rabbana Atina)",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbanā ātinā fid-dunyā ḥasanatan wa fil-ākhirati ḥasanatan wa qinā ʿaḏhāban-nār",
            translation = "Our Lord, give us in this world that which is good and in the Hereafter that which is good, and protect us from the punishment of the Fire.",
            reference = "Surah Al-Baqarah (2:201)",
            occasion = "The most frequent supplication of Prophet Muhammad (PBUH)",
            repeatCount = 3,
            benefit = "Encompasses every worldly and heavenly blessing and safeguard."
        ),
        DuaItem(
            id = "q2",
            category = "Quranic Prophetic Duas",
            title = "Supplication for Increased Knowledge & Wisdom",
            arabicText = "رَّبِّ زِدْنِي عِلْمًا",
            transliteration = "Rabbi zidnī ʿilmā",
            translation = "My Lord, increase me in knowledge.",
            reference = "Surah Ta-Ha (20:114)",
            occasion = "Before studying or reading Quran",
            repeatCount = 3,
            benefit = "The only worldly blessing Allah commanded the Prophet to ask for an increase in."
        ),
        DuaItem(
            id = "q3",
            category = "Quranic Prophetic Duas",
            title = "Dua for Steadfastness in Faith & Guidance",
            arabicText = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ",
            transliteration = "Rabbanā lā tuzigh qulūbanā baʿda iḏh hadaytanā wa hab lanā mil-ladunka raḥmah, innaka antal-Wahhāb",
            translation = "Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy. Indeed, You are the Bestower.",
            reference = "Surah Ali 'Imran (3:8)",
            occasion = "Daily spiritual anchoring",
            repeatCount = 1,
            benefit = "Guards the soul against doubt, heedlessness, and spiritual wavering."
        ),
        DuaItem(
            id = "q4",
            category = "Quranic Prophetic Duas",
            title = "Supplication of Ibrahim (AS) for Salah & Progeny",
            arabicText = "رَبِّ اجْعَلْنِي مُقِيمَ الصَّلَاةِ وَمِن ذُرِّيَّتِي ۚ رَبَّنَا وَتَقَبَّلْ دُعَاءِ",
            transliteration = "Rabbij-ʿalnī muqīmaṣ-ṣalāti wa min ḏhurriyyatī, Rabbanā wa taqabbal duʿā'",
            translation = "My Lord, make me an establisher of prayer, and from my descendants. Our Lord, and accept my supplication.",
            reference = "Surah Ibrahim (14:40)",
            occasion = "After prayer and for family welfare",
            repeatCount = 1,
            benefit = "Brings deep Khushu to prayer and righteous guidance to children."
        ),
        DuaItem(
            id = "q5",
            category = "Quranic Prophetic Duas",
            title = "Dua for Parents (Mercy & Gratitude)",
            arabicText = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            transliteration = "Rabbir-ḥamhumā kamā rabbayānī ṣaghīrā",
            translation = "My Lord, have mercy upon them both as they brought me up when I was small.",
            reference = "Surah Al-Isra (17:24)",
            occasion = "For living and departed parents",
            repeatCount = 3,
            benefit = "One of the highest duties of filial piety (Birr al-Walidayn)."
        ),
        DuaItem(
            id = "q6",
            category = "Quranic Prophetic Duas",
            title = "Dua for Joy in Spouse, Family & Leadership",
            arabicText = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            transliteration = "Rabbanā hab lanā min azwājinā wa ḏhurriyyātinā qurrata aʿyuniw-wajʿalnā lil-muttaqīna imāmā",
            translation = "Our Lord, grant us from among our spouses and offspring comfort to our eyes and make us an example for the righteous.",
            reference = "Surah Al-Furqan (25:74)",
            occasion = "For marital harmony and family blessing",
            repeatCount = 1,
            benefit = "Supplication of the beloved Servants of the Most Merciful (Ibad ur-Rahman)."
        )
    )
}
