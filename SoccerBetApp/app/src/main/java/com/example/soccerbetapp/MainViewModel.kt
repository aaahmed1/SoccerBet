package com.example.soccerbetapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    lateinit var authUser: AuthUser
    //private var loggedIn : MutableLiveData<Boolean> = MutableLiveData(false)
    var currentUser = invalidUser
}