package com.sonans.kotlin_todoapp.model

data class Todo(
    val _id: String,
    val title: String,
    val content: String,
    val status: Int,
    val userId: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)
