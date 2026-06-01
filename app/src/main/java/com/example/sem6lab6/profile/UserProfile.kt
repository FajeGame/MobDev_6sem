package com.example.sem6lab6.profile

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val fcmToken: String = "",
    val updatedAt: Long = 0L
)
