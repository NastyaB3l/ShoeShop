package com.example.shoeshop.data.model

import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("updated_at")
    val updated_at: String
)