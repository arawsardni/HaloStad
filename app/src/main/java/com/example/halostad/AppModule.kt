package com.example.halostad

import com.example.halostad.data.repository.AuthRepository
import com.example.halostad.data.repository.AuthRepositoryImpl
import com.example.halostad.data.repository.PostRepository // Import baru
import com.example.halostad.data.repository.PostRepositoryImpl // Import baru
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AppModule {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(auth, firestore) // Hapus parameter 'storage'
    }

    // --- TAMBAHAN UNTUK MILESTONE 2 ---
    val postRepository: PostRepository by lazy {
        PostRepositoryImpl(firestore)
    }


}