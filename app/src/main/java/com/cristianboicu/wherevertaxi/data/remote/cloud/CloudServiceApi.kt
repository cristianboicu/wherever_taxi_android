package com.cristianboicu.wherevertaxi.data.remote.cloud

import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.google.android.libraries.places.api.model.AutocompletePrediction
import retrofit2.Response
import javax.inject.Inject

class CloudServiceApi @Inject constructor(
    private val apiService: ApiService,
    private val placesApi: IPlacesApi,
) : ICloudServiceApi {

    override suspend fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
    ): Response<DirectionResponses> {
        return apiService.getDirection(origin, destination, apiKey)
    }

    override suspend fun getGeocoding(
        place_id: String,
        apiKey: String,
    ): Response<GeocodingResponse> {
        return apiService.getGeocoding(place_id, apiKey)
    }


    override suspend fun getPredictions(query: String): MutableList<AutocompletePrediction> {
        return placesApi.getPredictions(query)
    }

}