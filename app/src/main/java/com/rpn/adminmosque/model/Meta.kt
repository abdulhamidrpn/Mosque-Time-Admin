package com.rpn.adminmosque.model

data class Meta(
    val latitude: Double,
    val latitudeAdjustmentMethod: String,
    val longitude: Double,
    val method: Method,
    val midnightMode: String,
    val offset: Offset,
    val school: String,
    val timezone: String
)
data class MetaLite(
    val latitude: Double,
    val latitudeAdjustmentMethod: String,
    val longitude: Double,
    val school: String,
    val timezone: String
)