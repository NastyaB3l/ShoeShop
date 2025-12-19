package com.example.shoeshop.data.navigation

import EmailVerificationScreen
import RecoveryVerificationScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.shoeshop.ui.screens.CategoryProductsScreen
import com.example.shoeshop.ui.screens.ForgotPasswordScreen
import com.example.shoeshop.ui.screens.HomeScreen
import com.example.shoeshop.ui.screens.OnboardScreen
import com.example.shoeshop.ui.screens.RegisterAccountScreen
import com.example.shoeshop.ui.screens.SignInScreen
import com.example.shoeshop.ui.viewmodel.HomeViewModel

@Composable
fun NavigationApp(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "start_menu"
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

        composable("start_menu") {
            OnboardScreen (
                onGetStartedClick = { navController.navigate("register") },
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
                onSignUpClick = { navController.navigate("register") },
                onBackClick = { navController.popBackStack() }
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

        // home
        composable("home") { backStackEntry ->
            val homeViewModel: HomeViewModel = viewModel(backStackEntry)
            HomeScreen(
                homeViewModel = homeViewModel,
                onProductClick = { product ->
                    navController.navigate("product/${product.id}")
                },
                onCartClick = { /* ... */ },
                onSearchClick = { /* ... */ },
                onSettingsClick = { },
                onCategoryClick = { categoryName ->
                    navController.navigate("category/$categoryName")
                }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateToOtpVerification = { navController.navigate("reset_password") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("reset_password") {
            RecoveryVerificationScreen({},{}
            )
        }

        composable(
            route = "category/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""

            val homeBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            val homeViewModel: HomeViewModel = viewModel(homeBackStackEntry)

            CategoryProductsScreen(
                homeViewModel = homeViewModel,
                categoryName = categoryName,
                onProductClick = { product ->
                    navController.navigate("product/${product.id}")
                },
                onBackClick = { navController.popBackStack() },
                onCategorySelected = { newCategoryName ->
                    navController.navigate("category/$newCategoryName") {
                        popUpTo("category/{categoryName}") { inclusive = true }
                    }
                }
            )
        }
    }
}