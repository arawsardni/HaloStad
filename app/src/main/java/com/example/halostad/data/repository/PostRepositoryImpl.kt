package com.example.halostad.data.repository

import com.example.halostad.data.model.Post
import com.example.halostad.utils.UiState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PostRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PostRepository {

    override suspend fun createPost(post: Post): Flow<UiState<Boolean>> = callbackFlow {
        trySend(UiState.Loading)
        try {
            // Simpan ke collection "posts". ID dokumen dibuat otomatis oleh Firestore jika post.id kosong
            // Tapi kita generate ID di sini agar rapi
            val docRef = firestore.collection("posts").document()
            val finalPost = post.copy(id = docRef.id) // Set ID dokumen ke objek Post

            docRef.set(finalPost).await()
            trySend(UiState.Success(true))
        } catch (e: Exception) {
            trySend(UiState.Error(e.localizedMessage ?: "Gagal memposting"))
        }
        awaitClose { }
    }

    override fun getAllPosts(): Flow<UiState<List<Post>>> = callbackFlow {
        trySend(UiState.Loading)

        // Mengambil data secara REALTIME
        val listener = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Urutkan dari yang terbaru
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(UiState.Error(error.localizedMessage ?: "Gagal memuat data"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.toObjects(Post::class.java)
                    trySend(UiState.Success(posts))
                }
            }

        // Menutup listener saat Flow tidak lagi digunakan (mencegah memory leak)
        awaitClose { listener.remove() }
    }

    override suspend fun answerPost(
        postId: String,
        answer: String,
        ustadzId: String,
        ustadzName: String
    ): Flow<UiState<Boolean>> = callbackFlow {
        trySend(UiState.Loading)
        try {
            val updates = mapOf(
                "answer" to answer,
                "ustadzId" to ustadzId,
                "ustadzName" to ustadzName,
                "isAnswered" to true,     // Status berubah jadi terjawab
                "answeredAt" to Timestamp.now()
            )

            firestore.collection("posts").document(postId).update(updates).await()
            trySend(UiState.Success(true))
        } catch (e: Exception) {
            trySend(UiState.Error(e.localizedMessage ?: "Gagal mengirim jawaban"))
        }
        awaitClose { }
    }
}