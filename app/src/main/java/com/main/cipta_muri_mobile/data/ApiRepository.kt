package com.main.cipta_muri_mobile.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import java.math.BigDecimal

class ApiRepository(private val ctx: Context) {
    private val api by lazy { ApiClient.create(ctx) }
    private val apiPublic by lazy { ApiClient.createPublic() }

    suspend fun login(nik: String, tanggal: String) = runCatching {
        try {
            val res = api.login(mapOf("nik" to nik, "tanggal_lahir" to tanggal))
            if (res.success && res.data != null) {
                TokenStore.save(ctx, res.data.token)
            } else {
                error(res.message ?: "Login gagal")
            }
        } catch (e: retrofit2.HttpException) {
            val code = e.code()
            val msg = parseErrorMessage(e.response()?.errorBody())
            val fallback = when (code) {
                401 -> "Tidak terautentikasi. Periksa NIK/tanggal lahir."
                422 -> "Data tidak valid. Periksa format tanggal (YYYY-MM-DD)."
                404 -> "Endpoint tidak ditemukan. Cek base URL atau path."
                else -> "Kesalahan server (${code})."
            }
            throw RuntimeException(msg ?: fallback)
        } catch (e: Exception) {
            throw RuntimeException(e.message ?: "Gagal terhubung ke server")
        }
    }

    suspend fun refreshSessionFromServer() = runCatching {
        val summary = api.rekening().data
        val nasabah = summary?.rekening ?: api.profile().data

        if (nasabah != null) {
            val balanceStr = summary?.balance ?: nasabah.balance
            val balance = balanceStr?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val user = User(
                id = nasabah.id,
                name = nasabah.nama ?: "",
                nik = nasabah.nik ?: "",
                accountNumber = nasabah.noRekening ?: "",
                balance = balance.toDouble(),
                userId = nasabah.userId,
                points = summary?.pointsBalance ?: nasabah.pointsBalance,
                totalWasteKg = 0 // Tidak tersedia; opsional dari endpoint lain
            )
            SessionManager(ctx).createLoginSession(user)
        } else {
            error("Gagal memuat profil nasabah")
        }
    }

    suspend fun logout() = runCatching {
        try { api.logout() } catch (_: Exception) { /* ignore network errors on logout */ }
        TokenStore.clear(ctx)
        SessionManager(ctx).clearSession()
    }

    suspend fun getRekening() = runCatching { api.rekening().data }

    suspend fun getProfile() = runCatching { api.profile().data }

    suspend fun getSaldoTransactions(type: String? = null) =
        runCatching { api.saldoTransactions(type).data.orEmpty() }

    suspend fun getHargaSampah() = runCatching {
        // Coba pakai auth dulu; jika gagal (misal token kedaluwarsa/401) fallback ke public.
        val primary = runCatching {
            val res = api.sampah()
            if (!res.success) error(res.message ?: "Gagal memuat harga sampah")
            res.data.orEmpty()
        }
        primary.getOrElse {
            val res = apiPublic.sampah()
            if (!res.success) error(res.message ?: "Gagal memuat harga sampah (guest)")
            res.data.orEmpty()
        }
    }

    suspend fun createTarikSaldo(amount: Int, desc: String?) =
        runCatching { api.tarikSaldo(TarikSaldoRequest(amount, desc)).data }

    suspend fun getRiwayatSetoran() = runCatching {
        val res = api.setorSampah()
        if (!res.success) {
            // Coba fallback public jika token bermasalah
            val pubRes = apiPublic.setorSampah()
            if (!pubRes.success) error(pubRes.message ?: "Gagal memuat riwayat setoran")
            pubRes.data.orEmpty()
        } else {
            res.data.orEmpty()
        }
    }

    suspend fun getRiwayatTarikSaldo() = runCatching {
        val res = api.listTarikSaldo()
        if (!res.success) error(res.message ?: "Gagal memuat riwayat penarikan")
        res.data.orEmpty()
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? = try {
        BigDecimal(this.replace(",", "").trim())
    } catch (_: Exception) { null }

    private fun parseErrorMessage(body: ResponseBody?): String? {
        return try {
            val text = body?.string() ?: return null
            val json = Gson().fromJson(text, JsonObject::class.java)
            when {
                json.has("message") -> json.get("message").asString
                json.has("error") -> json.get("error").asString
                json.has("errors") -> json.get("errors").toString()
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }
}
