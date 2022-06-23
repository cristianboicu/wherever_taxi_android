package com.cristianboicu.wherevertaxi.data.remote.places

import com.google.android.libraries.places.api.model.AutocompletePrediction

interface IPlacesApi {
    suspend fun getPredictions(query: String): MutableList<AutocompletePrediction>
}