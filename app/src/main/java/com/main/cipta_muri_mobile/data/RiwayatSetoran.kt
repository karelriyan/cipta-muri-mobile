package com.main.cipta_muri_mobile.data

import com.google.gson.annotations.SerializedName

data class RiwayatSetoran(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("tanggal")
    val tanggal: String = "",

    @SerializedName("rekening_id")
    val rekeningId: Int = 0,

    @SerializedName("jenis_setoran")
    val jenisSetoran: String = "",

    @SerializedName("berat")
    val berat: Double = 0.0,

    @SerializedName("user_id")
    val userId: Int = 0,

    @SerializedName("total_saldo_dihasilkan")
    val totalSaldoDihasilkan: Double = 0.0,

    @SerializedName("calculation_performed")
    val calculationPerformed: Int = 0
) {
    val beratFormatted: String
        get() = "$berat kg"

    val totalSaldoFormatted: String
        get() = "Rp ${String.format("%,.0f", totalSaldoDihasilkan)}"

    val keterangan: String
        get() = "$jenisSetoran - Rekening: $rekeningId"

    val isCalculationPerformed: Boolean
        get() = calculationPerformed == 1
}