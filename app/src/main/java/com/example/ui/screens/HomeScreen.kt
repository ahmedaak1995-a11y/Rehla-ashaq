package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BookContent
import com.example.data.PreferencesManager
import com.example.ui.components.IslamicOrnamentalDivider
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.HusseiniRed

@Composable
fun HomeScreen(
    preferencesManager: PreferencesManager,
    onNavigateToReader: (String) -> Unit,
    onNavigateToJourneys: () -> Unit,
    onNavigateToVirtues: () -> Unit,
    onNavigateToKamalat: () -> Unit,
    onNavigateToMakroohat: () -> Unit,
    onNavigateToTasbeeh: () -> Unit,
    onNavigateToDuas: () -> Unit,
    onNavigateToBookmarks: () -> Unit
) {
    val context = LocalContext.current
    val lastChapterId by preferencesManager.lastChapterId.collectAsState()
    val activeChapter = remember(lastChapterId) {
        BookContent.chapters.find { it.id == lastChapterId } ?: BookContent.chapters.first()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_baghdad_alley),
                    contentDescription = "غلاف رحلة عشق",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = GoldSecondary.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GoldSecondary, Color.White)))
                    ) {
                        Text(
                            text = "رواية عرفانية أخلاقية",
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "رِحْـلَـةُ عِـشْـقٍ",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "الدكتور الشيخ علي الميّاحي",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "قصة شاب مؤمن بلغ درجات عالية في تزكية النفس",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Resume Reading Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onNavigateToReader(activeChapter.id) }
                    .testTag("resume_reading_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(GoldSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "استئناف القراءة",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "متابعة القراءة",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = activeChapter.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = activeChapter.maqam,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                    IconButton(
                        onClick = { onNavigateToReader(activeChapter.id) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "فتح الفصل",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // The Four Spiritual Stations (المقامات الأربعة)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "خريطة المقامات الأربعة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MaqamMiniBadge("المجاهدة", "تطهير النفس", EmeraldPrimary, Modifier.weight(1f))
                    MaqamMiniBadge("الكمالات", "منازل الفضيلة", GoldSecondary, Modifier.weight(1f))
                    MaqamMiniBadge("المحبة", "العشق الإلهي", HusseiniRed, Modifier.weight(1f))
                    MaqamMiniBadge("الشهود", "المعرفة الحقة", Color(0xFF2C3E50), Modifier.weight(1f))
                }
            }
        }

        item {
            IslamicOrnamentalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "✦ الأبواب التفاعلية ✦"
            )
        }

        // Main Portals Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PortalCard(
                        title = "رحلات الكتاب",
                        subtitle = "١٢ رحلة مفصلة مع الصور",
                        icon = Icons.Default.AutoStories,
                        tintColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_nav_journeys",
                        onClick = onNavigateToJourneys
                    )
                    PortalCard(
                        title = "جرد الملكات",
                        subtitle = "دفتر المشارطة والمراقبة",
                        icon = Icons.Default.EditNote,
                        tintColor = HusseiniRed,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_nav_virtues",
                        onClick = onNavigateToVirtues
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PortalCard(
                        title = "منازل الكمالات",
                        subtitle = "٢١ منزلاً أخلاقياً وعرفانياً",
                        icon = Icons.Default.Stars,
                        tintColor = GoldSecondary,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_nav_kamalat",
                        onClick = onNavigateToKamalat
                    )
                    PortalCard(
                        title = "المسبحة العرفانية",
                        subtitle = "أوراد الشيخ يقظان والحاج",
                        icon = Icons.Default.TouchApp,
                        tintColor = EmeraldLight,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_nav_tasbeeh",
                        onClick = onNavigateToTasbeeh
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PortalCard(
                        title = "مستنقعات المكروهات",
                        subtitle = "٣٦ مكروهاً للتنزه عنها",
                        icon = Icons.Default.WarningAmber,
                        tintColor = Color(0xFFC0392B),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_nav_makroohat",
                        onClick = onNavigateToMakroohat
                    )
                    PortalCard(
                        title = "أدعية ومناجيات",
                        subtitle = "التائبين، المحبين، وقصائد العشق",
                        icon = Icons.Default.VolunteerActivism,
                        tintColor = Color(0xFF6C3483),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_nav_duas",
                        onClick = onNavigateToDuas
                    )
                }
            }
        }

        // Bookmark and Saved Portals
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToBookmarks() }
                    .testTag("btn_nav_bookmarks"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "الإشارات المرجعية",
                        tint = GoldSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الإشارات المرجعية المحفوظة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "الوصول السريع إلى الفقرات والجمل التي حفظتها",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Daily Wisdom Highlight
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GoldSecondary, EmeraldPrimary)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "قبسٌ من رحلة عشق",
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "« من كان الحسين بقلب مسلم بن عقيل ما شعر بالغربة.. اللي بگلبه الحسين مو غريب، بس الي گلبه غافل وتايه وضايع ما يلگه صاحبه... »",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "الحاج سعد النداف",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "« من كان الحسين بقلب مسلم بن عقيل ما شعر بالغربة » - من كتاب رحلة عشق")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة الحكمة"))
                        },
                        modifier = Modifier.testTag("btn_share_wisdom")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة الحكمة")
                    }
                }
            }
        }
    }
}

@Composable
fun MaqamMiniBadge(
    title: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.12f),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(accentColor, accentColor.copy(alpha = 0.5f))))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PortalCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tintColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tintColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tintColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 1,
                    fontSize = 11.sp
                )
            }
        }
    }
}
