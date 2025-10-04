package com.main.cipta_muri_mobile.data


data class LoginResponse(
    val status: String,
    val message: String,
    val user: User?
)