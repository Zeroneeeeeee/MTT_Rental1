package com.example.mtt_rental.ui.tenant.mainscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mtt_rental.R

@Preview(showBackground = true)
@Composable
fun ProfileScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var roll by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var aadhaar by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        // Top bar
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text("Profile", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(10.dp))
        // Avatar & Edit
        Box(Modifier.size(105.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Avatar",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(66.dp)
                )
            }
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit Avatar",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color.LightGray), CircleShape)
            )
        }
        Spacer(Modifier.height(8.dp))
        // Tabs
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TabButton(text = "General", selected = selectedTab == 0) { selectedTab = 0 }
            TabButton(text = "Location", selected = selectedTab == 1) { selectedTab = 1 }
        }
        Spacer(Modifier.height(22.dp))
        if (selectedTab == 0) {
            // General tab fields
            ProfileField(
                label = "Name",
                value = name,
                onValueChange = { name = it },
                placeholder = "XXXXXXXXXX"
            )
            Spacer(Modifier.height(16.dp))
            ProfileField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "XXXXXXXX@gmail.com",
                trailingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray)
                })
            Spacer(Modifier.height(16.dp))
            ProfileField(
                label = "Roll Number",
                value = roll,
                onValueChange = { roll = it },
                placeholder = "202XXXXXX"
            )
            Spacer(Modifier.height(16.dp))
            ProfileField(
                label = "Date of Birth",
                value = dob,
                onValueChange = { dob = it },
                placeholder = "23/05/19XX",
                trailingIcon = {
                    Icon(
                        painter = painterResource(android.R.drawable.arrow_down_float),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                })
            Spacer(Modifier.height(16.dp))
            ProfileField(
                label = "Aadhaar Number",
                value = aadhaar,
                onValueChange = { aadhaar = it },
                placeholder = "3B02 0999 XXXX"
            )
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E206D))
            ) {
                Text("Save changes", color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Location tab content", color = Color.Gray)
            }
        }
    }
}

@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF3E206D) else Color(0xFFF5F5F5),
            contentColor = if (selected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text)
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = { Text(placeholder, color = Color.Gray) },
            trailingIcon = trailingIcon,
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    }
}
