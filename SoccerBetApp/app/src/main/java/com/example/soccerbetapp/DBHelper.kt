package com.example.soccerbetapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.soccerbetapp.model.Bet
import com.example.soccerbetapp.model.DBUser
import com.example.soccerbetapp.model.UserBet
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.firestore

class DBHelper(private var userListener: ListenerRegistration?, private var betListener: ListenerRegistration?) {
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
                    addUserListener(user, uid)
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
                addUserListener(user, uid)
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
                    addBetListener(bet, fixture)
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
                addBetListener(bet, fixture)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "set failed with ", exception)
            }
    }

    fun makeUserBet(points: Int, result: Int, uid: String, fixture: Int) {
        val userBet = UserBet(fixture, uid, points, result)
        val newUserBet = db.collection("userbets").document()
        val doc1 = db.collection("bets").document(fixture.toString())
        val doc2 = db.collection("users").document(uid)
        db.runBatch { batch ->
            batch.set(newUserBet, userBet)
            batch.update(doc1,"userBets", FieldValue.arrayUnion(uid))
            batch.update(doc2,"bets", FieldValue.arrayUnion(fixture))
            if (result == 0) batch.update(doc1,"homePoints", FieldValue.increment(points.toLong()))
            else if (result == 1) batch.update(doc1,"drawPoints", FieldValue.increment(points.toLong()))
            else batch.update(doc1, "awayPoints", FieldValue.increment(points.toLong()))
            batch.update(doc2,"total", FieldValue.increment(-1 * points.toLong()))
        }
        .addOnSuccessListener {
            Log.d(TAG, "Batch write success")
        }
        .addOnFailureListener {
            Log.d(TAG, "Batch write failure")
        }
    }

    fun addUserListener(user: MutableLiveData<DBUser>, uid: String) {
        val query = db.collection("users").document(uid)
        userListener = query.addSnapshotListener(MetadataChanges.INCLUDE) {snapshot, e ->
            if (e != null) {
                Log.w(TAG, "User Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val hasPendingWrites = snapshot.metadata.hasPendingWrites()
                if (!hasPendingWrites) {
                    user.postValue(snapshot.toObject(DBUser::class.java))
                }
                else {
                    Log.d(TAG, "User local change")
                }
            }
        }
    }

    fun addBetListener(bet: MutableLiveData<Bet>, fixture: Int) {
        val query = db.collection("bets").document(fixture.toString())
        betListener = query.addSnapshotListener(MetadataChanges.INCLUDE) {snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Bet Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val hasPendingWrites = snapshot.metadata.hasPendingWrites()
                if (!hasPendingWrites) {
                    bet.postValue(snapshot.toObject(Bet::class.java))
                }
                else {
                    Log.d(TAG, "Bet local change")
                }
            }
        }
    }
}