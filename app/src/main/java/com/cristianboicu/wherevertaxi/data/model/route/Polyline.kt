package com.cristianboicu.wherevertaxi.data.model.route

import com.google.gson.annotations.SerializedName

data class Polyline(
    @SerializedName("points")
    var points: String?
)