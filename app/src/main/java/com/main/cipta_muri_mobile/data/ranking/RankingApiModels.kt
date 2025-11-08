package com.main.cipta_muri_mobile.data.ranking

// Generic API envelope matching backend shape
data class ApiEnvelope<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

data class RankingPayload(
    val top_berat: List<TopBeratItem> = emptyList(),
    val top_setor: List<TopSetorItem> = emptyList()
)

data class TopBeratItem(
    val id: String,
    val nama: String?,
    val no_rekening: String?,
    val total_berat: String
)

data class TopSetorItem(
    val id: String,
    val nama: String?,
    val no_rekening: String?,
    val total_setor: Int
)

