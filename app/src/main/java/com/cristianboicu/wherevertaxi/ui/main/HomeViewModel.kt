package com.cristianboicu.wherevertaxi.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.data.model.driver.AvailableDriver
import com.cristianboicu.wherevertaxi.data.model.geocoding.GeoLocation
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequest
import com.cristianboicu.wherevertaxi.data.model.ride.RideRequestData
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.databinding.ItemCarBinding
import com.cristianboicu.wherevertaxi.utils.Event
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.API_KEY
import com.cristianboicu.wherevertaxi.utils.Util.getBitmapFromSvg
import com.cristianboicu.wherevertaxi.utils.Util.getCurrentDate
import com.cristianboicu.wherevertaxi.utils.Util.getCurrentTime
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private val _driverToClientPath = MutableLiveData<String>()
    val driverToClientPath = _driverToClientPath

    private val _clientToDestinationPath = MutableLiveData<String>()
    val clientToDestinationPath = _clientToDestinationPath

    private val destination = MutableLiveData<LatLng>()
    val origin = MutableLiveData<LatLng>()

    private val userId = repository.getLoggedUserId()

    init {
        updateUserCurrentLocation()
        listenAvailableDrivers()
        _rideState.value = RideState.SELECT_DESTINATION
    }

    fun onCarSelected(standardView: ItemCarBinding, comfortView: ItemCarBinding) {
        val standardCar =
            standardView.layoutCarType.isEnabled && standardView.layoutCarType.isSelected
        val comfortCar =
            comfortView.layoutCarType.isEnabled && comfortView.layoutCarType.isSelected
        Log.d(TAG, "$standardCar $comfortCar")

        var vehicleClass = "Standard"
        if (comfortCar) {
            vehicleClass = "Comfort"
        }
        requestRide(origin.value, destination.value, userId, vehicleClass)
    }

    private fun requestRide(
        origin: LatLng?,
        destination: LatLng?,
        uid: String?,
        vehicleClass: String,
    ) {
        //add request to db
        val drivers = availableDrivers.value?.toList()
        val driverLocation = drivers?.get(1)?.second?.currentLocation

        if (driverLocation != null && origin != null) {
            viewModelScope.launch {
                _driverToClientPath.value =
                    repository.getDirection(LatLng(driverLocation.lat!!, driverLocation.lng!!),
                        origin)
//                _rideState.value = RideState.SELECT_CAR
                val rideRequestData =
                    RideRequestData(
                        originLocation = GeoLocation(origin.latitude, origin.longitude),
                        destinationLocation = GeoLocation(destination!!.latitude,
                            destination.longitude),
                        vehicleClass = vehicleClass,
                        date = getCurrentDate(),
                        time = getCurrentTime(),
                        price = 15.8,
                        payment = "Cash"
                    )
                repository.postRideRequest(RideRequest(uid!!, rideRequestData))
            }
        }
    }

    fun onDestinationSelected(destinationId: String) {
        viewModelScope.launch {
            val destinationLatLng =
                repository.getGeocoding(destinationId, API_KEY)

            destinationLatLng?.let {
                destination.value = LatLng(it.results[0].geometry.location.lat,
                    it.results[0].geometry.location.lng)
                updateUserCurrentLocation()

                _clientToDestinationPath.value =
                    repository.getDirection(origin.value!!,
                        destination.value!!)

                _rideState.value = RideState.SELECT_CAR
            }
        }
    }

    private fun listenAvailableDrivers() {
        val availableDriversListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _availableDrivers.value = dataSnapshot.getValue<HashMap<String, AvailableDriver>>()
                Log.w(TAG, "data changed ${_availableDrivers.value}")

                _availableDrivers.value?.let {
                    val markerList = mutableListOf<LatLng>()
                    for (item in it) {
                        markerList.add(LatLng(item.value.currentLocation!!.lat!!,
                            item.value.currentLocation!!.lng!!))
                    }
                    generateAvailableDriverMarkers(markerList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        viewModelScope.launch {
            val res = repository.listenAvailableDrivers()
            res.addValueEventListener(availableDriversListener)
        }
    }

    private fun generateAvailableDriverMarkers(markerList: List<LatLng>) {
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
        _availableDriverMarkers.value = res
    }

    fun getPlacesPrediction(query: String) {
        viewModelScope.launch {
            placesPredictions.value = repository.getPredictions(query)
            _rideState.value = RideState.SELECT_DESTINATION
        }
    }

    fun goBackToSelectDestination() {
        _rideState.value = RideState.SELECT_DESTINATION
    }

    private fun updateUserCurrentLocation() {
        _requestCurrentLocation.value = Event(Unit)
    }
}

enum class RideState {
    SELECT_DESTINATION,
    LOADING,
    SELECT_CAR,
    ONGOING,
    FINISH
}