package com.main.cipta_muri_mobile.data

import com.google.gson.annotations.SerializedName

data class HargaSampahResponse(
    val id: String? = null,
    @SerializedName(value = "nama", alternate = ["nama_sampah", "sampah", "jenis", "jenis_sampah"])
    val nama: String? = null,
    @SerializedName(
        value = "harga_per_kg",
        alternate = ["harga", "harga_kg", "harga_perkilogram", "harga_kilogram", "harga_per_kilo"]
    )
    val hargaPerKg: String? = null,
    @SerializedName(
        value = "total_setoran",
        alternate = ["total_kg", "jumlah_setoran", "total_sampah", "total_berat", "jumlah_kg"]
    )
    val totalSetoranKg: String? = null,
    @SerializedName(value = "last_update", alternate = ["updated_at", "tanggal", "tgl", "created_at"])
    val lastUpdate: String? = null
)
