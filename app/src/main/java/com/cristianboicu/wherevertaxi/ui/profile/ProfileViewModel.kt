package com.cristianboicu.wherevertaxi.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: IRepository,
) : ViewModel() {

    private val _loadedUser = MutableLiveData<User?>()
    val loadedUser = _loadedUser

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            _loadedUser.value = repository.getAuthenticatedUser()
        }
    }

    private fun editCurrentUser() {

    }

    private fun logOutCurrentUser() {

    }
}