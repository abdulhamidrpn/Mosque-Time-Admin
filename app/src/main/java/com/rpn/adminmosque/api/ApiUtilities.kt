package com.rpn.jardindekashmir.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {
//    val BASE_URL = "https://api.subhani.xyz/"
    val BASE_URL = "https://api.aladhan.com/v1/"
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}