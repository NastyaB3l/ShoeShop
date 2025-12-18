package com.example.shoeshop.data.navigation

import EmailVerificationScreen
import RecoveryVerificationScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shoeshop.ui.screens.RegisterAccountScreen

@Composable
fun NavigationApp(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "register"
    ) {
        composable("register") {
            RegisterAccountScreen(
                onBackClick = { navController.popBackStack() },
                onSignInClick = { /* ... */ },
                onSignUpClick = { email -> // ИЗМЕНИТЕ: принимаем email
                    // Передаем email как параметр
                    navController.navigate("email_verification/$email")
                }
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
    }
}