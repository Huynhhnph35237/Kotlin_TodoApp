package com.sonans.kotlin_todoapp.api

import com.sonans.kotlin_todoapp.model.ApiResponse
import com.sonans.kotlin_todoapp.model.Todo
import com.sonans.kotlin_todoapp.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // user
    @POST("/api/signup-kotlin")
    fun signup(@Body usersKotlin: User): Call<ApiResponse<User>>
    @POST("/api/login-kotlin")
    fun login(@Body usersKotlin: User): Call<ApiResponse<User>>

    // todo
    @GET("/api/get-list-todo")
    fun getListTodo(): Call<ApiResponse<List<Todo>>>

    @POST("/api/add-todo")
    fun addTodo(@Body todo: Todo): Call<ApiResponse<Todo>>

    @DELETE("/api/destroy-todo-by-id/{id}")
    fun deleteTodo(@Path("id") todoId: String?): Call<ApiResponse<Todo>>

    @GET("/api/get-todo-by-id/{id}")
    fun getTodoById(
        @Path("id") todoId: String?
    ): Call<ApiResponse<Todo>>

    @PUT("/api/update-todo-by-id/{id}")
    fun updateTodo(
        @Path("id") todoId: String?,
        @Body todo: Todo?
    ): Call<ApiResponse<Todo>>

    @FormUrlEncoded
    @PUT("/api/update-status-todo/{id}")
    fun updateStatusTodo(
        @Path("id") todoId: String,
        @Field("status") status: Int
    ): Call<ApiResponse<Todo>>
}