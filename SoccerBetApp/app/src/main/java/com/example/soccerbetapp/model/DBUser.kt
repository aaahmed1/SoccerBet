package com.example.soccerbetapp.model

data class DBUser(
    var name: String = "",
    var total: Int = 5000,
    var bets: List<Int> = listOf()
)