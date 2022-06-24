package com.cristianboicu.wherevertaxi.data.remote.cloud

import com.google.android.libraries.places.api.model.AutocompletePrediction

interface IPlacesApi {
    suspend fun getPredictions(query: String): MutableList<AutocompletePrediction>
}