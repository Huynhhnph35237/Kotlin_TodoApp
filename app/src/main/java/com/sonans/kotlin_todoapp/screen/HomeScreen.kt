import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sonans.kotlin_todoapp.api.ApiService
import com.sonans.kotlin_todoapp.api.RetrofitBuilder
import com.sonans.kotlin_todoapp.model.ApiResponse
import com.sonans.kotlin_todoapp.model.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class) // Accept the experimental API
@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val todos = remember { mutableStateOf(listOf<Todo>()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val userId = getUserId(context)
    var searchText by remember { mutableStateOf(TextFieldValue()) } // State for storing search text

    LaunchedEffect(Unit) {
        // nếu userId không phải là null, nó sẽ gọi API để lấy danh sách các công việc từ máy chủ và lọc ra các công việc của người dùng hiện tại
        if (userId != null) {
            coroutineScope.launch(Dispatchers.IO) {
                val retrofitService = RetrofitBuilder.getClient().create(ApiService::class.java)
                retrofitService.getListTodo().enqueue(object : Callback<ApiResponse<List<Todo>>> {
                    override fun onResponse(call: Call<ApiResponse<List<Todo>>>, response: Response<ApiResponse<List<Todo>>>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && responseBody.status == 200) {
                                val currentDate = LocalDate.now()
                                val filteredTodos = responseBody.data.filter {
                                    it.userId == userId
                                }
                                todos.value = filteredTodos
                            } else {
                                // Handle failed response
                                //Nếu không có userId, sẽ hiển thị thông báo lỗi.
                            }
                        } else {
                            // Handle unsuccessful response
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<List<Todo>>>, t: Throwable) {
                        Toast.makeText(context, "no connection", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Toast.makeText(context, "khong ton tai userId", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F9FF))
            .padding(16.dp)
    ) {
        // Search TextField
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Todo") }, // Label for TextField
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .background(Color.White, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            val filteredTodos = todos.value.filter {
                it.title.contains(searchText.text, ignoreCase = true) // Filter todos based on searchText
            }
            items(filteredTodos) { todo ->
                ItemTodo(
                    title = todo.title,
                    status = todo.status,
                    idTodo = todo._id,
                    navController = navController
                )
            }
        }

        Button(
            onClick = { navController.navigate("add") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7392FF)
            )
        ) {
            Text(text = "Add")
        }

    }
}


// lấy user_id từ SharedPreferences
fun getUserId(context: Context): String? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("user_id", null)
}

@OptIn(ExperimentalMaterial3Api::class) // Accept the experimental API
@Composable
fun ItemTodo(title: String, status: Int, idTodo: String, modifier: Modifier = Modifier, navController: NavController) {
    var isChecked by remember { mutableStateOf(status == 1) }

    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { navController.navigate("update/${idTodo}") }
            .padding(8.dp)
            .background(if (isChecked) Color(0xFFCADFFF) else Color.White, RoundedCornerShape(8.dp)), // Rounded corners and background color based on isChecked
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = if (isChecked) Color.Blue else Color.Black,
            modifier = Modifier
                .padding(10.dp, 0.dp)
                .weight(1f)
                .padding(start = 8.dp),
            maxLines = 1, // Display only on one line
            overflow = TextOverflow.Ellipsis // Display "..." when exceeding one line
        )

        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                val newStatus = if (it) 1 else 0
                coroutineScope.launch {
                    updateStatus(idTodo, newStatus)
                }
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Blue, // Custom blue color when checked
                uncheckedColor = Color.Gray, // Default color when unchecked
                checkmarkColor = Color.White // Color of the checkmark
            )
        )

    }
}

fun deleteTodo(idTodo: String) {
    val retrofitService = RetrofitBuilder.getClient().create(ApiService::class.java)
    retrofitService.deleteTodo(idTodo).enqueue(object : Callback<ApiResponse<Todo>> {
        override fun onResponse(call: Call<ApiResponse<Todo>>, response: Response<ApiResponse<Todo>>) {
            if (response.isSuccessful) {
                // Handle successful deletion
            } else {
                // Handle unsuccessful response
            }
        }

        override fun onFailure(call: Call<ApiResponse<Todo>>, t: Throwable) {
            // Handle failure
        }
    })
}

fun updateStatus(idTodo: String, status: Int) {
    val retrofitService = RetrofitBuilder.getClient().create(ApiService::class.java)
    retrofitService.updateStatusTodo(idTodo, status).enqueue(object : Callback<ApiResponse<Todo>> {
        override fun onResponse(call: Call<ApiResponse<Todo>>, response: Response<ApiResponse<Todo>>) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.status == 200) {
                    // Successfully updated status
                } else {
                    // Handle failed update response
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
