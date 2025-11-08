package com.main.cipta_muri_mobile.data

import com.google.gson.annotations.SerializedName

data class RekeningSummary(
    val rekening: Nasabah,
    val balance: String?,
    @SerializedName("formatted_balance") val formattedBalance: String?,
    @SerializedName("points_balance") val pointsBalance: Int?
)

