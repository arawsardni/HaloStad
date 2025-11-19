package com.example.halostad.ui.home

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.halostad.AppModule
import com.example.halostad.ui.navigation.Screen
import com.example.halostad.utils.PrayerTimeHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    val jadwalSholat by viewModel.jadwalSholat.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUser = AppModule.authRepository.getCurrentUser()

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Efek awal: Ambil data saat pertama buka
    LaunchedEffect(key1 = locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.getUserLocationAndPrayerTimes(context)
        } else {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER DENGAN TOMBOL REFRESH ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Assalamualaikum,",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "User",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tombol Refresh
                IconButton(
                    onClick = {
                        if (locationPermissionsState.allPermissionsGranted) {
                            viewModel.getUserLocationAndPrayerTimes(context)
                        } else {
                            locationPermissionsState.launchMultiplePermissionRequest()
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh Lokasi")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- KARTU JADWAL SHOLAT ---
            if (locationPermissionsState.allPermissionsGranted) {
                if (jadwalSholat != null) {
                    PrayerTimeCard(jadwal = jadwalSholat!!)
                } else if (!isLoading) {
                    Text("Tekan tombol refresh untuk memuat data.")
                }
            } else {
                Text("Izin lokasi diperlukan.")
                Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                    Text("Berikan Izin")
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = {
                    AppModule.authRepository.logout()
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Keluar Akun")
            }
        }
    }
}

@Composable
fun PrayerTimeCard(jadwal: PrayerTimeHelper.JadwalSholat) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Jadwal Sholat Hari Ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // MENAMPILKAN LOKASI NYATA DI SINI
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "📍 ${jadwal.lokasi}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrayerTimeRow("Subuh", jadwal.subuh)
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
            PrayerTimeRow("Dzuhur", jadwal.dzuhur)
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
            PrayerTimeRow("Ashar", jadwal.ashar)
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
            PrayerTimeRow("Maghrib", jadwal.maghrib)
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
            PrayerTimeRow("Isya", jadwal.isya)
        }
    }
}

@Composable
fun PrayerTimeRow(name: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, style = MaterialTheme.typography.bodyLarge)
        Text(text = time, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}