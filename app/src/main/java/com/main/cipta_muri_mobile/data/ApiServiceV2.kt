package com.main.cipta_muri_mobile.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceV2 {
    @POST("nasabah/login")
    suspend fun login(@Body body: Map<String, String>): ApiResponse<LoginData>

    @GET("nasabah/profile")
    suspend fun profile(): ApiResponse<Nasabah>

    @POST("nasabah/logout")
    suspend fun logout(): ApiResponse<Unit>

    @GET("rekening")
    suspend fun rekening(): ApiResponse<RekeningSummary>

    @GET("rekening/saldo-transactions")
    suspend fun saldoTransactions(@Query("type") type: String? = null): ApiResponse<List<SaldoTransaction>>

    @GET("setor-sampah")
    suspend fun setorSampah(): ApiResponse<List<SetoranSampahResponse>>

    @POST("tarik-saldo")
    suspend fun tarikSaldo(@Body body: TarikSaldoRequest): ApiResponse<Any>

    @GET("tarik-saldo")
    suspend fun listTarikSaldo(): ApiResponse<List<TarikSaldoResponse>>

    @GET("berita")
    suspend fun berita(
        @Query("q") q: String? = null,
        @Query("category") category: String? = null
    ): ApiResponse<List<Any>>

    @GET("berita/{slug}")
    suspend fun beritaDetail(@Path("slug") slug: String): ApiResponse<Any>

    @GET("sampah")
    suspend fun sampah(): ApiResponse<List<HargaSampahResponse>>

    @GET("sampah/{id}")
    suspend fun sampahDetail(@Path("id") id: String): ApiResponse<HargaSampahResponse>
}
