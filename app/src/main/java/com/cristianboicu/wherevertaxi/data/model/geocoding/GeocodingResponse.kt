package com.cristianboicu.wherevertaxi.data.model.geocoding

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    @SerializedName("results")
    val results: ArrayList<GeocodingResult>,
)

data class GeocodingResult(
    @SerializedName("formatted_address")
    var formattedAddress: String,
    @SerializedName("geometry")
    var geometry: Geometry,
    @SerializedName("place_id")
    var placeId: String,
)

data class Geometry(
    @SerializedName("location")
    var location: GeoLocation,
)

data class GeoLocation(
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lng")
    var lng: Double,
)
