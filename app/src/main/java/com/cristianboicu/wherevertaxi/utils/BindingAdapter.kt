package com.cristianboicu.wherevertaxi.utils

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.data.model.driver.AvailableDriver
import com.cristianboicu.wherevertaxi.data.model.ride.CompletedRide
import com.cristianboicu.wherevertaxi.data.model.ride.OngoingRide
import com.cristianboicu.wherevertaxi.data.model.user.LocalUser
import com.cristianboicu.wherevertaxi.ui.home.RideState
import com.google.android.material.bottomsheet.BottomSheetBehavior

@SuppressLint("SetTextI18n")
@BindingAdapter("userName")
fun TextView.setUserName(user: LocalUser?) {
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
fun TextView.setUserPhone(user: LocalUser?) {
    user?.let {
        text = it.phone
    }
}

@BindingAdapter("userEmail")
fun TextView.setUserEmail(user: LocalUser?) {
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
fun ConstraintLayout.setSearchVisibility(state: RideState) {
    val behaviour = BottomSheetBehavior.from(parent as View)

    visibility = if (state == RideState.SELECT_DESTINATION) {
        View.VISIBLE
    } else {
        View.GONE
    }
    if (state != RideState.SELECT_DESTINATION) {
        behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
    }
}

@BindingAdapter("setRideAcceptedVisibility")
fun View.setRideAcceptedVisibility(state: RideState) {
    visibility = if (state == RideState.RIDE_ACCEPTED) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("setRidePendingVisibility")
fun View.setRidePendingVisibility(state: RideState) {
    visibility = if (state == RideState.RIDE_PENDING) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("setRideCompletedVisibility")
fun View.setRideCompletedVisibility(state: RideState) {
    visibility = if (state == RideState.RIDE_COMPLETED) {
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

@BindingAdapter("setEnabledDisabledStandard")
fun View.setEnabledDisabledStandard(availableDrivers: Map<String, AvailableDriver>) {
    var available = false
    for (item in availableDrivers) {
        if (item.value.vehicleClass == "Standard") {
            available = true
        }
    }
    rootView.isEnabled = available
}

@BindingAdapter("setEnabledDisabledComfort")
fun View.setEnabledDisabledComfort(availableDrivers: Map<String, AvailableDriver>?) {
    val comfort = findViewById<View>(R.id.comfort_car)
    val standard = findViewById<View>(R.id.standard_car)

    var availableComfort = false
    var availableStandard = false
    if (availableDrivers != null) {
        for (item in availableDrivers) {
            Log.d("Binding", "${item.value.vehicleClass}")

            if (item.value.vehicleClass == "Comfort") {
                availableComfort = true
            }
            if (item.value.vehicleClass == "Standard") {
                availableStandard = true
            }
        }
    }
    comfort.isEnabled = availableComfort
    standard.isEnabled = availableStandard
}

@BindingAdapter("setAcceptedRideData")
fun View.setAcceptedRideData(ongoingRide: OngoingRide?) {
    ongoingRide?.let {
        findViewById<TextView>(R.id.tv_driver_name).text = ongoingRide.driverName
        findViewById<TextView>(R.id.tv_driver_vehicle).text = ongoingRide.vehicle
        findViewById<TextView>(R.id.tv_driver_vehicle_class).text = ongoingRide.vehicleClass
        findViewById<TextView>(R.id.tv_driver_license_plate_number).text =
            ongoingRide.licensePlateNumber
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("setHistoryRideData")
fun View.setHistoryRideData(completedRide: CompletedRide?) {
    completedRide?.let {
        findViewById<TextView>(R.id.tv_ride_destination).text = completedRide.destinationPlain
        findViewById<TextView>(R.id.tv_ride_date_time).text =
            "${completedRide.date} ${completedRide.time}"
        findViewById<TextView>(R.id.tv_ride_price).text = "LEI " + completedRide.price.toString()
    }
}