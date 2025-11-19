package com.example.halostad.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halostad.utils.PrayerTimeHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val _jadwalSholat = MutableStateFlow<PrayerTimeHelper.JadwalSholat?>(null)
    val jadwalSholat: StateFlow<PrayerTimeHelper.JadwalSholat?> = _jadwalSholat

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    @SuppressLint("MissingPermission")
    fun getUserLocationAndPrayerTimes(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // 1. Ambil Lokasi GPS
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                // Menggunakan Priority High Accuracy agar emulator cepat merespon perubahan
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                if (location != null) {
                    // 2. Ambil Nama Kota (Reverse Geocoding)
                    // Kita lakukan di background thread (IO) agar UI tidak macet
                    val namaKota = withContext(Dispatchers.IO) {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            // Ambil 1 hasil alamat terbaik
                            @Suppress("DEPRECATION") // Untuk kompatibilitas umum
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                            if (!addresses.isNullOrEmpty()) {
                                // Prioritas: Locality (Kota) -> SubAdminArea (Kabupaten) -> "Lokasi Terdeteksi"
                                addresses[0].locality ?: addresses[0].subAdminArea ?: "Lokasi Terdeteksi"
                            } else {
                                "Lokasi Tidak Diketahui"
                            }
                        } catch (e: Exception) {
                            "Gagal memuat nama kota"
                        }
                    }

                    // 3. Hitung Waktu Sholat
                    val jadwalRaw = PrayerTimeHelper.getPrayerTimes(location.latitude, location.longitude)

                    // 4. Update State dengan Nama Kota yang baru didapat
                    _jadwalSholat.value = jadwalRaw.copy(lokasi = namaKota)

                } else {
                    _errorMessage.value = "Gagal mendeteksi lokasi. Coba nyalakan GPS/Lokasi."
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}