package com.example.shoeshop.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoeshop.R
import com.example.shoeshop.ui.components.*
import com.example.shoeshop.ui.theme.ShoeShopTheme
import com.example.shoeshop.ui.viewmodel.SignUpViewModel // ИЗМЕНИТЕ
import com.example.shoeshop.ui.viewmodel.SignUpState // ИЗМЕНИТЕ

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAccountScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSignInClick: () -> Unit,
    onSignUpClick: (email: String) -> Unit,
    viewModel: SignUpViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }

    val signUpState by viewModel.signUpState.collectAsStateWithLifecycle()

    LaunchedEffect(signUpState) {
        when (signUpState) {
            is SignUpState.Success -> {
                // Передаем email при навигации
                onSignUpClick(email) // Измените эту строку
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 23.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.Register),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(id = R.string.details),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Показывать ошибку если есть
            if (signUpState is SignUpState.Error) {
                Text(
                    text = (signUpState as SignUpState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            CustomTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.Name),
                placeholder = "xxxxxxxx",
                modifier = Modifier.padding(bottom = 20.dp)
            )

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.Email),
                placeholder = "xyz@gmail.com",
                isEmail = true,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.Password),
                placeholder = "**********",
                isPassword = true,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            CustomCheckbox(
                isChecked = isChecked,
                onCheckedChange = { isChecked = it },
                text = stringResource(R.string.agree),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )

            RegistrationButton(
                onClick = {
                    val nameValid = name.isNotEmpty() && name.length >= 2
                    val emailValid = email.isNotEmpty() &&
                            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    val passwordValid = password.isNotEmpty() && password.length >= 6

                    if (nameValid && emailValid && passwordValid && isChecked) {
                        viewModel.signUp(name, email, password)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 16.sp
                            )
                        ) {
                            append(stringResource(R.string.Already) + " ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append(stringResource(R.string.SignIn))
                        }
                    },
                    modifier = Modifier.clickable { onSignInClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterAccountScreenPreview() {
    ShoeShopTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RegisterAccountScreen(
                onBackClick = {},
                onSignInClick = {},
                onSignUpClick = {}
            )
        }
    }
}