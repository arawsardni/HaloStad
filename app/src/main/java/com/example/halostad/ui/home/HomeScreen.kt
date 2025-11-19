package com.example.halostad.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.halostad.AppModule
import com.example.halostad.ui.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    // Mengambil user yang sedang login
    val currentUser = AppModule.authRepository.getCurrentUser()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Selamat Datang di HaloStad!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Login sebagai: ${currentUser?.email}")

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Fitur Waktu Sholat akan hadir di sini (Milestone selanjutnya)", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Logout (Penting untuk testing)
        Button(onClick = {
            AppModule.authRepository.logout()
            // Kembali ke Login dan hapus semua history halaman (agar tidak bisa di-back)
            navController.navigate(Screen.Login.route) {
                popUpTo(0) // Menghapus semua stack
            }
        }) {
            Text("Keluar / Logout")
        }
    }
}