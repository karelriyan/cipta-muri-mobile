package com.main.cipta_muri_mobile.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userInitial = MutableLiveData<String>()
    val userInitial: LiveData<String> = _userInitial

    fun setUserName(name: String) {
        _userName.value = name
        _userInitial.value = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    }
}
