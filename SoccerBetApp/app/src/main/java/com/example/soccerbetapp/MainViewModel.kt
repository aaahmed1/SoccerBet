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
import com.example.soccerbetapp.model.UserBet
import com.google.firebase.firestore.ListenerRegistration
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
    private var userListener: ListenerRegistration? = null
    private var betListener: ListenerRegistration? = null
    private var usersListener: ListenerRegistration? = null
    private val dbHelper = DBHelper(userListener, betListener, usersListener)
    private var curBet = MutableLiveData<Bet>()
    private var myGames = MutableLiveData<List<GameData>>()
    private var users = MutableLiveData<List<DBUser>>()
    private var curUserBet = MutableLiveData<UserBet>()
    private var betFinished = MutableLiveData<Boolean>()

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        betListener?.remove()
        usersListener?.remove()
    }

    fun fetchNextGames() {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            nextGames.postValue(repo.getNextGames())
            //repo.getNextGamesTest()
        }
    }

    fun fetchMyGames() {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val myList = mutableListOf<GameData>()
            for (fixture in dbUser.value!!.bets) {
                myList.add(repo.getOneGame(fixture))
            }
            myGames.postValue(myList.toList())
        }
    }

    fun fetchCurGame() {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            curGame.postValue(repo.getOneGame(curGame.value!!.fixture.id))
        }
    }

    fun observeMyGames(): LiveData<List<GameData>> {
        return myGames
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

    fun clearCurBet() {
    }

    fun makeUserBet(points: Int, result: Int) {
        dbHelper.makeUserBet(points, result, currentUser.uid, curGame.value!!.fixture.id, curUserBet)
    }

    fun removeBetListener() {
        betListener?.remove()
    }

    fun removeUserListener() {
        userListener?.remove()
    }

    fun removeUsersListener() {
        usersListener?.remove()
    }

    fun observeUsers(): LiveData<List<DBUser>> {
        return users
    }

    fun updateUsers() {
        dbHelper.getUsers(users)
    }

    fun updateUserBet(fixture: Int) {
        dbHelper.getUserBet(fixture, currentUser.uid, curUserBet)
    }

    fun observeUserBet(): LiveData<UserBet> {
        return curUserBet
    }

    fun awardBet(fixture: Int, result: Int) {
        dbHelper.awardBet(fixture, result, betFinished)
    }

    fun observeBetFinished(): LiveData<Boolean> {
        return betFinished
    }

    fun setBetFinished(res: Boolean) {
        betFinished.value = res
    }
}