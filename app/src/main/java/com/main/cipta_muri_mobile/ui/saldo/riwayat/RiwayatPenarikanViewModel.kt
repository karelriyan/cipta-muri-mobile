package com.main.cipta_muri_mobile.ui.saldo.riwayat

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.ApiRepository
import com.main.cipta_muri_mobile.data.TarikSaldoResponse
import com.main.cipta_muri_mobile.util.Formatters
import kotlinx.coroutines.launch
import java.util.Locale

class RiwayatPenarikanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApiRepository(application.applicationContext)
    private val initialLimit = 10
    private val loadStep = 5

    private val _items = MutableLiveData<List<RiwayatPenarikanItem>>(emptyList())
    val items: LiveData<List<RiwayatPenarikanItem>> = _items

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage
    private var allItems: List<RiwayatPenarikanItem> = emptyList()
    private var visibleCount = 0

    fun loadRiwayatPenarikan() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            repository.getRiwayatTarikSaldo()
                .onSuccess { responses ->
                    allItems = responses.map { it.toUiModel() }
                    visibleCount = minOf(initialLimit, allItems.size)
                    _items.postValue(allItems.take(visibleCount))
                    if (allItems.isEmpty()) {
                        _errorMessage.postValue("Belum ada data penarikan.")
                    }
                }
                .onFailure { throwable ->
                    allItems = emptyList()
                    visibleCount = 0
                    _items.postValue(emptyList())
                    _errorMessage.postValue(throwable.message ?: "Gagal memuat riwayat penarikan")
                }

            _isLoading.postValue(false)
        }
    }

    fun loadMore() {
        if (allItems.isEmpty()) return
        val newCount = minOf(allItems.size, visibleCount + loadStep)
        if (newCount != visibleCount) {
            visibleCount = newCount
            _items.postValue(allItems.take(visibleCount))
            _errorMessage.postValue(null)
        }
    }

    fun hasMore(): Boolean = visibleCount < allItems.size

    private fun TarikSaldoResponse.toUiModel(): RiwayatPenarikanItem {
        val tanggalDisplay = Formatters.formatTanggalIndo(createdAt).ifBlank { createdAt ?: "" }
        val nominalDisplay = Formatters.formatRupiah(amount, false)
        val color = ContextCompat.getColor(getApplication(), R.color.red)
        val subtitleText = buildSubtitle(status, metode)
        val title = description?.takeIf { it.isNotBlank() } ?: "Penarikan Saldo"

        return RiwayatPenarikanItem(
            tanggal = tanggalDisplay,
            judul = title,
            subtitle = subtitleText,
            nominal = nominalDisplay,
            warnaNominal = color
        )
    }

    private fun buildSubtitle(status: String?, metode: String?): String {
        val parts = mutableListOf<String>()
        status?.takeIf { it.isNotBlank() }?.let {
            val formatted = it.replace('_', ' ').lowercase(Locale("id", "ID"))
                .replaceFirstChar { ch -> ch.titlecase(Locale("id", "ID")) }
            parts.add(formatted)
        }
        metode?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
        return if (parts.isEmpty()) "Lihat Rincian" else parts.joinToString(" • ")
    }
}
