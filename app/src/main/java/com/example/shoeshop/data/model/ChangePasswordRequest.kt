package com.example.shoeshop.data.model

import com.google.gson.annotations.SerializedName

// ChangePasswordRequest.kt
data class ChangePasswordRequest(
    @SerializedName("password")
    val password: String,

    @SerializedName("token")
    val token: String? = null,  // для JWT в теле

    @SerializedName("reset_token")
    val resetToken: String? = null // для reset token в теле
)