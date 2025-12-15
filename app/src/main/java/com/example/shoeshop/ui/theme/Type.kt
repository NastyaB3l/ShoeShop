package com.example.shoeshop.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
@Immutable
data class CustomTypography(
    // Heading styles
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,

    // Body styles
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle
)
// Set of Material typography styles to start with
val customTypography = CustomTypography(
    // Heading styles
    headlineLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),

    // Body styles
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)