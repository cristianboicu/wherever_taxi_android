package com.cristianboicu.wherevertaxi.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cristianboicu.wherevertaxi.data.model.LocalPaymentMethod
import com.cristianboicu.wherevertaxi.data.model.ride.LocalRide
import com.cristianboicu.wherevertaxi.data.model.user.LocalUser

@Dao
interface TaxiDao {
    @Query("SELECT * FROM user JOIN localRides ON user.uid = localRides.uid")
    fun loadLocalUserRides(): Map<LocalUser, List<LocalRide>>

    @Query("select * from user WHERE user.uid = :uid")
    fun observeUser(uid: String): LiveData<LocalUser?>

    @Query("SELECT * FROM user WHERE user.uid = :uid")
    fun getLocalUser(uid: String): LocalUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocalUser(user: LocalUser)

    @Query("delete from user WHERE user.uid = :uid")
    suspend fun deleteLocalUser(uid: String)

    @Query("delete from user")
    suspend fun deleteAllFromUsers()

    @Query("delete from localRides")
    suspend fun deleteAllFromRides()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePaymentMethod(localPaymentMethod: LocalPaymentMethod)

    @Query("delete from payment")
    suspend fun deleteAllPayment()

    @Query("delete from payment WHERE payment.id = :pid")
    suspend fun deletePaymentById(pid: String)

    @Query("select * from payment WHERE payment.uid = :uid")
    fun observeLocalPaymentMethods(uid: String): LiveData<List<LocalPaymentMethod>>
}
