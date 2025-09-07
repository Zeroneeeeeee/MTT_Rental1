package com.example.mtt_rental.ui.tenant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.mtt_rental.repo.UserRepo
import com.example.mtt_rental.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Preview(showBackground = true)
@Composable
fun LoginScreen(
    toRegister: () -> Unit = {},
    toUserScreen: () -> Unit = {},
    toManagerScreen: () -> Unit = {}
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var loginMessage by rememberSaveable { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users")
    val userList = remember { mutableStateListOf<User>() }
    LaunchedEffect(Unit) {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) userList.add(user)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
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
            onValueChange = { username = it; usernameError = null },
            label = { Text("Username") },
            isError = usernameError != null,
            supportingText = {
                if (usernameError != null) Text(usernameError!!, color = Color.Red)
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = { Text("Password") },
            singleLine = true,
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) Text(passwordError!!, color = Color.Red)
            },
            modifier = Modifier
                .fillMaxWidth(),
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
                usernameError = null; passwordError = null
                var hasError = false
                if (username.isBlank()) {
                    usernameError = "Không được bỏ trống username"
                    hasError = true
                } else if (userList.all { it.idUser != username }) {
                    usernameError = "Username không tồn tại!"
                    hasError = true
                }
                if (password.isBlank()) {
                    passwordError = "Không được bỏ trống password"
                    hasError = true
                } else if (userList.any { it.idUser == username } && userList.first { it.idUser == username }.password != password) {
                    passwordError = "Sai password!"
                    hasError = true
                }
                loginMessage = if (hasError) "" else "Đăng nhập thành công!"
                if (!hasError) {
                    val user = userList.first { it.idUser == username }
                    UserRepo.updateUser(user.idUser,user.profileName,user.email,user.phoneNumber,user.userType,user.address)
                    when(userList.first { it.idUser == username }.userType){
                        "User" -> toUserScreen()
                        "Manager" -> toManagerScreen()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text("Sign in", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        if (loginMessage.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                loginMessage,
                modifier = Modifier.fillMaxWidth(),
                color = if (loginMessage == "Đăng nhập thành công!") Color(0xFF00A65A)
                else Color.Red,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Don't have an account?",
                color = Color.Gray,
                fontSize = 13.sp
            )
            Button(
                onClick = toRegister,
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.padding(start = 2.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
            ) {
                Text("Create", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
    }
}
