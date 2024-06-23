package com.sonans.kotlin_todoapp

import HomeScreen
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sonans.kotlin_todoapp.screen.AddTodoScreen
import com.sonans.kotlin_todoapp.screen.DetailScreen

import com.sonans.kotlin_todoapp.screen.LoginScreen
import com.sonans.kotlin_todoapp.screen.SignUpScreen
import com.sonans.kotlin_todoapp.screen.UpdateTodoScreen
import com.sonans.kotlin_todoapp.screen.WelcomeScreen
import com.sonans.kotlin_todoapp.ui.theme.Kotlin_TodoAppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "welcome") {
                composable("home") {
                    HomeScreen(navController = navController)
                }
                composable("detail") {
                    DetailScreen(navController = navController)
                }

                composable("login") {
                    LoginScreen(navController = navController)
                }
                composable("signup") {
                    SignUpScreen(navController = navController)
                }

                composable("welcome") {
                    WelcomeScreen(navController = navController)
                }
                composable("add") {
                    AddTodoScreen(navController = navController)
                }

                composable("update/{todoId}") { backStackEntry ->
                    val todoId = backStackEntry.arguments?.getString("todoId")
                    UpdateTodoScreen(navController, todoId)
                }

            }
        }
    }
}
