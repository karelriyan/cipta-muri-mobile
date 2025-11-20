package com.main.cipta_muri_mobile.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.data.news.NewsItem
import com.main.cipta_muri_mobile.data.news.NewsRepository
import com.main.cipta_muri_mobile.data.news.NewsUiState
import kotlinx.coroutines.launch
import kotlin.math.min

class NewsViewModel : ViewModel() {

    private val repository = NewsRepository()
    private val initialLimit = 5
    private val loadStep = 5
    private var allNews: List<NewsItem> = emptyList()
    private var visibleCount = 0

    private val _state = MutableLiveData(NewsUiState())
    val state: LiveData<NewsUiState> = _state

    fun refresh() {
        _state.value = _state.value?.copy(loading = true, error = null)
            ?: NewsUiState(loading = true)
        viewModelScope.launch {
            repository.fetchNews()
                .onSuccess { items ->
                    allNews = items
                    visibleCount = min(initialLimit, allNews.size)
                    _state.postValue(
                        NewsUiState(
                            loading = false,
                            items = allNews.take(visibleCount),
                            hasMore = allNews.size > visibleCount
                        )
                    )
                }
                .onFailure { throwable ->
                    _state.postValue(
                        NewsUiState(
                            loading = false,
                            items = emptyList(),
                            error = throwable.message ?: "Gagal memuat berita",
                            hasMore = false
                        )
                    )
                }
        }
    }

    fun loadMore() {
        if (allNews.isEmpty()) return
        val newCount = min(allNews.size, visibleCount + loadStep)
        if (newCount != visibleCount) {
            visibleCount = newCount
            _state.postValue(
                NewsUiState(
                    loading = false,
                    items = allNews.take(visibleCount),
                    hasMore = allNews.size > visibleCount
                )
            )
        }
    }
}
