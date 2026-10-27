package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PreferencesManager
import com.example.data.VirtueItem
import com.example.ui.components.AppTopBar
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.HusseiniRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtueJournalScreen(
    preferencesManager: PreferencesManager,
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    val virtues by preferencesManager.virtues.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "دفتر جرد ومجاهدة الملكات",
                subtitle = "تطبيق وصية الشيخ يقظان والحاج سعد النداف",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_virtue")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة صفة أو ملكة للمجاهدة")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Explanation Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "دستور التزكية والمجاهدة (الأربعينية)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "أمر الشيخ يقظان حسناً بأخذ ورقة وقلم وجرد الملكات السيئة، ثم معالجة كل ملكة في أربعين يوماً عبر أربع خطوات محكمة:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StepBadge("١. المشارطة", "عهد الصباح")
                            StepBadge("٢. المراقبة", "يقظة النهار")
                            StepBadge("٣. المحاسبة", "كشف المساء")
                            StepBadge("٤. المعاتبة", "تأديب النفس")
                        }
                    }
                }
            }

            // Virtues List
            items(virtues) { item ->
                VirtueCard(
                    item = item,
                    onUpdate = { updated -> preferencesManager.updateVirtue(updated) },
                    onDelete = { preferencesManager.deleteVirtue(item.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        var nameInput by remember { mutableStateOf("") }
        var notesInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "إضافة صفة أو خُلق للمجاهدة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("اسم الملكة (مثل: الغضب، الكسل، النظر...)") },
                        modifier = Modifier.fillMaxWidth().testTag("virtue_name_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("برنامج العلاج / ملاحظات الأستاذ") },
                        modifier = Modifier.fillMaxWidth().testTag("virtue_notes_input"),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            preferencesManager.addVirtue(nameInput.trim(), notesInput.trim())
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("btn_save_virtue")
                ) {
                    Text("حفظ والبدء")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun StepBadge(title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = EmeraldPrimary.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary,
                fontSize = 10.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun VirtueCard(
    item: VirtueItem,
    onUpdate: (VirtueItem) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("virtue_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "حذف",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            if (item.notes.isNotEmpty()) {
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 40-Day Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأيام المنجزة: ${item.daysCount} من 40 يوماً",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldSecondary
                )
                Row {
                    OutlinedButton(
                        onClick = {
                            val newCount = (item.daysCount + 1).coerceAtMost(40)
                            onUpdate(item.copy(daysCount = newCount, isCompleted = newCount >= 40))
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("+ يوم", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    if (item.daysCount > 0) {
                        TextButton(
                            onClick = {
                                val newCount = (item.daysCount - 1).coerceAtLeast(0)
                                onUpdate(item.copy(daysCount = newCount, isCompleted = false))
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("-", fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (item.daysCount / 40f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (item.daysCount >= 40) Color(0xFF2E7D32) else EmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Daily 4 Steps Checkboxes
            Text(
                text = "البرنامج اليومي لليوم الحاضر:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = item.musharataActive,
                    onClick = { onUpdate(item.copy(musharataActive = !item.musharataActive)) },
                    label = { Text("المشارطة", fontSize = 10.sp) }
                )
                FilterChip(
                    selected = item.moraqabaActive,
                    onClick = { onUpdate(item.copy(moraqabaActive = !item.moraqabaActive)) },
                    label = { Text("المراقبة", fontSize = 10.sp) }
                )
                FilterChip(
                    selected = item.mohasabaDone,
                    onClick = { onUpdate(item.copy(mohasabaDone = !item.mohasabaDone)) },
                    label = { Text("المحاسبة", fontSize = 10.sp) }
                )
                FilterChip(
                    selected = item.moatabaDone,
                    onClick = { onUpdate(item.copy(moatabaDone = !item.moatabaDone)) },
                    label = { Text("المعاتبة", fontSize = 10.sp) }
                )
            }
        }
    }
}
