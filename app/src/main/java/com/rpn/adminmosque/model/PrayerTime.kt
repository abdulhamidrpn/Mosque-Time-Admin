package com.rpn.adminmosque.model

import com.google.gson.Gson

data class PrayerTime(
    val code: Int? = null,
    val `data`: List<Data>? = null,
    val status: String? = null
){
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

data class PrayerTimeDetail(
    val date: List<DateDetails>? = null,
    val timing: TimingDetails? = null,
    val status: String? = null
)
data class PrayerTimeDate(
    val date: List<DateDetails>? = null
)