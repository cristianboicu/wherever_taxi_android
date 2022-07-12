package com.cristianboicu.wherevertaxi.data.model.driver

import com.cristianboicu.wherevertaxi.data.model.ride.GeogLocation
import java.io.Serializable

data class Driver(
    val fname: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val licensePlateNumber: String? = null,
    val vehicle: String? = null,
    val vehicleClass: String? = null,
) : Serializable

data class AvailableDriver(
    val vehicleClass: String? = null,
    val currentLocation: GeogLocation? = null,
)