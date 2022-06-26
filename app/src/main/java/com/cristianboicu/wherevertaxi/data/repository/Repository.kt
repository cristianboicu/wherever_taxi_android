package com.cristianboicu.wherevertaxi.data.repository

import android.util.Log
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.remote.IRemoteDataSource
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.API_KEY
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class Repository @Inject constructor(private val remoteDataSource: IRemoteDataSource) :
    IRepository {

    override suspend fun getAuthenticatedUser(): User? {
        var authenticatedUser: User? = null
        val currentUser = remoteDataSource.getLoggedUserId()
        currentUser?.let {
            Log.d("Repository", "here")
            authenticatedUser = remoteDataSource.getLoggedUserData(it)
        }
        Log.d("Repository", "$authenticatedUser")
        Log.d("Repository", "$currentUser")
        return authenticatedUser
    }

    override fun getAuthenticatedUserId(): String? {
        return remoteDataSource.getLoggedUserId()?.uid
    }

    override suspend fun updateUserData(uid: String, updatedUser: User) {
        return remoteDataSource.updateUserData(uid, updatedUser)
    }

    override suspend fun postRideRequest(rideRequest: RideRequest) {
        return remoteDataSource.postRideRequest(rideRequest)
    }

    override fun logOutUser(): Boolean {
        return remoteDataSource.logOutUser()
    }

    override suspend fun getDirection(origin: LatLng, destination: LatLng): String? {
        return try {
            val fromOrigin = origin.latitude.toString() + "," + origin.longitude.toString()
            val toDestination =
                destination.latitude.toString() + "," + destination.longitude.toString()

            val res = remoteDataSource.getDirection(fromOrigin, toDestination, API_KEY)
            val shape: String? = res?.routes?.get(0)?.overviewPolyline?.points
            shape

        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPredictions(query: String): MutableList<AutocompletePrediction> {
        return remoteDataSource.getPredictions(query)
    }

    override fun getLoggedUserId(): String? {
        return remoteDataSource.getLoggedUserId()?.uid
    }

    override suspend fun getGeocoding(place_id: String, apiKey: String): GeocodingResponse? {
        return remoteDataSource.getGeocoding(place_id, apiKey)
    }

    override suspend fun listenAvailableDrivers(): DatabaseReference {
        return remoteDataSource.listenAvailableDrivers()
    }
}