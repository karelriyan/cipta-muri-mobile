package com.main.cipta_muri_mobile.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.main.cipta_muri_mobile.data.User

class MainViewModel : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    // LiveData tambahan untuk menyimpan huruf awal nama
    private val _userInitial = MutableLiveData<String>()
    val userInitial: LiveData<String> = _userInitial

    // Fungsi ini dipanggil setelah user berhasil login
    fun setUserData(user: User) {
        _user.value = user
        // ambil huruf pertama nama, jika kosong tampilkan '?'
        _userInitial.value = user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    }

    // Fungsi reset jika user logout
    fun clearUserData() {
        _user.value = null
        _userInitial.value = "?"
    }
}
