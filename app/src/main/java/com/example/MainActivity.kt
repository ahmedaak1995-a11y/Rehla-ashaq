package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.PreferencesManager
import com.example.ui.screens.*
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.RehlatIshqTheme

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Journeys : Screen("journeys")
    object Virtues : Screen("virtues")
    object Kamalat : Screen("kamalat")
    object Makroohat : Screen("makroohat")
    object Tasbeeh : Screen("tasbeeh")
    object Duas : Screen("duas")
    object Bookmarks : Screen("bookmarks")
    data class Reader(val chapterId: String, val sectionIndex: Int = 0) : Screen("reader")
}

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferencesManager = PreferencesManager(applicationContext)

        setContent {
            val themeMode by preferencesManager.themeMode.collectAsState()
            val isDark = when (themeMode) {
                "dark" -> true
                "light", "sepia" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            RehlatIshqTheme(darkTheme = isDark) {
                // Ensure RTL layout for authentic Arabic typography and reading flow
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                    val navItems = listOf(
                        NavItem("الرئيسية", Icons.Default.Home, Screen.Home, "bottom_nav_home"),
                        NavItem("الرحلات", Icons.Default.AutoStories, Screen.Journeys, "bottom_nav_journeys"),
                        NavItem("جرد الملكات", Icons.Default.EditNote, Screen.Virtues, "bottom_nav_virtues"),
                        NavItem("المسبحة", Icons.Default.TouchApp, Screen.Tasbeeh, "bottom_nav_tasbeeh"),
                        NavItem("الكمالات", Icons.Default.Stars, Screen.Kamalat, "bottom_nav_kamalat")
                    )

                    val isTopLevel = currentScreen is Screen.Home ||
                            currentScreen is Screen.Journeys ||
                            currentScreen is Screen.Virtues ||
                            currentScreen is Screen.Kamalat ||
                            currentScreen is Screen.Tasbeeh

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (isTopLevel) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 8.dp
                                ) {
                                    navItems.forEach { item ->
                                        val isSelected = currentScreen.route == item.screen.route
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { currentScreen = item.screen },
                                            icon = {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = item.title,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            },
                                            label = { Text(item.title) },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = EmeraldPrimary,
                                                selectedTextColor = EmeraldPrimary,
                                                indicatorColor = GoldSecondary.copy(alpha = 0.25f),
                                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            ),
                                            modifier = Modifier.testTag(item.testTag)
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            when (val screen = currentScreen) {
                                is Screen.Home -> {
                                    HomeScreen(
                                        preferencesManager = preferencesManager,
                                        onNavigateToReader = { chId ->
                                            currentScreen = Screen.Reader(chId, 0)
                                        },
                                        onNavigateToJourneys = { currentScreen = Screen.Journeys },
                                        onNavigateToVirtues = { currentScreen = Screen.Virtues },
                                        onNavigateToKamalat = { currentScreen = Screen.Kamalat },
                                        onNavigateToMakroohat = { currentScreen = Screen.Makroohat },
                                        onNavigateToTasbeeh = { currentScreen = Screen.Tasbeeh },
                                        onNavigateToDuas = { currentScreen = Screen.Duas },
                                        onNavigateToBookmarks = { currentScreen = Screen.Bookmarks }
                                    )
                                }
                                is Screen.Journeys -> {
                                    JourneysListScreen(
                                        preferencesManager = preferencesManager,
                                        onBackClick = { currentScreen = Screen.Home },
                                        onChapterSelect = { chId ->
                                            currentScreen = Screen.Reader(chId, 0)
                                        }
                                    )
                                }
                                is Screen.Virtues -> {
                                    VirtueJournalScreen(
                                        preferencesManager = preferencesManager,
                                        onBackClick = { currentScreen = Screen.Home }
                                    )
                                }
                                is Screen.Kamalat -> {
                                    KamalatListScreen(
                                        onBackClick = { currentScreen = Screen.Home }
                                    )
                                }
                                is Screen.Makroohat -> {
                                    MakroohatScreen(
                                        onBackClick = { currentScreen = Screen.Home }
                                    )
                                }
                                is Screen.Tasbeeh -> {
                                    TasbeehScreen(
                                        preferencesManager = preferencesManager,
                                        onBackClick = { currentScreen = Screen.Home }
                                    )
                                }
                                is Screen.Duas -> {
                                    DuaLibraryScreen(
                                        onBackClick = { currentScreen = Screen.Home }
                                    )
                                }
                                is Screen.Bookmarks -> {
                                    BookmarksScreen(
                                        preferencesManager = preferencesManager,
                                        onBackClick = { currentScreen = Screen.Home },
                                        onBookmarkClick = { chId, secIdx ->
                                            currentScreen = Screen.Reader(chId, secIdx)
                                        }
                                    )
                                }
                                is Screen.Reader -> {
                                    BookReaderScreen(
                                        chapterId = screen.chapterId,
                                        initialSectionIndex = screen.sectionIndex,
                                        preferencesManager = preferencesManager,
                                        onBackClick = { currentScreen = Screen.Home },
                                        onNavigateChapter = { chId ->
                                            currentScreen = Screen.Reader(chId, 0)
                                        }
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
