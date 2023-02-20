package com.rpn.adminmosque.utils

open class Event<out T>(private val data: T) {
    var hasBeenHandeled = false
        private set

    fun getContentIfNotHandeld(): T? {
        return if (hasBeenHandeled) {
            null
        } else {
            hasBeenHandeled = true
            data
        }
    }

    fun peekContent() = data

}