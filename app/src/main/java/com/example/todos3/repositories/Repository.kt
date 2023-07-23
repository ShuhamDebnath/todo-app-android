package com.example.todos3.repositories

import com.example.todos3.interfaces.ApiService
import com.example.todos3.models.Todo
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response

class Repository(private val apiService: ApiService) {

    fun getAllUser(): Call<List<Todo>> {
        return apiService.getAllUser()
    }

    suspend fun getUserById(id: String): Response<Todo> {
        return apiService.getUserById(id)
    }

    suspend fun createUser(body: JsonObject): Response<JsonObject> {
        return apiService.createUser(body)
    }

    suspend fun updateUserById(
        id: String,
        body: JsonObject
    ): Response<JsonObject> {
        return apiService.updateUserById(id, body)
    }

    suspend fun deleteUserById(id: String): Response<JsonObject> {
        return apiService.deleteUserById(id)
    }
    suspend fun deleteAll(): Response<JsonObject> {
        return apiService.deleteAll()
    }
}