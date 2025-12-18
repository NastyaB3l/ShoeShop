package com.example.shoeshop.data.navigation

import EmailVerificationScreen
import RecoveryVerificationScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shoeshop.ui.screens.ForgotPasswordScreen
import com.example.shoeshop.ui.screens.RegisterAccountScreen
import com.example.shoeshop.ui.screens.SignInScreen

@Composable
fun NavigationApp(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "register"
    ) {
        composable("register") {
            RegisterAccountScreen(
                onBackClick = { navController.popBackStack() },
                onSignInClick = { navController.navigate("sign_in") },
                onSignUpClick = { email ->
                    navController.navigate("email_verification/$email")
                }
            )
        }

        composable("email_verification") {
            EmailVerificationScreen(
                email = "", // просто пустая строка
                onSignInClick = {
                    navController.navigate("sign_in")
                },
                onVerificationSuccess = {
                    navController.navigate("sign_in")
                }
            )
        }

        composable("sign_in") {
            SignInScreen(
                onForgotPasswordClick = { navController.navigate("forgot_password") },
                onSignInClick = { navController.navigate("home") },
                onSignUpClick = { navController.navigate("sign_up") }
            )
        }

        composable("email_verification/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                email = email, // Передаем email как параметр
                onSignInClick = {
                    navController.navigate("register")
                },
                onVerificationSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            //HomeScreen({},{},{})
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateToOtpVerification = { navController.navigate("reset_password") },
            )
        }
    }
}