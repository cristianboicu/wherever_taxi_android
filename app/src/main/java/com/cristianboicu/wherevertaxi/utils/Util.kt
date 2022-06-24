package com.cristianboicu.wherevertaxi.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.SystemClock
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.util.regex.Pattern

object Util {
    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    private fun animateMarker(
        myMap: GoogleMap, marker: Marker, directionPoint: List<LatLng>,
        hideMarker: Boolean,
    ) {
        val handler = Handler()
        val start: Long = SystemClock.uptimeMillis()
        val proj = myMap.projection
        val duration: Long = 30000
        val interpolator: Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            var i = 0
            override fun run() {
                val elapsed: Long = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(elapsed.toFloat()
                        / duration)
                if (i < directionPoint.size) marker.position = directionPoint[i]
                i++
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 150)
                } else {
                    marker.isVisible = !hideMarker
                }
            }
        })
    }

    fun isValidEmail(email: CharSequence): Boolean {
        var isValid = true
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        if (!matcher.matches()) {
            isValid = false
        }
        return isValid
    }
}

