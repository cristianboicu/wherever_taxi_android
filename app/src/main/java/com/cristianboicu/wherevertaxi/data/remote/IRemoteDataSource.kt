package com.cristianboicu.wherevertaxi.data.remote

import com.cristianboicu.wherevertaxi.data.model.Driver
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.auth.FirebaseUser

interface IRemoteDataSource {

    fun getLoggedUserId(): FirebaseUser?

    suspend fun getLoggedUserData(currentUser: FirebaseUser): User?

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

    suspend fun getDrivers(): List<Driver?>?
}