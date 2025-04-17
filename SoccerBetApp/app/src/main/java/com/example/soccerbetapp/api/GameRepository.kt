package com.example.soccerbetapp.api

import android.util.Log

class GameRepository(private val soccerApi: SoccerApi) {
    suspend fun getNextGames(): List<GameData> {
        val response = soccerApi.getNextGames()
        //Log.d("Response", response.response.toString())
        return response.response
    }

    suspend fun getNextGamesTest() {
        val response = soccerApi.getNextGamesTest()
        if (response.isSuccessful) {
            var res = response.body()?.string()
            if (res == null) res = "nothing"
            Log.d("responseBody:", res)
        }
    }
}