package com.example.ui.tools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.localization.tr
import com.example.ui.MainViewModel
import com.example.ui.NoorDestination
import com.example.ui.SalatTab
import com.example.ui.components.NoorGlassIconButton
import com.example.ui.components.NoorTopBar
import com.example.ui.components.NoorTopBarGradient
import com.example.ui.home.IslamicIconAzkar
import com.example.ui.home.IslamicIconDua
import com.example.ui.home.IslamicIconMushaf
import com.example.ui.home.IslamicIconQibla
import com.example.ui.home.IslamicIconQuranAudio
import com.example.ui.home.IslamicIconSalat
import com.example.ui.home.IslamicIconTasbeeh
import com.example.ui.home.IslamicIconTask

private val NoorTealDark = Color(0xFF099382)
private val NoorTealVibrant = Color(0xFF13A795)
private val NoorDarkPine = Color(0xFF10261F)
private val NoorSageSlate = Color(0xFF5A756C)
private val NoorGoldAccent = Color(0xFFD4A340)
private val NoorGoldSoft = Color(0xFFFAF3E6)
private val NoorGoldBorder = Color(0xFFE8D4A8)
private val NoorCardBorder = Color(0xFFE2EBE6)
private val NoorSurfaceSoft = Color(0xFFF6FAF8)
private val NoorSoftGreenBg = Color(0xFFF2F8F5)
private val NoorSoftGreenBorder = Color(0xFFCCE4DC)

enum class ToolCategory(val enName: String, val arName: String) {
    ALL("All Tools", "جميع الأدوات"),
    QURAN_AUDIO("Quran & Audio", "القرآن والتلاوات"),
    PRAYER_QIBLA("Prayer & Qibla", "الصلاة والقبلة"),
    DHIKR_DUAS("Dhikr & Duas", "الأذكار والأدعية"),
    STREAKS_HABITS("Streaks & Habits", "الالتزام والعادات"),
    CLOUD_SETTINGS("Cloud & Settings", "الإعدادات والنسخ")
}

data class ToolItem(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val subtitleEn: String,
    val subtitleAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val category: ToolCategory,
    val badgeEn: String? = null,
    val badgeAr: String? = null,
    val isFeatured: Boolean = false,
    val iconContent: @Composable (Color) -> Unit,
    val onClick: (MainViewModel) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllToolsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isArabic = appLanguage == "ar"

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ToolCategory.ALL) }

    val allTools = remember {
        listOf(
            // 1. MP3 Quran Player (Featured)
            ToolItem(
                id = "mp3_quran",
                titleEn = "MP3 Quran Player",
                titleAr = "مشغل تلاوات القرآن الكريم",
                subtitleEn = "High Quality Audio Recitations",
                subtitleAr = "تلاوات قرآنية عذبة بصوت نخبة القراء",
                descriptionEn = "Stream full surahs in the background with continuous playback, repeat modes, and surah selection.",
                descriptionAr = "استمع إلى التلاوات العذبة في الخلفية مع خاصية التكرار واختيار السور والتنقل السلس.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "MP3 Audio",
                badgeAr = "صوتيات MP3",
                isFeatured = true,
                iconContent = { tint -> IslamicIconQuranAudio(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QURAN_AUDIO_STREAM) }
            ),

            // 2. Spiritual Streaks & Milestones (Featured)
            ToolItem(
                id = "streaks",
                titleEn = "Spiritual Streaks & Badges",
                titleAr = "سلسلة الالتزام والأوسمة الروحانية",
                subtitleEn = "Daily Devotion & Level Milestones",
                subtitleAr = "سجل المواظبة اليومية والأوسمة المكتسبة",
                descriptionEn = "Track consecutive days of prayer, Quran reading, dhikr, and unlock spiritual achievement badges.",
                descriptionAr = "حافظ على استمرارية العبادات اليومية، وتتبع سجل إنجازاتك وافتح أوسمة التميز الروحاني.",
                category = ToolCategory.STREAKS_HABITS,
                badgeEn = "Streaks & XP",
                badgeAr = "سلسلة وأوسمة",
                isFeatured = true,
                iconContent = { tint -> Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.STREAKS) }
            ),

            // 3. Holy Qur'an Reader
            ToolItem(
                id = "quran_reader",
                titleEn = "Holy Qur'an (114 Surahs)",
                titleAr = "المصحف الشريف (١١٤ سورة)",
                subtitleEn = "Complete Quran with Translation & Audio",
                subtitleAr = "المصحف الكامل بالرسم العثماني والترجمة",
                descriptionEn = "Read all 114 Surahs with Arabic calligraphy, English translation, transliteration, Tajweed marks, and verse audio.",
                descriptionAr = "تصفح القرآن الكريم كاملاً مع الترجمة الإنجليزية، التفسير، وعلامات التجويد والاستماع لكل آية.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "114 Surahs",
                badgeAr = "١١٤ سورة",
                iconContent = { tint -> IslamicIconMushaf(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QURAN_SURAH_LIST) }
            ),

            // 4. Quran Khatma Plan Tracker (Featured)
            ToolItem(
                id = "quran_khatma",
                titleEn = "Quran Khatma Planner",
                titleAr = "خطة ختمة القرآن الكريم",
                subtitleEn = "30-Day, Ramadan & Custom Khatmas",
                subtitleAr = "خطط ختمة مخصصة مع تتبع الإنجاز اليومي",
                descriptionEn = "Create custom Khatma plans, track your daily Juz & page pace, view completion streaks, and auto-resume reading.",
                descriptionAr = "أنشئ خطتك لختم القرآن (٣٠ يوماً، رمضان، أو مخصص)، وتابع وردك اليومي ونسبة الإنجاز.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "Goal Planner",
                badgeAr = "مخطط الختمة",
                isFeatured = true,
                iconContent = { tint -> Icon(Icons.Default.Bookmark, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QURAN_KHATMA) }
            ),

            // 5. Top Global Reciters
            ToolItem(
                id = "quran_reciters",
                titleEn = "Famous Quran Reciters",
                titleAr = "مشاهير قراء العالم الإسلامي",
                subtitleEn = "20+ Renowned Global Qaris",
                subtitleAr = "أكثر من ٢٠ قارئاً من كبار قراء العالم",
                descriptionEn = "Explore recitations from Mishary Alafasy, Abdulbasit, Al-Ghamdi, Sudais, Al-Minshawi, Al-Husary, and more.",
                descriptionAr = "اختر قارئك المفضل من بين نخبة من أشهر القراء: مشاري العفاسي، عبدالباسط، الغامدي، السديس، المنشاوي.",
                category = ToolCategory.QURAN_AUDIO,
                badgeEn = "20+ Reciters",
                badgeAr = "٢٠+ قارئ",
                iconContent = { tint -> Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QURAN_RECITERS) }
            ),

            // 6. Salat & Prayer Times
            ToolItem(
                id = "salat_times",
                titleEn = "Prayer Times & Adhan",
                titleAr = "مواقيت الصلاة والأذان",
                subtitleEn = "Accurate GPS-Based Daily Schedules",
                subtitleAr = "أوقات الأذان والصلوات بدقة الموقع الجغرافي",
                descriptionEn = "Live prayer times for Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha, and Qiyam with next prayer countdown.",
                descriptionAr = "مواقيت الصلاة الخمس مع الشروق وقيام الليل والعد التنازلي للأذان القادم مع التنبيهات.",
                category = ToolCategory.PRAYER_QIBLA,
                badgeEn = "GPS Times",
                badgeAr = "مواقيت دقيقة",
                iconContent = { tint -> IslamicIconSalat(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateToSalat(SalatTab.TIMES) }
            ),

            // 7. Prayer Performance Tracker
            ToolItem(
                id = "prayer_tracker",
                titleEn = "Salat Fulfillment Tracker",
                titleAr = "سجل أداء ومتابعة الصلوات",
                subtitleEn = "Daily & Weekly Prayer Logs",
                subtitleAr = "تسجيل الصلوات في وقتها وجماعة",
                descriptionEn = "Log your daily prayers (On-time, Jama'ah, Late), view completion rates, and build prayer consistency.",
                descriptionAr = "سجل صلواتك اليومية (في وقتها، جماعة)، وتابع مخطط التزامك الأسبوعي والشهري.",
                category = ToolCategory.PRAYER_QIBLA,
                badgeEn = "Daily Log",
                badgeAr = "سجل يومي",
                iconContent = { tint -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateToSalat(SalatTab.STREAKS) }
            ),

            // 8. Qada Missed Prayer Manager
            ToolItem(
                id = "qada_prayers",
                titleEn = "Qada Prayers Manager",
                titleAr = "حاسبة وسجل قضاء الفوائت",
                subtitleEn = "Track & Repay Missed Prayers",
                subtitleAr = "متابعة وقضاء الصلوات الفائتة بسهولة",
                descriptionEn = "Keep count of missed prayers across Fajr, Dhuhr, Asr, Maghrib, and Isha with easy one-tap decrementing.",
                descriptionAr = "تتبع وقضاء الصلوات الفائتة مع عداد إلكتروني سهل الاستخدام لكل صلاة.",
                category = ToolCategory.PRAYER_QIBLA,
                badgeEn = "Qada Counter",
                badgeAr = "قضاء الفوائت",
                iconContent = { tint -> Icon(Icons.Default.AccessTime, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateToSalat(SalatTab.QADA) }
            ),

            // 9. Qibla Direction Compass
            ToolItem(
                id = "qibla_compass",
                titleEn = "Qibla Direction Compass",
                titleAr = "بوصلة اتجاه القبلة الذكية",
                subtitleEn = "Live Direction to Holy Kaaba",
                subtitleAr = "تحديد اتجاه الكعبة المشرفة بدقة متناهية",
                descriptionEn = "Interactive sensor-based 3D compass with degree heading, distance to Makkah, and haptic feedback on alignment.",
                descriptionAr = "بوصلة تفاعلية بحساسات الجهاز ترشدك مباشرة للكعبة المشرفة بمكة المكرمة مع المسافة والاهتزاز.",
                category = ToolCategory.PRAYER_QIBLA,
                badgeEn = "Live Sensor",
                badgeAr = "حساس مباشر",
                iconContent = { tint -> IslamicIconQibla(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.QIBLA) }
            ),

            // 10. Smart Digital Tasbih
            ToolItem(
                id = "digital_tasbih",
                titleEn = "Smart Digital Tasbih",
                titleAr = "السبحة الإلكترونية الذكية",
                subtitleEn = "Tactile Dhikr Bead Counter",
                subtitleAr = "عداد تسبيح باللمس والاهتزاز وحلقات الذكر",
                descriptionEn = "Electronic beads counter with tactile vibration, loop targets (33, 99, 1000), preset adhkar, and lifetime counts.",
                descriptionAr = "سبحة إلكترونية تفاعلية باهتزازات لمسية وأهداف دورات الذكر (٣٣، ٩٩، ١٠٠٠) وإحصائيات التسبيح.",
                category = ToolCategory.DHIKR_DUAS,
                badgeEn = "Tactile Beads",
                badgeAr = "سبحة ذكية",
                iconContent = { tint -> IslamicIconTasbeeh(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.TASBIH) }
            ),

            // 11. Fortress of the Muslim (Duas Library)
            ToolItem(
                id = "duas_library",
                titleEn = "Fortress of the Muslim (Du'as)",
                titleAr = "حصن المسلم وموسوعة الأدعية",
                subtitleEn = "Authentic Supplications & Rabbana Duas",
                subtitleAr = "أدعية مأثورة من القرآن الكريم والسنة النبوية",
                descriptionEn = "Categorized supplications for Morning/Evening, Travel, Anxiety, Forgiveness, Quranic Rabbana Duas, and Parents.",
                descriptionAr = "مكتبة أدعية شاملة مبوبة: أدعية الصباح والمساء، السفر، الكرب، المغفرة، والرقية وأدعية القرآن الكريم.",
                category = ToolCategory.DHIKR_DUAS,
                badgeEn = "100+ Duas",
                badgeAr = "١٠٠+ دعاء",
                iconContent = { tint -> IslamicIconDua(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.DUAS_LIBRARY) }
            ),

            // 12. Daily Azkar Reader
            ToolItem(
                id = "daily_azkar",
                titleEn = "Daily Azkar Reader",
                titleAr = "أذكار الصباح والمساء واليوم",
                subtitleEn = "Step-by-Step Repetition Counters",
                subtitleAr = "قراءة تفاعلية للأذكار مع عداد التكرار والفضائل",
                descriptionEn = "Read Morning & Evening Azkar, Wakeup & Sleep Azkar with tap counters, virtues, and English translations.",
                descriptionAr = "أذكار الصباح والمساء، أذكار النوم والاستيقاظ مع عدادات تفاعلية وفضيلة كل ذكر.",
                category = ToolCategory.DHIKR_DUAS,
                badgeEn = "Azkar Counter",
                badgeAr = "عداد الأذكار",
                iconContent = { tint -> IslamicIconAzkar(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.AZKAR_READER) }
            ),

            // 13. Daily Sunnah Habit Tracker
            ToolItem(
                id = "habit_tracker",
                titleEn = "Sunnah & Daily Habits",
                titleAr = "متتبع السنن والعادات اليومية",
                subtitleEn = "Tahajjud, Duha, Witr & Sadaqah",
                subtitleAr = "التهجد، صلاة الضحى، الوتر، الصدقة والسنن",
                descriptionEn = "Track daily Sunnah practices like Tahajjud, Duha prayer, Witr, reading Surah Al-Kahf on Friday, and daily Sadaqah.",
                descriptionAr = "تابع سنن النبي ﷺ اليومية: صلاة الضحى، قيام الليل، قراءة سورة الكهف، الصدقة وصيام التطوع.",
                category = ToolCategory.STREAKS_HABITS,
                badgeEn = "Sunnah Habits",
                badgeAr = "سنن يومية",
                iconContent = { tint -> IslamicIconTask(modifier = Modifier.size(22.dp), tint = tint) },
                onClick = { vm -> vm.navigateTo(NoorDestination.HABIT_TRACKER) }
            ),

            // 14. Favorites & Saved Bookmarks
            ToolItem(
                id = "favorites",
                titleEn = "Favorites & Saved Verses",
                titleAr = "المفضلة والآيات المحفوظة",
                subtitleEn = "Quick Access to Bookmarked Ayahs & Duas",
                subtitleAr = "وصول سريع للآيات والأدعية التي قمت بحفظها",
                descriptionEn = "Access all your bookmarked Quran verses, favorite duas, and treasured spiritual reflections in one clean hub.",
                descriptionAr = "استعرض جميع الآيات المفضلة والأدعية المحفوظة لديك لسهولة الرجوع إليها في أي وقت.",
                category = ToolCategory.STREAKS_HABITS,
                badgeEn = "Bookmarks",
                badgeAr = "المحفوظات",
                iconContent = { tint -> Icon(Icons.Default.Favorite, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.FAVORITES) }
            ),

            // 15. Spiritual User Profile & Backup
            ToolItem(
                id = "user_profile",
                titleEn = "Spiritual Profile & Backup",
                titleAr = "الملف الشخصي والنسخ الاحتياطي",
                subtitleEn = "Account, Cloud Sync & Data Privacy",
                subtitleAr = "إدارة الحساب، المزامنة، والنسخ الاحتياطي",
                descriptionEn = "Manage your spiritual identity, export/import Khatma and streaks to Google Drive or Email, and adjust cloud sync.",
                descriptionAr = "إدارة حسابك وبياناتك، وتصدير واستيراد بيانات الختمة والسلسلة عبر Google Drive والبريد.",
                category = ToolCategory.CLOUD_SETTINGS,
                badgeEn = "Drive & Email",
                badgeAr = "نسخ سحابي",
                iconContent = { tint -> Icon(Icons.Default.Person, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.navigateTo(NoorDestination.PROFILE) }
            ),

            // 16. App Settings & Customization
            ToolItem(
                id = "app_settings",
                titleEn = "App Settings & Customization",
                titleAr = "إعدادات وتخصيص التطبيق",
                subtitleEn = "Languages, Adhan, Calculation Methods",
                subtitleAr = "اللغة، أصوات الأذان، وطرق حساب المواقيت",
                descriptionEn = "Switch between English and Arabic, select your preferred Adhan muezzin, change calculation method, and theme preferences.",
                descriptionAr = "تغيير لغة التطبيق (العربية والإنجليزية)، تخصيص صوت الأذان، وتعديل طرق حساب المواقيت الفلكية.",
                category = ToolCategory.CLOUD_SETTINGS,
                badgeEn = "Preferences",
                badgeAr = "التفضيلات",
                iconContent = { tint -> Icon(Icons.Default.Settings, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp)) },
                onClick = { vm -> vm.openSettingsModal() }
            )
        )
    }

    // Filter by category and search query
    val filteredTools = remember(selectedCategory, searchQuery, isArabic) {
        allTools.filter { tool ->
            val matchesCategory = selectedCategory == ToolCategory.ALL || tool.category == selectedCategory
            val matchesQuery = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                tool.titleEn.lowercase().contains(q) ||
                tool.titleAr.lowercase().contains(q) ||
                tool.subtitleEn.lowercase().contains(q) ||
                tool.subtitleAr.lowercase().contains(q) ||
                tool.descriptionEn.lowercase().contains(q) ||
                tool.descriptionAr.lowercase().contains(q) ||
                (tool.badgeEn?.lowercase()?.contains(q) == true) ||
                (tool.badgeAr?.lowercase()?.contains(q) == true)
            }
            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = if (isArabic) "جميع الأدوات والمميزات" else "All Tools & Features",
                eyebrow = if (isArabic) "دليل الخدمات الشامل" else "ALL ACCESS DIRECTORY",
                subtitle = if (isArabic) "استكشف كافة المزايا والخدمات الروحانية" else "Explore every spiritual tool in Al-Noor",
                onBackClick = onNavigateBack,
                backContentDescription = stringResource(R.string.action_back),
                actions = {
                    NoorGlassIconButton(
                        onClick = { viewModel.openSettingsModal() },
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            )
        },
        containerColor = Color.White,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Search Bar
            item(key = "search_bar") {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = if (isArabic) "ابحث عن أداة (مثل: MP3، ختمة، سلسلة، تسبيح، قبلة)..." else "Search tools (e.g. MP3, Khatma, Streak, Tasbih, Qibla)...",
                                style = MaterialTheme.typography.bodySmall.copy(color = NoorSageSlate.copy(alpha = 0.65f), fontSize = 12.5.sp)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = NoorTealDark,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = NoorSageSlate,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NoorTealDark,
                            unfocusedBorderColor = NoorCardBorder,
                            focusedContainerColor = NoorSurfaceSoft,
                            unfocusedContainerColor = NoorSurfaceSoft
                        ),
                        singleLine = true
                    )
                }
            }

            // 2. Category Filter Chips (Horizontal Scroll)
            item(key = "category_chips") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ToolCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NoorTealDark else NoorSurfaceSoft,
                            border = BorderStroke(1.dp, if (isSelected) NoorTealDark else NoorCardBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = category }
                        ) {
                            Text(
                                text = if (isArabic) category.arName else category.enName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else NoorDarkPine,
                                    fontSize = 12.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 3. Featured Hero Spotlight Cards (When viewing All and no search)
            if (searchQuery.isBlank() && selectedCategory == ToolCategory.ALL) {
                item(key = "featured_spotlight") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NoorGoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isArabic) "المميزات البارزة" else "FEATURED HIGHLIGHTS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NoorSageSlate,
                                    fontSize = 11.5.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        // Hero 1: MP3 Quran Player
                        FeaturedHeroCard(
                            title = if (isArabic) "مشغل تلاوات القرآن الكريم" else "MP3 Quran Audio Stream",
                            subtitle = if (isArabic) "استمع لتلاوات عذبة في الخلفية مع أكثر من ٢٠ قارئاً" else "Continuous background listening with 20+ top reciters",
                            badge = if (isArabic) "صوتيات كاملة" else "Full MP3",
                            icon = Icons.Default.Headphones,
                            gradient = NoorTopBarGradient,
                            onClick = { viewModel.navigateTo(NoorDestination.QURAN_AUDIO_STREAM) }
                        )

                        // Row of 2 Secondary Hero Cards: Streaks & Khatma
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FeaturedMiniCard(
                                modifier = Modifier.weight(1f),
                                title = if (isArabic) "سلسلة الالتزام" else "Spiritual Streaks",
                                subtitle = if (isArabic) "تتبع الأيام والأوسمة" else "Daily Milestones",
                                icon = Icons.Default.LocalFireDepartment,
                                accentColor = Color(0xFFE65100),
                                onClick = { viewModel.navigateTo(NoorDestination.STREAKS) }
                            )

                            FeaturedMiniCard(
                                modifier = Modifier.weight(1f),
                                title = if (isArabic) "ختمة القرآن" else "Khatma Planner",
                                subtitle = if (isArabic) "خطة ٣٠ يوماً ومخصص" else "Custom Goals",
                                icon = Icons.Default.Bookmark,
                                accentColor = NoorTealDark,
                                onClick = { viewModel.navigateTo(NoorDestination.QURAN_KHATMA) }
                            )
                        }
                    }
                }
            }

            // 4. Tools Count Header
            item(key = "tools_count_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "جميع الأدوات المتاحة (${filteredTools.size})" else "AVAILABLE TOOLS (${filteredTools.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NoorSageSlate,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    if (selectedCategory != ToolCategory.ALL || searchQuery.isNotBlank()) {
                        Text(
                            text = if (isArabic) "إعادة ضبط" else "Reset Filter",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NoorTealDark,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.clickable {
                                selectedCategory = ToolCategory.ALL
                                searchQuery = ""
                            }
                        )
                    }
                }
            }

            // 5. Tool Item Cards List
            if (filteredTools.isEmpty()) {
                item(key = "empty_tools") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = NoorSageSlate.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = if (isArabic) "لم يتم العثور على أدوات مطابقة" else "No matching tools found",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NoorDarkPine,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = if (isArabic) "جرب كلمة بحث أخرى أو اختر فئة مختلفة" else "Try a different search term or category",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NoorSageSlate,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredTools, key = { it.id }) { tool ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ToolCardItem(
                            tool = tool,
                            isArabic = isArabic,
                            onClick = { tool.onClick(viewModel) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolCardItem(
    tool: ToolItem,
    isArabic: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, NoorCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(NoorSoftGreenBg)
                    .border(1.dp, NoorSoftGreenBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                tool.iconContent(NoorTealDark)
            }

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isArabic) tool.titleAr else tool.titleEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NoorDarkPine,
                            fontSize = 15.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val badge = if (isArabic) tool.badgeAr else tool.badgeEn
                    if (badge != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NoorSoftGreenBg,
                            border = BorderStroke(0.8.dp, NoorSoftGreenBorder)
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NoorTealDark
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isArabic) tool.subtitleAr else tool.subtitleEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NoorTealDark,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isArabic) tool.descriptionAr else tool.descriptionEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NoorSageSlate,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Forward Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = NoorSageSlate.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun FeaturedHeroCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.5.sp
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFD4A340),
                                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = badge,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10261F)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun FeaturedMiniCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, NoorCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NoorDarkPine,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NoorSageSlate,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
