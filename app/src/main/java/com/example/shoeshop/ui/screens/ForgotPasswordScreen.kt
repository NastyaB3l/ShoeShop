package com.example.shoeshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoeshop.R
import com.example.shoeshop.ui.components.BackButton
import com.example.shoeshop.ui.components.PasswordResetAlertDialog
import com.example.shoeshop.ui.theme.customTypography
import com.example.shoeshop.ui.theme.ShoeShopTheme
import com.example.shoeshop.ui.viewmodel.ForgotPasswordViewModel
import com.example.shoeshop.ui.viewmodel.PasswordRecoveryState
import kotlinx.coroutines.launch
import kotlin.text.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNavigateToOtpVerification: (email: String) -> Unit = {},
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.passwordRecoveryState.collectAsState()
    val email by viewModel.email.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Состояние для отображения AlertDialog
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Обработка состояний
    LaunchedEffect(uiState) {
        when (uiState) {
            is PasswordRecoveryState.Success -> {
                showSuccessDialog = true
            }

            is PasswordRecoveryState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        (uiState as PasswordRecoveryState.Error).message,
                        withDismissAction = true
                    )
                }
            }

            else -> {}
        }
    }

    // Отображаем AlertDialog при успешной отправке
    if (showSuccessDialog && email.isNotEmpty()) {
        PasswordResetAlertDialog(
            onConfirm = {
                showSuccessDialog = false
                onNavigateToOtpVerification(email)
            },
            onDismiss = {
                showSuccessDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(23.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Кнопка назад
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                BackButton(
                    onClick = onBackClick
                )
            }

            // Заголовок
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.Forgot),
                    style = customTypography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.Enter),
                    style = customTypography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 54.dp)
                )
            }

            // Поле для ввода email
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.updateEmail(it) },
                placeholder = {
                    Text(
                        "example@mail.com",
                        style = customTypography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (email.isNotEmpty() && !isEmailValid)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.outline,
                    focusedBorderColor = if (isEmailValid)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textStyle = customTypography.labelMedium,
                singleLine = true,
                isError = email.isNotEmpty() && !isEmailValid,
                supportingText = {
                    if (email.isNotEmpty() && !isEmailValid) {
                        Text(
                            text = "Введите корректный email адрес",
                            color = MaterialTheme.colorScheme.error,
                            style = customTypography.displaySmall
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка отправки
            Button(
                onClick = { viewModel.recoverPassword() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isEmailValid && uiState !is PasswordRecoveryState.Loading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState is PasswordRecoveryState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.Send),
                        style = customTypography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ShoeShopTheme {
        ForgotPasswordScreen(
            onBackClick = {},
            onNavigateToOtpVerification = {}
        )
    }
}