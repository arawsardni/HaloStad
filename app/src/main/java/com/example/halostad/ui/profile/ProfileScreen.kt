package com.example.halostad.ui.profile

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.halostad.AppModule
import com.example.halostad.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController) {
    val currentUser = AppModule.authRepository.getCurrentUser()

    var role by remember { mutableStateOf("Loading...") }
    var profileImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Ambil data Firestore
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    role = doc.getString("role")?.replaceFirstChar { it.uppercase() } ?: "User"

                    val base64String = doc.getString("photoBase64")
                    if (!base64String.isNullOrBlank()) {
                        try {
                            val cleanBase64 = base64String.substringAfter(",")
                            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            profileImageBitmap = bmp.asImageBitmap()
                        } catch (_: Exception) {}
                    }

                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    // ======================================================
    // ===============  TAMPILAN UI (DESAIN 2)  ==============
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // ------------------ Background Header ------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF2D8A5B), Color(0xFF16A34A))
                    ),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)

                ){
                    Text(
                        text = "Profil Saya",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = {navController.navigate(Screen.EditProfile.route)}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // FOTO PROFIL MERGE
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(3.dp, Color.White, CircleShape)
                ) {
                    when {
                        profileImageBitmap != null -> {
                            androidx.compose.foundation.Image(
                                bitmap = profileImageBitmap!!,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        else -> {
                            AsyncImage(
                                model = "https://ui-avatars.com/api/?name=${currentUser?.displayName ?: "User"}",
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // USERNAME
                Text(
                    text = currentUser?.displayName ?: "Nama Pengguna",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ------------------ Card Settings ------------------
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                ProfileSettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifikasi Adzan",
                    subtitle = "Pengingat waktu sholat"
                )

                Divider()

                ProfileSettingItem(
                    icon = Icons.Default.DarkMode,
                    title = "Mode Gelap",
                    subtitle = "Tampilan gelap"
                )

                Divider()

                ProfileSettingItem(
                    icon = Icons.Default.Bookmark,
                    title = "Artikel Tersimpan",
                    subtitle = "24 artikel",
                    showArrow = true
                )

                Divider()

                ProfileSettingItem(
                    icon = Icons.Default.Help,
                    title = "Riwayat Tanya Ustad",
                    subtitle = "12 pertanyaan",
                    showArrow = true
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ------------------ Logout Button ------------------
        Button(
            onClick = {
                AppModule.authRepository.logout()
                navController.navigate(Screen.Login.route) { popUpTo(0) }
            },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(2.dp, Color.Red),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Keluar dari Akun", color = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ProfileSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showArrow: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF4CAF50))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, fontSize = 13.sp, color = Color.Gray)
        }

        if (showArrow) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
