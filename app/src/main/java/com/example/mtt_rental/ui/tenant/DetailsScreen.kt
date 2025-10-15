package com.example.mtt_rental.ui.tenant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.R
import com.example.mtt_rental.model.Review
import com.example.mtt_rental.model.RoomService
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.utils.UserRepo
import com.example.mtt_rental.viewmodel.tenant.ApartmentDetailsViewModel

@Composable
fun DetailsScreen(
    apartmentId: String = "",
    viewModel: ApartmentDetailsViewModel = viewModel(),
    toRentScreen: (String) -> Unit = {}
) {
    val apartment by viewModel.apartment
    val roomTypes by viewModel.roomTypes
    val services by viewModel.services
    var rating by remember { mutableStateOf(0.0) }
    val reviews by viewModel.reviews
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    var selectedRoomType by remember { mutableStateOf<RoomType?>(null) }

    // Load data
    LaunchedEffect(apartmentId) {
        if (apartmentId.isNotEmpty()) {
            viewModel.loadApartmentById(apartmentId)
            viewModel.loadRoomTypes(apartmentId)
            viewModel.loadReviewsByApartment(apartmentId)
            viewModel.calculateAverageRating(apartmentId){
                rating = it
            }
        }
    }
    LaunchedEffect(roomTypes) {
        if (roomTypes.isNotEmpty() && selectedRoomType == null) {
            selectedRoomType = roomTypes.first()
        }
    }
    LaunchedEffect(selectedRoomType) {
        if (apartmentId.isNotEmpty() && selectedRoomType != null) {
            viewModel.loadServices(apartmentId, selectedRoomType!!.idRoomType)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // nhẹ nhàng
    ) {
        // 🔹 Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton()
            Spacer(Modifier.weight(1f))
            Text("Apartment Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.weight(1f))
            if(UserRepo.idUser == "user") FavoriteButton()
        }

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFEFB8C8))
                }
            }

            error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red, fontSize = 16.sp)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ApartmentImage()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 🔹 Title + Price
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    apartment.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(Modifier.weight(1f))
                            }

                            // 🔹 Location + Rating
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painterResource(R.drawable.ic_location),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(apartment.location, fontSize = 14.sp, color = Color.Gray)
                                Spacer(Modifier.weight(1f))
                                RatingCard(rating)
                            }

                            // 🔹 Room Type Picker
                            Column {
                                Text("Choose Room Type", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(roomTypes) { type ->
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = if (selectedRoomType?.idRoomType == type.idRoomType)
                                                Color(0xFFE91E63) else Color.White,
                                            shadowElevation = 3.dp,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .clickable { selectedRoomType = type }
                                        ) {
                                            Text(
                                                text = "Type ${type.idRoomType}",
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                                color = if (selectedRoomType?.idRoomType == type.idRoomType)
                                                    Color.White else Color.DarkGray,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            // 🔹 Room Detail
                            if (selectedRoomType != null) {
                                RoomTypeDetail(
                                    roomType = selectedRoomType!!,
                                    services = services,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .padding(8.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 2.dp,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Reviews", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(Modifier.height(8.dp))

                                    if (reviews.isEmpty()) {
                                        Text("No reviews yet", color = Color.Gray, fontSize = 13.sp)
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            reviews.forEach { review ->
                                                ReviewItem(review)
                                            }
                                        }
                                    }
                                }
                            }

                            // 🔹 Rent Button
                            if(UserRepo.userType == "User") RentButton { toRentScreen(apartmentId) }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun InfoIconTextDetail(iconRes: Int, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painterResource(iconRes),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(40.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(text, fontSize = 20.sp, color = Color.DarkGray)
    }
}

@Composable
fun RentButton(onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFB8C8))
    ) {
        Text("Rent Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun ListImage() {
    val galleryImgs = listOf(
        R.drawable.ic_area,
        R.drawable.ic_area,
        R.drawable.ic_area,
        R.drawable.ic_area,
    )
    LazyRow {
        items(galleryImgs) { img ->
            Image(
                painterResource(img),
                contentDescription = null,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(13.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(6.dp))
        }
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color(0xFFEFB8C8))
            ) {
                Text("+24", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RatingCard(rating: Double = 0.0) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFEFB8C8),
                modifier = Modifier.size(15.dp)
            )
            Text(rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun RoomTypeDetail(
    roomType: RoomType,
    modifier: Modifier = Modifier,
    services: List<RoomService> = emptyList()
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp) // tạo khoảng cách giữa các phần
    ) {
        // --- Thông tin cơ bản ---
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "General Information",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoIconTextDetail(R.drawable.ic_area, "${roomType.area} sqft")
                    InfoIconTextDetail(R.drawable.ic_renters, "${roomType.maxRenter} Renters")
                    InfoIconTextDetail(R.drawable.ic_price, "${roomType.price} VND")
                }
            }

        }

        // --- Dịch vụ ---
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Services", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                if (services.isEmpty()) {
                    Text("No services available", color = Color.Gray, fontSize = 13.sp)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(services) { service ->
                            val iconRes = when (service.name.lowercase()) {
                                "electricity" -> R.drawable.ic_electric
                                "water" -> R.drawable.ic_water
                                else -> R.drawable.ic_general
                            }
                            InfoIconTextDetail(
                                iconRes,
                                "${service.fee} VND / ${service.unit}"
                            )
                        }
                    }
                }
            }
        }

        // --- Mô tả ---
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text(roomType.description, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}


@Composable
fun BackButton() {
    Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
        IconButton(onClick = { /*back*/ }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFEFB8C8)
            )
        }
    }
}

@Composable
fun FavoriteButton() {
    Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
        IconButton(onClick = { /*like*/ }) {
            Icon(
                Icons.Default.FavoriteBorder,
                contentDescription = "Like",
                tint = Color(0xFFEFB8C8)
            )
        }
    }
}

@Composable
fun ApartmentImage() {
    Image(
        painterResource(R.drawable.ic_launcher_background), contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp), contentScale = ContentScale.Crop
    )
}

@Composable
fun ReviewItem(review: Review) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        color = Color(0xFFF9F9F9),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                Spacer(Modifier.width(6.dp))
                Text("User: ${review.idUser}", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                RatingCard(review.rating?.toDouble() ?: 0.0)
            }
            Spacer(Modifier.height(6.dp))
            Text(review.comment ?: "No comment", fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

