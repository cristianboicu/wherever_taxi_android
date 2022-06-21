package com.cristianboicu.wherevertaxi.data.remote

import android.util.Log
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.USERS_PATH
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

class RemoteDataSource(
    private val firebaseAuth: FirebaseAuth,
    private val database: DatabaseReference,
) :
    IRemoteDataSource {

    override fun getLoggedUserId(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun getLoggedUserData(currentUser: FirebaseUser): User? {
        var fetchedUser: User? = null

        database.child(USERS_PATH).child(currentUser.uid).get().addOnSuccessListener {
            fetchedUser = it.getValue(User::class.java)
            Log.i("firebase", "Got value $fetchedUser")
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        return fetchedUser
    }
}