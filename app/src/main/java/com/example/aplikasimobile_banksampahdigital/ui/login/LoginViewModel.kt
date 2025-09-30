package com.example.aplikasimobile_banksampahdigital.ui.login

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    fun login(nik: String, pin: String): Boolean {
        return nik == "1234567890" && pin == "1234"
    }
}
