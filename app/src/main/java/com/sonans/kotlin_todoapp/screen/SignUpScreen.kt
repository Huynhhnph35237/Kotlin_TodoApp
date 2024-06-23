package com.sonans.kotlin_todoapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sonans.kotlin_todoapp.api.ApiService
import com.sonans.kotlin_todoapp.api.RetrofitBuilder
import com.sonans.kotlin_todoapp.model.ApiResponse
import com.sonans.kotlin_todoapp.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignUpScreen(navController: NavController){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),

            )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),

            )
        Button(onClick = {
            val newUser = User(_id = "", userName = username, password = password)
            AddUser(newUser, navController) }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )) {
                Text(text = "Signup")
        }
    }
}

fun AddUser(user: User, navController: NavController) {
    val retrofitService = RetrofitBuilder.getClient().create(ApiService::class.java)
    retrofitService.signup(user).enqueue(object : Callback<ApiResponse<User>> {
        override fun onResponse(call: Call<ApiResponse<User>>, response: Response<ApiResponse<User>>) {
            if (response.isSuccessful) {
                navController.navigate("login")

            } else {
                // Handle error
            }
        }

        override fun onFailure(call: Call<ApiResponse<User>>, t: Throwable) {
            // Handle failure
        }
    })
}