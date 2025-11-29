package com.example.halostad.ui.post

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
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

// ----------------- Helper warna kategori -----------------

data class CategoryColor(
    val bg: Color,
    val fg: Color
)

fun getCategoryColor(category: String): CategoryColor {
    return when (category) {
        "Keyakinan" -> CategoryColor(
            bg = Color(0xFFE0F2FE),
            fg = Color(0xFF1D4ED8)
        )
        "Praktik Ibadah" -> CategoryColor(
            bg = Color(0xFFDCFCE7),
            fg = Color(0xFF15803D)
        )
        "Sejarah Islam" -> CategoryColor(
            bg = Color(0xFFFFF7ED),
            fg = Color(0xFFC05621)
        )
        "Kehidupan" -> CategoryColor(
            bg = Color(0xFFFCE7F3),
            fg = Color(0xFFBE185D)
        )
        "Filsafat" -> CategoryColor(
            bg = Color(0xFFE0F2F1),
            fg = Color(0xFF00695C)
        )
        else -> CategoryColor(
            bg = Color(0xFFE5E7EB),
            fg = Color(0xFF4B5563)
        )
    }
}

// ----------------- Feed Screen -----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: PostViewModel = viewModel()
) {
    val feedState by viewModel.filteredFeedState.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val showOnlyUnanswered by viewModel.showOnlyUnanswered.collectAsState()

    var showAnswerDialog by remember { mutableStateOf(false) }
    var postToAnswer by remember { mutableStateOf<Post?>(null) }
    var answerText by remember { mutableStateOf("") }

    val categories = listOf(
        "Semua",
        "Keyakinan",
        "Praktik Ibadah",
        "Sejarah Islam",
        "Kehidupan",
        "Filsafat"
    )

    val primaryGreen = Color(0xFF16A34A)

    Scaffold(
        floatingActionButton = {
            if (userRole == "user") {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreatePost.route) },
                    containerColor = primaryGreen,
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Buat pertanyaan")
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 0.dp)
            ) {

                // HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Text(
                            text = "Tanya Jawab",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Konsultasi seputar ibadah & kehidupan sehari-hari",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }

                // FILTER BAR
                if (userRole == "ustadz") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = showOnlyUnanswered,
                            onClick = { viewModel.setShowOnlyUnanswered(!showOnlyUnanswered) },
                            label = { Text("Hanya Belum Dijawab") },
                            leadingIcon = {
                                if (showOnlyUnanswered) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            shape = MaterialTheme.shapes.large,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                iconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                selectedContainerColor = MaterialTheme.colorScheme.surface,
                                selectedLabelColor = primaryGreen,
                                selectedLeadingIconColor = primaryGreen
                            )
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            val selected = selectedCategory == category
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.setCategoryFilter(category) },
                                label = {
                                    Text(
                                        category,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                shape = MaterialTheme.shapes.large,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                    selectedContainerColor = primaryGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f),
                    thickness = 1.dp
                )

                // LIST POST
                when (val state = feedState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = primaryGreen
                            )
                        }
                    }

                    is UiState.Success -> {
                        val posts = state.data
                        if (posts.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Belum ada pertanyaan di kategori ini.",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(posts) { post ->
                                    PostItem(
                                        post = post,
                                        userRole = userRole ?: "user",
                                        primaryGreen = primaryGreen,
                                        lightGreen = Color(0xFFE8F5E9),
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

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Terjadi kesalahan, coba lagi.",
                                color = Color(0xFFEF4444)
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }

        // DIALOG JAWAB (USTADZ)
        if (showAnswerDialog && postToAnswer != null) {
            AlertDialog(
                onDismissRequest = { showAnswerDialog = false },
                title = { Text("Jawab Pertanyaan") },
                text = {
                    Column {
                        Text(
                            "Pertanyaan:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "\"${postToAnswer!!.question}\"",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = answerText,
                            onValueChange = { answerText = it },
                            label = { Text("Jawaban Ustadz") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.answerPost(postToAnswer!!, answerText)
                            showAnswerDialog = false
                        }
                    ) {
                        Text("Kirim Jawaban")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAnswerDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

// ----------------- Card Post -----------------

@Composable
fun PostItem(
    post: Post,
    userRole: String,
    primaryGreen: Color,
    lightGreen: Color,
    onAnswerClick: () -> Unit
) {
    val date = post.timestamp.toDate()
    val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
    val catColor = getCategoryColor(post.category)

    // Card surface selalu sama, kalau answered cuma dikasih border hijau tipis
    val isAnswered = post.isAnswered

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = if (isAnswered)
            BorderStroke(1.dp, primaryGreen.copy(alpha = 0.25f))
        else
            null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            // Header kategori + tanggal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(
                    containerColor = catColor.bg,
                    contentColor = catColor.fg,
                    modifier = Modifier.clip(MaterialTheme.shapes.large)
                ) {
                    Text(
                        text = post.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = dateFormat.format(date),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pertanyaan
            Text(
                text = post.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Oleh: ${post.userName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isAnswered) {
                HorizontalDivider(color = primaryGreen.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = primaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dijawab oleh Ustadz ${post.ustadzName}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bubble jawaban: hijau lembut, sedikit masuk ke dalam (padding horizontal)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp) // biar gak nempel ke edge card
                        .background(
                            color = primaryGreen.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = post.answer.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                if (userRole == "ustadz") {
                    Button(
                        onClick = onAnswerClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryGreen
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("Jawab pertanyaan ini")
                    }
                } else {
                    Text(
                        text = "Belum ada jawaban.",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
