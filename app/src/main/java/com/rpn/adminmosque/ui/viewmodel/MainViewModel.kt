package com.rpn.adminmosque.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rpn.adminmosque.databinding.ActivityMainBinding
import com.rpn.adminmosque.model.Data
import com.rpn.adminmosque.model.DateDetails
import com.rpn.adminmosque.model.MasjidInfo
import com.rpn.adminmosque.model.PrayerTime
import com.rpn.adminmosque.repository.FireStoreRepository
import com.rpn.adminmosque.repository.MainRepository
import com.rpn.adminmosque.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*


class MainViewModel : CoroutineViewModel(Dispatchers.Main),
    KoinComponent {
    val TAG = "MainFra"

    lateinit var binding: ActivityMainBinding

    val fireStoreRepository by inject<FireStoreRepository>()
    val mainRepository by inject<MainRepository>()
    val settingsUtility by inject<SettingsUtility>()
    var imgMosque = MutableLiveData<String?>()
    var imgMsg = MutableLiveData<MutableList<String?>>(mutableListOf())
    var masjidInfo = MutableLiveData<MasjidInfo?>()
    var isMasjidActivated = MutableLiveData<Boolean>(false)
    var dateLiteUltra = MutableLiveData<DateDetails?>()

    var selectedDate = MutableLiveData<Calendar>()

    val prayerTime: MutableLiveData<PrayerTime>
        get() = mainRepository.prayerTime


    init {

        try {
            masjidInfo.postValue(
                Gson().fromJson(
                    settingsUtility.mosqueInfo,
                    MasjidInfo::class.java
                )
            )
            getMessage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPrayerTime(
        month: String? = null,
        year: String? = null,
        onComplete: (listData: List<Data>?) -> Unit
    ) {

        viewModelScope.launch {
            mainRepository.getPrayerTime(month, year) {
                onComplete(it)
            }
        }


    }

    fun getMessage() {
        viewModelScope.launch {
            if (settingsUtility.mosqueDocumentId != "" && settingsUtility.mosqueDocumentId.isNotEmpty()) {
                fireStoreRepository.getMessage(settingsUtility.mosqueDocumentId) {

                    if (it.peekContent().status == Status.SUCCESS) {

                    }
                }
            } else {

            }
        }

    }

    fun getPrayerTime() {

        viewModelScope.launch {
            mainRepository.getPrayerTime() {

            }
        }


    }


    fun getMyMosqueDataChanged(
        onComplete: (msg: String?) -> Unit
    ) {
        viewModelScope.launch {
            if (settingsUtility.mosqueDocumentId.isNotEmpty() && settingsUtility.mosqueDocumentId != "") {
                fireStoreRepository.getMyMosqueDataChanged(settingsUtility.mosqueDocumentId) {
                    if (it.peekContent().status == Status.SUCCESS) {
                        val masjidInfo = it.peekContent().data
                        settingsUtility.mosqueInfo = masjidInfo.toString()
                        settingsUtility.mosqueActivated = masjidInfo?.activated ?: false
                        isMasjidActivated.postValue(masjidInfo?.activated ?: false)
                    } else {
                        settingsUtility.mosqueInfo = ""
                        settingsUtility.mosqueDocumentId = ""
                        settingsUtility.mosqueActivated = false
                        isMasjidActivated.postValue(false)
                    }
                    onComplete(it.peekContent().message)
                }
            }
        }
    }

    fun getMyMosquebyOwnerId(
        onComplete: (msg: String?) -> Unit
    ) {
        viewModelScope.launch {
            fireStoreRepository.getMyMosqueByUserId(settingsUtility.userId) {
                if (it.peekContent().status == Status.SUCCESS) {
                    val masjidInfoData = it.peekContent().data
                    masjidInfo.postValue(masjidInfoData)
                    settingsUtility.mosqueInfo = masjidInfoData.toString()
                    settingsUtility.mosqueImage = masjidInfoData?.image.toString()
                    settingsUtility.userPhone = masjidInfoData?.phoneNumber.toString()
                    settingsUtility.mosqueDocumentId = masjidInfoData?.documentId.toString()
                    settingsUtility.mosqueActivated = masjidInfoData?.activated ?: false
                    isMasjidActivated.postValue(masjidInfoData?.activated ?: false)
                } else {
                    settingsUtility.mosqueInfo = ""
                    settingsUtility.mosqueDocumentId = ""
                    settingsUtility.mosqueActivated = false
                    isMasjidActivated.postValue(false)
                }
                onComplete(it.peekContent().message)
            }
        }
    }


    fun uploadMasjidInfo(
        masjidInfo: MasjidInfo,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        viewModelScope.launch {

            imgMosque.value?.let {
                fireStoreRepository.uploadMasjidInfo(
                    binding,
                    masjidInfo,
                    it
                ) {
                    onComplete(it)
                }
            }
        }
    }

    fun updateMasjidInfo(
        masjidInfo: MasjidInfo,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        viewModelScope.launch {

            fireStoreRepository.updateMasjidInfo(
                binding,
                masjidInfo,
                imgMosque.value
            ) {
                onComplete(it)
            }

        }
    }

    fun updateSingleDateTime(
        date: String,
        dateLiteUltra: DateDetails?,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        viewModelScope.launch {

            fireStoreRepository.updateSingleDateTime(
                settingsUtility.mosqueDocumentId,
                date, dateLiteUltra
            ) {
                onComplete(it)
            }

        }
    }

    fun updateMessage(
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        viewModelScope.launch {
            Log.d(TAG, "updateMessage: imgMsg ${imgMsg.value}")
            /* fireStoreRepository.updateMessage(
                 binding,
                 settingsUtility.mosqueDocumentId,
                 imgMsg.value
             ) {
                 onComplete(it)
             }*/

            for (i in 0..(imgMsg.value?.size?.minus(1) ?: 0)) {
                val image = imgMsg.value?.get(i)
                Log.d(TAG, "updateMessage: foreach Image $image")
                withContext(Dispatchers.Default) {
                    fireStoreRepository.updateMessage(
                        binding,
                        settingsUtility.mosqueDocumentId,
                        image,
                        i
                    ) {
                        Log.d(TAG, "updateMessage: onComplete Image ${it.peekContent().message}")
                        onComplete(it)
                    }
                }
                delay(5000)
            }
            /*imgMsg.value?.forEach { image ->
                Log.d(TAG, "updateMessage: foreach Image $image")
                withContext(Dispatchers.Default) {
                    fireStoreRepository.updateMessage(
                        binding,
                        settingsUtility.mosqueDocumentId,
                        image
                    ) {
                        Log.d(TAG, "updateMessage: onComplete Image ${it.peekContent().message}")
                        onComplete(it)
                    }
                }
            }*/

            /*
            test_arr.forEach {
                Log.d(TAG, "updateMessage: testARR $it")
                withContext(Dispatchers.IO){
                    delay(2000)
                    Log.d(TAG, "updateMessage: testARR uploaded $it")
                }
            }
*/

        }
    }

    fun updateMessage(
        index: Int,
        onComplete: (String?) -> Unit
    ) {

        viewModelScope.launch {
            Log.d(TAG, "updateMessage: imgMsg ${imgMsg.value}")
            if (imgMsg.value != null && index < imgMsg.value?.size!!) {
                val image = imgMsg.value?.get(index)

                if (image?.contains("https://firebasestorage.googleapis.com") == false) {
                    fireStoreRepository.updateMessage(
                        binding,
                        settingsUtility.mosqueDocumentId,
                        image,
                        index
                    ) {
                        Log.d(TAG, "updateMessage: onComplete Image ${it.peekContent().message}")
                        onComplete(index.plus(1).toString() + " " + it.peekContent().message)
                    }
                } else {
                    onComplete(index.plus(1).toString() + " Already uploaded")
                }
            } else {
                onComplete(index.plus(1).toString() + " Select Again")
            }
        }
    }

    private fun getTiming() {
        launch {
            FireStoreUtil.getTiming {
                Log.d("FireStoreUtil", "getTiming viewmodel:\n $it")
            }

        }
    }

}



