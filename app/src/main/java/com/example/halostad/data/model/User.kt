package com.example.halostad.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val role: String = "user", // Values: "user" atau "ustadz"
    val gender: String = ""
)