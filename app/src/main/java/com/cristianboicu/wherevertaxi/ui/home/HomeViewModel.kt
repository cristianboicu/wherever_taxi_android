package com.cristianboicu.wherevertaxi.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.data.model.driver.AvailableDriver
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeoLocation
import com.cristianboicu.wherevertaxi.data.model.ride.OngoingRide
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.model.ride.toLatLng
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.utils.Event
import com.cristianboicu.wherevertaxi.utils.Util.getBitmapFromSvg
import com.cristianboicu.wherevertaxi.utils.Util.getCurrentDate
import com.cristianboicu.wherevertaxi.utils.Util.getCurrentTime
import com.cristianboicu.wherevertaxi.utils.Util.getRandom
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: IRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val userId = repository.getAuthenticatedUserId()

    private val _availableDriverMarkers = MutableLiveData<List<MarkerOptions>>()
    val availableDriverMarkers = _availableDriverMarkers

    private val _clearData = MutableLiveData<Event<Unit>>()
    val clearData = _clearData

    private val _requestCurrentLocation = MutableLiveData<Event<Unit>>()
    val requestCurrentLocation = _requestCurrentLocation

    private val _rideState = MutableLiveData<RideState>()
    val rideState = _rideState

    private val _placesPredictions = MutableLiveData<MutableList<AutocompletePrediction>>()
    val placesPredictions = _placesPredictions

    private val _availableDrivers = MutableLiveData<Map<String, AvailableDriver>?>()
    val availableDrivers = _availableDrivers

    private val _driverLocation = MutableLiveData<Event<LatLng>>()
    val driverLocation = _driverLocation

    private val _clientToDestinationPath = MutableLiveData<String?>()
    val clientToDestinationPath = _clientToDestinationPath

    var destination = MutableLiveData<LatLng>()
    var origin = MutableLiveData<LatLng>()

    private val userLocationAddress = MutableLiveData<String>()
    val userLocationAddressPlain = userLocationAddress.map {
        it.toString().split(',')[0]
    }

    val ongoingRideData = MutableLiveData<OngoingRide?>()
    var currentRideId: String? = null

    private lateinit var dbReferenceListener: DatabaseReference
    private lateinit var dbCompletedRidesListener: DatabaseReference
    private lateinit var dbAvailableDriversListener: DatabaseReference

    private val availableDriversListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            _availableDrivers.value = dataSnapshot.getValue<HashMap<String, AvailableDriver>>()

            _availableDrivers.value?.let {
                val markerList = mutableListOf<LatLng>()
                for (item in it) {
                    markerList.add(LatLng(item.value.currentLocation!!.lat!!,
                        item.value.currentLocation!!.lng!!))
                }
                _availableDriverMarkers.value = generateAvailableDriverMarkers(markerList)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    private val rideAcceptedListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val ongoingRideData = dataSnapshot.getValue<OngoingRide>()
                ongoingRideData?.let {
                    Log.d(TAG, "ride accepted")
                    this@HomeViewModel.ongoingRideData.value = ongoingRideData
                    onRideAccepted(currentRideId!!)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    private val rideOngoingListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            viewModelScope.launch {
                if (dataSnapshot.exists()) {
                    val ongoingRide = dataSnapshot.getValue<OngoingRide>()
                    ongoingRide?.driverLocation?.let {
                        _driverLocation.postValue(Event(it.toLatLng()))
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, databaseError.toException())
        }
    }

    private val rideCompletionListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            viewModelScope.launch {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    onRideCompleted(currentRideId!!)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }

    init {
        updateUserCurrentLocation()
        listenAvailableDrivers()
        _rideState.value = RideState.SELECT_DESTINATION
    }

    private fun updateUserCurrentLocation() {
        _requestCurrentLocation.value = Event(Unit)
    }

    private fun listenAvailableDrivers() {
        viewModelScope.launch {
            dbAvailableDriversListener = repository.listenAvailableDrivers()
            dbAvailableDriversListener.addValueEventListener(availableDriversListener)
        }
    }

    fun getPlacesPrediction(query: String) {
        viewModelScope.launch {
            placesPredictions.value = repository.getPredictions(query)
        }
    }

    fun onPlaceSelected(
        placeId: String,
        placePrimary: String,
        pickUpPoint: EditText,
        etWhereToPoint: EditText,
    ) {
        viewModelScope.launch {
            if (pickUpPoint.hasFocus()) {
                origin.value =
                    repository.getGeocoding(placeId)
                pickUpPoint.setText(placePrimary)
                Log.d(TAG, "origin selected: ${origin.value}")
            } else {
                destination.value =
                    repository.getGeocoding(placeId)
                etWhereToPoint.setText(placePrimary)
                Log.d(TAG, "destination selected: ${destination.value}")
            }

            if (origin.value != null && destination.value != null) {
                onRouteSelected(origin.value!!, destination.value!!)
            }
        }
    }

    fun onRouteSelected(originPoint: LatLng, destinationPoint: LatLng) {
        viewModelScope.launch {
            _rideState.value = RideState.SELECT_CAR
            try {
                _clientToDestinationPath.value =
                    repository.getDirection(originPoint,
                        destinationPoint)
            } catch (e: Exception) {
            }
        }
    }

    fun getReverseGeocoding(place: LatLng?) {
        viewModelScope.launch {
            place?.let {
                userLocationAddress.value = repository.getReverseGeocoding(place)
            }
        }
    }

    fun onVehicleClassSelected(vehicleClass: String) {
        viewModelScope.launch {
            val originPlainText = async { repository.getReverseGeocoding(origin.value!!) }
            val destinationPlainText = async { repository.getReverseGeocoding(destination.value!!) }

            val temp = createRideRequest(
                userId!!,
                origin.value!!,
                originPlainText.await(),
                destination.value!!,
                destinationPlainText.await(),
                vehicleClass
            )
            requestRide(temp)
        }
    }

    private fun createRideRequest(
        uid: String,
        origin: LatLng,
        originPlain: String,
        destination: LatLng,
        destinationPlain: String,
        vehicleClass: String,
    ): RideRequest {
        return RideRequest(
            uid = uid,
            originLocation = GeoLocation(origin.latitude, origin.longitude),
            destinationLocation = GeoLocation(destination.latitude,
                destination.longitude),
            originPlain = originPlain,
            destinationPlain = destinationPlain,
            vehicleClass = vehicleClass,
            date = getCurrentDate(),
            time = getCurrentTime(),
            price = getRandom(10, 30),
            payment = "Cash"
        )
    }

    private fun requestRide(rideRequest: RideRequest) {
        viewModelScope.launch {
            val key = repository.postRideRequest(rideRequest)
            key?.let {
                listenToRequestedRide(it)
            }
        }
    }

    private fun listenToRequestedRide(rideId: String) {
        viewModelScope.launch {
            currentRideId = rideId
            _rideState.value = RideState.RIDE_PENDING
            dbReferenceListener = repository.listenToRequestedRide(rideId)
            dbReferenceListener.addValueEventListener(rideAcceptedListener)
        }
    }

    fun onRideAccepted(rideId: String) {
        dbAvailableDriversListener.removeEventListener(availableDriversListener)
        dbReferenceListener.removeEventListener(rideAcceptedListener)
        dbReferenceListener.addValueEventListener(rideOngoingListener)
        _rideState.value = RideState.RIDE_ACCEPTED

        viewModelScope.launch {
            dbCompletedRidesListener = repository.listenToCompletedRide(rideId)
            dbCompletedRidesListener.addValueEventListener(rideCompletionListener)
        }
    }

    fun onRideCancel(rideId: String) {
        viewModelScope.launch {
            repository.cancelRide(rideId)
            dbReferenceListener.removeEventListener(rideAcceptedListener)
            resetData()
        }
    }

    fun onRideCompleted(rideId: String) {
        viewModelScope.launch {
            dbReferenceListener.removeEventListener(rideOngoingListener)
            dbCompletedRidesListener.removeEventListener(rideCompletionListener)
            _rideState.value = RideState.RIDE_COMPLETED
        }
    }

    private fun generateAvailableDriverMarkers(markerList: List<LatLng>): MutableList<MarkerOptions> {
        val res = mutableListOf<MarkerOptions>()
        val resizedBitmapIcon = getBitmapFromSvg(context, R.drawable.car_model)

        for (item in markerList) {
            res.add(MarkerOptions()
                .position(item)
                .icon(resizedBitmapIcon?.let {
                    BitmapDescriptorFactory.fromBitmap(it)
                })
            )
        }
        return res
    }

    fun resetData() {
        clearOriginAndDestination()
        currentRideId = null
        _clientToDestinationPath.value = null
        _clearData.value = Event(Unit)
        dbAvailableDriversListener.addValueEventListener(availableDriversListener)
        _rideState.value = RideState.SELECT_DESTINATION
    }

    private fun clearOriginAndDestination() {
        origin = MutableLiveData<LatLng>()
        updateUserCurrentLocation()
        destination = MutableLiveData<LatLng>()
    }
}

enum class RideState {
    SELECT_DESTINATION,
    SELECT_CAR,
    RIDE_PENDING,
    RIDE_ACCEPTED,
    RIDE_COMPLETED
}

enum class VehicleClass {
    STANDARD,
    COMFORT
}
