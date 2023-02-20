package com.rpn.adminmosque.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.rpn.adminmosque.utils.Event
import com.rpn.adminmosque.utils.Resource
import com.rpn.adminmosque.utils.SettingsUtility

class AuthAppRepository(
    private val application: Application,
    val settingsUtility: SettingsUtility
) {
    private val firebaseAuth: FirebaseAuth
    val userLiveData: MutableLiveData<FirebaseUser?>
    val loggedOutLiveData: MutableLiveData<Boolean>

    fun login(email: String?, password: String?) {
        firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(firebaseAuth.currentUser)
                    settingsUtility.userId = firebaseAuth.currentUser?.uid.toString()
                    settingsUtility.userEmail = firebaseAuth.currentUser?.email.toString()
                    settingsUtility.userName = firebaseAuth.currentUser?.displayName.toString()
                    Log.d("AuthTAG", "authAppRepository: ${firebaseAuth.currentUser?.uid}")
                } else {
                    Toast.makeText(
                        application.applicationContext,
                        "Login Failure: " + task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    userLiveData.postValue(null)
                }
            }
    }

    fun register(name: String?, email: String?, password: String?,
                 onComplete: (Event<Resource<String>>) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                Log.d ("AuthTAG", "authAppRepository: ${task.result}")
                if (task.isSuccessful) {

                    val user = FirebaseAuth.getInstance().currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build()

                    user!!.updateProfile(profileUpdates)
                        .addOnSuccessListener {
                            userLiveData.postValue(firebaseAuth.currentUser)
                            settingsUtility.userId = firebaseAuth.currentUser?.uid.toString()
                            settingsUtility.userEmail = firebaseAuth.currentUser?.email.toString()
                            settingsUtility.userName = firebaseAuth.currentUser?.displayName.toString()
                            Log.d ("AuthTAG", "authAppRepository: ${firebaseAuth.currentUser?.uid}")
                            onComplete(Event(Resource.success("User Registration Successful")))
                        }

                } else {
                    onComplete(Event(Resource.success("Registration Failure: " + task.exception!!.message)))
                    userLiveData.postValue(null)
                }
            }
    }

    fun logOut() {
        firebaseAuth.signOut()
        loggedOutLiveData.postValue(true)
        userLiveData.postValue(null)
        settingsUtility.clearAllData()
    }

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        userLiveData = MutableLiveData()
        loggedOutLiveData = MutableLiveData()
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
            settingsUtility.userId = firebaseAuth.currentUser?.uid.toString()
            settingsUtility.userEmail = firebaseAuth.currentUser?.email.toString()
            settingsUtility.userName = firebaseAuth.currentUser?.displayName.toString()
            Log.d("AuthTAG", "authAppRepository: ${firebaseAuth.currentUser}")
            Log.d("AuthTAG", "authAppRepository: ${firebaseAuth.currentUser?.uid}")
            loggedOutLiveData.postValue(false)
        } else {
            userLiveData.postValue(null)
            loggedOutLiveData.postValue(true)
        }
    }
}