package com.main.cipta_muri_mobile.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.main.cipta_muri_mobile.data.RetrofitClient
import com.main.cipta_muri_mobile.data.LoginResponse
import com.main.cipta_muri_mobile.data.SessionManager
import com.main.cipta_muri_mobile.data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application.applicationContext)

    fun login(nik: String, pin: String, callback: (Boolean, String?, User?) -> Unit) {

        RetrofitClient.instance.loginUser(nik, pin)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.status == "success" && body.user != null) {

                            Log.i("LOGIN_SUCCESS", "Login Berhasil untuk NIK: $nik")

                            sessionManager.createLoginSession(body.user)

                            callback(true, null, body.user)

                        } else {
                            val message = body?.message ?: "NIK atau Tanggal Lahir tidak valid."
                            Log.w("LOGIN_FAIL", message)
                            callback(false, message, null)
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Gagal terhubung ke server."
                        Log.e("LOGIN_ERROR", "HTTP Error: ${response.code()}, Pesan: $errorMsg")
                        callback(false, "Server Error. Cek konfigurasi API di XAMPP.", null)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LOGIN_FAILURE", "Gagal Koneksi Jaringan: ${t.message}")
                    callback(false, "Tidak dapat terhubung ke server. Cek WiFi/IP.", null)
                }
            })
    }
}