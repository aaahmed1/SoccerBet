package com.example.soccerbetapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.soccerbetapp.model.Bet
import com.example.soccerbetapp.model.DBUser
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class DBHelper {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "DBError"

    fun getDBUser(uid: String, name: String, user: MutableLiveData<DBUser>) {
        Log.d(TAG, "YO")
        val docRef = db.collection("users").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot: ${document.data}")
                    user.postValue(document.toObject(DBUser::class.java))
                } else {
                    Log.d(TAG, "else")
                    createDBUser(uid, name, user)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun createDBUser(uid: String, name: String, user: MutableLiveData<DBUser>) {
        Log.d(TAG, "create")
        val newUser = DBUser(name)
        db.collection("users").document(uid)
            .set(newUser)
            .addOnSuccessListener {
                user.postValue(newUser)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "set failed with ", exception)
            }
    }

    fun getBet(fixture: Int, bet: MutableLiveData<Bet>) {
        val docRef = db.collection("bets").document(fixture.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot: ${document.data}")
                    bet.postValue(document.toObject(Bet::class.java))
                } else {
                    Log.d(TAG, "else")
                    createBet(fixture, bet)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun createBet(fixture: Int, bet: MutableLiveData<Bet>) {
        Log.d(TAG, "create")
        val newBet = Bet()
        db.collection("bets").document(fixture.toString())
            .set(newBet)
            .addOnSuccessListener {
                bet.postValue(newBet)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "set failed with ", exception)
            }
    }
}