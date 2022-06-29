package com.cristianboicu.wherevertaxi.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.data.model.user.User
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.utils.Event
import com.cristianboicu.wherevertaxi.utils.Util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: IRepository,
) : ViewModel() {
    val uid = repository.getAuthenticatedUserId()!!

    private val _loadedUser = repository.observeUser(uid)
    val loadedUser = _loadedUser

    private val _launchEditUserFragment = MutableLiveData<Event<String?>>()
    val launchEditUserFragment = _launchEditUserFragment

    private val _navigateToProfile = MutableLiveData<Event<Unit>>()
    val navigateToProfile = _navigateToProfile

    private val _logOutUser = MutableLiveData<Event<Boolean>>()
    val logOutUser = _logOutUser

    private val _showToastError = MutableLiveData<Event<String>>()
    val showToastError = _showToastError

    fun editCurrentUser() {
        val uid = repository.getAuthenticatedUserId()
        _launchEditUserFragment.value = Event(uid)
    }

    fun logOutCurrentUser() {
        viewModelScope.launch {
            val res = repository.logOutUser()
            _logOutUser.value = Event(res)
        }
    }

    fun saveModifiedUser(fname: String?, sname: String?, email: String?) {
        val uid = repository.getAuthenticatedUserId()!!
        val updatedUser = User(fname, sname, _loadedUser.value?.phone, email)

        if (email.isNullOrEmpty() || isValidEmail(email)) {
            viewModelScope.launch {
                repository.updateUserData(uid, updatedUser)
                navigateBackToProfile()
            }
        } else {
            _showToastError.value = Event("Invalid email")
        }
    }

    fun navigateBackToProfile() {
        _navigateToProfile.value = Event(Unit)
    }
}