package com.example.soccerbetapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soccerbetapp.api.GameData
import com.example.soccerbetapp.api.GameRepository
import com.example.soccerbetapp.api.SoccerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    lateinit var authUser: AuthUser
    //private var loggedIn : MutableLiveData<Boolean> = MutableLiveData(false)
    var currentUser = invalidUser
    private val api = SoccerApi.create()
    private val repo = GameRepository(api)
    private var nextGames = MutableLiveData<List<GameData>>()
    lateinit var curGame: GameData

    fun fetchNextGames() {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            nextGames.postValue(repo.getNextGames())
            //repo.getNextGamesTest()
        }
    }

    fun observeNextGames(): LiveData<List<GameData>> {
        return nextGames
    }
}