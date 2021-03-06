package com.cristianboicu.wherevertaxi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cristianboicu.wherevertaxi.data.model.payment.LocalPaymentMethod
import com.cristianboicu.wherevertaxi.data.model.ride.LocalRide
import com.cristianboicu.wherevertaxi.data.model.user.LocalUser

@Database(entities = [LocalUser::class, LocalRide::class, LocalPaymentMethod::class], version = 1)
abstract class TaxiDatabase : RoomDatabase() {
    abstract fun getTaxiDao(): TaxiDao
}