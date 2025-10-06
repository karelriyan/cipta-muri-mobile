package com.main.cipta_muri_mobile.ui.setor.riwayat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.cipta_muri_mobile.data.ApiService
import com.main.cipta_muri_mobile.data.RetrofitClient
import com.main.cipta_muri_mobile.data.RiwayatSetoran
import kotlinx.coroutines.launch

class RiwayatSetoranViewModel : ViewModel() {

    private val apiService: ApiService = RetrofitClient.instance

    private val _riwayatList = MutableLiveData<List<RiwayatSetoran>>()
    val riwayatList: LiveData<List<RiwayatSetoran>> = _riwayatList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // ✅ Gunakan String untuk userId
    fun loadRiwayatSetoran(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getRiwayatSetoranByUserId(userId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "success" && !body.data.isNullOrEmpty()) {
                        _riwayatList.value = body.data
                    } else {
                        _riwayatList.value = emptyList()
                        _errorMessage.value = "Belum ada data setoran."
                    }
                } else {
                    _errorMessage.value = "Gagal memuat data (Code: ${response.code()})"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Kesalahan koneksi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
