package com.cristianboicu.wherevertaxi.data.local

import androidx.lifecycle.LiveData
import com.cristianboicu.wherevertaxi.data.model.LocalPaymentMethod
import com.cristianboicu.wherevertaxi.data.model.user.LocalUser
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val taxiDao: TaxiDao) : ILocalDataSource {
    override fun observeUser(uid: String): LiveData<LocalUser?> {
        return taxiDao.observeUser(uid)
    }

    override suspend fun getLocalUser(uid: String): LocalUser? {
        return taxiDao.getLocalUser(uid)
    }

    override suspend fun deleteLocalUser(uid: String) {
        return taxiDao.deleteLocalUser(uid)
    }

    override suspend fun saveLocalUser(localUser: LocalUser) {
        return taxiDao.saveLocalUser(localUser)
    }

    override suspend fun deleteAllData() {
        taxiDao.deleteAllFromRides()
        taxiDao.deleteAllFromUsers()
        taxiDao.deleteAllPayment()
    }

    override suspend fun savePaymentMethod(localPaymentMethod: LocalPaymentMethod) {
        return taxiDao.savePaymentMethod(localPaymentMethod)
    }

    override fun observeLocalPaymentMethods(uid: String): LiveData<List<LocalPaymentMethod>> {
        return taxiDao.observeLocalPaymentMethods(uid)
    }
}