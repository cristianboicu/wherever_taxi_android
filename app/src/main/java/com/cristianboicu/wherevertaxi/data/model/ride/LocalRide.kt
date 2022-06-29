package com.cristianboicu.wherevertaxi.data.model.ride

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "localRides")
data class LocalRide(
    @PrimaryKey
    val rideId: String,
    val uid: String,
    val origin: String?,
    val destination: String?,
    val driverName: String?,
    val vehicle: String?,
    val payment: String?,
    val price: Double?,
    val date: String?,
    var time: String?
)
