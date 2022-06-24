package com.cristianboicu.wherevertaxi.data.remote

import android.util.Log
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.cristianboicu.wherevertaxi.data.remote.cloud.ICloudServiceApi
import com.cristianboicu.wherevertaxi.data.remote.firebase.IFirebaseApi
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val firebaseApi: IFirebaseApi,
    private val cloudServiceApi: ICloudServiceApi,
) :
    IRemoteDataSource {

    override fun getLoggedUserId(): FirebaseUser? {
        return firebaseApi.getCurrentUser()
    }

    override suspend fun getLoggedUserData(currentUser: FirebaseUser): User? {
        val res = firebaseApi.getCurrentUserData(currentUser)
        Log.d("RemoteDataSource", "Got value $res")
        return res
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

    override suspend fun getGeocoding(place_id: String, apiKey: String): GeocodingResponse? {
        return try {
            val res = cloudServiceApi.getGeocoding(place_id, apiKey)
            Log.d("RemoteDataSource", "Got geocoding $res")
            res.body()
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "Got geocoding error")
            null
        }
    }

    override suspend fun getPredictions(query: String): MutableList<AutocompletePrediction> {
        return cloudServiceApi.getPredictions(query)
    }
}