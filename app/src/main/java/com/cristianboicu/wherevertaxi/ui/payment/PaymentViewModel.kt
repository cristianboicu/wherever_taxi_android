package com.cristianboicu.wherevertaxi.ui.payment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {

    private val _navigateBackFromAddCard = MutableLiveData<Event<Unit>>()
    val navigateBackFromAddCard = _navigateBackFromAddCard

    private val _navigateBackFromPayment = MutableLiveData<Event<Unit>>()
    val navigateBackFromPayment = _navigateBackFromPayment

    private val _navigateToAddCard = MutableLiveData<Event<Unit>>()
    val navigateToAddCard = _navigateToAddCard

    fun navigateBackFromAddCard() {
        navigateBackFromAddCard.value = Event(Unit)
    }

    fun navigateToAddCard() {
        navigateToAddCard.value = Event(Unit)
    }

    fun navigateBackFromPayment() {
        _navigateBackFromPayment.value = Event(Unit)
    }

    fun addNewCard() {
    }
}