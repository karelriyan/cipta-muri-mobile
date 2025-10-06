package com.main.cipta_muri_mobile.data

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("ciptamuri_api/get_rekening.php")
    fun getDaftarRekening(): Call<RekeningResponse>

    @FormUrlEncoded
    @POST("ciptamuri_api/login.php")
    fun loginUser(
        @Field("nik") nik: String,
        @Field("pin") pin: String
    ): Call<LoginResponse>

    @GET("mutasi_saldo.php")
    suspend fun getMutasiSaldo(
        @Query("user_id") userId: Int
    ): Response<List<MutasiSaldo>>



}
