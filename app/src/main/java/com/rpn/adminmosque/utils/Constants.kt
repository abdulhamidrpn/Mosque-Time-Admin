package com.rpn.adminmosque.utils

import android.content.Context
import android.widget.Toast

fun Context.toast(msg: String) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}


object Constants {

    const val KEY_COLLECTION_IMAGE_STORAGE = "IMAGE_STORAGE"

}
