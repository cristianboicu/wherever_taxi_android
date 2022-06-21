package com.cristianboicu.wherevertaxi.data.repository

import com.cristianboicu.wherevertaxi.data.model.User

interface IRepository {

    fun getAuthenticatedUser(): User?
}