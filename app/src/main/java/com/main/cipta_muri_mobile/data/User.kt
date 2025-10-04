package com.main.cipta_muri_mobile.data

import com.google.gson.annotations.SerializedName

data class User(
    // FIELDS WAJIB DARI TABEL REKENING (OUTPUT PHP LOGIN)

    // ID Rekening (Digunakan oleh user.id di SessionManager)
    val id: String,

    // Nama (Dari key PHP 'nama', di-map ke Kotlin field 'name')
    @SerializedName("nama")
    val name: String,

    // NIK (Digunakan oleh user.nik di SessionManager)
    val nik: String,

    // Saldo (balance)
    val balance: Double,

    // No Rekening (Dari key PHP 'no_rekening', di-map ke Kotlin field 'accountNumber')
    @SerializedName("no_rekening")
    val accountNumber: String,

    // Field Opsional (Jika diambil dari query PHP)
    @SerializedName("user_id")
    val userId: String?,

    // Asumsi Anda juga mengambil points_balance
    @SerializedName("points_balance")
    val points: Int?,

    @SerializedName("total_waste_kg")
    val totalWasteKg: Int
    // Jika ada field totalWasteKg, Anda harus mengambilnya dari API atau menghitungnya
    // val totalWasteKg: Int?
)