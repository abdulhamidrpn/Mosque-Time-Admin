package com.rpn.adminmosque.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rpn.adminmosque.model.DateDetails
import com.rpn.adminmosque.model.MasjidInfo
import com.rpn.adminmosque.model.PrayerTimeDate
import com.rpn.adminmosque.repository.FireStoreRepository
import com.rpn.adminmosque.repository.MainRepository
import com.rpn.adminmosque.utils.Event
import com.rpn.adminmosque.utils.GeneralUtils.sdfDate
import com.rpn.adminmosque.utils.Resource
import com.rpn.adminmosque.utils.SettingsUtility
import com.rpn.adminmosque.utils.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


class HomeViewModel :
    CoroutineViewModel(Dispatchers.IO),
    KoinComponent {
    val TAG = "HomeViewModel"

//    lateinit var binding: FragmentHomeBinding
    val fireStoreRepository by inject<FireStoreRepository>()
    val mainRepository by inject<MainRepository>()
    val settingsUtility by inject<SettingsUtility>()

    var getHour = true
    var getMinute = true
    var masjidInfo = MutableLiveData<MasjidInfo?>()
    var todayMosqueTime = MutableLiveData<DateDetails?>()
    var allMosqueTime = MutableLiveData<PrayerTimeDate?>()
    var currentDate = MutableLiveData<Calendar>()

    val currentTime = liveData {
        while (true) {
            emit(Calendar.getInstance().time)
            kotlinx.coroutines.delay(1000)
        }
    }

    val todayFormat = MutableLiveData<String>()
    var currentTimeDayCheck = ""
    var currentTimeHourCheck = ""
    val currentTimeHour = liveData {
        while (true) {
            if (currentTimeSecond.value == "00" && currentTimeMinute.value == "00" || getHour) {
                getHour = false
                Log.d(TAG, "current time second in minute: ${currentTimeSecond.value} ")
                currentTimeHourCheck = currentHour()

                val today = sdfDate.format(Calendar.getInstance().time)
                if (today != currentTimeDayCheck) {
                    todayFormat.postValue(today)
                    val todayTime = allMosqueTime.value?.date?.find {
                        it.readable == today
                    }
                    Log.d(TAG, "Check time hour and post new date time: $todayTime ")
                    todayMosqueTime.postValue(todayTime)

                }
                emit(currentHour())
            }
            if (currentTimeHourCheck != currentHour()) {
                getHour = true
            }
            kotlinx.coroutines.delay(1000)
        }
    }
    var currentTimeMinuteCheck = ""
    val currentTimeMinute = liveData {
        while (true) {
            if (currentTimeSecond.value == "00" || getMinute) {
                getMinute = false
                Log.d(TAG, "current time second in minute: ${currentTimeSecond.value} ")
                currentTimeMinuteCheck = currentMunite()
                emit(currentMunite())
            }
            if (currentTimeMinuteCheck != currentMunite()) {
                getMinute = true
            }
            kotlinx.coroutines.delay(1000)
        }
    }
    val currentTimeSecond = liveData {
        while (true) {
            emit(currentSecond())

            kotlinx.coroutines.delay(1000)
        }
    }

    init {

        try {
            masjidInfo.postValue(
                Gson().fromJson(
                    settingsUtility.mosqueInfo,
                    MasjidInfo::class.java
                )
            )
            currentDate.postValue(Calendar.getInstance())
            setTodayMosqueTime()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setTodayMosqueTime() {

        val prayerTimeDate = Gson().fromJson(settingsUtility.mosqueTime, PrayerTimeDate::class.java)
        val todayDateFormat = sdfDate.format(Calendar.getInstance().time)
        val todayTime = prayerTimeDate.date?.find {
            it.readable == todayDateFormat
        }
        todayFormat.postValue(todayDateFormat)
        allMosqueTime.postValue(prayerTimeDate)
        todayMosqueTime.postValue(todayTime)

        if (todayMosqueTime.value == null) {
            getMyMosqueTime {
                Log.d(TAG, "setTodayMosqueTime: " + it.peekContent().message)
            }
        }

    }

    fun currentMunite(): String {
        return if (Calendar.getInstance().get(Calendar.MINUTE).toString().length == 1)
            "0" + Calendar.getInstance().get(Calendar.MINUTE).toString()
        else Calendar.getInstance().get(Calendar.MINUTE).toString()
    }

    fun currentSecond(): String {
        return if (Calendar.getInstance().get(Calendar.SECOND).toString().length == 1)
            "0" + Calendar.getInstance().get(Calendar.SECOND).toString()
        else Calendar.getInstance().get(Calendar.SECOND).toString()

    }

    fun currentHour(): String {
        return if (Calendar.getInstance().get(Calendar.HOUR).toString().length == 1)
            "0" + Calendar.getInstance().get(Calendar.HOUR).toString()
        else Calendar.getInstance().get(Calendar.HOUR).toString()
    }

    fun getMyMosqueTime(
        onComplete: (Event<Resource<String>>) -> Unit
    ) {
        viewModelScope.launch {
            if (settingsUtility.mosqueDocumentId != "" && settingsUtility.mosqueDocumentId.isNotEmpty()) {
                fireStoreRepository.getMyMosqueTime(settingsUtility.mosqueDocumentId) {

                    if (it.peekContent().status == Status.SUCCESS) {
                        val todayFormatx = sdfDate.format(Calendar.getInstance().time)
                        todayFormat.postValue(todayFormatx)
                        val todayTime = it.peekContent().data?.find {
                            it.readable == todayFormatx
                        }
                        allMosqueTime.postValue(PrayerTimeDate(it.peekContent().data))
                        currentTimeDayCheck = todayTime?.readable ?: ""
                        todayMosqueTime.postValue(todayTime)


                        onComplete(Event(Resource.success("Your mosque time fetched")))
                    } else {
                        onComplete(Event(Resource.error(it.peekContent().message)))
                    }
                }
            } else {
                onComplete(Event(Resource.error("Select a mosque")))
            }
        }

    }


}
