package com.main.cipta_muri_mobile.data

import com.google.gson.annotations.SerializedName

data class Rekening(
    val id: String,

    @SerializedName("no_rekening")
    val noRekening: String,

    val nama: String,
    val dusun: String?,
    val rt: String?,
    val rw: String?,
    val alamat: String?,
    val gender: String,

    @SerializedName("no_kk")
    val noKk: String?,

    val nik: String?,

    @SerializedName("tanggal_lahir")
    val tanggalLahir: String?,

    val pendidikan: String?,
    val telepon: String?,

    @SerializedName("balance")
    val saldo: Double, // Mengubah nama field untuk lebih mudah dipahami

    @SerializedName("points_balance")
    val pointsBalance: Int,

    @SerializedName("status_pegadaian")
    val statusPegadaian: Boolean, // Data tipe TINYINT(1) bisa di-map ke Boolean

    @SerializedName("status_lengkap")
    val statusLengkap: Boolean,

    @SerializedName("status_desa")
    val statusDesa: Boolean,

    @SerializedName("user_id")
    val userId: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("deleted_at")
    val deletedAt: String?
)
