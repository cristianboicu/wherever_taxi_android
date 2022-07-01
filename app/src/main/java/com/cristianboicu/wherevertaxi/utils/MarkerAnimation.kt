package com.cristianboicu.wherevertaxi.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.util.Log
import android.util.Property
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class MarkerAnimation {
    var trips: ArrayList<LatLng> = arrayListOf()
    var marker: Marker? = null
    var latLngInterpolator: LatLngInterpolator = LatLngInterpolator.Spherical()

    //
//    fun animateLine(
//        marker: Marker,
//    ) {
//        this.marker = marker
//        animateMarker()
//    }
    companion object {

        fun animateMarkerToICS(
            marker: Marker?,
            finalPosition: LatLng?,
            latLngInterpolator: LatLngInterpolator = LatLngInterpolator.Spherical(),
        ) {
            val typeEvaluator: TypeEvaluator<LatLng> =
                TypeEvaluator { fraction, startValue, endValue ->
                    latLngInterpolator.interpolate(fraction,
                        startValue!!,
                        endValue!!)
                }
            val property = Property.of(
                Marker::class.java,
                LatLng::class.java, "position")
            val animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition)
            animator.duration = 1500
            animator.start()
        }
    }

    fun animateMarker(
        marker: Marker?,
    ) {
        Log.d("Home Animator", trips.toString())
        val typeEvaluator: TypeEvaluator<LatLng> =
            TypeEvaluator { fraction, startValue, endValue ->
                latLngInterpolator.interpolate(fraction,
                    startValue!!,
                    endValue!!)
            }
        val property: Property<Marker, LatLng> = Property.of(Marker::class.java,
            LatLng::class.java, "position")
        val animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, trips[0])

        //ObjectAnimator animator = ObjectAnimator.o(view, "alpha", 0.0f);
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationCancel(animation: Animator) {
                //  animDrawable.stop();
            }

            override fun onAnimationRepeat(animation: Animator) {
                //  animDrawable.stop();
            }

            override fun onAnimationStart(animation: Animator) {
                //  animDrawable.stop();
            }

            override fun onAnimationEnd(animation: Animator) {
                //  animDrawable.stop();
                if (trips.size > 1) {
                    trips.removeAt(0)
                    animateMarker(marker)
                }
            }
        })
        animator.duration = 1000
        animator.start()
    }
}