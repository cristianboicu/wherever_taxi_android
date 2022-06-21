package com.cristianboicu.wherevertaxi.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.repository.IRepository

class ProfileViewModel(private val repository: IRepository) : ViewModel() {
    private val _loadedUser = MutableLiveData<User?>()
    val loadedUser = _loadedUser

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        _loadedUser.value = repository.getAuthenticatedUser()
    }

    private fun editCurrentUser() {

    }

    private fun logOutCurrentUser() {

    }
}