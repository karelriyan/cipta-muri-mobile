package com.main.cipta_muri_mobile.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.data.news.NewsRepository
import com.main.cipta_muri_mobile.data.news.NewsUiState
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val repository = NewsRepository()

    private val _state = MutableLiveData(NewsUiState())
    val state: LiveData<NewsUiState> = _state

    fun refresh(limit: Int? = null) {
        _state.value = _state.value?.copy(loading = true, error = null)
            ?: NewsUiState(loading = true)
        viewModelScope.launch {
            repository.fetchNews(limit)
                .onSuccess { items ->
                    _state.postValue(NewsUiState(loading = false, items = items))
                }
                .onFailure { throwable ->
                    _state.postValue(
                        NewsUiState(
                            loading = false,
                            items = emptyList(),
                            error = throwable.message ?: "Gagal memuat berita"
                        )
                    )
                }
        }
    }
}
