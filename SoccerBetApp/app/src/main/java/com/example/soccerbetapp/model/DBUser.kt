package com.example.soccerbetapp.model

import com.google.firebase.firestore.DocumentId

data class DBUser(
    var name: String = "",
    var total: Int = 5000,
    var bets: List<Int> = listOf(),
    @DocumentId var id: String = ""
)