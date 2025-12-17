// components/CustomTextField.kt
package com.example.shoeshop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoeshop.R
import com.example.shoeshop.ui.theme.LocalCustomTypography
import com.example.shoeshop.ui.theme.ShoeShopTheme

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isEmail: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    error: String? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = if (isEmail) KeyboardType.Email else KeyboardType.Text,
    onTrailingIconClick: (() -> Unit)? = null,
    trailingIconContent: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val customTypography = LocalCustomTypography.current

    Column(modifier = modifier) {
        // Label
        Text(
            text = label,
            style = customTypography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Text Field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    style = customTypography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (error != null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.outline,
                focusedBorderColor = if (error != null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error,
                errorCursorColor = MaterialTheme.colorScheme.error,
                errorTextColor = MaterialTheme.colorScheme.error
            ),
            textStyle = customTypography.labelMedium,
            singleLine = singleLine,
            enabled = enabled,
            readOnly = readOnly,
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(
                        text = error,
                        style = customTypography.displaySmall.copy(
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                }
            },
            trailingIcon = {
                if (isPassword) {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) {
                                    R.drawable.eye_close
                                } else {
                                    R.drawable.eye_open
                                }
                            ),
                            contentDescription = if (passwordVisible) {
                                "Скрыть пароль"
                            } else {
                                "Показать пароль"
                            },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (trailingIconContent != null) {
                    trailingIconContent()
                }
            }
        )
    }
}

@Composable
fun CustomTextFieldWithValidation(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isEmail: Boolean = false,
    isRequired: Boolean = false,
    validationRules: List<(String) -> String?> = emptyList(),
    enabled: Boolean = true
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(value) {
        if (value.isNotEmpty()) {
            errorMessage = null
            for (rule in validationRules) {
                val error = rule(value)
                if (error != null) {
                    errorMessage = error
                    break
                }
            }
        } else if (isRequired) {
            errorMessage = null
        }
    }

    CustomTextField(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
            // Сбрасываем ошибку при вводе
            if (errorMessage != null && newValue.isNotEmpty()) {
                errorMessage = null
            }
        },
        label = if (isRequired) "$label *" else label,
        placeholder = placeholder,
        modifier = modifier,
        isPassword = isPassword,
        isEmail = isEmail,
        enabled = enabled,
        error = errorMessage
    )
}
// components/CustomTextField.kt (только превью часть)
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CustomTextFieldPreview_Default() {
    ShoeShopTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Имя",
                placeholder = "Введите ваше имя"
            )

            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Email",
                placeholder = "email@example.com",
                isEmail = true
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CustomTextFieldPreview_WithValue() {
    ShoeShopTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = "Алексей",
                onValueChange = {},
                label = "Имя",
                placeholder = "Введите ваше имя"
            )

            CustomTextField(
                value = "alexey@example.com",
                onValueChange = {},
                label = "Email",
                placeholder = "email@example.com",
                isEmail = true
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CustomTextFieldPreview_Password() {
    ShoeShopTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = "password123",
                onValueChange = {},
                label = "Пароль",
                placeholder = "Введите пароль",
                isPassword = true
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CustomTextFieldPreview_WithError() {
    ShoeShopTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Имя",
                placeholder = "Введите ваше имя",
                error = "Это поле обязательно для заполнения"
            )

            CustomTextField(
                value = "неправильный email",
                onValueChange = {},
                label = "Email",
                placeholder = "email@example.com",
                isEmail = true,
                error = "Некорректный формат email"
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CustomTextFieldWithValidationPreview() {
    var text by remember { mutableStateOf("") }

    ShoeShopTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextFieldWithValidation(
                value = text,
                onValueChange = { text = it },
                label = "Email",
                placeholder = "email@example.com",
                isEmail = true,
                isRequired = true,
                validationRules = listOf(
                    { value ->
                        if (value.isEmpty()) "Пожалуйста, введите email адрес" else null
                    },
                    { value ->
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches())
                            "Введите корректный email адрес"
                        else null
                    }
                )
            )

            Text(
                text = "Введите некорректный email, чтобы увидеть ошибку",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, name = "All States Preview")
@Composable
fun CustomTextFieldPreview_AllStates() {
    ShoeShopTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Default state
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Текстовое поле",
                placeholder = "Введите текст"
            )

            // With value
            CustomTextField(
                value = "Текст в поле",
                onValueChange = {},
                label = "Текстовое поле",
                placeholder = "Введите текст"
            )

            // Email field
            CustomTextField(
                value = "user@email.com",
                onValueChange = {},
                label = "Email",
                placeholder = "email@example.com",
                isEmail = true
            )

            // Password field
            CustomTextField(
                value = "password123",
                onValueChange = {},
                label = "Пароль",
                placeholder = "Введите пароль",
                isPassword = true
            )

            // Error state
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Обязательное поле",
                placeholder = "Введите значение",
                error = "Это поле обязательно для заполнения"
            )

            // Disabled state
            CustomTextField(
                value = "Заблокированное значение",
                onValueChange = {},
                label = "Заблокированное поле",
                placeholder = "Недоступно",
                enabled = false
            )
        }
    }
}