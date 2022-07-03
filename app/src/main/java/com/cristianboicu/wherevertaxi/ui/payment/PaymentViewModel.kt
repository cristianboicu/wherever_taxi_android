package com.cristianboicu.wherevertaxi.ui.payment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cristianboicu.wherevertaxi.data.model.LocalPaymentMethod
import com.cristianboicu.wherevertaxi.data.model.user.PaymentMethod
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {

    private val _navigateBackFromAddCard = MutableLiveData<Event<Unit>>()
    val navigateBackFromAddCard = _navigateBackFromAddCard

    private val _navigateBackFromPayment = MutableLiveData<Event<Unit>>()
    val navigateBackFromPayment = _navigateBackFromPayment

    private val _navigateToAddCard = MutableLiveData<Event<Unit>>()
    val navigateToAddCard = _navigateToAddCard

    val paymentMethods = repository.observeLocalPaymentMethods()

    fun navigateBackFromAddCard() {
        navigateBackFromAddCard.value = Event(Unit)
    }

    fun navigateToAddCard() {
        navigateToAddCard.value = Event(Unit)
    }

    fun navigateBackFromPayment() {
        _navigateBackFromPayment.value = Event(Unit)
    }

    fun addNewCard(cardNumber: String, cardExpiry: String, cardCode: Int) {
        viewModelScope.launch {
            val uid = repository.getAuthenticatedUserId()
            val paymentMethod = PaymentMethod( cardNumber, cardExpiry, cardCode)
            repository.savePaymentMethod(paymentMethod)
            _navigateBackFromAddCard.value = Event(Unit)
        }
    }
}