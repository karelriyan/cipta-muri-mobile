package com.main.cipta_muri_mobile.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.data.User
import com.main.cipta_muri_mobile.data.ranking.RankingRepository
import com.main.cipta_muri_mobile.data.news.NewsRepository
import com.main.cipta_muri_mobile.data.news.NewsUiState
import kotlinx.coroutines.launch
import java.math.RoundingMode

class MainViewModel : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    // Inisial nama
    private val _userInitial = MutableLiveData<String>()
    val userInitial: LiveData<String> = _userInitial

    // Teks total berat dinamis (default 0 Kg)
    private val _totalWeightText = MutableLiveData("Total Berat Sampah Terjual: 0 Kg")
    val totalWeightText: LiveData<String> = _totalWeightText

    private val rankingRepo = RankingRepository()
    private val newsRepository = NewsRepository()

    private val _newsState = MutableLiveData(NewsUiState())
    val newsState: LiveData<NewsUiState> = _newsState

    // Dipanggil setelah user berhasil login
    fun setUserData(user: User) {
        _user.value = user
        _userInitial.value = user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        // Refresh total berat berdasarkan rekening
        if (user.accountNumber.isNotEmpty()) {
            refreshTotalWeightFromRanking(user.accountNumber)
        }
    }

    // Hit API ranking untuk cari total berat akun saat ini (all-time)
    fun refreshTotalWeightFromRanking(accountNumber: String) {
        viewModelScope.launch {
            rankingRepo.getRanking(limit = 100, startDate = null, endDate = null, includeDonasi = false)
                .onSuccess { payload ->
                    val item = payload.top_berat.firstOrNull { it.no_rekening == accountNumber }
                    val display = if (item != null) {
                        try {
                            val bd = item.total_berat.toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                            "Total Berat Sampah Terjual: ${bd} Kg"
                        } catch (_: Exception) { "Total Berat Sampah Terjual: 0 Kg" }
                    } else {
                        "Total Berat Sampah Terjual: 0 Kg"
                    }
                    _totalWeightText.postValue(display)
                }
                .onFailure {
                    // Biarkan nilai sebelumnya jika gagal
                }
        }
    }

    fun fetchLatestNews(limit: Int = 5) {
        _newsState.value = _newsState.value?.copy(loading = true, error = null)
            ?: NewsUiState(loading = true)
        viewModelScope.launch {
            newsRepository.fetchNews(limit)
                .onSuccess { items ->
                    _newsState.postValue(
                        NewsUiState(
                            loading = false,
                            items = items,
                            error = null
                        )
                    )
                }
                .onFailure { throwable ->
                    _newsState.postValue(
                        NewsUiState(
                            loading = false,
                            items = emptyList(),
                            error = throwable.message ?: "Gagal memuat berita"
                        )
                    )
                }
        }
    }

    fun clearUserData() {
        _user.value = null
        _userInitial.value = "?"
        _totalWeightText.value = "Total Berat Sampah Terjual: 0 Kg"
        _newsState.value = NewsUiState()
    }
}
