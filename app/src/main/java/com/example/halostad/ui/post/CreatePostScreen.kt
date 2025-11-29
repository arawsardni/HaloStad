package com.example.halostad.ui.post

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    val categories = listOf(
        "Keyakinan",
        "Praktik Ibadah",
        "Sejarah Islam",
        "Kehidupan",
        "Filsafat",
        "Lainnya"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
        }

    val uploadState by viewModel.uploadState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UiState.Success -> {
                Toast.makeText(
                    context,
                    "Pertanyaan berhasil diposting!",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetUploadState()
                navController.popBackStack()
            }

            is UiState.Error -> {
                Toast.makeText(
                    context,
                    (uploadState as UiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetUploadState()
            }

            else -> {}
        }
    }

    val primaryGreen = Color(0xFF16A34A)
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Buat Pertanyaan",
                        fontWeight = FontWeight.SemiBold
                    )
                },
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
                .fillMaxSize()
                .background(bgColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // CARD UTAMA – mirip editor sosmed
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = surfaceColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    // Bar atas: avatar dummy + info kecil
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(primaryGreen.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "U",
                                color = primaryGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Tulis pertanyaanmu",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = onSurface
                            )
                            Text(
                                text = "Tetap sopan & jelas ya 🙂",
                                style = MaterialTheme.typography.labelSmall,
                                color = onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // KATEGORI – chips
                    Text(
                        text = "Kategori",
                        style = MaterialTheme.typography.labelMedium,
                        color = onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val selected = selectedCategory == cat
                            FilterChip(
                                selected = selected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) },
                                shape = MaterialTheme.shapes.large,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = surfaceVariant,
                                    labelColor = onSurface,
                                    selectedContainerColor = primaryGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // TEXTFIELD PERTANYAAN – flat, rounded
                    Text(
                        text = "Pertanyaan",
                        style = MaterialTheme.typography.labelMedium,
                        color = onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = question,
                        onValueChange = { question = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp),
                        placeholder = { Text("Tulis pertanyaanmu di sini...") },
                        maxLines = 8,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = surfaceVariant,
                            unfocusedContainerColor = surfaceVariant,
                            disabledContainerColor = surfaceVariant,
                            cursorColor = primaryGreen,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // AREA TAMBAH GAMBAR
                    Column {
                        Text(
                            text = "Lampiran (opsional)",
                            style = MaterialTheme.typography.labelMedium,
                            color = onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (imageUri == null) {
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        imagePickerLauncher.launch("image/*")
                                    },
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(primaryGreen.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = primaryGreen
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Tambah gambar",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Format JPG/PNG, 1 file",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(primaryGreen.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = primaryGreen
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "1 gambar terpilih",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = imageUri?.lastPathSegment ?: "",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = onSurface.copy(alpha = 0.7f),
                                            maxLines = 1
                                        )
                                    }
                                    IconButton(onClick = { imageUri = null }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Hapus gambar"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BUTTON POSTING
            val isLoading = uploadState is UiState.Loading
            val canPost = question.isNotBlank() && !isLoading

            Button(
                onClick = {
                    // nanti kalau VM support gambar: createPost(question, selectedCategory, imageUri)
                    viewModel.createPost(question, selectedCategory)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = canPost,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen,
                    disabledContainerColor = primaryGreen.copy(alpha = 0.4f)
                ),
                shape = MaterialTheme.shapes.large
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Posting Pertanyaan")
                }
            }
        }
    }
}
