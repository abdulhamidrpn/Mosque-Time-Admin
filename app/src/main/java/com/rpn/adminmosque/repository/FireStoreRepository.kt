package com.rpn.adminmosque.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.gson.Gson
import com.rpn.adminmosque.alias.toDataClass
import com.rpn.adminmosque.databinding.ActivityMainBinding
import com.rpn.adminmosque.extensions.notifyObserver
import com.rpn.adminmosque.model.*
import com.rpn.adminmosque.ui.viewmodel.MainViewModel
import com.rpn.adminmosque.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File


class FireStoreRepository() : KoinComponent {


    val mainViewModel by inject<MainViewModel>()
    val settingsUtility by inject<SettingsUtility>()

    val TAG = "FireStoreRepository"

    val KEY_COLLECTION_TIMINGS = "PRAYER_TIMES"//"MOSQUE_TIMING"
    val KEY_COLLECTION_MASJID_INFO = "MOSQUE_LIST"
    val KEY_COLLECTION_MASJID_MESSAGE = "MOSQUE_MESSAGE"

    var fetchSource: Source = Source.DEFAULT
    val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val timingsRef = firestoreInstance.collection(KEY_COLLECTION_TIMINGS)
    val masjidInfoRef = firestoreInstance.collection(KEY_COLLECTION_MASJID_INFO)

    private val imageStorageRef: StorageReference
        get() = storageInstance.reference
            .child(
                Constants.KEY_COLLECTION_IMAGE_STORAGE ?: throw NullPointerException("UID is null.")
            )


    fun uploadImageStoreFile(
        binding: ActivityMainBinding,
        image: String,
        imageType: String,
        onSuccess: (url: String) -> Unit
    ) {

        val fileFrompath = File(image)
        val ref = imageStorageRef.child("$imageType/${fileFrompath.nameWithoutExtension}")

        ref.getDownloadUrl().addOnSuccessListener {
            Log.d(TAG, "file found in firestore uri $it")
            onSuccess(it.toString())
        }.addOnFailureListener {
            var uploadTask = ref.putFile(Uri.fromFile(File(fileFrompath.path)))

            uploadTask.addOnProgressListener { (bytesTransferred, totalByteCount) ->
                val progress = (100.0 * bytesTransferred) / totalByteCount
                binding.progressBar.max = 100
                binding.progressBar.progress = progress.toInt()
                Log.d(TAG, "Upload Image is $progress'%' done")
            }.addOnPausedListener {
                Log.d(TAG, "Upload is paused")
            }.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // Handle successful uploads on complete
                // ...
                binding.progressBar.progress = 0
                storageInstance.getReference(ref.path).downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "file upload in firestore uri $it")
                    onSuccess(it.toString())
                }
            }
        }

    }

    fun pathToReference(path: String) = storageInstance.getReference(path)


    suspend fun getTiming(
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

    suspend fun uploadMasjidInfo(
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

                    settingsUtility.mosqueImage = imageUrl
                    settingsUtility.mosqueDocumentId = documentSnapshot.id
                    settingsUtility.mosqueInfo = masjidInfo.toString()
                    mainViewModel.masjidInfo.postValue(masjidInfo)
                    onComplete(Event(Resource.success("Upload Mosque Info Successfully")))
                }
                .addOnFailureListener {
                    onComplete(Event(Resource.error("Document not added successfully", null)))
                }
        }
    }

    suspend fun updateMessage(
        binding: ActivityMainBinding,
        mosqueId: String,
        selectedImage: MutableList<String?>?,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {
        selectedImage?.forEach { image ->
            if (image?.contains("https://firebasestorage.googleapis.com") == false) {
                Log.d(TAG, "updateMessage: selectedImage.size ${selectedImage.size} image $image")
                withContext(Dispatchers.IO) {
                    image.let {
                        FireMediaUtil.uploadImageStoreFile(
                            binding,
                            it,
                            KEY_COLLECTION_MASJID_MESSAGE
                        ) { imageUrl ->
                            Log.d(
                                TAG,
                                "updateMessage: index ${selectedImage.indexOf(image)} size ${selectedImage.size} url $imageUrl"
                            )
                            masjidInfoRef
                                .document(mosqueId)
                                .collection("Messages")
                                .document("Current Messages")
                                .update(selectedImage.indexOf(image).toString(), imageUrl)
                                .addOnSuccessListener { documentSnapshot ->
//                                    if (image == selectedImage.lastOrNull()) {
                                    onComplete(Event(Resource.success("Updated Message Successfully")))
//                                    }
                                }
                                .addOnFailureListener {
                                    masjidInfoRef
                                        .document(mosqueId)
                                        .collection("Messages")
                                        .document("Current Messages")
                                        .set(
                                            mapOf<String, String>(
                                                selectedImage.indexOf(image).toString() to imageUrl
                                            )
                                        )
                                        .addOnSuccessListener {
//                                            if (image == selectedImage.lastOrNull()) {
                                            onComplete(Event(Resource.success("Updated Message Successfully")))
//                                            }
                                        }.addOnFailureListener {
                                            onComplete(Event(Resource.error("${it.message}", null)))
                                        }
                                }
                        }
                    }
                }
            }
        }
    }


    suspend fun updateMessage(
        binding: ActivityMainBinding,
        mosqueId: String,
        selectedImage: String?,
        index: Int = 0,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {
        selectedImage?.let { image ->
//            Log.d(TAG, "updateMessage: selectedImage.size $image")
            FireMediaUtil.uploadImageStoreFile(
                binding,
                image,
                KEY_COLLECTION_MASJID_MESSAGE
            ) { imageUrl ->
//                    Log.d(TAG, "updateMessage: index ${selectedImage} url $imageUrl")
                masjidInfoRef
                    .document(mosqueId)
                    .collection("Messages")
                    .document("Current Messages")
                    .update(index.toString(), imageUrl)
                    .addOnSuccessListener { documentSnapshot ->
                        mainViewModel.imgMsg.value?.set(index, imageUrl)
                        onComplete(Event(Resource.success("Updated Message Successfully")))
                    }
                    .addOnFailureListener {
                        masjidInfoRef
                            .document(mosqueId)
                            .collection("Messages")
                            .document("Current Messages")
                            .set(mapOf<String, String>(index.toString() to imageUrl))
                            .addOnSuccessListener {
                                mainViewModel.imgMsg.value?.set(index, imageUrl)
                                onComplete(Event(Resource.success("Updated Message Successfully")))
                            }.addOnFailureListener {
                                onComplete(Event(Resource.error("${it.message}", null)))
                            }
                    }

            }

        }
    }

    suspend fun updateMasjidInfo(
        binding: ActivityMainBinding,
        masjidInfo: MasjidInfo,
        selectedImage: String? = null,
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
                        settingsUtility.mosqueImage = imageUrl
                        settingsUtility.mosqueInfo = masjidInfo.toString()
                        onComplete(Event(Resource.success("Document Updated Successfully")))
                    }
                    .addOnFailureListener {
                        onComplete(Event(Resource.error("Document not updated", null)))
                    }
            }
        } else {
            masjidInfoRef
                .document(masjidInfo.documentId)
                .set(masjidInfo)
                .addOnSuccessListener { documentSnapshot ->
                    settingsUtility.mosqueInfo = masjidInfo.toString()
                    onComplete(Event(Resource.success("Document Updated Successfully")))
                }
                .addOnFailureListener {
                    onComplete(Event(Resource.error("Document not updated", null)))
                }
        }
    }

    suspend fun updateSingleDateTime(
        mosqueId: String,
        date: String,
        dateLiteUltra: DateDetails?,
        onComplete: (Event<Resource<String>>) -> Unit
    ) {


        // TODO: Update string to date


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


    suspend fun getMyMosque(
        mosqueId: String,
        onComplete: (Event<Resource<MasjidInfo>>) -> Unit
    ) {
        masjidInfoRef
            .document(mosqueId)
            .get(fetchSource)
            .addOnSuccessListener {
                val masjidInfo = it.toObject(MasjidInfo::class.java)
                onComplete(Event(Resource.success("Fetched My Mosque Info", masjidInfo)))
            }.addOnFailureListener {
                onComplete(Event(Resource.error("${it.message}")))
            }


    }

    suspend fun getMyMosqueTime(
        mosqueId: String,
        onComplete: (Event<Resource<List<DateDetails>>>) -> Unit
    ) {

        masjidInfoRef
            .document(mosqueId)
            .collection("Time Range")
            .document("Current Running Time")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    onComplete(Event(Resource.error("${e.message}")))
                    return@addSnapshotListener
                }


                if (snapshot != null && snapshot.exists()) {
                    try {
                        Log.d(TAG, "getMyMosqueTime: ${snapshot.data}")
                        val listOfMosqueTime = arrayListOf<DateDetails>()
                        val values: Map<String, Any> = snapshot.data as Map<String, Any>;
                        // TODO: Fix and remove extra code
                        Log.d(TAG, "getMyMosqueTime: keys ${values.keys}")
                        values.keys.forEach {
                            val MosqueTimeValue = (values.get(it) as Map<String, Object>)
                            val MosqueTime: DateDetails = MosqueTimeValue.toDataClass<DateDetails>()
                            listOfMosqueTime.add(MosqueTime)
                        }
                        Log.d(TAG, "getMyMosqueTime: date MosqueTime ${listOfMosqueTime}")
                        settingsUtility.mosqueTime = Gson().toJson(PrayerTimeDetail(listOfMosqueTime)).toString()
                        onComplete(Event(Resource.success("Fetched My Mosque Info", listOfMosqueTime)))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onComplete(Event(Resource.error("${e.message}")))
                    }


                } else {
                    onComplete(Event(Resource.error("${e?.message}")))
                }

/*
                try {
                    Log.d(TAG, "getMyMosqueTime: ${it.data}")
                    val listOfDateLiteUltra = arrayListOf<DateLiteUltra>()
                    val values: Map<String, Any> = it.data as Map<String, Any>;
                    // TODO: Fix and remove extra code
                    Log.d(TAG, "getMyMosqueTime: keys ${values.keys}")
                    values.keys.forEach {
                        val dateLiteUltraValue = (values.get(it) as Map<String, Object>)
                        val dateLiteUltra: DateLiteUltra =
                            dateLiteUltraValue.toDataClass<DateLiteUltra>()
                        val times =
                            ((dateLiteUltraValue.get("timingsLiteUltra")) as Map<String, Object>)
                        val sunRise =
                            ((dateLiteUltraValue.get("timingsLiteUltra")) as Map<String, Object>)
                        val sunSet =
                            ((dateLiteUltraValue.get("timingsLiteUltra")) as Map<String, Object>)
                        val timingsLiteUltrax = TimingsLiteUltra(
                            asr = times.get("asr").toString(),
                            isha = times.get("isha").toString(),
                            imsak = times.get("imsak").toString(),
                            dhuhr = times.get("dhuhr").toString(),
                            fajr = times.get("fajr").toString(),
                            maghrib = times.get("maghrib").toString(),
                        )
                        dateLiteUltra.apply {
                            timingsLiteUltra = timingsLiteUltrax
                        }
                        listOfDateLiteUltra.add(dateLiteUltra)
                    }
                    Log.d(TAG, "getMyMosqueTime: date dateLiteUltra ${listOfDateLiteUltra}")
                    settingsUtility.mosqueTime =
                        Gson().toJson(PrayerTimeDate(listOfDateLiteUltra)).toString()
                    onComplete(
                        Event(
                            Resource.success(
                                "Fetched My Mosque Info",
                                listOfDateLiteUltra
                            )
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    onComplete(Event(Resource.error("${e.message}")))
                }*/
            }

    }


    suspend fun getMessage(
        mosqueId: String,
        onComplete: (Event<Resource<MutableList<String?>>>) -> Unit
    ) {

        masjidInfoRef
            .document(mosqueId)
            .collection("Messages")
            .document("Current Messages")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    onComplete(Event(Resource.error("${e.message}")))
                    return@addSnapshotListener
                }

                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "$source data: ${snapshot.data}")


                    try {
                        val values: Map<String, String> = snapshot.data as Map<String, String>;
                        mainViewModel.imgMsg.value?.clear()
                        values.keys.forEach {
                            Log.d(TAG, "getMessage: ${values.get(it)}")
                            mainViewModel.imgMsg.value?.add(values.get(it))
                        }
                        mainViewModel.imgMsg.notifyObserver()
                        onComplete(
                            Event(
                                Resource.success(
                                    "Fetched Message",
                                    mainViewModel.imgMsg.value
                                )
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onComplete(Event(Resource.error("${e.message}")))
                    }


                } else {
                    Log.d(TAG, "$source data: null")
                }
            }

    }

    suspend fun getMyMosqueDataChanged(
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
                            onComplete(
                                Event(
                                    Resource.success(
                                        "On Added Mosque Info ${masjidInfo.masjidName}",
                                        masjidInfo
                                    )
                                )
                            )
                            Log.d(TAG, "New city: ${dc.document.data}")
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val masjidInfo = dc.document.toObject(MasjidInfo::class.java)
                            if (mosqueId == masjidInfo.documentId) {
                                val msg =
                                    if (masjidInfo.activated) "Your Mosque is Registered Successfully" else "Your Mosque is not Registered"
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

    suspend fun getMyMosqueByUserId(
        ownerUid: String,
        onComplete: (Event<Resource<MasjidInfo>>) -> Unit
    ) {
        masjidInfoRef
            .whereEqualTo("ownerUid", ownerUid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(TAG, "getMyMosqueByUserId: dc Document ${dc.document}")
                            val masjidInfo = dc.document.toObject(MasjidInfo::class.java)
                            onComplete(
                                Event(
                                    Resource.success(
                                        "On Added Mosque Info ${masjidInfo.masjidName}",
                                        masjidInfo
                                    )
                                )
                            )
                            Log.d(TAG, "New city: ${dc.document.data}")
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val masjidInfo = dc.document.toObject(MasjidInfo::class.java)
                            val msg =
                                if (masjidInfo.activated) "Your Mosque is Registered Successfully" else "Your Mosque is not Registered"
                            onComplete(Event(Resource.success(msg, masjidInfo)))
                            Log.d(TAG, "Modified city: ${dc.document.data}")

                        }
                        DocumentChange.Type.REMOVED -> {
                            val masjidInfo = dc.document.toObject(MasjidInfo::class.java)
                            onComplete(Event(Resource.error("Mosque Removed")))
                            Log.d(TAG, "Removed city: ${dc.document.data}")

                        }
                    }
                }
            }

    }

}