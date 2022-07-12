package com.cristianboicu.wherevertaxi

import android.app.Activity
import androidx.lifecycle.LiveData
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeocodingResponse
import com.cristianboicu.wherevertaxi.data.model.payment.LocalPaymentMethod
import com.cristianboicu.wherevertaxi.data.model.ride.CompletedRide
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.model.user.LocalUser
import com.cristianboicu.wherevertaxi.data.model.user.PaymentMethod
import com.cristianboicu.wherevertaxi.data.model.user.User
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference

class FakeRepository: IRepository {
    override fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        authenticationCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
    ) {
    }

    override suspend fun refreshAuthenticatedUser() {
        TODO("Not yet implemented")
    }

    override suspend fun saveUserData(uid: String, user: User): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAuthenticatedUserId(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun logOutUser(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun cancelRide(rideId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getDirection(origin: LatLng, destination: LatLng): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getPredictions(query: String): MutableList<AutocompletePrediction> {
        TODO("Not yet implemented")
    }

    override suspend fun getGeocoding(place_id: String): LatLng? {
        TODO("Not yet implemented")
    }

    override suspend fun getReverseGeocoding(placeLatLng: LatLng): String {
        TODO("Not yet implemented")
    }

    override suspend fun listenAvailableDrivers(): DatabaseReference {
        TODO("Not yet implemented")
    }

    override suspend fun listenToRequestedRide(rideId: String): DatabaseReference {
        TODO("Not yet implemented")
    }

    override suspend fun listenToCompletedRide(rideId: String): DatabaseReference {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserData(uid: String, updatedUser: User) {
        TODO("Not yet implemented")
    }

    override suspend fun postRideRequest(rideRequest: RideRequest): String? {
        TODO("Not yet implemented")
    }

    override fun observeUser(uid: String): LiveData<LocalUser?> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocalUser(uid: String): LocalUser? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLocalUser(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveLocalUser(localUser: LocalUser) {
        TODO("Not yet implemented")
    }

    override suspend fun getCompletedRidesByUserId(uid: String): List<CompletedRide?> {
        TODO("Not yet implemented")
    }

    override suspend fun savePaymentMethod(paymentMethod: PaymentMethod): Boolean {
        TODO("Not yet implemented")
    }

    override fun observeLocalPaymentMethods(): LiveData<List<LocalPaymentMethod>> {
        TODO("Not yet implemented")
    }
}