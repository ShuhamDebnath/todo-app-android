package com.example.todos3.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
//    http://localhost:5000/api/todo
//    http://192.168.29.132:5000/api/todo
    private const val BASE_URL = "http://192.168.29.132:5000/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}