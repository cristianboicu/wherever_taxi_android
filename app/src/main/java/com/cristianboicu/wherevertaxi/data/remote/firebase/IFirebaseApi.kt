package com.cristianboicu.wherevertaxi.data.remote.firebase

import com.cristianboicu.wherevertaxi.data.model.driver.Driver
import com.cristianboicu.wherevertaxi.data.model.ride.CompletedRide
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.model.user.PaymentMethod
import com.cristianboicu.wherevertaxi.data.model.user.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

interface IFirebaseApi {
    fun getCurrentUser(): FirebaseUser?

    suspend fun saveRemoteUser(uid: String, user: User)

    suspend fun getRemoteUser(uid: String): User?

    suspend fun updateUserData(uid: String, updatedUser: User)

    suspend fun getAvailableDrivers(): List<Driver?>?

    suspend fun listenAvailableDrivers(): DatabaseReference

    suspend fun listenToRequestedRide(rideId: String): DatabaseReference

    suspend fun listenToCompletedRide(uid: String, rideId: String): DatabaseReference

    suspend fun getCompletedRidesByUserId(uid: String): List<CompletedRide?>

    suspend fun cancelRide(rideId: String)

    fun logOutUser(): Boolean

    suspend fun postRideRequest(
        rideRequest: RideRequest,
    ): String?

    suspend fun savePaymentMethod(uid: String, remotePayment: PaymentMethod): String?
}