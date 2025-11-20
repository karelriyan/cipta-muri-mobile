package com.main.cipta_muri_mobile.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.BuildConfig
import com.main.cipta_muri_mobile.data.chat.ChatMessage
import com.main.cipta_muri_mobile.data.chat.ChatRepository
import com.main.cipta_muri_mobile.data.chat.Role
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.JavaNetCookieJar
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

private const val HISTORY_LIMIT = 10

data class ChatUiState(
    val isSheetOpen: Boolean = false,
    val input: String = "",
    val isSending: Boolean = false,
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            role = Role.MODEL,
            content = "Halo, saya CiptaMuri AI. Ada yang bisa saya bantu hari ini?",
        )
    ),
    val errorMessage: String? = null,
)

class ChatViewModel(
    private val repository: ChatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun toggleSheet(forceOpen: Boolean? = null) {
        _uiState.update { current ->
            val target = forceOpen ?: !current.isSheetOpen
            current.copy(isSheetOpen = target, errorMessage = null)
        }
    }

    fun updateInput(newValue: String) {
        _uiState.update { it.copy(input = newValue) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun sendMessage() {
        val current = _uiState.value
        if (current.isSending) return
        val trimmed = current.input.trim()
        if (trimmed.isEmpty()) return

        val userMessage = ChatMessage(role = Role.USER, content = trimmed)
        val newHistory = (current.messages + userMessage)
        val requestHistory = newHistory.takeLast(HISTORY_LIMIT)

        _uiState.update {
            it.copy(
                messages = newHistory,
                input = "",
                isSending = true,
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            val result = repository.sendMessage(trimmed, requestHistory)
            result.onSuccess { message ->
                _uiState.update {
                    it.copy(
                        messages = it.messages + ChatMessage(
                            role = Role.MODEL,
                            content = message.trim(),
                        ),
                        isSending = false,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSending = false,
                        errorMessage = throwable.message ?: "Terjadi kesalahan. Coba lagi ya.",
                    )
                }
            }
        }
    }
}

class ChatViewModelFactory : ViewModelProvider.Factory {
    private val repository by lazy {
        val baseUrl = BuildConfig.CHAT_BASE_URL.trim().trimEnd('/')
        val endpoint = when {
            baseUrl.endsWith("/api") -> "$baseUrl/chat"
            else -> "$baseUrl/api/chat"
        }
        val csrfBase = if (baseUrl.endsWith("/api")) {
            baseUrl.removeSuffix("/api")
        } else {
            baseUrl
        }
        val csrfUrl = "$csrfBase/sanctum/csrf-cookie"
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val cookieManager = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        }
        val client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        ChatRepository(
            endpoint = endpoint,
            client = client,
            moshi = moshi,
            csrfBootstrapUrl = csrfUrl,
            cookieManager = cookieManager,
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
