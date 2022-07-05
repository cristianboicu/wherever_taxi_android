package com.cristianboicu.wherevertaxi.data.remote

import android.util.Log
import com.cristianboicu.wherevertaxi.data.model.driver.Driver
import com.cristianboicu.wherevertaxi.data.model.user.User
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.ride.CompletedRide
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.cristianboicu.wherevertaxi.data.model.user.PaymentMethod
import com.cristianboicu.wherevertaxi.data.remote.cloud.ICloudServiceApi
import com.cristianboicu.wherevertaxi.data.remote.firebase.IFirebaseApi
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val firebaseApi: IFirebaseApi,
    private val cloudServiceApi: ICloudServiceApi,
) : IRemoteDataSource {

    override fun getLoggedUserId(): FirebaseUser? {
        return firebaseApi.getCurrentUser()
    }

    override suspend fun getRemoteUser(uid: String): User? {
        val res = firebaseApi.getRemoteUser(uid)
        Log.d("RemoteDataSource", "Got value $res")
        return res
    }

    override suspend fun listenAvailableDrivers(): DatabaseReference {
        return firebaseApi.listenAvailableDrivers()
    }

    override suspend fun listenToRequestedRide(rideId: String): DatabaseReference {
        return firebaseApi.listenToRequestedRide(rideId)
    }

    override suspend fun listenToCompletedRide(uid:String, rideId: String): DatabaseReference {
        return firebaseApi.listenToCompletedRide(uid, rideId)
    }

    override suspend fun cancelRide(rideId: String) {
        return firebaseApi.cancelRide(rideId)
    }

    override fun logOutUser(): Boolean {
        return firebaseApi.logOutUser()
    }

    override suspend fun updateUserData(uid: String, updatedUser: User) {
        return firebaseApi.updateUserData(uid, updatedUser)
    }

    override suspend fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
    ): DirectionResponses? {
        return try {
            val res = cloudServiceApi.getDirection(origin, destination, apiKey)
            Log.d("RemoteDataSource", "Got direction $res")
            res.body()
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "Got direction error")
            null
        }
    }

    //to do return just latLng
    override suspend fun getGeocoding(place_id: String): GeocodingResponse? {
        return try {
            val res = cloudServiceApi.getGeocoding(place_id)
            Log.d("RemoteDataSource", "Got geocoding $res")
            res.body()
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "Got geocoding error")
            null
        }
    }

    override suspend fun getReverseGeocoding(placeLatLng: String): String? {
        return try {
            val res = cloudServiceApi.getReverseGeocoding(placeLatLng)
            res.body()?.let {
                return it.results[0].formattedAddress
            }
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "Got geocoding error")
            null
        }
    }

    override suspend fun getCompletedRidesByUserId(uid: String): List<CompletedRide?> {
        return firebaseApi.getCompletedRidesByUserId(uid)
    }

    override suspend fun getPredictions(query: String): MutableList<AutocompletePrediction> {
        return cloudServiceApi.getPredictions(query)
    }

    override suspend fun getAvailableDrivers(): List<Driver?>?{
        return firebaseApi.getAvailableDrivers()
    }

    override suspend fun postRideRequest(rideRequest: RideRequest): String? {
        return firebaseApi.postRideRequest(rideRequest)
    }

    override suspend fun saveRemoteUser(uid: String, user: User) {
        return firebaseApi.saveRemoteUser(uid, user)
    }

    override suspend fun savePaymentMethod(uid: String, remotePayment: PaymentMethod): String? {
        return firebaseApi.savePaymentMethod(uid, remotePayment)
    }
}