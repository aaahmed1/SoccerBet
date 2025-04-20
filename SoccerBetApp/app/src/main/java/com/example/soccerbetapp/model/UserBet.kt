package com.example.soccerbetapp.model

data class UserBet(
    var fixture: Int = -1,
    var uid: String = "",
    var points: Int = 0,
    var result: Int = -1
)