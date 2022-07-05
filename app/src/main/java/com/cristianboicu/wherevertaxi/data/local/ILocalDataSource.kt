package com.cristianboicu.wherevertaxi.data.local

import androidx.lifecycle.LiveData
import com.cristianboicu.wherevertaxi.data.model.payment.LocalPaymentMethod
import com.cristianboicu.wherevertaxi.data.model.user.LocalUser

interface ILocalDataSource {

    fun observeUser(uid: String): LiveData<LocalUser?>

    suspend fun getLocalUser(uid: String): LocalUser?

    suspend fun deleteLocalUser(uid: String)

    suspend fun saveLocalUser(localUser: LocalUser)

    suspend fun deleteAllData()

    suspend fun savePaymentMethod(localPaymentMethod: LocalPaymentMethod)

    fun observeLocalPaymentMethods(uid: String): LiveData<List<LocalPaymentMethod>>
}