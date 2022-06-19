package com.cristianboicu.wherevertaxi.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cristianboicu.wherevertaxi.data.User
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.DATABASE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    private var database: DatabaseReference =
        Firebase.database(DATABASE_URL).reference

    private val _loadedUser = MutableLiveData<User?>()
    val loadedUser = _loadedUser

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if ( currentUser != null){
            database.child("users").child(currentUser.uid).get().addOnSuccessListener {
                _loadedUser.value = it.getValue(User::class.java)
                Log.i("firebase", "Got value ${_loadedUser.value}")
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }
    }

    private fun editCurrentUser(){

    }

    private fun logOutCurrentUser(){

    }
}