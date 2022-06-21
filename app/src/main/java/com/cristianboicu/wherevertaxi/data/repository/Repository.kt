package com.cristianboicu.wherevertaxi.data.repository

import android.util.Log
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.remote.IRemoteDataSource
import javax.inject.Inject

class Repository @Inject constructor (private val remoteDataSource: IRemoteDataSource) : IRepository {

    override suspend fun getAuthenticatedUser(): User? {
        var authenticatedUser: User? = null
        val currentUser = remoteDataSource.getLoggedUserId()
        currentUser?.let {
            Log.d("Repository", "here")
            authenticatedUser = remoteDataSource.getLoggedUserData(it)
        }
        Log.d("Repository", "$authenticatedUser")
        Log.d("Repository", "$currentUser")
        return authenticatedUser
    }
}