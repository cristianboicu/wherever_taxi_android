package com.cristianboicu.wherevertaxi.data.repository

import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

interface IRepository {

    suspend fun getAuthenticatedUser(): User?

    fun getAuthenticatedUserId(): String?

    fun logOutUser(): Boolean

    suspend fun getDirection(origin: LatLng, destination: LatLng): String?

    suspend fun getPredictions(query: String): MutableList<AutocompletePrediction>

    fun getLoggedUserId(): String?

    suspend fun getGeocoding(
        place_id: String,
        apiKey: String,
    ): GeocodingResponse?

    suspend fun listenAvailableDrivers(): DatabaseReference

    suspend fun listenToRequestedRide(uid: String): DatabaseReference

    suspend fun updateUserData(uid: String, updatedUser: User)

    suspend fun postRideRequest(
        rideRequest: RideRequest
    )
}