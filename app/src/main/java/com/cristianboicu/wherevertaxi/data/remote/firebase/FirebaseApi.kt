package com.cristianboicu.wherevertaxi.data.remote.firebase

import android.util.Log
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.model.driver.Driver
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.utils.ProjectConstants
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.RIDE_REQUEST_PATH
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

    private val TAG = "FirebaseApi"

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun getCurrentUserData(currentUser: FirebaseUser): User? {
        return try {
            val res =
                database.child(ProjectConstants.USERS_PATH).child(currentUser.uid).get().await()
            val fetchedUser = res.getValue(User::class.java)
            Log.d(TAG, "Got value $fetchedUser")
            fetchedUser
        } catch (e: Exception) {
            Log.d(TAG, "Error getting data ${e.message}")
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

    override suspend fun getAvailableDrivers(): List<Driver?>? {
        return try {
            val availableDrivers = mutableListOf<Driver?>()
            val res =
                database.child(ProjectConstants.AVAILABLE_DRIVERS_PATH).get().await()

            for (driver in res.children) {
                availableDrivers.add(driver.getValue(Driver::class.java))
            }

            Log.d(TAG, "Got value $availableDrivers")
            availableDrivers
        } catch (e: Exception) {
            Log.d(TAG, "Got drivers error ${e.message}")
            null
        }
    }

    override suspend fun listenAvailableDrivers(): DatabaseReference {
        return database.child(ProjectConstants.AVAILABLE_DRIVERS_PATH)
    }

    override suspend fun listenToRequestedRide(uid: String): DatabaseReference {
        return database.child(ProjectConstants.ONGOING_RIDES_PATH).child(uid)
    }

    override fun logOutUser(): Boolean {
        return try {
            firebaseAuth.signOut()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun postRideRequest(
        rideRequest: RideRequest,
    ) {
        try {
            database.child(RIDE_REQUEST_PATH).child(rideRequest.uid)
                .setValue(rideRequest.rideRequestData).await()
            Log.d(TAG, "succes adding ride request")
        } catch (e: Exception) {
            Log.d(TAG, "error adding ride request ${e.message}")
        }
    }
}