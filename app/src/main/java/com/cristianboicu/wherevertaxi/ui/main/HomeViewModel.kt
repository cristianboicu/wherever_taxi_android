package com.cristianboicu.wherevertaxi.ui.main

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.cristianboicu.wherevertaxi.data.remote.IRemoteDataSource
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.API_KEY
import com.cristianboicu.wherevertaxi.utils.Util.getResizedBitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.PolyUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    @ApplicationContext val context: Context,
) : ViewModel() {

    private val _drawMarkers = MutableLiveData<List<MarkerOptions>>()
    val drawMarkers = _drawMarkers

    private val _drawPolyLine = MutableLiveData<PolylineOptions>()
    val drawPolyLine = _drawPolyLine

    private val _requestCurrentLocation = MutableLiveData<Unit>()
    val requestCurrentLocation = _requestCurrentLocation

    private val _placesPredictions = MutableLiveData<List<AutocompletePrediction>>()
    val placesPredictions = _placesPredictions

    private val destination = MutableLiveData<LatLng>()


    init {
        showDrivers()
    }

    fun getPlacesPrediction(query: String) {
        viewModelScope.launch {
            placesPredictions.value = remoteDataSource.getPredictions(query)
        }
    }

    fun onPlaceSelected(placeId: String) {
        viewModelScope.launch {
            val res =
                remoteDataSource.getGeocoding(placeId, API_KEY)
            res?.let {
                destination.value = LatLng(it.results[0].geometry.location.lat,
                    it.results[0].geometry.location.lng)
                requestCurrentLocation()
            }
        }
    }

    private fun requestCurrentLocation() {
        _requestCurrentLocation.value = Unit
        Log.d("HomeViewModel", destination.value.toString())
    }

    fun routeSelected(origin: LatLng) {
        destination.value?.let {
            requestRoutePath(origin, it)
        }
    }

    private fun requestRoutePath(origin: LatLng, destination: LatLng) {
        val fromOrigin = origin.latitude.toString() + "," + origin.longitude.toString()
        val toDestination = destination.latitude.toString() + "," + destination.longitude.toString()

        viewModelScope.launch {
            val response = remoteDataSource.getDirection(fromOrigin, toDestination, API_KEY)
            drawPolyline(response)
            generateMarkers(listOf(destination), false)
        }
    }

    private fun drawPolyline(response: DirectionResponses?) {
        val shape: String? = response?.routes?.get(0)?.overviewPolyline?.points

        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.BLUE)
        _drawPolyLine.value = polyline
    }

    private fun generateMarkers(markerList: List<LatLng>, isDriver: Boolean) {
        val res = mutableListOf<MarkerOptions>()

        if (isDriver) {
            var bitmapIcon =
                BitmapFactory.decodeResource(context.resources, R.drawable.car_model)
            val resizedBitmapIcon = getResizedBitmap(bitmapIcon, 56, 56)

            for (item in markerList) {
                res.add(MarkerOptions()
                    .position(item)
                    .icon(resizedBitmapIcon?.let {
                        BitmapDescriptorFactory.fromBitmap(it)
                    })
                    .title("Driver")
                )
            }
        } else {
            for (item in markerList) {
                res.add(MarkerOptions()
                    .position(item)
                    .title("Destination")
                )
            }
        }

        drawMarkers.value = res
    }

    private fun showDrivers() {
        viewModelScope.launch {
            val res = remoteDataSource.getDrivers()
            res?.let {
                val markerList = mutableListOf<LatLng>()
                for (item in it) {
                    item?.let { driver ->
                        Log.d("HomeViewModel", "Driver location ${driver.location?.lat}")
                        markerList.add(LatLng(item.location!!.lat!!, item.location.lng!!))
                    }
                }
                generateMarkers(markerList, true)
            }
        }
    }
}
