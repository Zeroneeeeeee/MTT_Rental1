package com.example.mtt_rental.ui.tenant.mainscreen

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.mtt_rental.utils.UserRepo
import com.example.mtt_rental.model.Apartment

@Preview(showBackground = true)
@Composable
fun FavoriteScreen() {
    val favoriteApartments = UserRepo.favoriteApartments

    LaunchedEffect(Unit) {
        UserRepo.refreshFavoriteApartments()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(16.dp)
    ) {
        Text(
            "Favorite Houses",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color(0xFFEFB8C8)
        )
        Spacer(Modifier.height(22.dp))

        if (favoriteApartments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No favorite apartments yet",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Start adding apartments to your favorites!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(favoriteApartments) { apartment ->
                    FavoriteHouseCard(
                        apartment = apartment,
                        title = apartment.title,
                        price = "3500000/month", //Can Sua
                        address = apartment.location,
                        imageRes = R.drawable.ic_menu_gallery,
                        rating = apartment.rating.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteHouseCard(
    apartment: Apartment? = null,
    title: String,
    price: String,
    address: String,
    imageRes: Int,
    rating: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.background(Color.White).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(address, fontSize = 13.sp, color = Color.Gray)
                Text(
                    price,
                    color = Color(0xFFEFB8C8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (apartment != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    UserRepo.removeFromFavorites(apartment.apartmentId)
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = "Remove from favorites",
                                    tint = Color.Red,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFEFB8C8),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(rating, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
