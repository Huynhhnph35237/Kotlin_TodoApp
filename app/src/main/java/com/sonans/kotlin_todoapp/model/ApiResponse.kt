package com.sonans.kotlin_todoapp.model

data class ApiResponse<T>(
    val status: Int,
    val messenger: String,
    val data: T
)
