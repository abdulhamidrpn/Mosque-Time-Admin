package com.rpn.adminmosque.utils

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.rpn.adminmosque.model.MasjidInfo
import com.rpn.adminmosque.databinding.ActivityMainBinding
import com.rpn.adminmosque.model.DateDetails
import com.rpn.adminmosque.model.PrayerTimeDate
import com.rpn.adminmosque.model.PrayerTimeDetail

/*val source = if (it.metadata.isFromCache)
    "local cache"
else
    "server"
Timber.d("trackingX source $source")*/

object FireStoreUtil {
    val TAG = "FireStoreUtil"

    val KEY_COLLECTION_TIMINGS = "PRAYER_TIMES"//"MOSQUE_TIMING"
    val KEY_COLLECTION_MASJID_INFO = "MOSQUE_LIST"
    var fetchSource: Source = Source.DEFAULT
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }


    val timingsRef = firestoreInstance.collection(KEY_COLLECTION_TIMINGS)
    val masjidInfoRef = firestoreInstance.collection(KEY_COLLECTION_MASJID_INFO)

    fun getTiming(
        onComplete: (list: List<Any?>?) -> Unit
    ) {
        val existsPlaylist = mutableListOf<Any>()
        timingsRef
            .get(fetchSource)
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (i in it.documents) {
                        try {

//                            val timingItem = Gson().fromJson(
//                                i.data.toString(),
//                                Timing::class.java
//                            )
//                            existsPlaylist.add(timingItem)
                            Log.d(TAG, "getTiming: i\n ${i.data.toString()}")
                        } catch (e: Exception) {
                            Log.d(TAG, "getTiming: exception ${e.message}")
                        }
                    }
                    onComplete(existsPlaylist)
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "getTiming: exception $it")
            }
    }

    fun uploadMasjidInfo(
        binding: ActivityMainBinding,
        masjidInfo: MasjidInfo,
        selectedImage: String,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        FireMediaUtil.uploadImageStoreFile(
            binding,
            selectedImage,
            KEY_COLLECTION_MASJID_INFO
        ) { imageUrl ->
            masjidInfo.image = imageUrl

            masjidInfoRef
                .add(masjidInfo)
                .addOnSuccessListener { documentSnapshot ->
                    documentSnapshot.update("documentId", documentSnapshot.id)
                    Log.d(TAG, "uploadMasjidInfo: Successfull")
                    onComplete(Event(Resource.success(data = documentSnapshot.id)))
                }
                .addOnFailureListener {
                    onComplete(Event(Resource.error("Document not added successfully", null)))
                }
        }
    }

    fun updateMasjidInfo(
        binding: ActivityMainBinding,
        mosqueDocumentId: String,
        masjidInfo: MasjidInfo,
        selectedImage: String?=null,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        if (selectedImage != null) {
            FireMediaUtil.uploadImageStoreFile(
                binding,
                selectedImage,
                KEY_COLLECTION_MASJID_INFO
            ) { imageUrl ->
                masjidInfo.image = imageUrl

                masjidInfoRef
                    .document(masjidInfo.documentId)
                    .set(masjidInfo)
                    .addOnSuccessListener { documentSnapshot ->
                        Log.d(TAG, "uploadMasjidInfo: Successfull")
                        onComplete(Event(Resource.success("Document Updated Successfully")))
                    }
                    .addOnFailureListener {
                        onComplete(Event(Resource.error("Document not updated", null)))
                    }
            }
        }else{
            masjidInfoRef
                .document(masjidInfo.documentId)
                .set(masjidInfo)
                .addOnSuccessListener { documentSnapshot ->
                    Log.d(TAG, "uploadMasjidInfo: Successfull")
                    onComplete(Event(Resource.success("Document Updated Successfully")))
                }
                .addOnFailureListener {
                    onComplete(Event(Resource.error("Document not updated", null)))
                }
        }
    }
    fun updateMasjidInfo(
        binding: ActivityMainBinding,
        id:String,
        field:String,
        value:String,
        selectedImage: String?=null,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        if (selectedImage != null) {
            FireMediaUtil.uploadImageStoreFile(
                binding,
                selectedImage,
                KEY_COLLECTION_MASJID_INFO
            ) { imageUrl ->
                val value = imageUrl

                masjidInfoRef
                    .document(id)
                    .update(field,value)
                    .addOnSuccessListener { documentSnapshot ->
                        Log.d(TAG, "Update MasjidInfo: Successfull")
                        onComplete(Event(Resource.success("Document Updated Successfully")))
                    }
                    .addOnFailureListener {
                        onComplete(Event(Resource.error("Document not added successfully", null)))
                    }
            }
        }else{
            masjidInfoRef
                .document(id)
                .update(field,value)
                .addOnSuccessListener { documentSnapshot ->
                    Log.d(TAG, "Update MasjidInfo: Successfull")
                    onComplete(Event(Resource.success("Document Updated Successfully")))
                }
                .addOnFailureListener {
                    onComplete(Event(Resource.error("Document not added successfully", null)))
                }
        }
    }

    fun uploadMasjidTimingUltra(
        binding: ActivityMainBinding,
        mosqueId: String,
        timeRange: String,
        prayerTime: PrayerTimeDetail,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        masjidInfoRef
            .document(mosqueId)
            .collection("Time Range")
            .document("Current Running Time")
            .set(prayerTime)
            .addOnSuccessListener {
                onComplete(Event(Resource.success("Updated Times")))
            }.addOnFailureListener {
                onComplete(Event(Resource.error("Document not added successfully", null)))
            }

    }

    fun uploadMasjidTimingYearData(
        mosqueId: String,
        year: String,
        timeRange: String,
        prayerTime: PrayerTimeDate,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        masjidInfoRef
            .document(mosqueId)
            .collection(year)
            .document("Month $timeRange")
            .set(prayerTime)
            .addOnSuccessListener {
                onComplete(Event(Resource.success("Updated Times")))
            }.addOnFailureListener {
                onComplete(Event(Resource.error("Document not added successfully", null)))
            }

    }


    fun updateSingleDateTime(
        mosqueId: String,
        date: String,
        dateLiteUltra: DateDetails?,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {

        masjidInfoRef
            .document(mosqueId)
            .collection("Time Range")
            .document("Current Running Time")
            .update(date, dateLiteUltra)
            .addOnSuccessListener {
                onComplete(Event(Resource.success("Updated Times")))
            }.addOnFailureListener {

                if (dateLiteUltra != null) {
                    masjidInfoRef
                        .document(mosqueId)
                        .collection("Time Range")
                        .document("Current Running Time")
                        .set(mapOf<String, DateDetails>(date to dateLiteUltra))
                        .addOnSuccessListener {
                            onComplete(Event(Resource.success("Updated Times")))
                        }.addOnFailureListener {
                            onComplete(Event(Resource.error("${it.message}", null)))
                        }
                }
            }

    }


    fun getMyMosque(
        mosqueId: String,
        onComplete: (Event<Resource<MasjidInfo>>) -> Unit
    ) {
        masjidInfoRef
            .document(mosqueId)
            .get(fetchSource)
            .addOnSuccessListener {
                val masjidInfo = it.toObject(MasjidInfo::class.java)
                onComplete(Event(Resource.success("Fetched My Mosque Info",masjidInfo)))
            }.addOnFailureListener {
                onComplete(Event(Resource.error("${it.message}")))
            }


    }
    fun getMyMosqueDataChanged(
        mosqueId: String,
        onComplete: (Event<Resource<MasjidInfo>>) -> Unit
    ) {
        masjidInfoRef
            .whereEqualTo("documentId", mosqueId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val masjidInfo = dc.document.toObject(MasjidInfo::class.java)
                            onComplete(Event(Resource.success("On Added Mosque Info ${masjidInfo.masjidName}",masjidInfo)))
                            Log.d(TAG, "New city: ${dc.document.data}")
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val masjidInfo = dc.document.toObject(MasjidInfo::class.java)
                            if (mosqueId == masjidInfo.documentId) {
                                val msg = if (masjidInfo.activated) "Your Mosque is Registered Successfully" else "Your Mosque is not Registered"
                                onComplete(Event(Resource.success(msg, masjidInfo)))
                                Log.d(TAG, "Modified city: ${dc.document.data}")
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            val masjidInfo = dc.document.toObject(MasjidInfo::class.java)
                            if (mosqueId == masjidInfo.documentId) {
                                onComplete(Event(Resource.error("Mosque Removed")))
                                Log.d(TAG, "Removed city: ${dc.document.data}")
                            }
                        }
                    }
                }
            }

    }

}