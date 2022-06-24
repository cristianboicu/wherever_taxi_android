package com.cristianboicu.wherevertaxi.data.remote.cloud

import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.google.android.libraries.places.api.model.AutocompletePrediction
import retrofit2.Response

interface ICloudServiceApi {
    suspend fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
    ): Response<DirectionResponses>

    suspend fun getGeocoding(
        place_id: String,
        apiKey: String,
    ): Response<GeocodingResponse>

    suspend fun getPredictions(query: String): MutableList<AutocompletePrediction>
}
