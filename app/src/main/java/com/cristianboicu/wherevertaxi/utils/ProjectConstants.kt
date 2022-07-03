package com.cristianboicu.wherevertaxi.utils

import com.cristianboicu.wherevertaxi.BuildConfig

object ProjectConstants {
    const val DATABASE_URL = "https://wherever-taxi-default-rtdb.europe-west1.firebasedatabase.app/"
    const val USERS_PATH = "users"
    const val COMPLETED_RIDES = "completedRides"
    const val DRIVERS_PATH = "drivers"
    const val AVAILABLE_DRIVERS_PATH = "availableDrivers"
    const val RIDE_REQUEST_PATH = "rideRequests"
    const val PAYMENT_PATH = "payment"
    const val COMPLETED_RIDES_PATH = "completedRides"
    const val ONGOING_RIDES_PATH = "ongoingRides"
    const val MAPS_URL = "https://maps.googleapis.com/"
    const val API_KEY = BuildConfig.MAPS_API_KEY
}