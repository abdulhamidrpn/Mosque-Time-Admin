package com.rpn.adminmosque.ui.calendar

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendar.view.ViewContainer
import com.rpn.adminmosque.R

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.exOneDayText)

    // With ViewBinding
    // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}

