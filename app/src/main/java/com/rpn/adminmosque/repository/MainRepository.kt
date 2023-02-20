package com.rpn.adminmosque.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rpn.adminmosque.api.ApiInterface
import com.rpn.adminmosque.model.Data
import com.rpn.adminmosque.model.PrayerTime
import com.rpn.adminmosque.ui.viewmodel.MainViewModel
import com.rpn.adminmosque.utils.SettingsUtility
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainRepository(private val apiInterface: ApiInterface) : KoinComponent {


    val mainViewModel by inject<MainViewModel>()
    val settingsUtility by inject<SettingsUtility>()
    val TAG = "MainRepository"
    val prayerTimeLiveData = MutableLiveData<PrayerTime>()

    val prayerTime: MutableLiveData<PrayerTime>
        get() = prayerTimeLiveData

    suspend fun getPrayerTime(
        month: String? = null,
        year: String? = null,
        onComplete: (listData: List<Data>?) -> Unit
    ) {
        Log.d(
            TAG, "getPrayerTime: ${mainViewModel.masjidInfo.value?.latitude},\n" +
                    "            ${mainViewModel.masjidInfo.value?.longitude},\"2\",\n" +
                    "            $month, $year"
        )

        val result = apiInterface.getPrayerTime(
            mainViewModel.masjidInfo.value?.latitude.toString(),
            mainViewModel.masjidInfo.value?.longitude.toString(), "2",
            month.toString(), year.toString()
        )
        if (result.body() != null) {
            Log.d(TAG, "Successfully fetched: ${result.body()}")
            prayerTimeLiveData.postValue(result.body())
            onComplete(result.body()?.data)
        } else {
            Log.d(
                TAG,
                "Error: PN errorBody ${result.isSuccessful()} ${result.code()} ${result.message()}"
            )
        }
    }

    suspend fun getPrayerTime(onComplete: (listData: List<Data>?) -> Unit) {
        val result = apiInterface.getPrayerTime()
        if (result.body() != null) {
            Log.d(TAG, "Successfully fetched: ${result.body()}")
            prayerTimeLiveData.postValue(result.body())
            onComplete(result.body()?.data)
        } else {
            Log.d(
                TAG,
                "Error: PN errorBody ${result.isSuccessful()} ${result.code()} ${result.message()}"
            )
        }
    }

}