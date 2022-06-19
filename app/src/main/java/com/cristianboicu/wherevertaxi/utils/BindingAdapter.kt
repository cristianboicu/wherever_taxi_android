package com.cristianboicu.wherevertaxi.utils

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.cristianboicu.wherevertaxi.data.User

@SuppressLint("SetTextI18n")
@BindingAdapter("userName")
fun TextView.setUserName(user: User?) {
    user?.let {
        text = if (!it.fname.isNullOrEmpty() || !it.sname.isNullOrEmpty()) {
            "${it.fname} ${it.sname}"
        } else {
            "Not provided"
        }
    }
}

@BindingAdapter("userPhone")
fun TextView.setUserPhone(user: User?) {
    user?.let {
        text = it.phone
    }
}

@BindingAdapter("userEmail")
fun TextView.setUserEmail(user: User?) {
    user?.let {
        text = if (!it.email.isNullOrEmpty()) {
            "${it.email}"
        } else {
            "Not provided"
        }
    }
}