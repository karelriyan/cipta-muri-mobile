package com.main.cipta_muri_mobile.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "settings")

object TokenStore {
    private val KEY = stringPreferencesKey("auth_token")

    suspend fun save(ctx: Context, token: String) {
        ctx.dataStore.edit { it[KEY] = token }
    }

    suspend fun clear(ctx: Context) {
        ctx.dataStore.edit { it.remove(KEY) }
    }

    suspend fun read(ctx: Context): String? {
        return ctx.dataStore.data.first()[KEY]
    }
}

