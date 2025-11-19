package com.example.halostad.ui.post

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.halostad.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: PostViewModel = viewModel()
) {
    var question by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // Untuk Dropdown

    // Daftar Kategori sesuai PRD
    val categories = listOf("Keyakinan", "Praktik Ibadah", "Sejarah Islam", "Kehidupan", "Filsafat", "Lainnya")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val uploadState by viewModel.uploadState.collectAsState()
    val context = LocalContext.current

    // Handle Efek Samping (Sukses/Gagal Upload)
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UiState.Success -> {
                Toast.makeText(context, "Pertanyaan berhasil diposting!", Toast.LENGTH_SHORT).show()
                viewModel.resetUploadState()
                navController.popBackStack() // Kembali ke Feed
            }
            is UiState.Error -> {
                Toast.makeText(context, (uploadState as UiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetUploadState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Pertanyaan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 1. Pilihan Kategori (Dropdown)
            Text("Kategori Pertanyaan", style = MaterialTheme.typography.labelLarge)
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Input Pertanyaan
            Text("Pertanyaan Kamu", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Lebih tinggi agar muat banyak teks
                placeholder = { Text("Tulis pertanyaanmu di sini...") },
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Tombol Kirim
            Button(
                onClick = { viewModel.createPost(question, selectedCategory) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uploadState !is UiState.Loading
            ) {
                if (uploadState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Posting Pertanyaan")
                }
            }
        }
    }
}