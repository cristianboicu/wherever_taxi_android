package com.cristianboicu.wherevertaxi.data.model.driver

import java.io.Serializable

data class Driver(
    public val fname: String? = null,
    public val sname: String? = null,
    public val phone: String? = null,
    public val email: String? = null,
    public val location: DriverLocation? = null,
) : Serializable {}

data class DriverLocation(
    public var lat: Double? = null,
    public var lng: Double? = null,
) : Serializable {}

data class AvailableDriver(
    val vehicleClass: String? = null,
    val currentLocation: DriverLocation? = null,
) {

}