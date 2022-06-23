package com.cristianboicu.wherevertaxi.data.remote

import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.google.android.libraries.places.api.model.Place
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("maps/api/directions/json")
    fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
    ): Call<DirectionResponses>

    @GET("maps/api/geocode/json")
    fun getGeocoding(
        @Query("place_id") place_id: String,
        @Query("key") apiKey: String,
    ): Call<GeocodingResponse>
}