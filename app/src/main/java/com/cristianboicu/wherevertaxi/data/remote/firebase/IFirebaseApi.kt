package com.cristianboicu.wherevertaxi.data.remote.firebase

import com.cristianboicu.wherevertaxi.data.model.Driver
import com.cristianboicu.wherevertaxi.data.model.User
import com.google.firebase.auth.FirebaseUser

interface IFirebaseApi {
    fun getCurrentUser(): FirebaseUser?

    suspend fun getCurrentUserData(currentUser: FirebaseUser): User?

    suspend fun updateUserData(uid: String, updatedUser: User)

    suspend fun getDrivers(): List<Driver?>?

    fun logOutUser(): Boolean
}