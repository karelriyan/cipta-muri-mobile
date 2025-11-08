package com.main.cipta_muri_mobile.data

import com.google.gson.annotations.SerializedName

data class SaldoTransaction(
    val id: String,
    @SerializedName("rekening_id") val rekeningId: String,
    val amount: String,
    val type: String, // "credit" | "debit"
    val description: String?,
    @SerializedName("created_at") val createdAt: String?
)

