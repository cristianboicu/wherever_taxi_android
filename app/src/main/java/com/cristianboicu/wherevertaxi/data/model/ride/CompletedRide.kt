package com.cristianboicu.wherevertaxi.data.model.ride

data class CompletedRide(
    var rideId: String? = null,
    var did: String? = null,
    var originPlain: String? = null,
    var destinationPlain: String? = null,
    var driverName: String? = null,
    var vehicle: String? = null,
    var payment: String? = null,
    var price: Double? = null,
    var date: String? = null,
    var time: String? = null,
)