package com.main.cipta_muri_mobile.data.news

import com.main.cipta_muri_mobile.data.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("berita")
    suspend fun getNews(
        @Query("q") query: String? = null,
        @Query("category") category: String? = null
    ): ApiResponse<List<NewsItem>>
}
