package com.example.halostad

import android.os.Bundle
import android.util.Log // Tambahan untuk Logcat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.halostad.ui.theme.HaloStadTheme
// Tambahan Import Firebase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- KODE TEST FIREBASE (MULAI) ---
        try {
            // Kita coba panggil instance Auth.
            // Jika error, berarti setup google-services.json atau gradle salah.
            val auth = Firebase.auth

            // Mencetak pesan sukses ke Logcat
            Log.d("CEK_FIREBASE", "Sukses! Firebase Auth sudah aktif.")
            Log.d("CEK_FIREBASE", "Current User: ${auth.currentUser}") // Harusnya null karena belum login

        } catch (e: Exception) {
            Log.e("CEK_FIREBASE", "Error: Gagal inisialisasi Firebase", e)
        }
        // --- KODE TEST FIREBASE (SELESAI) ---

        setContent {
            HaloStadTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "HaloStad", // Ubah sedikit biar kelihatan bedanya
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HaloStadTheme {
        Greeting("HaloStad")
    }
}