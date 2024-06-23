package com.sonans.kotlin_todoapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sonans.kotlin_todoapp.api.ApiService
import com.sonans.kotlin_todoapp.api.RetrofitBuilder
import com.sonans.kotlin_todoapp.model.ApiResponse
import com.sonans.kotlin_todoapp.model.Todo
import getUserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun AddTodoScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val context = LocalContext.current

    val userId = getUserId(context)

    Column (modifier = Modifier.padding(16.dp)){
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

        Button(
            onClick = {
                //sử dụng hàm getUserId
                if (userId != null) {
                    val newTodo = Todo(
                        _id = "",
                        title = title,
                        content = content,
                        status = 0,
                        userId = userId,
                        createdAt = "",
                        updatedAt = "",
                        __v = 0
                    )
                    addTodoToServer(newTodo, navController)
                } else {
                    // Handle the case where userId is null
                }
            },modifier = Modifier
                    .fillMaxWidth()
                .padding(bottom = 40.dp, start = 10.dp, end = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7392FF)
            )
        ) {
            Text("Add Todo")
        }
    }
}


// hàm addTodoToServer gửi todo mới lên máy chủ = cách use ApiService.
// phương thức addTodo trên ApiService, truyền đối tượng Todo mới.
// addTodo là một cuộc gọi bất đồng bộ => hàm sử dụng phương thức enqueue để xử lý phản hồi
fun addTodoToServer(todo: Todo, navController: NavController) {
    val retrofitService = RetrofitBuilder.getClient().create(ApiService::class.java)
    retrofitService.addTodo(todo).enqueue(object : Callback<ApiResponse<Todo>> {
        override fun onResponse(call: Call<ApiResponse<Todo>>, response: Response<ApiResponse<Todo>>) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
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
