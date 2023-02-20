package com.rpn.adminmosque.model

import com.google.gson.Gson
import java.io.Serializable

data class Date(
    var gregorian: Gregorian,
    var hijri: Hijri,
    var readable: String,
    var timestamp: String
)


data class DateDetails(
    var readable: String? = null,
    var hijri: HijriLite? = null,
    var timingDetails: TimingDetails? = null,
    var timestamp: String? = null,
    var sunrise: String? = null,
    var sunset: String? = null
):Serializable {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}