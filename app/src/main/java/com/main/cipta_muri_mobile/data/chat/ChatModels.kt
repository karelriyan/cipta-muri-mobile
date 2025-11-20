package com.main.cipta_muri_mobile.data.chat

import java.util.UUID

/**
 * Model chat mengikuti panduan implementasi CiptaMuri AI.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: Role,
    val content: String,
)

enum class Role { USER, MODEL }

data class ChatRequest(
    val message: String,
    val history: List<HistoryMessage>,
)

data class HistoryMessage(
    val role: String,
    val content: String,
)

data class ChatResponse(
    val response: String,
)
