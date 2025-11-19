package com.example.halostad

import com.example.halostad.data.repository.AuthRepository
import com.example.halostad.data.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AppModule {
    // Kita inisialisasi Auth & Firestore sekali saja di sini
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Repository siap pakai
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(auth, firestore)
    }
}