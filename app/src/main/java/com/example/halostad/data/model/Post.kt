package com.example.halostad.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val question: String = "",
    val category: String = "", // Sejarah, Ibadah, dll.
    val timestamp: Timestamp = Timestamp.now(),

    // Bagian Jawaban Ustadz
    @get:PropertyName("isAnswered")
    val isAnswered: Boolean = false,
    val answer: String? = null,
    val ustadzId: String? = null,
    val ustadzName: String? = null,
    val answeredAt: Timestamp? = null
)