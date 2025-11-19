package com.example.halostad.data.repository

import com.example.halostad.data.model.Post
import com.example.halostad.utils.UiState
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    // User: Membuat pertanyaan baru
    suspend fun createPost(post: Post): Flow<UiState<Boolean>>

    // Semua: Mengambil semua postingan (Realtime)
    fun getAllPosts(): Flow<UiState<List<Post>>>

    // Ustadz: Mengirim jawaban
    suspend fun answerPost(postId: String, answer: String, ustadzId: String, ustadzName: String): Flow<UiState<Boolean>>
}