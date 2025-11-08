package com.main.cipta_muri_mobile.data

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://ciptamuri.com/api/"

    fun create(ctx: Context): ApiServiceV2 {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { TokenStore.read(ctx) })
            // Log method + URL for quick verification
            .addInterceptor { chain ->
                val req = chain.request()
                android.util.Log.d("API", "${req.method} ${req.url}")
                chain.proceed(req)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                // Verbose logging to validate final URL + body in Logcat
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiServiceV2::class.java)
    }
}
