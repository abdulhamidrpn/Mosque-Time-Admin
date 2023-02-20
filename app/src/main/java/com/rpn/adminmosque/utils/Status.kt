package com.rpn.adminmosque.utils


data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(message: String? = null, data: T? = null) = Resource(Status.SUCCESS, data, message)

        fun <T> error(message: String? = null, data: T? = null) = Resource(Status.ERROR, data, message)

        fun <T> loading(message: String? = null, data: T? = null) = Resource(Status.LOADING, data, message)

    }

}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}