package com.main.cipta_muri_mobile.ui.donasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DonasiSampahViewModel : ViewModel() {

    private val _saldo = MutableLiveData<Double>().apply { value = 110000.0 }
    val saldo: LiveData<Double> = _saldo

    private val _nominalDonasi = MutableLiveData<Double>().apply { value = 0.0 }
    val nominalDonasi: LiveData<Double> = _nominalDonasi

    fun pilihNominal(jumlah: Double) {
        _nominalDonasi.value = jumlah
    }

    fun ubahNominalManual(input: String) {
        val value = input.replace("[^0-9]".toRegex(), "").toDoubleOrNull() ?: 0.0
        _nominalDonasi.value = value
    }

    fun cekSaldoCukup(): Boolean {
        return (_saldo.value ?: 0.0) >= (_nominalDonasi.value ?: 0.0)
    }
}
