package com.main.cipta_muri_mobile.data.news

data class NewsUiState(
    val loading: Boolean = false,
    val items: List<NewsItem> = emptyList(),
    val error: String? = null,
    val hasMore: Boolean = false
)
