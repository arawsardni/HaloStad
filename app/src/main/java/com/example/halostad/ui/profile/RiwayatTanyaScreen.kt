package com.example.halostad.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RiwayatTanyaScreen(
    navController: NavController
) {
    val dummyRiwayat = remember {
        listOf(
            RiwayatTanyaItem(
                id = "1",
                ustadName = "Ust. Ahmad Fikri",
                question = "Ustadz, bagaimana hukumnya kalau sering terlambat sholat karena ketiduran?",
                date = "12 Nov 2025 • 21.13",
                status = TanyaStatus.TERJAWAB,
                category = "Fiqih Ibadah"
            ),
            RiwayatTanyaItem(
                id = "2",
                ustadName = "Ust. Rifqi An-Nasr",
                question = "Kalau lagi down terus malas ibadah, sebaiknya mulai dari mana dulu ustadz?",
                date = "10 Nov 2025 • 09.40",
                status = TanyaStatus.TERJAWAB,
                category = "Motivasi & Hati"
            ),
            RiwayatTanyaItem(
                id = "3",
                ustadName = "Ust. Hasan",
                question = "Apakah boleh qadha puasa Ramadhan digabung dengan puasa sunnah Senin Kamis?",
                date = "8 Nov 2025 • 15.20",
                status = TanyaStatus.MENUNGGU,
                category = "Puasa"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // --------- HEADER SIMPLE TANPA BOX HIJAU ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFF16A34A),        // cuma icon yang hijau
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Riwayat Tanya Ustad",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A)            // judul hijau
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Lihat kembali pertanyaan yang pernah kamu kirim",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }


        // --------- LIST CARD RIWAYAT ----------
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dummyRiwayat) { item ->
                RiwayatTanyaCard(item = item)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ==================== CARD & MODEL ====================

@Composable
private fun RiwayatTanyaCard(
    item: RiwayatTanyaItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top row: Ustad + status chip
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.ustadName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusChip(status = item.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question text
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.ChatBubbleOutline,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = item.question,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private enum class TanyaStatus {
    TERJAWAB,
    MENUNGGU
}

private data class RiwayatTanyaItem(
    val id: String,
    val ustadName: String,
    val question: String,
    val date: String,
    val status: TanyaStatus,
    val category: String
)

@Composable
private fun StatusChip(status: TanyaStatus) {
    val (bgColor, textColor, label) = when (status) {
        TanyaStatus.TERJAWAB -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "Sudah dijawab"
        )
        TanyaStatus.MENUNGGU -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFEF6C00),
            "Menunggu jawaban"
        )
    }

    Box(
        modifier = Modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
