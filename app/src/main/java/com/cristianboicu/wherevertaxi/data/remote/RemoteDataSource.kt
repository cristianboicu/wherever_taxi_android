package com.cristianboicu.wherevertaxi.data.remote

import android.util.Log
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.USERS_PATH
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: DatabaseReference,
) :
    IRemoteDataSource {

    override fun getLoggedUserId(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun getLoggedUserData(currentUser: FirebaseUser): User? {

        return try {
            val res = database.child(USERS_PATH).child(currentUser.uid).get().await()
            val fetchedUser = res.getValue(User::class.java)
            Log.d("RemoteDataSource", "Got value $fetchedUser")
            fetchedUser
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "Error getting data ${e.message}")
            null
        }

    }
}