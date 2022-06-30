package com.cristianboicu.wherevertaxi.ui.rideHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.data.model.ride.CompletedRide
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideHistoryViewModel @Inject constructor(val repository: IRepository) : ViewModel() {

    private val _completedRides = MutableLiveData<List<CompletedRide?>>()
    val completedRides = _completedRides

    private val _navigateBack = MutableLiveData<Event<Unit>>()
    val navigateBack = _navigateBack

    init {
        val uid = repository.getAuthenticatedUserId()
        uid?.let {
            viewModelScope.launch {
                _completedRides.value = repository.getCompletedRidesByUserId(uid)
            }
        }
    }

    fun navigateBackToHome() {
        _navigateBack.value = Event(Unit)
    }
}