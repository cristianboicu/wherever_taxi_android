package com.cristianboicu.wherevertaxi.ui.main

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.cristianboicu.wherevertaxi.data.remote.IRemoteDataSource
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.API_KEY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.PolyUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
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
            generateMarkers(origin, destination)
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

    private fun generateMarkers(origin: LatLng, destination: LatLng) {
        val markerDestination = MarkerOptions()
            .position(destination)
            .title("Destination")

        drawMarkers.value = listOf(markerDestination)
    }

}
