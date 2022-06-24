package com.cristianboicu.wherevertaxi.data.remote.firebase

import android.util.Log
import com.cristianboicu.wherevertaxi.data.model.Driver
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.utils.ProjectConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseApi
@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: DatabaseReference,
) : IFirebaseApi {

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun getCurrentUserData(currentUser: FirebaseUser): User? {
        return try {
            val res =
                database.child(ProjectConstants.USERS_PATH).child(currentUser.uid).get().await()
            val fetchedUser = res.getValue(User::class.java)
            Log.d("RemoteDataSource", "Got value $fetchedUser")
            fetchedUser
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "Error getting data ${e.message}")
            null
        }
    }

    override suspend fun updateUserData(uid: String, updatedUser: User) {
        try {
            database.child(ProjectConstants.USERS_PATH).child(uid).child("fname")
                .setValue(updatedUser.fname).await()
            database.child(ProjectConstants.USERS_PATH).child(uid).child("sname")
                .setValue(updatedUser.sname).await()
            database.child(ProjectConstants.USERS_PATH).child(uid).child("email")
                .setValue(updatedUser.email).await()
        } catch (e: Exception) {

        }
    }

    override suspend fun getDrivers(): List<Driver?>? {
        return try {
            val listResult = mutableListOf<Driver?>()
            val res =
                database.child(ProjectConstants.DRIVERS_PATH).get().await()
            for (driver in res.children) {
                listResult.add(driver.getValue(Driver::class.java))
            }
            Log.d("RemoteDataSource", "Got value $listResult")
            listResult
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "Got drivers error ${e.message}")
            null
        }
    }

    override fun logOutUser(): Boolean {
        return try {
            firebaseAuth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }
}