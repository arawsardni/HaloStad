package com.example.halostad.ui.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.halostad.data.model.Post
import com.example.halostad.ui.navigation.Screen
import com.example.halostad.utils.UiState
import java.text.SimpleDateFormat
import java.util.Locale
// ... imports (Pastikan tambah import FilterChip)
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: PostViewModel = viewModel()
) {
    // GANTI: Gunakan 'filteredFeedState' bukan 'feedState'
    val feedState by viewModel.filteredFeedState.collectAsState()

    val userRole by viewModel.userRole.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val showOnlyUnanswered by viewModel.showOnlyUnanswered.collectAsState()

    // State dialog (Sama seperti sebelumnya)
    var showAnswerDialog by remember { mutableStateOf(false) }
    var postToAnswer by remember { mutableStateOf<Post?>(null) }
    var answerText by remember { mutableStateOf("") }

    // Daftar Kategori Filter
    val categories = listOf("Semua", "Keyakinan", "Praktik Ibadah", "Sejarah Islam", "Kehidupan", "Filsafat")

    Scaffold(
        floatingActionButton = {
            if (userRole == "user") {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreatePost.route) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Buat Post")
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // 1. Header Judul
            Text(
                text = "Tanya Jawab",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )

            // 2. BAGIAN FILTER (BARU)
            if (userRole == "ustadz") {
                // --- FILTER KHUSUS USTADZ ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = showOnlyUnanswered,
                        onClick = { viewModel.setShowOnlyUnanswered(!showOnlyUnanswered) },
                        label = { Text("Hanya Belum Dijawab") },
                        leadingIcon = {
                            if (showOnlyUnanswered) Icon(Icons.Default.Check, contentDescription = null)
                        }
                    )
                }
            } else {
                // --- FILTER KHUSUS USER (Scrollable Row) ---
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { viewModel.setCategoryFilter(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.Gray.copy(alpha = 0.1f))

            // 3. List Postingan (SAMA SEPERTI SEBELUMNYA)
            when (val state = feedState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Success -> {
                    val posts = state.data
                    if (posts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tidak ada pertanyaan yang sesuai filter.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(posts) { post ->
                                PostItem(
                                    post = post,
                                    userRole = userRole ?: "user",
                                    onAnswerClick = {
                                        postToAnswer = post
                                        answerText = ""
                                        showAnswerDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> { /* Tampilkan Error */ }
                else -> {}
            }
        }

        // --- Dialog (SAMA SEPERTI SEBELUMNYA) ---
        if (showAnswerDialog && postToAnswer != null) {
            // ... (Paste kode AlertDialog yang lama di sini) ...
            AlertDialog(
                onDismissRequest = { showAnswerDialog = false },
                title = { Text("Jawab Pertanyaan") },
                text = {
                    Column {
                        Text("Pertanyaan: \"${postToAnswer!!.question}\"", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = answerText,
                            onValueChange = { answerText = it },
                            label = { Text("Jawaban Ustadz") },
                            modifier = Modifier.fillMaxWidth().height(150.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.answerPost(postToAnswer!!, answerText)
                        showAnswerDialog = false
                    }) { Text("Kirim Jawaban") }
                },
                dismissButton = {
                    TextButton(onClick = { showAnswerDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    userRole: String,
    onAnswerClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (post.isAnswered) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Post: Kategori & Tanggal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(post.category, modifier = Modifier.padding(4.dp))
                }
                // Format tanggal sederhana
                val date = post.timestamp.toDate()
                val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
                Text(dateFormat.format(date), style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Isi Pertanyaan
            Text(
                text = post.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Oleh: ${post.userName}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Logika Tampilan Jawaban
            if (post.isAnswered) {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dijawab oleh Ustadz ${post.ustadzName}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.answer ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                // Jika BELUM dijawab
                if (userRole == "ustadz") {
                    // Tombol Khusus Ustadz
                    Button(
                        onClick = onAnswerClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Jawab Pertanyaan Ini")
                    }
                } else {
                    // Tampilan untuk User biasa
                    Text(
                        text = "Belum ada jawaban.",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}