package com.cristianboicu.wherevertaxi.ui.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.data.model.user.User
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.utils.Event
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {

    private val _verificationCodeSent: MutableLiveData<Event<String>> = MutableLiveData()
    val verificationCodeSent = _verificationCodeSent

    private val _verificationCodeReceived: MutableLiveData<String> = MutableLiveData()
    val verificationCodeReceived = _verificationCodeReceived

    private val _toastMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val toastMessage = _toastMessage

    fun saveIfNewUser(uid: String, user: User) {
        Log.d("VerificationFragment", "save new user to db ${user.payment}")

        viewModelScope.launch {
            if (!repository.saveUserData(uid, user)) {
                Log.d("VerificationFragment", "it is not a new user so just refresh db")
                repository.refreshAuthenticatedUser()
            }
        }
    }

    fun sendVerificationCode(activity: Activity, phone: String) {
        repository.sendVerificationCode(activity, phone, authCallback)
    }

    private val authCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken,
            ) {
                super.onCodeSent(s, forceResendingToken)
                _verificationCodeSent.value = Event(s)
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                code?.let {
                    _verificationCodeReceived.value = it
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _toastMessage.value = Event(e.message.toString())
            }
        }
}