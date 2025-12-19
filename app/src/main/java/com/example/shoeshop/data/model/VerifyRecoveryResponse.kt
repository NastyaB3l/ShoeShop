package com.example.shoeshop.data.model

data class VerifyRecoveryResponse(
    val success: Boolean,
    val message: String,
    val reset_token: String? = null // токен для сброса пароля
)