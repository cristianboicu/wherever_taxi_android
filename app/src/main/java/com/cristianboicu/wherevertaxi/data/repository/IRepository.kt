package com.cristianboicu.wherevertaxi.data.repository

import androidx.lifecycle.LiveData
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.ride.CompletedRide
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.model.user.LocalUser
import com.cristianboicu.wherevertaxi.data.model.user.User
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.database.DatabaseReference

interface IRepository {

    suspend fun refreshAuthenticatedUser()

    suspend fun saveIfNewUser(uid: String, user: User): Boolean

    fun getAuthenticatedUserId(): String?

    suspend fun logOutUser(): Boolean

    suspend fun cancelRide(rideId: String)

    suspend fun getDirection(origin: LatLng, destination: LatLng): String?

    suspend fun getPredictions(query: String): MutableList<AutocompletePrediction>

    fun getLoggedUserId(): String?

    suspend fun getGeocoding(
        place_id: String,
    ): GeocodingResponse?

    suspend fun getReverseGeocoding(
        placeLatLng: LatLng,
    ): String

    suspend fun listenAvailableDrivers(): DatabaseReference

    suspend fun listenToRequestedRide(rideId: String): DatabaseReference

    suspend fun listenToCompletedRide(rideId: String): DatabaseReference

    suspend fun updateUserData(uid: String, updatedUser: User)

    suspend fun postRideRequest(
        rideRequest: RideRequest,
    ): String?

    fun observeUser(uid: String): LiveData<LocalUser?>

    suspend fun getLocalUser(uid: String): LocalUser?

    suspend fun deleteLocalUser(uid: String)

    suspend fun saveLocalUser(localUser: LocalUser)

    suspend fun getCompletedRidesByUserId(uid: String): List<CompletedRide?>
}
