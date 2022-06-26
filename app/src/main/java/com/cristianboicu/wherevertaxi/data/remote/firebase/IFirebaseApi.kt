package com.cristianboicu.wherevertaxi.data.remote.firebase

import com.cristianboicu.wherevertaxi.data.model.driver.Driver
import com.cristianboicu.wherevertaxi.data.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

interface IFirebaseApi {
    fun getCurrentUser(): FirebaseUser?

    suspend fun getCurrentUserData(currentUser: FirebaseUser): User?

    suspend fun updateUserData(uid: String, updatedUser: User)

    suspend fun getAvailableDrivers(): List<Driver?>?

    suspend fun listenAvailableDrivers(): DatabaseReference

    fun logOutUser(): Boolean
}