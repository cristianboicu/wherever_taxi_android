package com.cristianboicu.wherevertaxi.data.model.user

import com.cristianboicu.wherevertaxi.data.model.LocalPaymentMethod
import java.io.Serializable

data class User(
    val fname: String? = null,
    val sname: String? = null,
    val phone: String? = null,
    val email: String? = null,
//    val payment: List<PaymentMethod>? = null,
    val payment: Map<String, PaymentMethod>? = null,
) : Serializable

data class PaymentMethod(
    val cardNumber: String? = null,
    val cardExpirationDate: String? = null,
    val cardCode: Int? = null,
) : Serializable

fun Map<String, PaymentMethod>.toLocalPaymentList(uid: String): List<LocalPaymentMethod> {
    val res = mutableListOf<LocalPaymentMethod>()
    for (i in iterator()) {
        res.add(LocalPaymentMethod(i.key, uid,
            i.value.cardNumber!!,
            i.value.cardExpirationDate!!,
            i.value.cardCode!!))
    }
    return res
}
