package com.cristianboicu.wherevertaxi.ui.main

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.data.model.route.DirectionResponses
import com.cristianboicu.wherevertaxi.data.remote.ApiService
import com.cristianboicu.wherevertaxi.data.remote.places.IPlacesApi
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.PolyUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val placesApi: IPlacesApi,
) : ViewModel() {

    private val _drawMarkers = MutableLiveData<List<MarkerOptions>>()
    val drawMarkers = _drawMarkers

    private val _drawPolyLine = MutableLiveData<PolylineOptions>()
    val drawPolyLine = _drawPolyLine

    private val _requestCurrentLocation = MutableLiveData<Unit>()
    val requestCurrentLocation = _requestCurrentLocation

    private val _placesPredictions = MutableLiveData<List<AutocompletePrediction>>()
    val placesPredictions = _placesPredictions

    val destination = MutableLiveData<String>()

    fun getPlacesPrediction(query: String) {
        viewModelScope.launch {
            placesPredictions.value = placesApi.getPredictions(query)
        }
    }


    fun requestCurrentLocation() {
        _requestCurrentLocation.value = Unit
        Log.d("HomeViewModel", destination.value.toString())
    }

    private val destinations = mutableListOf(LatLng(45.059854, 25.013080),
        LatLng(46.659854, 25.613080),
        LatLng(45.659854, 24.813080))

    fun routeSelected(origin: LatLng) {
//        val origin: LatLng = LatLng(45.659854, 25.613080)
        val v = destination.value?.split(',')
        val destination: LatLng = destinations.removeAt(destinations.size - 1)
        requestRoutePath(origin, destination)
    }

    private fun requestRoutePath(origin: LatLng, destination: LatLng) {

        val fromOrigin = origin.latitude.toString() + "," + origin.longitude.toString()
        val toDestination = destination.latitude.toString() + "," + destination.longitude.toString()

        //TODO: REMOVE KEY

        apiService.getDirection(fromOrigin,
            toDestination,
            "API_KEY")
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(
                    call: Call<DirectionResponses>,
                    response: Response<DirectionResponses>,
                ) {
                    Log.d("HomeViewModel", "ebananans")
                    drawPolyline(response)
                    generateMarkers(origin, destination)
                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("HomeViewModel", t.localizedMessage)
                }
            })

    }

    private fun drawPolyline(response: Response<DirectionResponses>) {
        if (response.isSuccessful) {
            val shape: String? = response.body()?.routes?.get(0)?.overviewPolyline?.points

            val polyline = PolylineOptions()
                .addAll(PolyUtil.decode(shape))
                .width(8f)
                .color(Color.BLUE)
            _drawPolyLine.value = polyline
        }
    }

    private fun generateMarkers(origin: LatLng, destination: LatLng) {
//        val markerOrigin = MarkerOptions()
//            .position(origin)
//            .title("Origin")
        val markerDestination = MarkerOptions()
            .position(destination)
            .title("Destination")

        drawMarkers.value = listOf(markerDestination)
    }

}