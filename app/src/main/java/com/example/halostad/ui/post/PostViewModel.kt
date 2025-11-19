package com.example.halostad.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halostad.AppModule
import com.example.halostad.data.model.Post
import com.example.halostad.utils.UiState
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PostViewModel : ViewModel() {

    private val repository = AppModule.postRepository
    private val authRepository = AppModule.authRepository

    // State untuk Daftar Postingan (Feed)
    private val _feedState = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
    private val _selectedCategory = MutableStateFlow("Semua") // Default: Semua Kategori
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _showOnlyUnanswered = MutableStateFlow(false) // Default: Tampilkan semua (dijawab & belum)
    val showOnlyUnanswered: StateFlow<Boolean> = _showOnlyUnanswered

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _uploadState = MutableStateFlow<UiState<Boolean>?>(null)
    val uploadState: StateFlow<UiState<Boolean>?> = _uploadState

    // --- BARU: Gabungkan Feed Asli dengan Filter ---
    // Logic ini otomatis jalan setiap kali feedState, kategori, atau status filter berubah
    val filteredFeedState: StateFlow<UiState<List<Post>>> = combine(
        _feedState,
        _selectedCategory,
        _showOnlyUnanswered
    ) { state, category, onlyUnanswered ->
        if (state is UiState.Success) {
            var posts = state.data

            // 1. Filter Kategori (Jika bukan "Semua")
            if (category != "Semua") {
                posts = posts.filter { it.category == category }
            }

            // 2. Filter Status (Jika Ustadz pilih "Belum Dijawab")
            if (onlyUnanswered) {
                posts = posts.filter { !it.isAnswered }
            }

            UiState.Success(posts)
        } else {
            state // Jika Loading/Error, kembalikan apa adanya
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    init {
        loadPosts()
        fetchUserRole()
    }

    // --- BARU: Fungsi Mengubah Filter ---
    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    fun setShowOnlyUnanswered(show: Boolean) {
        _showOnlyUnanswered.value = show
    }

    // Fungsi untuk mengecek Role dari Firestore
    private fun fetchUserRole() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            // Kita tembak langsung ke Firestore collection 'users'
            FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        _userRole.value = document.getString("role") ?: "user"
                    }
                }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            repository.getAllPosts().collect { state ->
                _feedState.value = state
            }
        }
    }

    fun createPost(question: String, category: String) {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            _uploadState.value = UiState.Error("Anda harus login terlebih dahulu")
            return
        }

        if (question.isBlank() || category.isBlank()) {
            _uploadState.value = UiState.Error("Pertanyaan dan Kategori tidak boleh kosong")
            return
        }

        val newPost = Post(
            userId = currentUser.uid,
            userName = currentUser.displayName ?: currentUser.email ?: "Hamba Allah",
            question = question,
            category = category,
            timestamp = Timestamp.now(),
            isAnswered = false
        )

        viewModelScope.launch {
            repository.createPost(newPost).collect { state ->
                _uploadState.value = state
            }
        }
    }

    // Fungsi untuk Ustadz Menjawab
    fun answerPost(post: Post, answer: String) {
        val currentUser = authRepository.getCurrentUser()
        // Validasi sederhana (Idealnya cek role di backend/firestore rules juga)
        if (currentUser == null) return

        if (answer.isBlank()) {
            // Handle error jika jawaban kosong (bisa tambahkan state khusus jawaban nanti)
            return
        }

        viewModelScope.launch {
            // Kita gunakan state upload yang sama untuk feedback loading
            repository.answerPost(
                postId = post.id,
                answer = answer,
                ustadzId = currentUser.uid,
                ustadzName = currentUser.displayName ?: "Ustadz"
            ).collect { state ->
                // Kita bisa gunakan _uploadState atau buat state baru _answerState
                // Untuk MVP, kita pakai log atau toast nanti di UI
            }
        }
    }

    fun resetUploadState() {
        _uploadState.value = null
    }
}