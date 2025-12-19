package com.example.shoeshop.data.model

import com.google.gson.annotations.SerializedName

data class SendOtpRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("type")
    val type: String = "recovery" // "recovery", "signup", "magiclink"
)