package com.example.shoeshop.data

import com.example.shoeshop.data.service.UserManagementService // ИЗМЕНИТЕ ИМПОРТ
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy

object RetrofitInstance {
    const val SUBABASE_URL = "https://wbhheqswpoozupzxuptx.supabase.co"
    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndiaGhlcXN3cG9venVwenh1cHR4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU3NTY0NTIsImV4cCI6MjA4MTMzMjQ1Mn0.lVaxu4Cx2rxLAu7GxdUu1fKdkNSHl9blYgoIllpVVjk"

    var proxy: Proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("10.207.106.71", 3128))

    var client: OkHttpClient = OkHttpClient.Builder()
        .proxy(proxy)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer $API_KEY")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SUBABASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val userManagementService = retrofit.create(UserManagementService::class.java)
}