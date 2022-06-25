package com.cristianboicu.wherevertaxi.utils

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.cristianboicu.wherevertaxi.data.model.User
import com.cristianboicu.wherevertaxi.ui.main.RideState

@SuppressLint("SetTextI18n")
@BindingAdapter("userName")
fun TextView.setUserName(user: User?) {
    user?.let {
        var temp = ""
        if (!it.fname.isNullOrEmpty()) {
            temp += "${it.fname}"
        }
        if (!it.sname.isNullOrEmpty()) {
            temp += " ${it.sname}"
        }
        if (it.sname.isNullOrEmpty() && it.fname.isNullOrEmpty()) {
            temp = "Not provided"
        }
        text = temp
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

@BindingAdapter("setCarsVisibility")
fun View.setCarsVisibility(state: RideState) {
    visibility = if (state == RideState.SELECT_CAR) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("setSearchVisibility")
fun View.setSearchVisibility(state: RideState) {
    visibility = if (state == RideState.SEARCH) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
@BindingAdapter("setNavigationDrawerVisibility")
fun View.setNavigationDrawerVisibility(state: RideState) {
    visibility = if (state != RideState.SELECT_CAR) {
        View.VISIBLE
    } else {
        View.GONE
    }
}