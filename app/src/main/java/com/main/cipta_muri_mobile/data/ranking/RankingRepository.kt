package com.main.cipta_muri_mobile.data.ranking

class RankingRepository(private val api: RankingService = RankingApiClient.service) {
    suspend fun getRanking(
        limit: Int = 10,
        startDate: String? = null,
        endDate: String? = null,
        includeDonasi: Boolean = false
    ) = runCatching {
        val res = api.getRanking(limit, startDate, endDate, includeDonasi)
        if (!res.success) error(res.message ?: "Gagal memuat ranking")
        res.data ?: RankingPayload()
    }
}

