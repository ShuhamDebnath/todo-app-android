package com.example.todos3.models

data class Todo(

    val _id: String?,
    val task: String,
    val priority: String?,
    val date: String?,
    var completed: Boolean = false,
    val __v : Int?,
)
