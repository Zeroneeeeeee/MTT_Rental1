package com.example.mtt_rental.ui.tenant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.viewmodel.tenant.LoginResult
import com.example.mtt_rental.viewmodel.tenant.LoginViewModel

@Preview(showBackground = true)
@Composable
fun LoginScreen(
    toRegister: () -> Unit = {},
    toUserScreen: () -> Unit = {},
    toManagerScreen: () -> Unit = {}
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val viewModel: LoginViewModel = viewModel()
    val isLoading by viewModel.isLoading
    val loginResult by viewModel.loginResult

    // Handle login result
    LaunchedEffect(loginResult) {
        val result = loginResult
        when (result) {
            is LoginResult.Success -> {
                when (result.user.userType) {
                    "User" -> toUserScreen()
                    "Manager" -> toManagerScreen()
                }
                viewModel.clearLoginResult()
            }

            is LoginResult.Error -> {
                // Handle specific field errors
                when {
                    result.message.contains("username") -> {
                        usernameError = result.message
                    }

                    result.message.contains("password") -> {
                        passwordError = result.message
                    }

                    else -> {
                        usernameError = result.message
                    }
                }
            }

            null -> {
                // No result yet
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(68.dp))
        Text(
            text = "Hello",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Sign in to your account",
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(30.dp))
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = null
                viewModel.clearLoginResult()
            },
            label = { Text("Username") },
            isError = usernameError != null,
            supportingText = {
                if (usernameError != null) Text(usernameError!!, color = Color.Red)
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                viewModel.clearLoginResult()
            },
            label = { Text("Password") },
            singleLine = true,
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) Text(passwordError!!, color = Color.Red)
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Forgot your password?",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        )
        Spacer(Modifier.height(34.dp))
        Button(
            onClick = {
                usernameError = null
                passwordError = null
                viewModel.login(username, password)
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Sign in", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Don't have an account?",
                color = Color.Gray,
                fontSize = 13.sp
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "Create",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.Blue,
                modifier = Modifier.clickable(onClick = toRegister)
            )
        }
        Spacer(Modifier.height(18.dp))
    }
}
