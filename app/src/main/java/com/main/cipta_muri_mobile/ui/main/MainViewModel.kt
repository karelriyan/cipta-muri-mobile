package com.main.cipta_muri_mobile.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.main.cipta_muri_mobile.data.User

class MainViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    // Fungsi ini dipanggil setelah user berhasil login
    fun setUserData(user: User) {
        _user.value = user
    }

    // (Opsional) Fungsi reset jika user logout
    fun clearUserData() {
        _user.value = null
    }
}
