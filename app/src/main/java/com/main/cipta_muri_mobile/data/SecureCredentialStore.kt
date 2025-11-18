package com.main.cipta_muri_mobile.data

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Handles secure persistence of login credentials for autofill.
 */
class SecureCredentialStore(context: Context) {

    private val prefs = runCatching {
        val appContext = context.applicationContext
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            appContext,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrElse {
        Log.e(TAG, "Unable to initialize encrypted preferences", it)
        // Fallback to unencrypted SharedPreferences if crypto initialization fails.
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun remember(nik: String, birthDateDisplay: String) {
        runCatching {
            prefs.edit()
                .putString(KEY_NIK, nik)
                .putString(KEY_BIRTH_DATE, birthDateDisplay)
                .apply()
        }.onFailure {
            Log.e(TAG, "Unable to save remembered credentials", it)
        }
    }

    fun getNik(): String? = runCatching { prefs.getString(KEY_NIK, null) }.getOrNull()

    fun getBirthDate(): String? = runCatching { prefs.getString(KEY_BIRTH_DATE, null) }.getOrNull()

    fun hasCredentials(): Boolean = !getNik().isNullOrBlank() && !getBirthDate().isNullOrBlank()

    fun clear() {
        runCatching { prefs.edit().clear().apply() }
            .onFailure { Log.e(TAG, "Unable to clear remembered credentials", it) }
    }

    companion object {
        private const val TAG = "SecureCredentialStore"
        private const val PREF_NAME = "CIPTA_MURI_CREDENTIALS"
        private const val KEY_NIK = "remembered_nik"
        private const val KEY_BIRTH_DATE = "remembered_birth_date"
    }
}
