package com.main.cipta_muri_mobile.data.chat

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.CookieManager
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Repository ringan berbasis OkHttp sesuai panduan AI model.
 */
class ChatRepository(
    private val endpoint: String,
    private val client: OkHttpClient,
    moshi: com.squareup.moshi.Moshi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val csrfBootstrapUrl: String? = null,
    private val cookieManager: CookieManager? = null,
) {
    private companion object {
        private const val TAG = "ChatRepository"
    }
    private val requestAdapter = moshi.adapter(ChatRequest::class.java)
    private val responseAdapter = moshi.adapter(ChatResponse::class.java)
    private val csrfMutex = Mutex()
    @Volatile
    private var hasFetchedCsrf = false

    suspend fun sendMessage(message: String, history: List<ChatMessage>): Result<String> {
        return withContext(dispatcher) {
            runCatching { performSend(message, history) }
        }
    }

    private suspend fun performSend(
        message: String,
        history: List<ChatMessage>,
        allowRetry: Boolean = true,
    ): String {
        ensureCsrfCookie()

        val payload = ChatRequest(
            message = message,
            history = history.map {
                HistoryMessage(
                    role = if (it.role == Role.USER) "user" else "model",
                    content = it.content,
                )
            },
        )

        val body = requestAdapter.toJson(payload)
            .toRequestBody("application/json".toMediaType())

        val requestBuilder = Request.Builder()
            .url(endpoint)
            .post(body)
            .addHeader("Accept", "application/json")
            .addHeader("X-Requested-With", "XMLHttpRequest")

        xsrfToken()?.let { token ->
        requestBuilder.addHeader("X-XSRF-TOKEN", token)
        }

        client.newCall(requestBuilder.build()).execute().use { response ->
            if (response.code == 419 && allowRetry && csrfBootstrapUrl != null) {
                hasFetchedCsrf = false
                ensureCsrfCookie(force = true)
                return performSend(message, history, allowRetry = false)
            }

            if (!response.isSuccessful) {
                val errorBody = response.body?.string().orEmpty()
                Log.w(
                    TAG,
                    "Chat call failed code=${response.code} url=$endpoint bodyPreview=${errorBody.take(320)}"
                )
                val reason = if (response.code == 419) {
                    "Sesi chat belum siap. Coba kirim lagi."
                } else {
                    "HTTP ${response.code} saat memuat balasan chat"
                }
                throw IOException(reason)
            }
            val responseBody = response.body?.string()
                ?: throw IOException("Body kosong")
            val parsed = responseAdapter.fromJson(responseBody)
                ?: throw IOException("Gagal parsing respon chat")
            return parsed.response
        }
    }

    private suspend fun ensureCsrfCookie(force: Boolean = false) {
        if (csrfBootstrapUrl == null || cookieManager == null) return
        if (hasFetchedCsrf && !force) return
        csrfMutex.withLock {
            if (force) {
                // Clear stale cookies before refreshing CSRF to avoid reused/expired tokens.
                cookieManager.cookieStore.removeAll()
            } else if (hasFetchedCsrf) {
                return@withLock
            }
            val request = Request.Builder()
                .url(csrfBootstrapUrl)
                .get()
                .addHeader("Accept", "application/json")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Gagal menyiapkan sesi chat (HTTP ${response.code})")
                }
            }
            hasFetchedCsrf = true
        }
    }

    private fun xsrfToken(): String? {
        val raw = cookieManager?.cookieStore?.cookies
            ?.firstOrNull { it.name.equals("XSRF-TOKEN", ignoreCase = true) }
            ?.value
        return raw?.let {
            runCatching { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) }.getOrDefault(it)
        }
    }
}
