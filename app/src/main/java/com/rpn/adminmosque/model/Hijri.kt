package com.rpn.adminmosque.model

data class Hijri(
    val date: String,
    val day: String,
    val designation: Designation,
    val format: String,
    val holidays: List<String>,
    val month: MonthX,
    val weekday: WeekdayX,
    val year: String
)
data class HijriLite(
    val date: String?=null,
    val month: MonthX?=null,
    val weekday: WeekdayX?=null,
    val year: String?=null
)