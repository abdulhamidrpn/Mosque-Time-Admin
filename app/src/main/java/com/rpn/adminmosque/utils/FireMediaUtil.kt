package com.rpn.adminmosque.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.rpn.adminmosque.databinding.ActivityMainBinding
import java.io.File


object FireMediaUtil {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val imageStorageRef: StorageReference
        get() = storageInstance.reference
            .child(
                Constants.KEY_COLLECTION_IMAGE_STORAGE
                    ?: throw NullPointerException("UID is null.")
            )


    fun uploadImageStoreFile(
        binding: ActivityMainBinding,
        image: String,
        imageType: String,
        isCompressed: Boolean = false,
        onSuccess: (url: String) -> Unit
    ) {

        val fileFrompath = File(image)
        val ref = imageStorageRef.child("$imageType/${fileFrompath.nameWithoutExtension}")

        Log.d(TAG, "uploadImageStoreFile: FileFrom path $fileFrompath")
        
        ref.getDownloadUrl().addOnSuccessListener {
            Log.d(TAG, "file found in firestore uri $it")
            onSuccess(it.toString())
        }.addOnFailureListener {
            var uploadTask = if (isCompressed) ref.putFile(Uri.fromFile(File(fileFrompath.path)))
            else ref.putFile(Uri.parse(image))
            Log.d(TAG, "uploadImageStoreFile: Is compressed $isCompressed")
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

    val TAG = "FIREMEDIA"

    fun pathToReference(path: String) = storageInstance.getReference(path)


}
