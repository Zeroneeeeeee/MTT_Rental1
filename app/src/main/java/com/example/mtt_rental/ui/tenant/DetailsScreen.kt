package com.example.mtt_rental.ui.tenant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.example.mtt_rental.viewmodel.tenant.ApartmentDetailsViewModel

@Preview(showBackground = true)
@Composable
fun DetailsScreen(
    apartmentId: String = "",
    viewModel: ApartmentDetailsViewModel = viewModel(),
    toRentScreen: (String) -> Unit = {}
) {
    val apartment by viewModel.apartment
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    LaunchedEffect(apartmentId) {
        if (apartmentId.isNotEmpty()) {
            viewModel.loadApartmentById(apartmentId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton()
            Spacer(Modifier.weight(1f))
            Text("Details", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color.Gray)
            Spacer(Modifier.weight(1f))
            FavoriteButton()
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFEFB8C8))
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error!!,
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }

            else -> {
                // Image
                ApartmentImage()
                // Duplex Home Info Card
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(apartment.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(Modifier.weight(1f))
                        Text(
                            apartment.price.toString(),
                            color = Color(0xFFEFB8C8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.ic_location),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(apartment.location, fontSize = 13.sp, color = Color.Gray)
                        Spacer(Modifier.weight(1f))
                        RatingCard(apartment.rating)
                    }
                    RoomTypeDetail()
                }
                // Gallery section
                Column(modifier = Modifier.padding(start = 20.dp, top = 7.dp)) {
                    Text("Gallery", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    ListImage()
                }
                Spacer(Modifier.height(22.dp))
                // Rent Now button
                RentButton(onClick = {
                    toRentScreen(apartmentId)
                })
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

@Preview(showBackground = true)
@Composable
fun ServiceList() {
    Spacer(Modifier.height(7.dp))
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            InfoIconTextDetail(R.drawable.ic_electric, "Electricity")
        }
        item {
            InfoIconTextDetail(R.drawable.ic_water, "Water")
        }
        item {
            InfoIconTextDetail(R.drawable.ic_internet, "Internet")
        }
        item {
            InfoIconTextDetail(R.drawable.ic_general, "General")
        }
        item {
            InfoIconTextDetail(R.drawable.ic_furnitures, "Furniture")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailList() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        InfoIconTextDetail(R.drawable.ic_area, "2450 sqft")
        InfoIconTextDetail(R.drawable.ic_renters, "Max Renters")
        InfoIconTextDetail(R.drawable.ic_price, "Priceeeeeeee")
    }
}

@Preview(showBackground = true)
@Composable
fun RoomTypeDetail(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        DetailList()
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Text("Service")
            ServiceList()
        }
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Text("Description")
            Text("This is a description of the room type.")
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
