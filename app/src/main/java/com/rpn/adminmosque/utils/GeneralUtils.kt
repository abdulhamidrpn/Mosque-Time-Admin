package com.rpn.adminmosque.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.util.Log
import android.view.LayoutInflater
import com.rpn.adminmosque.databinding.DialogLoadingBinding
import java.text.SimpleDateFormat
import java.util.*

object GeneralUtils {

    //    val sdfDate = SimpleDateFormat("yyyy-MMMM-dd")
    val sdfDate = SimpleDateFormat("dd MMM yyyy")
    val sdfDateMonth = SimpleDateFormat("dd MM")
    val sdfTimeAm = SimpleDateFormat("hh:mm aa")
    val sdfTime24 = SimpleDateFormat("HH:mm")
    val sdfTimeSecondAm = SimpleDateFormat("hh:mm:ss aa")
    val sdfTimeSecond1Am = SimpleDateFormat("HH:mm:ss aa")
    var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa")

    fun convertTimeToAm(time: String): String {
        val hour24Format = sdfTime24.parse(time)
        return sdfTimeAm.format(hour24Format)
    }

    fun convertTimeTo24(time: String): String {
        val hour12Format = sdfTimeAm.parse(time)
        return sdfTime24.format(hour12Format)
    }

    private fun setDateFormat() {
        val c = Calendar.getInstance()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.time)

        val sdfDate = SimpleDateFormat("yyyy-MMMM-dd").format(c.time)
        //(yyyy-MMM-dd -> 2022-Jan-14), (yyyy-MM-dd -> 2022-01-14), (yyyy-MMMM-dd -> 2022-January-14)

        val sdfTime24 = SimpleDateFormat("HH:mm:ss aa").format(c.time)

        val sdfTimeAm = SimpleDateFormat("hh:mm aa")

        val defaultStartTime = c.time
        val ce = Calendar.getInstance()
        ce.add(Calendar.HOUR, 1)
        val d = ce.time
        val defaultEndTime: String = sdfTimeAm.format(d)

        Log.d(
            "Date",
            "\n sdf : $sdf" +
                    "\n sdfDate : $sdfDate" +
                    "\n sdfTime24 : $sdfTime24" +
                    "\n sdfTimeAm : $sdfTimeAm"
        )

    }

    /* fun setTimeFormat(routine: Routine?): String {
         var timePeriodText = ""
         val sdfTimeAm = SimpleDateFormat("hh:mm aa")

         if (routine != null && routine.startTime != null && routine.endTime != null) {
             timePeriodText =
                 sdfTimeAm.format(routine.startTime!!) + " - " + sdfTimeAm.format(routine.endTime!!)
         } else if (routine != null && routine.startTime != null && routine.endTime == null) {
             timePeriodText = sdfTimeAm.format(routine.startTime!!)
         }

         return timePeriodText

     }*/

    fun addZeros(number: Int?): String {
        if (number!! < 10) return "0${number}"
        if (number < 100) return "${number}"
        return number.toString()
    }

    fun setTimer(
        context: Context,
        defaultHour: Int? = null,
        defaultMinute: Int? = null,
        is24HourView: Boolean = false,
        onComplete: (selectedTime: Date?) -> Unit,
    ) {

        val picker: TimePickerDialog
        val cldr: Calendar = Calendar.getInstance()
        cldr.time.time = System.currentTimeMillis()
        if (defaultHour != null)
            cldr.set(Calendar.HOUR_OF_DAY, defaultHour)
        if (defaultMinute != null)
            cldr.set(Calendar.MINUTE, defaultMinute)

        val hour: Int = cldr.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = cldr.get(Calendar.MINUTE)
        val selectedTime = Calendar.getInstance()
        // time picker dialog

        picker = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { tp, sHour, sMinute ->
                selectedTime.set(Calendar.HOUR_OF_DAY, sHour)
                selectedTime.set(Calendar.MINUTE, sMinute)
                onComplete(selectedTime.time)

            },
            hour,
            minutes,
            is24HourView
        )
        picker.show()
    }
    fun setTimer(
        context: Context,
        extraHour: Int? = null,
        is24HourView: Boolean = false,
        onComplete: (selectedTime: Date?) -> Unit,
    ) {

        val picker: TimePickerDialog
        val cldr: Calendar = Calendar.getInstance()
        if (extraHour != null)
            cldr.add(Calendar.HOUR, extraHour)
        val hour: Int = cldr.get(Calendar.HOUR_OF_DAY)
        val minutes: Int = cldr.get(Calendar.MINUTE)
        val selectedTime = Calendar.getInstance()
        // time picker dialog

        picker = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { tp, sHour, sMinute ->
                selectedTime.set(Calendar.HOUR_OF_DAY, sHour)
                selectedTime.set(Calendar.MINUTE, sMinute)
                onComplete(selectedTime.time)

            },
            hour,
            minutes,
            is24HourView
        )
        picker.show()
    }

    fun getCurrentDay(): Int {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]
        return when (day) {
            Calendar.SUNDAY -> 0
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> 0
        }
    }

    fun getDateByWeekDay(dateSerial: Int): Date {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]
        calendar[Calendar.DAY_OF_WEEK] = dateSerial + 1

        Log.d("DayTTAG", "dateSerial $dateSerial calendar.time ${calendar.time}" + "\n")
        Log.d(
            "DayTTAG",
            "getDayStartTime ${sdf.format(getDayStartTime(dateSerial))} ----------- getDayEndTime ${
                sdf.format(getDayEndTime(dateSerial))
            }" + "\n"
        )
        return calendar.time
    }

    fun getDayStartTime(dateSerial: Int): Date {
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_WEEK] = dateSerial + 1
        calendar[Calendar.HOUR] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.AM_PM] = 0

        return calendar.time
    }

    fun getDayEndTime(dateSerial: Int): Date {
        //get dateSerial from spinner and get week day and set hour
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_WEEK] = dateSerial + 1
        calendar[Calendar.HOUR] = 11
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 999
        calendar[Calendar.AM_PM] = 1
        return calendar.time
    }

    fun getBlackWhiteColor(color: Int): Int {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return if (darkness >= 0.5) {
            Color.WHITE
        } else Color.BLACK
    }


    fun isOreo() = SDK_INT >= O

    fun Context.loadingDialog(onShow: (dialog: Dialog) -> Unit) {
        var bindingView = DialogLoadingBinding.inflate(LayoutInflater.from(this))
        val dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(bindingView.root)
        dialog.show()
        onShow(dialog)
    }
}