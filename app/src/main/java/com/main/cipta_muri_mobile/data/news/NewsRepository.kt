package com.main.cipta_muri_mobile.data.news

class NewsRepository(private val service: NewsService = NewsApiClient.service) {
    suspend fun fetchNews(limit: Int? = null) = runCatching {
        val response = service.getNews()
        if (!response.success) error(response.message ?: "Gagal memuat berita")
        val items = response.data.orEmpty()
        limit?.let { items.take(it) } ?: items
    }
}
