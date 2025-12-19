package com.example.shoeshop.data.service

import com.example.shoeshop.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserManagementService {
    @POST("auth/v1/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<SignInResponse>

    @POST("auth/v1/verify")
    suspend fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): Response<SignInResponse> // Используем SignInResponse

    @POST("auth/v1/recover")
    suspend fun recoverPassword(
        @Body forgotPasswordRequest: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    // Должно быть что-то одно из этих:



    // Вариант 2: С reset_token в теле запроса
    @PUT("auth/v1/user")
    suspend fun changePassword(
        @Header("Authorization") token: String, // Bearer токен пользователя
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<ChangePasswordResponse>
}