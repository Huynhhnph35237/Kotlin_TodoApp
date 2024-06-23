package com.sonans.kotlin_todoapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sonans.kotlin_todoapp.api.ApiService
import com.sonans.kotlin_todoapp.api.RetrofitBuilder
import com.sonans.kotlin_todoapp.model.ApiResponse
import com.sonans.kotlin_todoapp.model.Todo
import getUserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun UpdateTodoScreen(navController: NavController, todoId: String?) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val userId = getUserId(context)

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(todoId) {
   // Nếu todoId không phải là null, nó sẽ gọi API để lấy thông tin chi tiết của công việc & cập nhật các trạng thái title và content.
        if (todoId != null) {
            coroutineScope.launch(Dispatchers.IO) {
                val retrofitService = RetrofitBuilder.getClient().create(ApiService::class.java)
                retrofitService.getTodoById(todoId).enqueue(object :
                    Callback<ApiResponse<Todo>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Todo>>,
                        response: Response<ApiResponse<Todo>>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && responseBody.status == 200) {
                                val todo = responseBody.data
                                title = todo.title
                                content = todo.content
                            } else {
                                // Handle error
                            }
                        } else {
                            // Handle error
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Todo>>, t: Throwable) {
                        // Handle failure
                    }
                })
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(text = "Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text(text = "Content") },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = {
                    if (userId != null && todoId != null) {
                        val newTodo = Todo(
                            _id = todoId,
                            title = title,
                            content = content,
                            status = 0,
                            userId = userId,
                            createdAt = "",
                            updatedAt = "",
                            __v = 0
                        )
                        updateTodoToServer(newTodo, navController, todoId)
                    } else {
                        // Handle the case where userId or todoId is null
                    }
                }
            ) {
                Text("Update Todo")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { deleteTodoToServer(navController, todoId) }) {
                Text(text = "Delete")
            }
        }
    }
}

fun updateTodoToServer(todo: Todo, navController: NavController, todoId: String?) {
    val retrofitService = RetrofitBuilder.getClient().create(ApiService::class.java)
    retrofitService.updateTodo(todoId ?: "", todo).enqueue(object : Callback<ApiResponse<Todo>> {
        override fun onResponse(call: Call<ApiResponse<Todo>>, response: Response<ApiResponse<Todo>>) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.status == 200) {
                    navController.navigate("home")
                } else {
                    // Handle failed response
                }
            } else {
                // Handle error
            }
        }

        override fun onFailure(call: Call<ApiResponse<Todo>>, t: Throwable) {
            // Handle failure
        }
    })
}


fun deleteTodoToServer( navController: NavController, todoId: String?) {
    val retrofitService = RetrofitBuilder.getClient().create(ApiService::class.java)
    retrofitService.deleteTodo(todoId ?: "").enqueue(object : Callback<ApiResponse<Todo>> {
        override fun onResponse(call: Call<ApiResponse<Todo>>, response: Response<ApiResponse<Todo>>) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.status == 200) {
                    navController.navigate("home")
                } else {
                    // Handle failed response
                }
            } else {
                // Handle error
            }
        }

        override fun onFailure(call: Call<ApiResponse<Todo>>, t: Throwable) {
            // Handle failure
        }
    })
}
