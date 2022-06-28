package com.cristianboicu.wherevertaxi.data.model.ride

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class OngoingRide(
    var uid: String? = null,
    var ongoingRideData: OngoingRideData? = null,
) : Serializable

data class OngoingRideData(
    val did: String? = null,
    val driverLocation: GeogLocation? = null,
    val driverName: String? = null,
    val clientName: String? = null,
    val vehicle: String? = null,
    val licensePlateNumber: String? = null,
    val originLocation: GeogLocation? = null,
    val destinationLocation: GeogLocation? = null,
    val vehicleClass: String? = null,
    val date: String? = null,
    val time: String? = null,
    val price: Double? = null,
    val payment: String? = null,
) : Serializable

data class GeogLocation(
    var lat: Double? = null,
    var lng: Double? = null,
) : Serializable {}

fun GeogLocation.toLatLng(): LatLng {
    return LatLng(lat!!, lng!!)
}