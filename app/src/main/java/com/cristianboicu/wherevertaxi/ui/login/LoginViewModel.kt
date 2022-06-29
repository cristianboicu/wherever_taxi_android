package com.cristianboicu.wherevertaxi.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.data.model.user.User
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val repository: IRepository) : ViewModel() {


    fun saveIfNewUser(uid: String, user: User) {
        Log.d("VerificationFragment", "save new user to db")

        viewModelScope.launch {
            if (!repository.saveIfNewUser(uid, user)) {
                Log.d("VerificationFragment", "it is not a new user so just refresh db")
                repository.refreshAuthenticatedUser()
            }
        }
    }

}