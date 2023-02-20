package com.rpn.adminmosque.model

import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.Gson
import java.io.Serializable
import java.util.Date

data class MasjidInfo(
    @ServerTimestamp
    var creationDate: Date? = null,
    var activated: Boolean = true,//If false need to update in main admin panel to register new mosque
    var city: String = "",
    var country: String = "",
    var email: String = "",
    var image: String = "",
    var jumua: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var masjidName: String = "",
    var ownerName: String = "",
    var ownerUid: String = "",
    var phoneNumber: String = "",
    var documentId: String = "",
    var bottomMessage: String = "",
) : Serializable {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
