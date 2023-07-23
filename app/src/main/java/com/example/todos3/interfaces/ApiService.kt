package com.example.todos3.interfaces

import com.example.todos3.models.Todo
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    //http://localhost:5000/api/todo
    @GET("api/todo")
    fun getAllUser(): Call<List<Todo>>

    @GET("api/todo{id}")
    suspend fun getUserById(@Path("id") id: String): Response<Todo>

    @POST("api/todo")
    suspend fun createUser(@Body body: JsonObject): Response<JsonObject>

    @PATCH("api/todo/{id}")
    suspend fun updateUserById(@Path("id") id: String, @Body body: JsonObject): Response<JsonObject>

    @DELETE("api/todo/{id}")
    suspend fun deleteUserById(@Path("id") id: String): Response<JsonObject>

    @DELETE("api/todo")
    suspend fun deleteAll(): Response<JsonObject>
}