package com.cristianboicu.wherevertaxi.data.repository

import com.cristianboicu.wherevertaxi.data.model.User

interface IRepository {

    suspend fun getAuthenticatedUser(): User?

    fun getAuthenticatedUserId(): String?

    fun logOutUser(): Boolean

    suspend fun updateUserData(uid: String, updatedUser: User)
}