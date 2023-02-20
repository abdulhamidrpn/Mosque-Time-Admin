package com.rpn.adminmosque.model

import com.google.gson.Gson
import java.io.Serializable

data class Timings(
    var Asr: String?=null,
    var Dhuhr: String?=null,
    var Fajr: String?=null,
    var Firstthird: String?=null,
    var Imsak: String?=null,
    var Isha: String?=null,
    var Lastthird: String?=null,
    var Maghrib: String?=null,
    var Midnight: String?=null,
    var Sunrise: String?=null,
    var Sunset: String?=null,
    var Jumma: String?=null
)

data class TimingDetails(
    var asr: String?=null,
    var isha: String?=null,
    var imsak: String?=null,
    var dhuhr: String?=null,
    var fajr: String?=null,
    var maghrib: String?=null
): Serializable {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

