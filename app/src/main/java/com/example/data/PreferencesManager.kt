package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("rehlat_ishq_prefs", Context.MODE_PRIVATE)

    private val _lastChapterId = MutableStateFlow(prefs.getString("last_chapter_id", "intro") ?: "intro")
    val lastChapterId: StateFlow<String> = _lastChapterId.asStateFlow()

    private val _fontSize = MutableStateFlow(prefs.getInt("font_size", 18))
    val fontSize: StateFlow<Int> = _fontSize.asStateFlow()

    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "system") ?: "system") // system, light, dark, sepia
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _bookmarks = MutableStateFlow<List<BookmarkItem>>(loadBookmarks())
    val bookmarks: StateFlow<List<BookmarkItem>> = _bookmarks.asStateFlow()

    private val _virtues = MutableStateFlow<List<VirtueItem>>(loadVirtues())
    val virtues: StateFlow<List<VirtueItem>> = _virtues.asStateFlow()

    private val _tasbeehCounts = MutableStateFlow<Map<String, Int>>(loadTasbeehCounts())
    val tasbeehCounts: StateFlow<Map<String, Int>> = _tasbeehCounts.asStateFlow()

    fun setLastChapter(chapterId: String) {
        _lastChapterId.value = chapterId
        prefs.edit().putString("last_chapter_id", chapterId).apply()
    }

    fun setFontSize(size: Int) {
        val bounded = size.coerceIn(14, 28)
        _fontSize.value = bounded
        prefs.edit().putInt("font_size", bounded).apply()
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode).apply()
    }

    fun addBookmark(item: BookmarkItem) {
        val updated = _bookmarks.value.filterNot { it.chapterId == item.chapterId && it.sectionIndex == item.sectionIndex } + item
        _bookmarks.value = updated
        saveBookmarks(updated)
    }

    fun removeBookmark(id: String) {
        val updated = _bookmarks.value.filterNot { it.id == id }
        _bookmarks.value = updated
        saveBookmarks(updated)
    }

    fun isBookmarked(chapterId: String, sectionIndex: Int): Boolean {
        return _bookmarks.value.any { it.chapterId == chapterId && it.sectionIndex == sectionIndex }
    }

    private fun loadBookmarks(): List<BookmarkItem> {
        val jsonStr = prefs.getString("bookmarks_json", null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<BookmarkItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    BookmarkItem(
                        id = obj.getString("id"),
                        chapterId = obj.getString("chapterId"),
                        chapterTitle = obj.getString("chapterTitle"),
                        sectionIndex = obj.getInt("sectionIndex"),
                        snippet = obj.getString("snippet"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveBookmarks(list: List<BookmarkItem>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("chapterId", item.chapterId)
                put("chapterTitle", item.chapterTitle)
                put("sectionIndex", item.sectionIndex)
                put("snippet", item.snippet)
                put("createdAt", item.createdAt)
            }
            array.put(obj)
        }
        prefs.edit().putString("bookmarks_json", array.toString()).apply()
    }

    // Virtues / Vices Journal (جرد الملكات)
    fun addVirtue(name: String, notes: String) {
        val newItem = VirtueItem(
            id = System.currentTimeMillis().toString(),
            name = name,
            notes = notes,
            daysCount = 0
        )
        val updated = _virtues.value + newItem
        _virtues.value = updated
        saveVirtues(updated)
    }

    fun updateVirtue(item: VirtueItem) {
        val updated = _virtues.value.map { if (it.id == item.id) item else it }
        _virtues.value = updated
        saveVirtues(updated)
    }

    fun deleteVirtue(id: String) {
        val updated = _virtues.value.filterNot { it.id == id }
        _virtues.value = updated
        saveVirtues(updated)
    }

    private fun loadVirtues(): List<VirtueItem> {
        val jsonStr = prefs.getString("virtues_json", null)
        if (jsonStr == null) {
            // Default sample virtues from the book to get started
            val initial = listOf(
                VirtueItem(
                    id = "1",
                    name = "الغضب والعصبية عند الانفعال",
                    notes = "وادي الغضب، الاستعاذة والوضوء وذكر الحلم",
                    daysCount = 12,
                    musharataActive = true,
                    moraqabaActive = true
                ),
                VirtueItem(
                    id = "2",
                    name = "النظر المحرم وملاحقة المشتتات",
                    notes = "تذكر وصية الحاج النداف: الفوك فوك والجوّه جوّه",
                    daysCount = 7,
                    musharataActive = true
                ),
                VirtueItem(
                    id = "3",
                    name = "التهاون عن صلاة الصبح في أول وقتها",
                    notes = "النوم المبكر وقراءة آخر آية من سورة الكهف",
                    daysCount = 15,
                    musharataActive = true,
                    mohasabaDone = true
                )
            )
            saveVirtues(initial)
            return initial
        }
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<VirtueItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VirtueItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        notes = obj.optString("notes", ""),
                        daysCount = obj.optInt("daysCount", 0),
                        musharataActive = obj.optBoolean("musharataActive", false),
                        moraqabaActive = obj.optBoolean("moraqabaActive", false),
                        mohasabaDone = obj.optBoolean("mohasabaDone", false),
                        moatabaDone = obj.optBoolean("moatabaDone", false),
                        isCompleted = obj.optBoolean("isCompleted", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveVirtues(list: List<VirtueItem>) {
        val array = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("notes", item.notes)
                put("daysCount", item.daysCount)
                put("musharataActive", item.musharataActive)
                put("moraqabaActive", item.moraqabaActive)
                put("mohasabaDone", item.mohasabaDone)
                put("moatabaDone", item.moatabaDone)
                put("isCompleted", item.isCompleted)
            }
            array.put(obj)
        }
        prefs.edit().putString("virtues_json", array.toString()).apply()
    }

    // Tasbeeh
    fun updateTasbeehCount(key: String, count: Int) {
        val map = _tasbeehCounts.value.toMutableMap()
        map[key] = count
        _tasbeehCounts.value = map
        prefs.edit().putInt("tasbeeh_$key", count).apply()
    }

    private fun loadTasbeehCounts(): Map<String, Int> {
        val keys = listOf("salawat", "ya_baseer", "ya_mueen", "astaghfirullah", "nade_ali", "zahra")
        val map = mutableMapOf<String, Int>()
        keys.forEach { key ->
            map[key] = prefs.getInt("tasbeeh_$key", 0)
        }
        return map
    }
}
