package com.example.halostad.ui.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.halostad.R
import com.example.halostad.ui.navigation.Screen
import com.example.halostad.utils.UiState
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // role: "user" atau "ustadz"
    var selectedRole by remember { mutableStateOf("user") }

    val context = LocalContext.current
    val uiState by viewModel.authState.collectAsState()

    val accentGreen = Color(0xFF2D8A5B)
    val goldYellow = Color(0xFFF4B000)
    val disabledGrey = Color(0xFFBDBDBD)

    val isFormValid =
        name.isNotBlank() && email.isNotBlank() && password.isNotBlank()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                Toast.makeText(context, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
                viewModel.resetState()
            }
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_hirup_logo),
                contentDescription = "Logo HaloStad",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // HaloStad (Hijau + Gold)
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = accentGreen)) {
                        append("Halo")
                    }
                    withStyle(SpanStyle(color = goldYellow)) {
                        append("Stad")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Buat akun untuk mulai bertanya dan berkonsultasi dengan ustadz.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // ==== FORM ====
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nama
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Nama lengkap") },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = accentGreen,
                        cursorColor = accentGreen,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = accentGreen,
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email") },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = accentGreen,
                        cursorColor = accentGreen,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = accentGreen,
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Kata sandi") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    androidx.compose.material.icons.Icons.Default.VisibilityOff
                                else
                                    androidx.compose.material.icons.Icons.Default.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = accentGreen,
                        cursorColor = accentGreen,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = accentGreen,
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTrailingIconColor = Color.Black,
                        focusedTrailingIconColor = accentGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ======= DAFTAR SEBAGAI (layout kayak gambar) =======
                Text(
                    text = "Bantu kami sesuaikan personalisasi Anda",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "Daftar Sebagai",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentGreen,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RoleCard(
                        title = "Jamaah",
                        isSelected = selectedRole == "user",
                        accentGreen = accentGreen,
                        imageRes = R.drawable.ic_role_user, // ganti dengan drawable kamu
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedRole = "user"
                    }

                    RoleCard(
                        title = "Ustadz",
                        isSelected = selectedRole == "ustadz",
                        accentGreen = accentGreen,
                        imageRes = R.drawable.ic_role_ustadz, // ganti dengan drawable kamu
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedRole = "ustadz"
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Daftar
                Button(
                    onClick = {
                        viewModel.register(name, email, password, selectedRole)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    enabled = isFormValid && uiState !is UiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentGreen,
                        contentColor = Color.White,
                        disabledContainerColor = disabledGrey,
                        disabledContentColor = Color.White
                    )
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Daftar",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sudah punya akun?
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Sudah punya akun? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    TextButton(
                        onClick = { navController.popBackStack() },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Masuk",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Kartu pilihan role (layout mirip contoh Perempuan / Laki-Laki)
 */
@Composable
private fun RoleCard(
    title: String,
    isSelected: Boolean,
    accentGreen: Color,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) accentGreen else Color(0xFFE0E0E0)
    val highlightBg = if (isSelected) accentGreen.copy(alpha = 0.18f) else Color(0xFFF7F7F7)

    Surface(
        modifier = modifier
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color.White, // card tetap putih
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor), // 🟢 edge keliatan
        shadowElevation = if (isSelected) 6.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // area gambar dengan background highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(highlightBg, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier.size(90.dp)
                )
            }

            // teks di bawah
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) accentGreen else Color.Black,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
