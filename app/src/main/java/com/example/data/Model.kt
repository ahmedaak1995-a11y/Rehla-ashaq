package com.example.data

data class BookChapter(
    val id: String,
    val number: Int,
    val title: String,
    val maqam: String,
    val summary: String,
    val keyQuote: String,
    val imageRes: Int? = null,
    val sections: List<BookSection>
)

data class BookSection(
    val id: String,
    val title: String,
    val paragraphs: List<String>,
    val calloutQuote: String? = null,
    val imageRes: Int? = null,
    val imageCaption: String? = null,
    val reflectionPrompt: String? = null
)

data class VirtueItem(
    val id: String,
    val name: String,
    val notes: String = "",
    val daysCount: Int = 0,
    val musharataActive: Boolean = false,
    val moraqabaActive: Boolean = false,
    val mohasabaDone: Boolean = false,
    val moatabaDone: Boolean = false,
    val isCompleted: Boolean = false
)

data class KamalatManzil(
    val number: Int,
    val title: String,
    val concept: String,
    val narration: String,
    val practicalTip: String
)

data class MakroohItem(
    val number: Int,
    val text: String,
    val category: String
)

data class DuaItem(
    val id: String,
    val title: String,
    val source: String,
    val excerpt: String,
    val fullText: String
)

data class BookmarkItem(
    val id: String,
    val chapterId: String,
    val chapterTitle: String,
    val sectionIndex: Int,
    val snippet: String,
    val createdAt: Long = System.currentTimeMillis()
)
