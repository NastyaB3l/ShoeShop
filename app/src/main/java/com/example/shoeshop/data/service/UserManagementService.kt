package com.example.shoeshop.data.service

import com.example.shoeshop.data.model.SignUpRequest
import com.example.shoeshop.data.model.SignUpResponse
import com.example.shoeshop.data.model.VerifyOtpRequest
import com.example.shoeshop.data.model.VerifyOtpResponse
import com.example.shoeshop.data.model.VerifyRecoveryResponse
import com.example.shoeshop.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserManagementService {
    // Уберите @Headers - они уже в RetrofitInstance
    @POST("auth/v1/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<SignInResponse>

    @POST("auth/v1/verify")
    suspend fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): Response<VerifyOtpResponse>

    // Убедитесь что endpoint существует!
    @POST("auth/v1/recover")
    suspend fun verifyRecoveryOtp(@Body request: VerifyOtpRequest): Response<VerifyRecoveryResponse>

    @POST("auth/v1/recover")
    suspend fun recoverPassword(
        @Body forgotPasswordRequest: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @PUT("auth/v1/user")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<ChangePasswordResponse>
}