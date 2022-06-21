package com.cristianboicu.wherevertaxi.data.repository

import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.remote.IRemoteDataSource

class Repository(private val remoteDataSource: IRemoteDataSource) : IRepository {

    override fun getAuthenticatedUser(): User? {
        var authenticatedUser: User? = null
        val currentUser = remoteDataSource.getLoggedUserId()
        currentUser?.let {
            authenticatedUser = remoteDataSource.getLoggedUserData(it)
        }
        return authenticatedUser
    }
}