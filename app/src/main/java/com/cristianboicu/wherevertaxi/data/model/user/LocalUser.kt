package com.cristianboicu.wherevertaxi.data.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class LocalUser(
    @PrimaryKey
    val uid: String,
    val fname: String?,
    val sname: String?,
    val phone: String?,
    val email: String?
)