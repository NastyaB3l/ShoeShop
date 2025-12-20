package com.example.shoeshop.data.navigation

import EmailVerificationScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.shoeshop.ui.screens.CartScreen
import com.example.shoeshop.ui.screens.CategoryProductsScreen
import com.example.shoeshop.ui.screens.CreateNewPasswordScreen
import com.example.shoeshop.ui.screens.ForgotPasswordScreen
import com.example.shoeshop.ui.screens.HomeScreen
import com.example.shoeshop.ui.screens.OnboardScreen
import com.example.shoeshop.ui.screens.ProductDetailScreen
import com.example.shoeshop.ui.screens.RecoveryVerificationScreen
import com.example.shoeshop.ui.screens.RegisterAccountScreen
import com.example.shoeshop.ui.screens.SignInScreen
import com.example.shoeshop.ui.viewmodel.CartViewModel
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

        composable(
            route = "email_verification/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val emailArg = backStackEntry.arguments?.getString("email") ?: ""

            EmailVerificationScreen(
                email = emailArg,
                onSignInClick = { navController.navigate("sign_in") },
                onVerificationSuccess = { navController.navigate("home") }
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

        composable("forgot_password") {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToOtpVerification = { email ->
                    navController.navigate("recovery_verification/$email")
                }
            )
        }

        // NavigationApp.kt
        composable(
            route = "recovery_verification/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            RecoveryVerificationScreen(
                onSignInClick = {
                    // Просто переходим на вход - пароль уже сброшен
                    navController.navigate("sign_in") {
                        popUpTo("recovery_verification/{email}") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() },
                onResetPasswordClick = { accessToken ->
                    // Переходим на экран смены пароля
                    navController.navigate("create_new_password/$accessToken")
                }
            )
        }

        composable(
            "create_new_password/{token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")

            CreateNewPasswordScreen(
                userToken = token,
                onPasswordChanged = {
                    navController.navigate("sign_in") {
                        popUpTo("sign_in") { inclusive = false }
                    }
                }
            )
        }
        composable("reset_password") {
            RecoveryVerificationScreen(
                onSignInClick = { navController.navigate("sign_in") },
                onResetPasswordClick = { accessToken ->
                    navController.navigate("create_new_password/$accessToken")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("home") { backStackEntry ->
            val homeViewModel: HomeViewModel = viewModel(backStackEntry)
            HomeScreen(
                homeViewModel = homeViewModel,
                onProductClick = { product ->
                    navController.navigate("product/${product.id}")
                },
                onCartClick = { navController.navigate("cart") },
                onSearchClick = { /* ... */ },
                onSettingsClick = { },
                onCategoryClick = { categoryName ->
                    navController.navigate("category/$categoryName")
                }
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

        composable(
            route = "product/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""

            val homeBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            val homeViewModel: HomeViewModel = viewModel(homeBackStackEntry)

            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onAddToCart = { /* TODO */ },
                onToggleFavoriteInHome = { product ->
                    homeViewModel.toggleFavorite(product)
                }
            )
        }
        composable("cart") { backStackEntry ->
            val homeBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry("home")
            }
            val homeViewModel: HomeViewModel = viewModel(homeBackStackEntry)
            val cartViewModel: CartViewModel = viewModel(backStackEntry)

            val state by cartViewModel.uiState.collectAsStateWithLifecycle()

            CartScreen(
                items = state.items,
                isLoading = state.isLoading,
                onBackClick = { navController.popBackStack() },
                onIncrement = { item ->
                    cartViewModel.increment(item)
                    homeViewModel.refreshCartFlags()
                },
                onDecrement = { item ->
                    cartViewModel.decrement(item)
                    homeViewModel.refreshCartFlags()
                },
                onRemove = { item ->
                    cartViewModel.remove(item)
                    homeViewModel.refreshCartFlags()
                },
                onCheckoutClick = {
                    navController.navigate("checkout")
                }
            )
        }
    }
}