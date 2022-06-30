package com.cristianboicu.wherevertaxi.data.remote.cloud

import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.cristianboicu.wherevertaxi.utils.ProjectConstants
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import retrofit2.Response
import javax.inject.Inject

class CloudServiceApi @Inject constructor(
    private val apiService: ApiService,
    private val placesApi: IPlacesApi,
) : ICloudServiceApi {

    //use project constants for api_key
    override suspend fun getDirection(
        origin: String,
        destination: String,
        apiKey: String,
    ): Response<DirectionResponses> {
        return apiService.getDirection(origin, destination, apiKey)
    }

    override suspend fun getGeocoding(
        placeId: String,
    ): Response<GeocodingResponse> {
        return apiService.getGeocoding(placeId, ProjectConstants.API_KEY)
    }

    override suspend fun getReverseGeocoding(
        placeLatLng: String,
    ): Response<GeocodingResponse> {
        return apiService.getReverseGeocoding(placeLatLng, ProjectConstants.API_KEY)
    }


    override suspend fun getPredictions(query: String): MutableList<AutocompletePrediction> {
        return placesApi.getPredictions(query)
    }

}