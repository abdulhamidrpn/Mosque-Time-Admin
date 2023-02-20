package com.rpn.adminmosque.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsUtility(context: Context) {

    private val sPreferences: SharedPreferences

    fun clearAllData(){
        userId=""
        userEmail=""
        userName=""
        userPhone=""
        mosqueDocumentId=""
        mosqueImage = ""
        mosqueTime = ""
        mosqueInfo=""
        mosqueActivated=false
    }


    var userId: String
        get() = sPreferences.getString(KEY_USER_ID, null) ?: ""
        set(value) {
            setPreference(KEY_USER_ID, value)
        }
    var userEmail: String
        get() = sPreferences.getString(KEY_USER_EMAIL, null) ?: ""
        set(value) {
            setPreference(KEY_USER_EMAIL, value)
        }
    var userName: String
        get() = sPreferences.getString(KEY_USER_NAME, null) ?: ""
        set(value) {
            setPreference(KEY_USER_NAME, value)
        }
    var userPhone: String
        get() = sPreferences.getString(KEY_USER_PHONE, null) ?: ""
        set(value) {
            setPreference(KEY_USER_PHONE, value)
        }
    var mosqueDocumentId: String
        get() = sPreferences.getString(MOSQUE_DOCUMNET_ID_KEY, null) ?: ""
        set(value) {
            setPreference(MOSQUE_DOCUMNET_ID_KEY, value)
        }
    var mosqueImage: String
        get() = sPreferences.getString(MOSQUE_IMAGE_KEY, null) ?: ""
        set(value) {
            setPreference(MOSQUE_IMAGE_KEY, value)
        }
    var mosqueTime: String
        get() = sPreferences.getString(MOSQUE_TIME_KEY, null) ?: ""
        set(value) {
            setPreference(MOSQUE_TIME_KEY, value)
        }
    var mosqueInfo: String
        get() = sPreferences.getString(MOSQUE_INFO_KEY, null) ?: ""
        set(value) {
            setPreference(MOSQUE_INFO_KEY, value)
        }

    var mosqueActivated: Boolean
        get() = sPreferences.getBoolean(KEY_MOSQUE_ACTIVATED, false)
        set(value) {
            setPreference(KEY_MOSQUE_ACTIVATED, value)
        }

    private fun setPreference(key: String, value: String) {
        val editor = sPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun setPreference(key: String, value: Int) {
        val editor = sPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }


    private fun setPreference(key: String, value: Long) {
        val editor = sPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    private fun setPreference(key: String, state: Boolean) {
        val editor = sPreferences.edit()
        editor.putBoolean(key, state)
        editor.apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_FILE_NAME = "configs"
        private const val MOSQUE_DOCUMNET_ID_KEY = "moque_document_id"
        private const val MOSQUE_IMAGE_KEY = "moque_image"
        private const val MOSQUE_INFO_KEY = "moque_info"
        private const val MOSQUE_TIME_KEY = "mosque_time"

        const val KEY_MOSQUE_ACTIVATED = "mosque_activated_key"
        const val KEY_USER_ID = "user_id_key"
        const val KEY_USER_EMAIL = "user_email_key"
        const val KEY_USER_NAME = "user_name_key"
        const val KEY_USER_PHONE = "user_phone_key"
    }

    init {
        sPreferences = context.getSharedPreferences(
            SHARED_PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }
}