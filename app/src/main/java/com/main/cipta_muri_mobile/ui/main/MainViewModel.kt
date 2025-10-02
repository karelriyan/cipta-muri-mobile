package com.main.cipta_muri_mobile.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.main.cipta_muri_mobile.data.User

// Ini adalah ViewModel.
// Ia tidak tahu tentang Activity atau XML, tugasnya hanya menyediakan data.
class MainViewModel : ViewModel() {

    // MutableLiveData bisa diubah di dalam ViewModel
    private val _user = MutableLiveData<User>()

    // View akan mengamati (observe) LiveData ini.
    // Ini tidak bisa diubah dari luar, jadi lebih aman.
    val user: LiveData<User> = _user

    // Biasanya, data ini diambil dari Repository (yang mengambil dari API atau DB).
    // Untuk contoh ini, kita buat data palsu saat ViewModel dibuat.
    init {
        loadUserData()
    }

    private fun loadUserData() {
        // Simulasi memuat data dari internet atau database
        val dummyUser = User(
            name = "Sudrajat",
            accountNumber = "3210123456******",
            balance = 110000.0,
            points = 10,
            totalWasteKg = 20
        )
        _user.value = dummyUser
    }

    // Di sini nanti bisa ditambahkan fungsi lain,
    // misalnya untuk mengambil data berita, riwayat transaksi, dll.
}
