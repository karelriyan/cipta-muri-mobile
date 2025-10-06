package com.main.cipta_muri_mobile.data

data class ApiResponse<T>(
    val status: String,
    val data: T
)
