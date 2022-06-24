package com.cristianboicu.wherevertaxi.data.remote.cloud

import android.util.Log
import com.cristianboicu.wherevertaxi.data.remote.cloud.IPlacesApi
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlacesApi @Inject constructor(private val placesClient: PlacesClient) : IPlacesApi {
    private val TAG = "PlacesApi"

    override suspend fun getPredictions(query: String): MutableList<AutocompletePrediction> {
        val token = AutocompleteSessionToken.newInstance()

        val bounds = RectangularBounds.newInstance(
            LatLng(43.830194, 30.072558),
            LatLng(48.127130, 20.331747)
        )

        val request =
            FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setCountry("ro")
                .setSessionToken(token)
                .setQuery(query)
                .build()

        val predictionsList = mutableListOf<AutocompletePrediction>()
        try {
            val result = placesClient.findAutocompletePredictions(request).await()
            for (prediction in result.autocompletePredictions) {
                predictionsList.add(prediction)
            }
            Log.d(TAG, "Success getting predictions")

        } catch (e: Exception) {
            Log.d(TAG, "Error getting predictions")
        }

        return predictionsList
    }
}