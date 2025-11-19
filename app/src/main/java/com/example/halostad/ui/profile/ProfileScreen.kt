package com.example.halostad.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.halostad.AppModule
import com.example.halostad.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController) {
    val currentUser = AppModule.authRepository.getCurrentUser()
    var role by remember { mutableStateOf("Memuat...") }

    // Ambil Role dari Firestore untuk ditampilkan
    LaunchedEffect(Unit) {
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)
                .get().addOnSuccessListener {
                    role = it.getString("role")?.replaceFirstChar { char -> char.uppercase() } ?: "User"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profil Saya", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        // 1. Foto Profil (Icon Besar)
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Info User
        Text(
            text = currentUser?.displayName ?: "Nama Pengguna",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = currentUser?.email ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Badge Role
        SuggestionChip(
            onClick = { },
            label = { Text(role) },
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )

        Spacer(modifier = Modifier.weight(1f)) // Dorong tombol ke bawah

        // 3. Tombol Logout (Pindah dari Home)
        Button(
            onClick = {
                AppModule.authRepository.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar Akun")
        }

        // Tambahan Spacer agar tidak tertutup BottomBar
        Spacer(modifier = Modifier.height(80.dp))
    }
}