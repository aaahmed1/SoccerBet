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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class DBHelper(private var userListener: ListenerRegistration?, private var betListener: ListenerRegistration?,
               private var usersListener: ListenerRegistration?) {
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

    fun awardBet(fixture: Int) {
        val userBetsRef = db.collection("userbets")
        val userBetsQuery = userBetsRef.whereEqualTo("fixture", fixture)
        userBetsQuery.get()
            .addOnSuccessListener{documents ->
                db.runTransaction{transaction ->
                    val betRef = db.collection("bets").document(fixture.toString())
                    val betDoc = transaction.get(betRef)

                    val betObj = betDoc.toObject(Bet::class.java)!!
                    val total = betObj.homePoints + betObj.drawPoints + betObj.awayPoints
                    var mod1 = 1.0
                    var mod2 = 1.0
                    var mod3 = 1.0
                    if (betObj.homePoints > 0) mod1 = total.toDouble() / betObj.homePoints
                    if (betObj.drawPoints > 0) mod2 = total.toDouble() / betObj.drawPoints
                    if (betObj.awayPoints > 0) mod3 = total.toDouble() / betObj.awayPoints

                    for (document in documents) {
                        val userBetObj = document.toObject(UserBet::class.java)
                        val userRef = db.collection("users").document(userBetObj.uid)

                        val userDoc = transaction.get(userRef)
                        val userPoints = userBetObj.points
                        val homeResult: Long = (userPoints.toDouble() * mod1).toLong()
                        val drawResult: Long = (userPoints.toDouble() * mod2).toLong()
                        val awayResult: Long = (userPoints.toDouble() * mod3).toLong()
                        val bet = userBetObj.result

                        if (bet == 0) transaction.update(userRef, "total", FieldValue.increment(homeResult))
                        else if (bet == 1) transaction.update(userRef, "total", FieldValue.increment(drawResult))
                        else if (bet == 2) transaction.update(userRef, "total", FieldValue.increment(awayResult))
                    }
                    transaction.update(betRef, "finished", true)
                }
                    .addOnSuccessListener{result ->
                        Log.d(TAG, "Transaction success: $result")
                    }
                    .addOnFailureListener{exception ->
                        Log.d(TAG, "Transaction failure, ", exception)
                    }
            }
            .addOnFailureListener{exception ->
                Log.d(TAG, "award query failed, ", exception)
            }
    }

    fun getUsers(users: MutableLiveData<List<DBUser>>) {
        val leaders = mutableListOf<DBUser>()
        val usersRef = db.collection("users")
        usersRef.orderBy("total", Query.Direction.DESCENDING).get()
            .addOnSuccessListener{result ->
                for (document in result) {
                    val user = document.toObject(DBUser::class.java)
                    leaders.add(user)
                }
                Log.d(TAG, "users size: ${leaders.size}")
                users.postValue(leaders.toList())
                addUsersListener(users)
            }
            .addOnFailureListener {exception ->
                Log.d(TAG, "users query failed, ", exception)
            }
    }

    fun addUsersListener(users: MutableLiveData<List<DBUser>>) {
        val usersRef = db.collection("users")
        usersListener = usersRef.orderBy("total", Query.Direction.DESCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE){ snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Bet Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val hasPendingWrites = snapshot.metadata.hasPendingWrites()
                    if (!hasPendingWrites) {
                        val leaders = mutableListOf<DBUser>()
                        for (document in snapshot) {
                            val user = document.toObject(DBUser::class.java)
                            leaders.add(user)
                        }
                        Log.d(TAG, "snapshot size (listen): ${snapshot.size()}")
                        Log.d(TAG, "leaders size (listen): ${leaders.size}")
                        users.postValue(leaders.toList())
                    }
                    else {
                        Log.d(TAG, "Users local change")
                    }
                }
        }
    }
}