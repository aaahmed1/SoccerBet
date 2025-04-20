package com.example.soccerbetapp.model

data class Bet(
    var homePoints: Int = 0,
    var drawPoints: Int = 0,
    var awayPoints: Int = 0,
    var finished: Boolean = false,
    var userBets: List<String> = listOf()
)