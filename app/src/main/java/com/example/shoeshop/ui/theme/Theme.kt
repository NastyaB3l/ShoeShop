package com.example.shoeshop.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.shoeshop.R

val LocalCustomTypography = staticCompositionLocalOf {
    customTypography
}
val Inter = FontFamily(
    Font(R.font.raleway, FontWeight.Normal),
    Font(R.font.raleway_bold, FontWeight.Bold),
    Font(R.font.raleway_medium, FontWeight.Medium),
    Font(R.font.raleway_semibold, FontWeight.SemiBold),
)

// Светлая цветовая схема
private val LightColorScheme = lightColorScheme(
    primary = Accent,
    secondary = Disable,
    surface = Block,
    background = Background,
    onBackground = Hint,
    onSurface = Text,
    onPrimary = Color.White,
    onSecondary = Color.White,
    error = Red,
    outline = SubtextDark
)

@Composable
fun ShoeShopTheme( content: @Composable () -> Unit
) {
    val customTypography = customTypography
    CompositionLocalProvider(
        LocalCustomTypography provides customTypography,
        content = content,
    )
}