package com.main.cipta_muri_mobile.data


import com.main.cipta_muri_mobile.data.User
import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("CIPTA_MURI_SESSION", Context.MODE_PRIVATE)

    companion object {
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_NAMA = "nama"
        const val KEY_REKENING_ID = "rekeningId"
        const val KEY_NIK = "nik"
        const val KEY_BALANCE = "balance" // Saldo
    }

    fun createLoginSession(user: User) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            // Field ini sekarang merujuk ke properti di data class Anda
            putString(KEY_REKENING_ID, user.id)
            putString(KEY_NAMA, user.name)
            putString(KEY_NIK, user.nik)
            putFloat(KEY_BALANCE, user.balance.toFloat())
            apply()
        }
    }

    // 2. Cek status login
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // 3. Ambil nama pengguna
    fun getUserName(): String? {
        return prefs.getString(KEY_NAMA, null)
    }

    // 4. Logout
    fun logoutUser() {
        prefs.edit().clear().apply()
    }
}