package com.example.halostad.ui.profile

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.halostad.AppModule
import com.example.halostad.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController) {
    val currentUser = AppModule.authRepository.getCurrentUser()

    var role by remember { mutableStateOf("Memuat...") }
    // Kita simpan langsung sebagai ImageBitmap agar tidak perlu Coil lagi untuk Base64
    var profileImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Ambil Data dari Firestore
    LaunchedEffect(Unit) {
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)
                .get().addOnSuccessListener { document ->
                    role = document.getString("role")?.replaceFirstChar { it.uppercase() } ?: "User"

                    val base64String = document.getString("photoBase64")
                    if (!base64String.isNullOrBlank()) {
                        try {
                            // Hapus prefix "data:image/jpeg;base64," jika ada
                            val cleanBase64 = base64String.substringAfter(",")

                            // Decode Base64 ke ByteArray -> Bitmap -> ImageBitmap
                            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            profileImageBitmap = bitmap.asImageBitmap()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    isLoading = false
                }.addOnFailureListener {
                    isLoading = false
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

        // --- FOTO PROFIL ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(Color.LightGray) // Background loading
        ) {
            if (profileImageBitmap != null) {
                // Tampilkan Gambar dari Base64 Firestore
                androidx.compose.foundation.Image(
                    bitmap = profileImageBitmap!!,
                    contentDescription = "Foto Profil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback: Tampilkan Avatar Default (Coil)
                AsyncImage(
                    model = "https://ui-avatars.com/api/?name=${currentUser?.displayName ?: "User"}",
                    contentDescription = "Avatar Default",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        SuggestionChip(
            onClick = { },
            label = { Text(role) },
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Edit Profil
        Button(
            onClick = { navController.navigate(Screen.EditProfile.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profil")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Tombol Logout
        Button(
            onClick = {
                AppModule.authRepository.logout()
                navController.navigate(Screen.Login.route) { popUpTo(0) }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar Akun")
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}