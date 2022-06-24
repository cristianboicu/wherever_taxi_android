package com.cristianboicu.wherevertaxi.data.remote.cloud

import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.google.android.libraries.places.api.model.Place
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
    ): Response<DirectionResponses>

    @GET("maps/api/geocode/json")
    suspend fun getGeocoding(
        @Query("place_id") place_id: String,
        @Query("key") apiKey: String,
    ): Response<GeocodingResponse>
}