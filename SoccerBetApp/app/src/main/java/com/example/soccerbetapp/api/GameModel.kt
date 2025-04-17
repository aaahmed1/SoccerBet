package com.example.soccerbetapp.api

data class GameResponse(val response: List<GameData>)

data class GameData(val fixture: Fixture, val teams: Teams, val goals: Goals)

data class Fixture(val id: Int, val date: String, val status: Status)

data class Status(val long: String, val short: String, val elapsed: Int?)

data class Teams(val home: Team, val away: Team)

data class Team(val id: Int, val name: String, val logo: String, val winner: Boolean?)

data class Goals(val home: Int?, val away: Int?)