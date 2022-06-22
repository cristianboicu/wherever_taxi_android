package com.cristianboicu.wherevertaxi.data.model.route

import com.google.gson.annotations.SerializedName

data class OverviewPolyline(
    @SerializedName("points")
    var points: String?
)