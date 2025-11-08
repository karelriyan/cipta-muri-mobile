package com.main.cipta_muri_mobile.data.ranking

import retrofit2.http.GET
import retrofit2.http.Query

interface RankingService {
    @GET("ranking")
    suspend fun getRanking(
        @Query("limit") limit: Int = 10,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("include_donasi") includeDonasi: Boolean = false
    ): ApiEnvelope<RankingPayload>
}

