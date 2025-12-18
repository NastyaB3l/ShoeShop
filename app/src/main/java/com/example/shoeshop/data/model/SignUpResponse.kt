// data/model/SignUpResponse.kt
package com.example.myfirstproject.data.model

data class SignUpResponse(
    val id: String,
    val name: String,
    val email: String,
    val token: String? = null // если используется JWT
)