package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookContent
import com.example.data.DuaItem
import com.example.ui.components.AppTopBar
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaLibraryScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    BackHandler { onBackClick() }

    val duas = BookContent.bookDuas
    var selectedTab by remember { mutableIntStateOf(0) }
    var fontScale by remember { mutableFloatStateOf(19f) }

    val currentDua = duas[selectedTab]

    Scaffold(
        topBar = {
            AppTopBar(
                title = "أدعية ومناجيات العشق الإلهي",
                subtitle = "المناجاة والأشعار الواردة في كتاب رحلة عشق",
                showBackButton = true,
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(currentDua.title, currentDua.fullText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ الدعاء إلى الحافظة", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("dua_copy_btn")
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "نسخ الدعاء",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Scrollable / primary Tab Row for Duas
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary,
                edgePadding = 16.dp
            ) {
                duas.forEachIndexed { index, dua ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = dua.title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            // Text size adjuster row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentDua.source,
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldSecondary,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { fontScale = (fontScale - 2f).coerceAtLeast(14f) }) {
                        Text("أ-", fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { fontScale = (fontScale + 2f).coerceAtMost(28f) }) {
                        Text("أ+", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Dua Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 32.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = currentDua.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = GoldSecondary.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = currentDua.fullText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = fontScale.sp,
                                    lineHeight = (fontScale * 1.7).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
