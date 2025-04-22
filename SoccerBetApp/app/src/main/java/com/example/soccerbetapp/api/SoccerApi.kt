package com.example.soccerbetapp.api

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SoccerApi {
    @GET("/fixtures?league=39&season=2024&next=5")
    suspend fun getNextGamesTest(): Response<ResponseBody>

    @GET("/fixtures?league=39&season=2024&next=10")
    suspend fun getNextGames(): GameResponse

    @GET("/fixtures")
    suspend fun getOneGame(@Query("id") id: Int): GameResponse

    companion object {
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("v3.football.api-sports.io")
            .build()
        fun create(): SoccerApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): SoccerApi {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("x-apisports-key", "3082d5281d1446b7e4735e5d5564d097")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.HEADERS
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SoccerApi::class.java)
        }
    }
}