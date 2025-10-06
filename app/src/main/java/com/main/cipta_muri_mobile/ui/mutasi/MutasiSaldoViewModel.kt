package com.main.cipta_muri_mobile.ui.mutasi

import androidx.lifecycle.*
import com.main.cipta_muri_mobile.data.ApiService
import com.main.cipta_muri_mobile.data.RetrofitClient
import com.main.cipta_muri_mobile.data.MutasiSaldo
import kotlinx.coroutines.launch

class MutasiSaldoViewModel(private val apiService: ApiService = RetrofitClient.instance) : ViewModel() {

    private val _mutasiList = MutableLiveData<List<MutasiSaldo>>()
    val mutasiList: LiveData<List<MutasiSaldo>> = _mutasiList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadMutasiSaldo(userId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.getMutasiSaldo(userId)
                if (response.isSuccessful && response.body() != null) {
                    _mutasiList.value = response.body()
                } else {
                    _errorMessage.value = "Gagal memuat data (${response.code()})"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
