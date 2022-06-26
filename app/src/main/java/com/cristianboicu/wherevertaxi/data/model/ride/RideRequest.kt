package com.cristianboicu.wherevertaxi.data.model.ride

import com.cristianboicu.wherevertaxi.data.model.geocoding.GeoLocation

data class RideRequest constructor(
    val uid: String,
    val rideRequestData: RideRequestData,
)

data class RideRequestData constructor(
    val originLocation: GeoLocation,
    val destinationLocation: GeoLocation,
    val vehicleClass: String,
    val date: String,
    val time: String,
    val price: Double,
    val payment: String,
)