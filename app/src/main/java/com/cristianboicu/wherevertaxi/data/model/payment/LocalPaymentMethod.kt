package com.cristianboicu.wherevertaxi.data.model.payment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment")
data class LocalPaymentMethod(
    @PrimaryKey
    val id: String,
    val uid: String? = null,
    val cardNumber: String,
    val cardExpirationDate: String,
    val cardCode: Int,
)
