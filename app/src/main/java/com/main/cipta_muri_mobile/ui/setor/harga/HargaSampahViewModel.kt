package com.main.cipta_muri_mobile.ui.setor.harga

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.data.ApiRepository
import com.main.cipta_muri_mobile.data.HargaSampahResponse
import com.main.cipta_muri_mobile.util.Formatters
import kotlinx.coroutines.launch
import java.math.RoundingMode

class HargaSampahViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApiRepository(application.applicationContext)
    private val masterList = mutableListOf<HargaSampahUi>()
    private var nextIndex = 0
    private val pageSize = 5

    private val _items = MutableLiveData<List<HargaSampahUi>>(emptyList())
    val items: LiveData<List<HargaSampahUi>> = _items

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _showLoadMore = MutableLiveData(false)
    val showLoadMore: LiveData<Boolean> = _showLoadMore

    private val _lastUpdated = MutableLiveData<String>()
    val lastUpdated: LiveData<String> = _lastUpdated

    fun loadHargaSampah() {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)

        viewModelScope.launch {
            repository.getHargaSampah()
                .onSuccess { raw ->
                    val mapped = raw.map { it.toUiModel() }
                    masterList.clear()
                    masterList.addAll(mapped)
                    nextIndex = 0
                    publishNextPage(reset = true)
                    val tanggal = resolveLastUpdate(raw)
                    if (!tanggal.isNullOrBlank()) {
                        _lastUpdated.postValue(tanggal)
                    }
                    if (masterList.isEmpty()) {
                        _errorMessage.postValue("Belum ada data harga sampah.")
                    }
                }
                .onFailure { throwable ->
                    masterList.clear()
                    _items.postValue(emptyList())
                    _showLoadMore.postValue(false)
                    _errorMessage.postValue(throwable.message ?: "Gagal memuat harga sampah")
                }
            _isLoading.postValue(false)
        }
    }

    fun loadMore() {
        if (_isLoading.value == true) return
        publishNextPage(reset = false)
    }

    private fun publishNextPage(reset: Boolean) {
        if (reset) nextIndex = 0
        if (masterList.isEmpty()) {
            _items.postValue(emptyList())
            _showLoadMore.postValue(false)
            return
        }
        val end = (nextIndex + pageSize).coerceAtMost(masterList.size)
        val slice = masterList.subList(0, end)
        _items.postValue(slice.toList())
        nextIndex = end
        _showLoadMore.postValue(nextIndex < masterList.size)
    }

    private fun resolveLastUpdate(list: List<HargaSampahResponse>): String? {
        val raw = list.firstOrNull { !it.lastUpdate.isNullOrBlank() }?.lastUpdate
        if (raw.isNullOrBlank()) return null
        return Formatters.formatTanggalIndo(raw).ifBlank { raw }
    }

    private fun HargaSampahResponse.toUiModel(): HargaSampahUi {
        val namaBahan = nama?.takeIf { it.isNotBlank() } ?: "Jenis Sampah"
        val hargaLabel = if (!hargaPerKg.isNullOrBlank()) {
            "${Formatters.formatRupiah(hargaPerKg)}/Kg"
        } else {
            "Rp 0/Kg"
        }
        val total = totalSetoranKg?.takeIf { it.isNotBlank() } ?: "0"
        val totalFormatted = "Total pernah disetor: ${formatKg(total)}"
        return HargaSampahUi(
            nama = namaBahan,
            hargaPerKg = hargaLabel,
            totalSetoran = totalFormatted
        )
    }

    private fun formatKg(raw: String): String {
        val normalized = raw.replace(",", ".").replace(Regex("[^0-9.+-]"), "").trim()
        val value = normalized.toBigDecimalOrNull()
        if (value != null) {
            val stripped = value.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros()
            return "${stripped.toPlainString()} Kg"
        }
        val fallback = if (normalized.isNotBlank()) normalized else raw
        return "$fallback Kg"
    }
}
