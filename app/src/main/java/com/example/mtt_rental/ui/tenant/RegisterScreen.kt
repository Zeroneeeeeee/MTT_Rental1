package com.example.mtt_rental.ui.tenant

import android.R
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.mtt_rental.model.User
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.viewmodel.tenant.RegisterViewModel
import com.example.mtt_rental.viewmodel.tenant.ValidationResult

@Preview(showBackground = true)
@Composable
fun RegisterScreen(
    toLogin: () -> Unit = {},
    toOTPVerification: (String, User) -> Unit = { _, _ -> },
    viewModel: RegisterViewModel = viewModel()
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var mobile by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("") }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading
    val validationResult by viewModel.validationResult

    // Handle validation result
    LaunchedEffect(validationResult) {
        val result = validationResult
        when (result) {
            is ValidationResult.Success -> {
                toOTPVerification(mobile, result.user)
                viewModel.clearValidationResult()
            }

            is ValidationResult.Error -> {
                usernameError = result.errors["username"]
                passwordError = result.errors["password"]
                emailError = result.errors["email"]
                mobileError = result.errors["mobile"]
            }

            null -> {
                // No result yet
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            "Create account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        // Username
        InputFieldRoundedWithIcon(
            value = username,
            onValueChange = { username = it; usernameError = null },
            label = "Username",
            icon = Icons.Default.Person,
            errorText = usernameError
        )
        Spacer(Modifier.height(4.dp))
        // Password
        InputFieldRoundedWithIcon(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = "Password",
            icon = Icons.Default.Lock,
            isPassword = true,
            errorText = passwordError
        )
        Spacer(Modifier.height(4.dp))
        // Email
        InputFieldRoundedWithIcon(
            value = email,
            onValueChange = { email = it; emailError = null },
            label = "E-mail",
            icon = Icons.Default.Email,
            inputKeyboardType = KeyboardType.Email,
            errorText = emailError
        )
        Spacer(Modifier.height(4.dp))
        // Mobile
        InputFieldRoundedWithIcon(
            value = mobile,
            onValueChange = { mobile = it; mobileError = null },
            label = "Mobile (with country code, e.g. +84)",
            icon = Icons.Default.Phone,
            inputKeyboardType = KeyboardType.Phone,
            errorText = mobileError
        )
        Spacer(Modifier.height(4.dp))
        RolePicker({role = it})
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Create", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = {
                    usernameError = null; passwordError = null; emailError = null; mobileError = null
                    viewModel.validateRegistration(
                        username = username,
                        password = password,
                        email = email,
                        mobile = mobile,
                        role = role
                    )
                },
                shape = CircleShape,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Go")
            }
        }
        Spacer(Modifier.height(16.dp))
        // Social login
        Spacer(Modifier.height(32.dp))
        Text(
            "Or create account using social media",
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SocialButtonCircle(iconRes = R.drawable.ic_menu_share) // fake facebook
            Spacer(Modifier.width(16.dp))
            SocialButtonCircle(iconRes = R.drawable.ic_menu_send) // fake twitter
            Spacer(Modifier.width(16.dp))
            SocialButtonCircle(iconRes = R.drawable.ic_input_add) // fake google
        }
        Spacer(Modifier.height(32.dp))
        TextButton(
            onClick = toLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already have an account? Login")
        }
    }
}

@Composable
private fun InputFieldRoundedWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    inputKeyboardType: KeyboardType = KeyboardType.Text,
    errorText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = label) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(50),
        isError = errorText != null,
        supportingText = {
            if (errorText != null) Text(errorText, color = Color.Red)
        },
        modifier = Modifier
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = inputKeyboardType,
            autoCorrectEnabled = false
        )
    )
}

@Composable
private fun SocialButtonCircle(iconRes: Int) {
    Button(
        onClick = {},
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = Modifier.size(48.dp),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun RolePicker(getRole: (String) -> Unit){
    val options = listOf("User", "Manager")
    var selectedOption by remember { mutableStateOf(options[0]) }
    LaunchedEffect(selectedOption) {
        getRole(selectedOption)
        Log.d("RolePicker", "Selected option: $selectedOption")
    }
    Row {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        selectedOption = option
                    } // cho phép click vào cả hàng
            ) {
                RadioButton(
                    selected = (selectedOption == option),
                    onClick = { selectedOption = option }
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
