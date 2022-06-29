package com.cristianboicu.wherevertaxi.data.remote

import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.model.driver.Driver
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

interface IRemoteDataSource {

    fun getLoggedUserId(): FirebaseUser?

    suspend fun getLoggedUserData(currentUser: FirebaseUser): User?

    suspend fun listenAvailableDrivers(): DatabaseReference

    suspend fun listenToRequestedRide(rideId: String): DatabaseReference

    suspend fun listenToCompletedRide(rideId: String): DatabaseReference

    fun logOutUser(): Boolean

    suspend fun updateUserData(uid: String, updatedUser: User)

    suspend fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
    ): DirectionResponses?

    suspend fun getGeocoding(
        place_id: String,
        apiKey: String,
    ): GeocodingResponse?

    suspend fun getPredictions(query: String): MutableList<AutocompletePrediction>

    suspend fun getAvailableDrivers(): List<Driver?>?

    suspend fun postRideRequest(
        rideRequest: RideRequest
    ): String?
}