package com.cristianboicu.wherevertaxi.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.data.model.driver.AvailableDriver
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeoLocation
import com.cristianboicu.wherevertaxi.data.model.ride.OngoingRideData
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.model.ride.toLatLng
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.databinding.ItemCarBinding
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
    @ApplicationContext val context: Context,
) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _availableDriverMarkers = MutableLiveData<List<MarkerOptions>>()
    val availableDriverMarkers = _availableDriverMarkers

    private val _clearMap = MutableLiveData<Event<Unit>>()
    val clearMap = _clearMap

    private val _requestCurrentLocation = MutableLiveData<Event<Unit>>()
    val requestCurrentLocation = _requestCurrentLocation

    private val _rideState = MutableLiveData<RideState>()
    val rideState = _rideState

    private val _placesPredictions = MutableLiveData<List<AutocompletePrediction>>()
    val placesPredictions = _placesPredictions

    private val _availableDrivers = MutableLiveData<HashMap<String, AvailableDriver>?>()
    val availableDrivers = _availableDrivers

    private val _driverLocation = MutableLiveData<Event<LatLng>>()
    val driverLocation = _driverLocation

    private val _clientToDestinationPath = MutableLiveData<String?>()
    val clientToDestinationPath = _clientToDestinationPath

    private val destination = MutableLiveData<LatLng>()
    val origin = MutableLiveData<LatLng>()

    private val userId = repository.getAuthenticatedUserId()

    val currentRideData = MutableLiveData<OngoingRideData?>()

    var currentRideId: String? = null

    private lateinit var dbReferenceListener: DatabaseReference
    private lateinit var dbCompletedRidesListener: DatabaseReference
    private lateinit var dbAvailableDriversListener: DatabaseReference

    private val availableDriversListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            _availableDrivers.value = dataSnapshot.getValue<HashMap<String, AvailableDriver>>()
            Log.w(TAG, "data changed ${_availableDrivers.value}")

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
                val ongoingRideData = dataSnapshot.getValue<OngoingRideData>()
                ongoingRideData?.let {
                    Log.d(TAG, "ride accepted")
                    currentRideData.value = ongoingRideData
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
                    Log.d(TAG, "ongoing ride listener")
                    val ongoingRide = dataSnapshot.getValue<OngoingRideData>()
                    ongoingRide?.driverLocation?.let {
                        _driverLocation.postValue(Event(it.toLatLng()))
                        Log.d(TAG, "driver lcoation ${_driverLocation.value}")
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
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
        Log.d(TAG, "init vm${_clientToDestinationPath.value}")
        _rideState.value = RideState.SELECT_DESTINATION
    }

    fun onCarSelected(vehicleClass: String) {
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

    private fun requestRide(
        rideRequest: RideRequest,
    ) {
        viewModelScope.launch {
            val key = repository.postRideRequest(rideRequest)
            key?.let {
                listenToRequestedRide(it)
            }
        }
    }

    private fun listenToRequestedRide(rideId: String) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "we are pending ride")
            currentRideId = rideId
            _rideState.value = RideState.RIDE_PENDING
            dbReferenceListener = repository.listenToRequestedRide(rideId)
            dbReferenceListener.addValueEventListener(rideAcceptedListener)
        }
    }

    fun onRideCancel(rideId: String) {
        viewModelScope.launch {
            repository.cancelRide(rideId)
            dbReferenceListener.removeEventListener(rideAcceptedListener)
            resetData()
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

    fun onRideCompleted(rideId: String) {
        viewModelScope.launch {
            dbReferenceListener.removeEventListener(rideOngoingListener)
            dbCompletedRidesListener.removeEventListener(rideCompletionListener)
            _rideState.value = RideState.RIDE_COMPLETED
        }
    }

    fun resetData() {
        Log.d(TAG, "reset data")
        currentRideId = null
        _clientToDestinationPath.value = null
        _clearMap.value = Event(Unit)
        dbAvailableDriversListener.addValueEventListener(availableDriversListener)
        _rideState.value = RideState.SELECT_DESTINATION
    }

    fun onDestinationSelected(destinationId: String) {
        viewModelScope.launch {
            val destinationLatLng =
                repository.getGeocoding(destinationId)

            destinationLatLng?.let {
                _rideState.value = RideState.SELECT_CAR
                destination.value = LatLng(it.results[0].geometry.location.lat,
                    it.results[0].geometry.location.lng)
                updateUserCurrentLocation()

                _clientToDestinationPath.value =
                    repository.getDirection(origin.value!!,
                        destination.value!!)

            }
        }
    }

    private fun listenAvailableDrivers() {
        viewModelScope.launch {
            dbAvailableDriversListener = repository.listenAvailableDrivers()
            dbAvailableDriversListener.addValueEventListener(availableDriversListener)
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

    fun getPlacesPrediction(query: String) {
        viewModelScope.launch {
            placesPredictions.value = repository.getPredictions(query)
            _rideState.value = RideState.SELECT_DESTINATION
        }
    }

    private fun updateUserCurrentLocation() {
        _requestCurrentLocation.value = Event(Unit)
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