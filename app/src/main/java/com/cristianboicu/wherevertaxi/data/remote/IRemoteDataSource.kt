package com.cristianboicu.wherevertaxi.data.remote

import com.cristianboicu.wherevertaxi.data.model.User
import com.google.firebase.auth.FirebaseUser

interface IRemoteDataSource {

    fun getLoggedUserId(): FirebaseUser?

    fun getLoggedUserData(currentUser: FirebaseUser): User?
}