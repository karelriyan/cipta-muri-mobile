package com.main.cipta_muri_mobile.ui.setor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SetorSampahViewModel : ViewModel() {

    enum class Destination {
        BANK, JADWAL, RIWAYAT, HARGA
    }

    private val _navigateTo = MutableLiveData<Destination>()
    val navigateTo: LiveData<Destination> get() = _navigateTo

    fun onMenuClicked(destination: Destination) {
        _navigateTo.value = destination
    }
}
