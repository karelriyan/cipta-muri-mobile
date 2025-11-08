package com.main.cipta_muri_mobile.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.data.ranking.RankingPayload
import com.main.cipta_muri_mobile.data.ranking.RankingRepository
import com.main.cipta_muri_mobile.data.ranking.TopBeratItem
import com.main.cipta_muri_mobile.data.ranking.TopSetorItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RankingUiState(
    val loading: Boolean = false,
    val berat: List<TopBeratItem> = emptyList(),
    val setor: List<TopSetorItem> = emptyList(),
    val error: String? = null,
    val filterAllTime: Boolean = false,
    val startDate: String? = null,
    val endDate: String? = null
)

class RankingViewModel(
    private val repo: RankingRepository = RankingRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(RankingUiState())
    val state: StateFlow<RankingUiState> = _state.asStateFlow()

    fun load(limit: Int = 10, start: String? = null, end: String? = null, donasi: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val (s, e) = ensureDateRange(start, end, _state.value.filterAllTime)
            repo.getRanking(limit, s, e, donasi)
                .onSuccess { payload: RankingPayload ->
                    _state.update {
                        it.copy(
                            loading = false,
                            berat = payload.top_berat,
                            setor = payload.top_setor,
                            startDate = s,
                            endDate = e
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(loading = false, error = e.message ?: "Gagal memuat ranking") }
                }
        }
    }

    fun applyFilterLast3Months(limit: Int = 10, donasi: Boolean = false) {
        _state.update { it.copy(filterAllTime = false) }
        load(limit = limit, start = null, end = null, donasi = donasi)
    }

    fun applyFilterAllTime(limit: Int = 10, donasi: Boolean = false) {
        _state.update { it.copy(filterAllTime = true) }
        load(limit = limit, start = null, end = null, donasi = donasi)
    }

    private fun ensureDateRange(start: String?, end: String?, allTime: Boolean): Pair<String?, String?> {
        if (allTime) return null to null
        if (start != null || end != null) return start to end
        // default: last 3 months from today
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val cal = java.util.Calendar.getInstance()
            val endStr = sdf.format(cal.time)
            cal.add(java.util.Calendar.MONTH, -3)
            val startStr = sdf.format(cal.time)
            startStr to endStr
        } catch (_: Exception) {
            null to null
        }
    }
}
