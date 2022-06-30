package com.cristianboicu.wherevertaxi.ui.rideHistory

import androidx.lifecycle.ViewModel
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RideHistoryViewModel @Inject constructor(val repository: IRepository) : ViewModel() {


}