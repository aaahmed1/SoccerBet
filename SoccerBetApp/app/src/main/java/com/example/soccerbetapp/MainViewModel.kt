package com.example.soccerbetapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerbetapp.api.GameData
import com.example.soccerbetapp.api.GameRepository
import com.example.soccerbetapp.api.SoccerApi
import com.example.soccerbetapp.model.Bet
import com.example.soccerbetapp.model.DBUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    lateinit var authUser: AuthUser
    //private var loggedIn : MutableLiveData<Boolean> = MutableLiveData(false)
    var currentUser = invalidUser
    private val api = SoccerApi.create()
    private val repo = GameRepository(api)
    private var nextGames = MutableLiveData<List<GameData>>()
    private var curGame = MutableLiveData<GameData>()
    private var dbUser = MutableLiveData<DBUser>()
    private val dbHelper = DBHelper()
    private var curBet = MutableLiveData<Bet>()

    fun fetchNextGames() {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            nextGames.postValue(repo.getNextGames())
            //repo.getNextGamesTest()
        }
    }

    fun observeNextGames(): LiveData<List<GameData>> {
        return nextGames
    }

    fun observeCurGame(): LiveData<GameData> {
        return curGame
    }

    fun setCurGame(game: GameData) {
        curGame.value = game
    }

    fun observeDBUser(): LiveData<DBUser> {
        return dbUser
    }

    fun setDBUser(user: DBUser) {
        dbUser.value = user
    }

    fun updateDBUser() {
        dbHelper.getDBUser(currentUser.uid, currentUser.name, dbUser)
    }

    fun observeCurBet(): LiveData<Bet> {
        return curBet
    }

    fun updateCurBet(fixture: Int) {
        dbHelper.getBet(fixture, curBet)
    }
}