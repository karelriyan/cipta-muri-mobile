package com.main.cipta_muri_mobile.data

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("ciptamuri_api/get_rekening.php")
    fun getDaftarRekening(): Call<RekeningResponse>

    @FormUrlEncoded
    @POST("ciptamuri_api/login.php")
    fun loginUser(
        @Field("nik") nik: String,
        @Field("pin") pin: String
    ): Call<LoginResponse>
}