package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PreferencesManager
import com.example.ui.components.AppTopBar
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSecondary

data class TasbeehPreset(
    val key: String,
    val title: String,
    val arabicText: String,
    val target: Int,
    val spiritualNote: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbeehScreen(
    preferencesManager: PreferencesManager,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    BackHandler { onBackClick() }

    val vibrator = remember {
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    val presets = remember {
        listOf(
            TasbeehPreset(
                key = "salawat",
                title = "الورد الأكبر: الصلاة على محمد وآل محمد",
                arabicText = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَآلِ مُحَمَّدٍ",
                target = 1000,
                spiritualNote = "أوصى به الشيخ يقظان: ما من ذكر أفضل منه على الإطلاق، وهو أساس الكمالات ويهدم الذنوب هدماً."
            ),
            TasbeehPreset(
                key = "ya_baseer",
                title = "ذكر وادي التبصّر",
                arabicText = "يَا بَصِيرُ",
                target = 300,
                spiritualNote = "ذكر علمه الشيخ لحسن في بداية الطريق لفتح بصيرة القلب وإدراك العيوب."
            ),
            TasbeehPreset(
                key = "ya_mueen",
                title = "ذكر الاستعانة",
                arabicText = "يَا مُعِينُ",
                target = 100,
                spiritualNote = "أمر به الشيخ يقظان للاستعانة بالله تعالى في محاربة الملكات السيئة واجتياز وادي الغضب."
            ),
            TasbeehPreset(
                key = "astaghfirullah",
                title = "ذكر التوبة والاستغفار",
                arabicText = "أَسْتَغْفِرُ اللَّهَ رَبِّي وَأَتُوبُ إِلَيْهِ",
                target = 70,
                spiritualNote = "ماء اليقظة لغسل قذارة الغفلة والتطهر في جلسات التوبة ومحاسبة النفس."
            ),
            TasbeehPreset(
                key = "nade_ali",
                title = "ورد نادِ علياً",
                arabicText = "نَادِ عَلِيّاً مَظْهَرَ الْعَجَائِبِ تَجِدْهُ عَوْناً لَكَ فِي النَّوَائِبِ",
                target = 110,
                spiritualNote = "الورد المنجي عند الشدائد، علمه الشيخ يقظان لحسن أثناء حادثة مستشفى الراهبات."
            ),
            TasbeehPreset(
                key = "zahra",
                title = "تسبيح السيدة فاطمة الزهراء (ع)",
                arabicText = "الله أكبر (٣٤) - الحمد لله (٣٣) - سبحان الله (٣٣)",
                target = 100,
                spiritualNote = "أعظم تعقيبات الصلاة المأثورة وآداب النوم التي كان يواظب عليها أهل السلوك."
            )
        )
    }

    var selectedPreset by remember { mutableStateOf(presets.first()) }
    val countsMap by preferencesManager.tasbeehCounts.collectAsState()
    val currentCount = countsMap[selectedPreset.key] ?: 0

    fun triggerVibration() {
        vibrator?.let { v ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(45)
            }
        }
    }

    fun increment() {
        triggerVibration()
        val newCount = currentCount + 1
        preferencesManager.updateTasbeehCount(selectedPreset.key, newCount)
    }

    fun reset() {
        preferencesManager.updateTasbeehCount(selectedPreset.key, 0)
    }

    val progress by animateFloatAsState(
        targetValue = (currentCount.toFloat() / selectedPreset.target).coerceIn(0f, 1f),
        label = "progress"
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "المسبحة العرفانية الذكية",
                subtitle = "أذكار وأوراد كتاب رحلة عشق",
                showBackButton = true,
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { reset() },
                        modifier = Modifier.testTag("tasbeeh_reset_btn")
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "إعادة التعيين",
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
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Preset Selection Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(presets) { preset ->
                    FilterChip(
                        selected = selectedPreset.key == preset.key,
                        onClick = { selectedPreset = preset },
                        label = { Text(preset.title.take(24) + "...") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Spiritual Context Note
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = selectedPreset.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedPreset.spiritualNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Dhikr Arabic Text
            Text(
                text = selectedPreset.arabicText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 34.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Large Circular Interactive Bead / Button
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(EmeraldPrimary, EmeraldDark)
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = GoldSecondary, bounded = true),
                        onClick = { increment() }
                    )
                    .testTag("tasbeeh_tap_area"),
                contentAlignment = Alignment.Center
            ) {
                // Circular Progress Ring
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(230.dp),
                    color = GoldSecondary,
                    strokeWidth = 6.dp,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentCount",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 54.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "الهدف: ${selectedPreset.target}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "المس للتسبيح",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Bottom Reset & Target reached message
            if (currentCount >= selectedPreset.target) {
                Surface(
                    color = GoldSecondary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "تبارك الله! أتممت وردك اليومي بنجاح ✦",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
