package com.cristianboicu.wherevertaxi.data.repository

import android.app.Activity
import androidx.lifecycle.LiveData
import com.cristianboicu.wherevertaxi.data.local.ILocalDataSource
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.payment.LocalPaymentMethod
import com.cristianboicu.wherevertaxi.data.model.ride.CompletedRide
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.model.user.LocalUser
import com.cristianboicu.wherevertaxi.data.model.user.PaymentMethod
import com.cristianboicu.wherevertaxi.data.model.user.User
import com.cristianboicu.wherevertaxi.data.model.user.toLocalPaymentList
import com.cristianboicu.wherevertaxi.data.remote.IRemoteDataSource
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.API_KEY
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class Repository @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource,
) : IRepository {


    override fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        authenticationCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
    ) {
        remoteDataSource.sendVerificationCode(activity, phoneNumber, authenticationCallback)
    }


    override suspend fun saveNewUserData(uid: String, user: User): Boolean {
        try {
            val remoteUser = remoteDataSource.getRemoteUser(uid)
            if (remoteUser == null) {
                remoteDataSource.saveRemoteUser(uid, user)
                localDataSource.saveLocalUser(LocalUser(uid,
                    user.fname,
                    user.sname,
                    user.phone,
                    user.email))
                return true
            }
            remoteUser.payment?.let {
                saveLocalPaymentMethods(it.toLocalPaymentList(uid))
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    private suspend fun saveLocalPaymentMethods(paymentMethods: List<LocalPaymentMethod>) {
        for (p in paymentMethods) {
            localDataSource.savePaymentMethod(p)
        }
    }

    override suspend fun refreshAuthenticatedUser() {
        var authenticatedUser: User?
        try {
            val remoteFirebaseUser = remoteDataSource.getLoggedUserId()

            remoteFirebaseUser?.let { remoteFirebaseUser ->
                authenticatedUser = remoteDataSource.getRemoteUser(remoteFirebaseUser.uid)

                authenticatedUser?.let {
                    saveLocalUser(LocalUser(remoteFirebaseUser.uid,
                        it.fname,
                        it.sname,
                        it.phone,
                        it.email))
                }
            }
        } catch (e: Exception) {

        }
    }

    override fun getAuthenticatedUserId(): String? {
        return remoteDataSource.getLoggedUserId()?.uid
    }

    override suspend fun updateUserData(uid: String, updatedUser: User) {
        try {
            remoteDataSource.updateUserData(uid, updatedUser)
            localDataSource.saveLocalUser(LocalUser(uid,
                updatedUser.fname,
                updatedUser.sname,
                updatedUser.phone,
                updatedUser.email))
        } catch (e: Exception) {

        }
    }

    override fun observeUser(uid: String): LiveData<LocalUser?> {
        return localDataSource.observeUser(uid)
    }

    override suspend fun logOutUser(): Boolean {
        getAuthenticatedUserId()?.let {
            localDataSource.deleteAllData()
        }
        return remoteDataSource.logOutUser()
    }

    override suspend fun getLocalUser(uid: String): LocalUser? {
        return localDataSource.getLocalUser(uid)
    }

    override suspend fun deleteLocalUser(uid: String) {
        return localDataSource.deleteLocalUser(uid)
    }

    override suspend fun saveLocalUser(localUser: LocalUser) {
        return localDataSource.saveLocalUser(localUser)
    }

    override suspend fun getCompletedRidesByUserId(uid: String): List<CompletedRide?> {
        return remoteDataSource.getCompletedRidesByUserId(uid)
    }

    override suspend fun savePaymentMethod(paymentMethod: PaymentMethod): Boolean {
        return try {
            val uid = getAuthenticatedUserId()
            val key = remoteDataSource.savePaymentMethod(uid!!, paymentMethod)
            val localPaymentMethod = LocalPaymentMethod(key!!,
                uid,
                paymentMethod.cardNumber!!,
                paymentMethod.cardExpirationDate!!,
                paymentMethod.cardCode!!)
            localDataSource.savePaymentMethod(localPaymentMethod)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun observeLocalPaymentMethods(): LiveData<List<LocalPaymentMethod>> {
        val uid = getAuthenticatedUserId()
        return localDataSource.observeLocalPaymentMethods(uid!!)
    }

    override suspend fun postRideRequest(rideRequest: RideRequest): String? {
        return remoteDataSource.postRideRequest(rideRequest)
    }

    override suspend fun cancelRide(rideId: String) {
        return remoteDataSource.cancelRide(rideId)
    }

    override suspend fun getDirection(origin: LatLng, destination: LatLng): String? {
        return try {
            val fromOrigin = origin.latitude.toString() + "," + origin.longitude.toString()
            val toDestination =
                destination.latitude.toString() + "," + destination.longitude.toString()

            val res = remoteDataSource.getDirection(fromOrigin, toDestination, API_KEY)
            val shape: String? = res?.routes?.get(0)?.overviewPolyline?.points
            shape

        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPredictions(query: String): MutableList<AutocompletePrediction> {
        return remoteDataSource.getPredictions(query)
    }

    override suspend fun getGeocoding(place_id: String): GeocodingResponse? {
        return remoteDataSource.getGeocoding(place_id)
    }

    override suspend fun getReverseGeocoding(placeLatLng: LatLng): String {
        val latLng = "${placeLatLng.latitude},${placeLatLng.longitude}"
        var res = remoteDataSource.getReverseGeocoding(latLng)
        if (res == null) {
            res = "Address not found"
        }
        return res
    }

    override suspend fun listenAvailableDrivers(): DatabaseReference {
        return remoteDataSource.listenAvailableDrivers()
    }

    override suspend fun listenToRequestedRide(rideId: String): DatabaseReference {
        return remoteDataSource.listenToRequestedRide(rideId)
    }

    override suspend fun listenToCompletedRide(rideId: String): DatabaseReference {
        val tempUid = getAuthenticatedUserId()!!
        return remoteDataSource.listenToCompletedRide(tempUid, rideId)
    }
}