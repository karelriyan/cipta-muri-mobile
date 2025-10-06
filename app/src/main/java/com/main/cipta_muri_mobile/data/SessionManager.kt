package com.main.cipta_muri_mobile.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("CIPTA_MURI_SESSION", Context.MODE_PRIVATE)

    companion object {
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_ID = "id"
        const val KEY_NAMA = "nama"
        const val KEY_NIK = "nik"
        const val KEY_USER_ID = "userId"
        const val KEY_ACCOUNT_NUMBER = "accountNumber"
        const val KEY_BALANCE = "balance"
        const val KEY_POINTS = "points"
        const val KEY_TOTAL_WASTE = "totalWasteKg"
    }

    // ✅ Simpan seluruh data user saat login
    fun createLoginSession(user: User) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_ID, user.id)
            putString(KEY_NAMA, user.name)
            putString(KEY_NIK, user.nik)
            putString(KEY_USER_ID, user.userId)
            putString(KEY_ACCOUNT_NUMBER, user.accountNumber)
            putFloat(KEY_BALANCE, user.balance?.toFloat() ?: 0f)
            putInt(KEY_POINTS, user.points ?: 0)
            putInt(KEY_TOTAL_WASTE, user.totalWasteKg ?: 0)
            apply()
        }
    }

    // ✅ Fungsi lainnya tetap di dalam class
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): String? = prefs.getString(KEY_ID, null)
    fun getUserName(): String? = prefs.getString(KEY_NAMA, null)
    fun getUserNik(): String? = prefs.getString(KEY_NIK, null)
    fun getUsername(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserAccountNumber(): String? = prefs.getString(KEY_ACCOUNT_NUMBER, null)
    fun getUserBalance(): Double = prefs.getFloat(KEY_BALANCE, 0f).toDouble()
    fun getUserPoints(): Int = prefs.getInt(KEY_POINTS, 0)
    fun getUserTotalWaste(): Int = prefs.getInt(KEY_TOTAL_WASTE, 0)

    fun logoutUser() {
        prefs.edit().clear().apply()
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

}
