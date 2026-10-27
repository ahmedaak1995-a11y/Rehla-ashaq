package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookChapter
import com.example.data.BookContent
import com.example.data.BookmarkItem
import com.example.data.PreferencesManager
import com.example.ui.components.IslamicOrnamentalDivider
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.SepiaBg
import com.example.ui.theme.SepiaSurface
import com.example.ui.theme.SepiaText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderScreen(
    chapterId: String,
    initialSectionIndex: Int = 0,
    preferencesManager: PreferencesManager,
    onBackClick: () -> Unit,
    onNavigateChapter: (String) -> Unit
) {
    val context = LocalContext.current
    BackHandler { onBackClick() }

    val chapters = BookContent.chapters
    val currentChapterIndex = remember(chapterId) {
        chapters.indexOfFirst { it.id == chapterId }.coerceAtLeast(0)
    }
    val chapter = chapters[currentChapterIndex]

    // Save as last read
    LaunchedEffect(chapter.id) {
        preferencesManager.setLastChapter(chapter.id)
    }

    val fontSize by preferencesManager.fontSize.collectAsState()
    val themeMode by preferencesManager.themeMode.collectAsState()
    val bookmarks by preferencesManager.bookmarks.collectAsState()

    var showAppearanceDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Scroll to initial section if specified
    LaunchedEffect(initialSectionIndex) {
        if (initialSectionIndex > 0) {
            listState.animateScrollToItem((initialSectionIndex + 1).coerceAtMost(chapter.sections.size))
        }
    }

    // Determine custom reader background based on Sepia mode
    val readerBackground = when (themeMode) {
        "sepia" -> SepiaBg
        else -> MaterialTheme.colorScheme.background
    }
    val readerTextColor = when (themeMode) {
        "sepia" -> SepiaText
        else -> MaterialTheme.colorScheme.onBackground
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = chapter.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = chapter.maqam,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("reader_back_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isSearching = !isSearching },
                        modifier = Modifier.testTag("reader_search_toggle")
                    ) {
                        Icon(
                            if (isSearching) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(
                        onClick = { showAppearanceDialog = true },
                        modifier = Modifier.testTag("reader_appearance_btn")
                    ) {
                        Icon(
                            Icons.Default.FormatSize,
                            contentDescription = "تخصيص الخط والنمط",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Chapter
                    TextButton(
                        onClick = {
                            if (currentChapterIndex > 0) {
                                onNavigateChapter(chapters[currentChapterIndex - 1].id)
                            }
                        },
                        enabled = currentChapterIndex > 0,
                        modifier = Modifier.testTag("reader_prev_chapter_btn")
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("الرحلة السابقة")
                    }

                    Text(
                        text = "${currentChapterIndex + 1} / ${chapters.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldSecondary
                    )

                    // Next Chapter
                    TextButton(
                        onClick = {
                            if (currentChapterIndex < chapters.size - 1) {
                                onNavigateChapter(chapters[currentChapterIndex + 1].id)
                            }
                        },
                        enabled = currentChapterIndex < chapters.size - 1,
                        modifier = Modifier.testTag("reader_next_chapter_btn")
                    ) {
                        Text("الرحلة التالية")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(readerBackground)
        ) {
            // Search field if open
            if (isSearching) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث في كلمات هذه الرحلة...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("reader_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
            ) {
                // Chapter Header Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GoldSecondary, EmeraldPrimary)))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = GoldSecondary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = chapter.maqam,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GoldSecondary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = readerTextColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = chapter.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = readerTextColor.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = EmeraldPrimary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "« ${chapter.keyQuote} »",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(12.dp),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                // Chapter Sections
                itemsIndexed(chapter.sections) { index, section ->
                    val isBookmarked = preferencesManager.isBookmarked(chapter.id, index)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("section_card_$index"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (themeMode == "sepia") SepiaSurface else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Section Title & Bookmark Icon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = section.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                                Row {
                                    // Copy Section Text
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Rehlat Ishq", section.paragraphs.joinToString("\n\n"))
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "تم نسخ النص إلى الحافظة", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = readerTextColor.copy(alpha = 0.6f))
                                    }
                                    // Bookmark Toggle
                                    IconButton(
                                        onClick = {
                                            if (isBookmarked) {
                                                val existing = bookmarks.find { it.chapterId == chapter.id && it.sectionIndex == index }
                                                existing?.let { preferencesManager.removeBookmark(it.id) }
                                                Toast.makeText(context, "تمت إزالة العلامة المرجعية", Toast.LENGTH_SHORT).show()
                                            } else {
                                                preferencesManager.addBookmark(
                                                    BookmarkItem(
                                                        id = "${chapter.id}_$index",
                                                        chapterId = chapter.id,
                                                        chapterTitle = chapter.title,
                                                        sectionIndex = index,
                                                        snippet = section.paragraphs.firstOrNull()?.take(90) ?: section.title
                                                    )
                                                )
                                                Toast.makeText(context, "تم حفظ العلامة المرجعية", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.testTag("btn_bookmark_$index")
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "علامة مرجعية",
                                            tint = if (isBookmarked) GoldSecondary else readerTextColor.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Illustrative Image for the section (Realistic Islamic Shia style)
                            if (section.imageRes != null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                                ) {
                                    Column {
                                        Image(
                                            painter = painterResource(id = section.imageRes),
                                            contentDescription = section.imageCaption ?: section.title,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                        if (section.imageCaption != null) {
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.75f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = section.imageCaption,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = GoldLight,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Paragraphs
                            section.paragraphs.forEach { paragraph ->
                                val matchesSearch = searchQuery.isNotEmpty() && paragraph.contains(searchQuery, ignoreCase = true)
                                Text(
                                    text = paragraph,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = fontSize.sp,
                                        lineHeight = (fontSize * 1.6).sp
                                    ),
                                    color = if (matchesSearch) GoldSecondary else readerTextColor,
                                    fontWeight = if (matchesSearch) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                )
                            }

                            // Callout Quote
                            if (section.calloutQuote != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GoldSecondary, EmeraldPrimary))),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "« ${section.calloutQuote} »",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = (fontSize + 1).sp,
                                                lineHeight = ((fontSize + 1) * 1.5).sp
                                            ),
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            color = EmeraldPrimary
                                        )
                                    }
                                }
                            }

                            // Spiritual Reflection Prompt
                            if (section.reflectionPrompt != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    color = GoldSecondary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = GoldSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "وقفة تأملية للسالك:",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldSecondary
                                            )
                                            Text(
                                                text = section.reflectionPrompt,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = readerTextColor.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Appearance Bottom Sheet / Dialog
    if (showAppearanceDialog) {
        AlertDialog(
            onDismissRequest = { showAppearanceDialog = false },
            title = {
                Text(
                    text = "تخصيص القراءة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Font Size Controller
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("حجم الخط", style = MaterialTheme.typography.labelLarge)
                            Text("${fontSize}sp", fontWeight = FontWeight.Bold, color = GoldSecondary)
                        }
                        Slider(
                            value = fontSize.toFloat(),
                            onValueChange = { preferencesManager.setFontSize(it.toInt()) },
                            valueRange = 14f..28f,
                            steps = 14,
                            modifier = Modifier.testTag("font_size_slider")
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("أصغر (14)", style = MaterialTheme.typography.labelSmall)
                            Text("أكبر (28)", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Reading Palette Modes
                    Column {
                        Text("نمط الإضاءة والخلفية", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ModeChip(
                                label = "نهاري",
                                isSelected = themeMode == "light" || themeMode == "system",
                                onClick = { preferencesManager.setThemeMode("light") },
                                modifier = Modifier.weight(1f)
                            )
                            ModeChip(
                                label = "ليلي",
                                isSelected = themeMode == "dark",
                                onClick = { preferencesManager.setThemeMode("dark") },
                                modifier = Modifier.weight(1f)
                            )
                            ModeChip(
                                label = "ورقي مريح",
                                isSelected = themeMode == "sepia",
                                onClick = { preferencesManager.setThemeMode("sepia") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAppearanceDialog = false }) {
                    Text("تم")
                }
            }
        )
    }
}

@Composable
fun ModeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant),
        color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
