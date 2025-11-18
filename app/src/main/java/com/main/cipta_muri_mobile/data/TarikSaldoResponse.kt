package com.main.cipta_muri_mobile.data

import com.google.gson.annotations.SerializedName

data class TarikSaldoResponse(
    val id: String? = null,
    @SerializedName(value = "amount", alternate = ["nominal", "jumlah"])
    val amount: String? = null,
    @SerializedName(value = "status", alternate = ["state", "progress"])
    val status: String? = null,
    @SerializedName(value = "description", alternate = ["keterangan", "catatan"])
    val description: String? = null,
    @SerializedName(value = "metode", alternate = ["method", "bank"])
    val metode: String? = null,
    @SerializedName(value = "created_at", alternate = ["tanggal", "tanggal_pengajuan", "updated_at"])
    val createdAt: String? = null
)
