package com.main.cipta_muri_mobile.data

data class MutasiSaldo(
    val id: Int,
    val tanggal: String,
    val keterangan: String,
    val nominal: Double,
    val tipe: String // "masuk" atau "keluar"
)
