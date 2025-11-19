package com.example.halostad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.halostad.ui.auth.LoginScreen
import com.example.halostad.ui.auth.RegisterScreen
import com.example.halostad.ui.home.HomeScreen
import com.example.halostad.ui.navigation.Screen
import com.example.halostad.ui.post.CreatePostScreen
import com.example.halostad.ui.post.FeedScreen
import com.example.halostad.ui.theme.HaloStadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HaloStadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HaloStadApp()
                }
            }
        }
    }
}

@Composable
fun HaloStadApp() {
    // 1. Inisialisasi NavController (Pengatur lalu lintas halaman)
    val navController = rememberNavController()

    // 2. Cek status login pengguna saat ini
    val currentUser = AppModule.authRepository.getCurrentUser()

    // 3. Tentukan halaman awal: Kalau sudah login -> Home, kalau belum -> Login
    val startDestination = if (currentUser != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    // 4. Definisi NavHost (Peta Navigasi)
    NavHost(navController = navController, startDestination = startDestination) {

        // Rute ke Halaman Login
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        // Rute ke Halaman Register
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // Rute ke Halaman Home
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // 2. Route Feed
        composable(route = Screen.Feed.route) {
            FeedScreen(navController = navController)
        }

        // 3. Route Create Post
        composable(route = Screen.CreatePost.route) {
            CreatePostScreen(navController = navController)
        }
    }
}