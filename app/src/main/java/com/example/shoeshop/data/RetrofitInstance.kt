package com.example.shoeshop.data

import com.example.shoeshop.data.service.CategoriesService
import com.example.shoeshop.data.service.FavouriteService
import com.example.shoeshop.data.service.ProductsService
import com.example.shoeshop.data.service.ProfileService
import com.example.shoeshop.data.service.UserManagementService // ИЗМЕНИТЕ ИМПОРТ
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy

object RetrofitInstance {
    const val SUPABASE_URL = "https://wbhheqswpoozupzxuptx.supabase.co"
    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndiaGhlcXN3cG9venVwenh1cHR4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU3NTY0NTIsImV4cCI6MjA4MTMzMjQ1Mn0.lVaxu4Cx2rxLAu7GxdUu1fKdkNSHl9blYgoIllpVVjk"

    const val REST_URL = "$SUPABASE_URL/rest/v1/"

    var client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val url = original.url.toString()

            val builder = original.newBuilder()
                .header("apikey", API_KEY)
                .header("Content-Type", "application/json")

            // Ключевое изменение: для favourites используем пользовательский токен
            if (url.contains("favorites") || url.contains("profiles")) {
                // Для избранного и профилей - JWT токен пользователя
                val userToken = SessionManager.accessToken
                if (userToken != null) {
                    builder.header("Authorization", "Bearer $userToken")
                } else {
                    // Если нет токена, всё равно пробуем с anon key
                    builder.header("Authorization", "Bearer $API_KEY")
                }
            } else {
                // Для всех остальных запросов - anon key
                builder.header("Authorization", "Bearer $API_KEY")
            }

            val request = builder.build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SUPABASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val retrofitRest = Retrofit.Builder()
        .baseUrl(REST_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val userManagementService = retrofit.create(UserManagementService::class.java)
    val productsService = retrofitRest.create(ProductsService::class.java)
    val categoriesService = retrofitRest.create(CategoriesService::class.java)
    val favouriteService: FavouriteService = retrofitRest.create(FavouriteService::class.java)
    val profileService: ProfileService = retrofitRest.create(ProfileService::class.java)
}