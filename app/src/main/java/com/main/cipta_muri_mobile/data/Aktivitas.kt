package com.main.cipta_muri_mobile.data

data class Aktivitas(
    val tanggal: String, // tanggal saja (untuk header grup)
    val jenis: String,
    val keterangan: String,
    val jumlah: String,
    val isMasuk: Boolean,
    val waktu: String // HH:mm untuk tiap transaksi
)
