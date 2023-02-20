package com.rpn.adminmosque.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson

fun <T> LiveData<T>.observeOnce(onEmission: (T) -> Unit) {
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            onEmission(value)
            removeObserver(this)
        }
    }
    observeForever(observer)
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun <T> Collection<T>.filterNotIn(collection: Collection<T>): Collection<T> {
    val set = collection.toSet()
    return filterNot { set.contains(it) }
}

