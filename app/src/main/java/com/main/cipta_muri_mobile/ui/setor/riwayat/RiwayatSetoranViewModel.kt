package com.main.cipta_muri_mobile.ui.setor.riwayat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RiwayatSetoranViewModel : ViewModel() {

    private val _riwayatList = MutableLiveData<List<RiwayatSetoran>>()
    val riwayatList: LiveData<List<RiwayatSetoran>> = _riwayatList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // 🚀 Versi dummy: tidak perlu panggil API sama sekali
    fun loadRiwayatSetoran(userId: String) {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                // 📦 langsung isi dengan dummy data
                _riwayatList.value = getDummyData()
            } catch (e: Exception) {
                _errorMessage.value = "Kesalahan: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ Dummy data
    private fun getDummyData(): List<RiwayatSetoran> {
        return listOf(
            RiwayatSetoran(
                tanggal = "05 Februari 2025",
                jenisSetoran = "Setor Plastik",
                beratFormatted = "15 kg",
                totalSaldoFormatted = "Rp 12.000"
            ),
            RiwayatSetoran(
                tanggal = "15 Maret 2025",
                jenisSetoran = "Setor Kardus",
                beratFormatted = "8 kg",
                totalSaldoFormatted = "Rp 8.000"
            ),
            RiwayatSetoran(
                tanggal = "30 April 2025",
                jenisSetoran = "Setor Logam",
                beratFormatted = "5 kg",
                totalSaldoFormatted = "Rp 20.000"
            )
        )
    }
}
