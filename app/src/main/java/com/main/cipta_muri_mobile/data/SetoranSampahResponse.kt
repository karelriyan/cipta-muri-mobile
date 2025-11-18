package com.main.cipta_muri_mobile.data

import com.google.gson.annotations.SerializedName

data class SetoranSampahResponse(
    val id: String? = null,
    @SerializedName(value = "tanggal", alternate = ["tanggal_setor", "tgl", "created_at"])
    val tanggal: String? = null,
    @SerializedName(value = "jenis_setoran", alternate = ["jenis", "kategori", "tipe"])
    val jenisSetoran: String? = null,
    @SerializedName(value = "keterangan", alternate = ["deskripsi", "description"])
    val keterangan: String? = null,
    @SerializedName(value = "total_harga", alternate = ["total_saldo", "total"])
    val totalHarga: String? = null,
    @SerializedName(value = "total_berat", alternate = ["total_kg", "berat_total", "total_waste"])
    val totalBerat: String? = null,
    val status: String? = null,
    val detail: List<SetoranDetailResponse>? = null
)

data class SetoranDetailResponse(
    @SerializedName(value = "nama_sampah", alternate = ["nama", "sampah", "jenis"])
    val namaSampah: String? = null,
    @SerializedName(value = "kategori", alternate = ["category", "jenis_kategori"])
    val kategori: String? = null,
    @SerializedName(value = "jumlah_kg", alternate = ["berat", "qty", "kuantitas"])
    val jumlahKg: String? = null,
    @SerializedName(value = "harga_total", alternate = ["total_harga", "jumlah_harga"])
    val hargaTotal: String? = null
)
