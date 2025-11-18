package com.main.cipta_muri_mobile.ui.setor.riwayat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.data.ApiRepository
import com.main.cipta_muri_mobile.data.SetoranSampahResponse
import com.main.cipta_muri_mobile.util.Formatters
import kotlinx.coroutines.launch
import java.math.RoundingMode

class RiwayatSetoranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApiRepository(application.applicationContext)

    private val _riwayatList = MutableLiveData<List<RiwayatSetoran>>()
    val riwayatList: LiveData<List<RiwayatSetoran>> = _riwayatList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadRiwayatSetoran(userId: String? = null) {
        _isLoading.postValue(true)
        _errorMessage.postValue("")

        viewModelScope.launch {
            repository.getRiwayatSetoran()
                .onSuccess { items ->
                    val mapped = items.map { it.toUiModel() }
                    _riwayatList.postValue(mapped)
                }
                .onFailure { throwable ->
                    _riwayatList.postValue(emptyList())
                    _errorMessage.postValue(throwable.message ?: "Gagal memuat riwayat setoran")
                }

            _isLoading.postValue(false)
        }
    }

    private fun SetoranSampahResponse.toUiModel(): RiwayatSetoran {
        val detailItem = detail?.firstOrNull()
        val jenis = detailItem?.namaSampah
            ?: detailItem?.kategori
            ?: jenisSetoran
            ?: "Setoran Sampah"

        val beratRaw = totalBerat ?: detailItem?.jumlahKg
        val beratFormatted = formatKg(beratRaw)

        val nominalRaw = totalHarga ?: detailItem?.hargaTotal
        val nominalFormatted = if (!nominalRaw.isNullOrBlank()) {
            Formatters.formatRupiah(nominalRaw)
        } else {
            "Rp 0"
        }

        val tanggalFormatted = Formatters.formatTanggalIndo(tanggal).ifBlank { tanggal ?: "" }

        return RiwayatSetoran(
            tanggal = tanggalFormatted,
            jenisSetoran = jenis,
            beratFormatted = beratFormatted,
            totalSaldoFormatted = nominalFormatted
        )
    }

    private fun formatKg(raw: String?): String {
        if (raw.isNullOrBlank()) return "0 Kg"
        val normalized = raw.replace(",", ".").trim()
        val value = normalized.toBigDecimalOrNull()
        return if (value != null) {
            val stripped = value.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros()
            "${stripped.toPlainString()} Kg"
        } else {
            "$raw Kg"
        }
    }
}
