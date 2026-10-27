package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "SpiritualAiViewModel"

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Success(val text: String) : AiUiState
    data class Error(val message: String) : AiUiState
}

data class AiChatMessage(
    val sender: String, // "user" or "gemini"
    val content: String,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

class SpiritualAiViewModel : ViewModel() {

    // Lazy initialization using gemini-3.8-flash via Firebase.ai
    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "أنت «المرشد الروحاني لكتاب رحلة عشق» للدكتور الشيخ علي الميّاحي. مهمتك تقديم إرشادات عرفانية وأخلاقية مستقاة حصراً من مدرسة أهل البيت (ع) والقرآن الكريم لمساعدة السالك في تزكية النفس، والتخلص من الملكات السيئة وفق منهج الأربعينية (المشارطة، المراقبة، المحاسبة، المعاتبة)، وشرح مقامات الرواية ومنازل الكمالات بأسلوب وقور وموجز يفيض بالسكينة."
                )
            }
        )
    }

    private val _uiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                sender = "gemini",
                content = "السلام عليكم ورحمة الله وبركاته يا سالك درب المحبة.. مرحباً بك في قبس العرفان لكتاب «رحلة عشق». كيف يمكنني إعانتك في مسير تزكية نفسك أو شرح مقامات الرواية؟"
            )
        )
    )
    val messages: StateFlow<List<AiChatMessage>> = _messages.asStateFlow()

    private var lastQuery: String = ""

    fun askQuestion(query: String) {
        if (query.isBlank()) return
        lastQuery = query.trim()

        val userMsg = AiChatMessage(sender = "user", content = lastQuery)
        _messages.value = _messages.value + userMsg
        _uiState.value = AiUiState.Loading

        viewModelScope.launch {
            try {
                // Use unary generateContent with realistic 45-second timeout window
                val response = withTimeout(45_000L) {
                    model.generateContent(lastQuery)
                }
                val text = response.text
                if (!text.isNullOrBlank()) {
                    _uiState.value = AiUiState.Success(text)
                    _messages.value = _messages.value + AiChatMessage(sender = "gemini", content = text)
                } else {
                    val emptyMsg = "لم يرد جواب من النموذج، يرجى المحاولة مرة أخرى."
                    _uiState.value = AiUiState.Error(emptyMsg)
                    _messages.value = _messages.value + AiChatMessage(sender = "gemini", content = emptyMsg, isError = true)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini generation timed out", e)
                val timeoutMsg = "استغرقت الإجابة وقتاً أطول من المعتاد نظراً لضغط الشبكة. يمكنك النقر على إعادة المحاولة."
                _uiState.value = AiUiState.Error(timeoutMsg)
                _messages.value = _messages.value + AiChatMessage(sender = "gemini", content = timeoutMsg, isError = true)
            } catch (e: CancellationException) {
                // MUST re-throw CancellationException to preserve cooperative cancellation
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI generation failed", e)
                val errorMsg = "تعذر الحصول على الإجابة: ${e.localizedMessage ?: "يرجى التحقق من الاتصال والمحاولة ثانية"}"
                _uiState.value = AiUiState.Error(errorMsg)
                _messages.value = _messages.value + AiChatMessage(sender = "gemini", content = errorMsg, isError = true)
            }
        }
    }

    fun retryLastQuestion() {
        if (lastQuery.isNotBlank()) {
            askQuestion(lastQuery)
        }
    }

    fun clearChat() {
        _messages.value = listOf(
            AiChatMessage(
                sender = "gemini",
                content = "السلام عليكم ورحمة الله وبركاته يا سالك درب المحبة.. مرحباً بك في قبس العرفان لكتاب «رحلة عشق». كيف يمكنني إعانتك في مسير تزكية نفسك أو شرح مقامات الرواية؟"
            )
        )
        _uiState.value = AiUiState.Idle
        lastQuery = ""
    }
}
