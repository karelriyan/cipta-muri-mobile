package com.main.cipta_muri_mobile.ui.login

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    fun login(nik: String, pin: String): Boolean {
        return nik == "1234567890" && pin == "1234"
    }
}
