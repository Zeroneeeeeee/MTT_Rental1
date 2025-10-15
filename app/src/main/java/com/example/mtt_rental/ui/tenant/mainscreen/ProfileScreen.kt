package com.example.mtt_rental.ui.tenant.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.R
import com.example.mtt_rental.utils.UserRepo
import com.example.mtt_rental.viewmodel.tenant.ProfileViewModel

@Preview(showBackground = true)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    toReviewScreen: (String) -> Unit = {},
    toFeedbackScreen: (String) -> Unit = {},
    toLogin:()-> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf(UserRepo.profileName) }
    var email by remember { mutableStateOf(UserRepo.email) }
    var phone by remember { mutableStateOf(UserRepo.phoneNumber) }
    var enable by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) } // thêm biến error

    LaunchedEffect(Unit) {
        viewModel.loadUserLocation(UserRepo.idUser) // lấy idUser từ repo
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item{
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { enable = true }
                )
            }
            Spacer(Modifier.height(4.dp))
            Text("Profile", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(Modifier.height(10.dp))

            // Avatar
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
            }
            Spacer(Modifier.height(8.dp))

            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabButton(text = "General", selected = selectedTab == 0) { selectedTab = 0 }
                TabButton(text = "Location", selected = selectedTab == 1) { selectedTab = 1 }
            }
            Spacer(Modifier.height(22.dp))

            if (selectedTab == 0) {
                // General tab
                ProfileField(
                    label = "Name",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "XXXXXXXXXX",
                    enabled = enable
                )
                Spacer(Modifier.height(16.dp))
                ProfileField(
                    label = "Email",
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null // clear error khi user gõ lại
                    },
                    placeholder = "XXXXXXXX@gmail.com",
                    trailingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray)
                    },
                    enabled = enable
                )
                // hiển thị lỗi email nếu có
                if (emailError != null) {
                    Text(
                        text = emailError ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                    )
                }

                Spacer(Modifier.height(16.dp))
                ProfileField(
                    label = "Phone Number",
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "202XXXXXX",
                    enabled = enable
                )
                Spacer(Modifier.height(40.dp))

                if (enable) {
                    Button(
                        onClick = {
                            if (!email.endsWith("@gmail.com")) {
                                emailError = "Email must end with @gmail.com"
                            } else {
                                viewModel.updateUserProfile(
                                    idUser = UserRepo.idUser,
                                    profileName = name,
                                    email = email,
                                    phoneNumber = phone
                                )
                                enable = false
                                // gọi viewModel.updateProfile(...) ở đây nếu muốn lưu
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E206D))
                    ) {
                        Text("Save changes", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            UserRepo.clearUser()
                            toLogin()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Logout", color = Color.White)
                    }
                }
            } else {
                LocationTab(viewModel, UserRepo.idUser, toReviewScreen, toFeedbackScreen)
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
    enabled: Boolean = false,
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
            shape = RoundedCornerShape(8.dp),
            enabled = enabled
        )
    }
}

@Composable
fun LocationTab(
    viewModel: ProfileViewModel,
    userId: String,
    toReviewScreen: (String) -> Unit = { },
    toFeedbackScreen: (String) -> Unit = { }
) {
    val apartment by viewModel.apartment
    val roomType by viewModel.roomType
    val room by viewModel.room
    val loading by viewModel.loading
    val error by viewModel.error
    val services by viewModel.services
    val currentRenterCount by viewModel.currentRenterCount

    LaunchedEffect(Unit) {
        viewModel.loadUserLocation(userId)
    }

    when {
        loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Lỗi: $error", color = Color.Red, fontSize = 16.sp)
            }
        }

        apartment != null && roomType != null && room != null -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tiêu đề
                    Text(
                        text = "Thông tin chỗ ở",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    // Tên căn hộ và địa chỉ
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = Color(0xFF1976D2)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(apartment!!.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.ic_location),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(apartment!!.location, fontSize = 14.sp, color = Color.Gray)
                    }
                    androidx.compose.material3.Divider(thickness = 1.dp, color = Color(0xFFF5F5F5))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = Color(0xFF388E3C)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Loại phòng: ${roomType!!.idRoomType}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = Color(0xFF8E24AA)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Phòng: ${room!!.name}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    androidx.compose.material3.Divider(thickness = 1.dp, color = Color(0xFFF5F5F5))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painterResource(R.drawable.ic_price),
                                contentDescription = null,
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${roomType!!.price} VND",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painterResource(R.drawable.ic_area),
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${roomType!!.area} m²",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painterResource(R.drawable.ic_renters),
                                contentDescription = null,
                                tint = Color(0xFF388E3C),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "$currentRenterCount/${roomType!!.maxRenter} người",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { toReviewScreen(apartment!!.apartmentId) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E206D))
                        ) {
                            Text("Review", color = Color.White)
                        }
                        Button(
                            onClick = { toFeedbackScreen(apartment!!.ownerId) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Text("Feedback", color = Color.White)
                        }
                    }
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No data available", fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}