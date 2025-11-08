package com.main.cipta_muri_mobile.data

import com.google.gson.annotations.SerializedName

data class Nasabah(
    val id: String,
    val nama: String?,
    val nik: String?,
    @SerializedName("no_rekening") val noRekening: String?,
    val telepon: String?,
    // amount/decimal values may arrive as strings
    val balance: String?,
    @SerializedName("points_balance") val pointsBalance: Int?,
    @SerializedName("formatted_balance") val formattedBalance: String?,
    @SerializedName("user_id") val userId: String? = null
)
