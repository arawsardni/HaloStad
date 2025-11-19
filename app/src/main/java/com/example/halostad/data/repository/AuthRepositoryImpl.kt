package com.example.halostad.data.repository

import com.example.halostad.data.model.User
import com.example.halostad.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    // --- LOGIKA LOGIN ---
    override suspend fun login(email: String, pass: String): Flow<UiState<User>> = callbackFlow {
        try {
            trySend(UiState.Loading) // 1. Kirim status Loading

            // 2. Proses Login ke Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid

            if (uid != null) {
                // 3. Jika login auth sukses, AMBIL data detail user (role, nama, dll) dari Firestore
                val documentSnapshot = firestore.collection("users").document(uid).get().await()

                // Konversi dokumen Firestore menjadi objek User kita
                val user = documentSnapshot.toObject(User::class.java)

                if (user != null) {
                    trySend(UiState.Success(user)) // 4. Kirim Sukses beserta datanya
                } else {
                    trySend(UiState.Error("Data pengguna tidak ditemukan di database."))
                }
            } else {
                trySend(UiState.Error("Gagal mendapatkan ID Pengguna."))
            }

        } catch (e: Exception) {
            // Tangkap error (misal password salah, sinyal jelek)
            trySend(UiState.Error(e.localizedMessage ?: "Terjadi kesalahan tidak diketahui"))
        }
        awaitClose { }
    }

    // --- LOGIKA REGISTER ---
    override suspend fun register(name: String, email: String, pass: String, role: String): Flow<UiState<User>> = callbackFlow {
        try {
            trySend(UiState.Loading)

            // 1. Buat akun di Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid

            if (uid != null) {
                // 2. Siapkan data User untuk disimpan
                val newUser = User(
                    id = uid,
                    name = name,
                    email = email,
                    role = role, // 'user' atau 'ustadz'
                    photoUrl = "",
                    gender = "" // Bisa diupdate nanti di profil
                )

                // 3. Simpan data tersebut ke Firestore di koleksi 'users'
                firestore.collection("users").document(uid).set(newUser).await()

                // 4. Kirim Sukses
                trySend(UiState.Success(newUser))
            } else {
                trySend(UiState.Error("Gagal membuat User ID."))
            }

        } catch (e: Exception) {
            trySend(UiState.Error(e.localizedMessage ?: "Gagal mendaftar"))
        }
        awaitClose { }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUser() = auth.currentUser
}