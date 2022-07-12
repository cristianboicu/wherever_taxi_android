package com.cristianboicu.wherevertaxi.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.cristianboicu.wherevertaxi.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.roundToInt

object Util {
    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
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

    fun getBitmapFromSvg(context: Context?, id: Int): Bitmap? {
        val bitmapIcon =
            BitmapFactory.decodeResource(context?.resources, id)
        return getResizedBitmap(bitmapIcon, 56, 56)
    }

    fun getRandom(min: Int, max: Int): Double {
        require(min < max) { "Invalid range [$min, $max]" }
        val random = min + Math.random() * (max - min)
        return (random * 100.0).roundToInt() / 100.0
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

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }

    fun getCurrentTime(): String {
        val c = Calendar.getInstance()

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return "$hour:$minute"
    }

    fun saveSelectedPayment(activity: Activity, id: Int) {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt("selectedPayment", id)
            apply()
        }
    }

    fun getSelectedPayment(activity: Activity): Int {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getInt("selectedPayment", -1)
    }

}

