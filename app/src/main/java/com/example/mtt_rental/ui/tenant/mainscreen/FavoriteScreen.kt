package com.example.mtt_rental.ui.tenant.mainscreen

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Preview(showBackground = true)
@Composable
fun FavoriteScreen() {
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
        FavoriteHouseCard(
            title = "Cozy Villa",
            price = "$4000/month",
            address = "123 Blossom Hill Rd",
            imageRes = R.drawable.ic_menu_gallery,
            beds = "5 Beds",
            baths = "3 Baths",
            size = "3000 sqft",
            rating = "5.0"
        )
        Spacer(Modifier.height(14.dp))
        FavoriteHouseCard(
            title = "Modern Flat",
            price = "$2700/month",
            address = "456 Market Street",
            imageRes = R.drawable.ic_menu_gallery,
            beds = "2 Beds",
            baths = "2 Baths",
            size = "1100 sqft",
            rating = "4.7"
        )
        Spacer(Modifier.height(14.dp))
        FavoriteHouseCard(
            title = "Family Home",
            price = "$3200/month",
            address = "789 Oak Ave",
            imageRes = R.drawable.ic_menu_gallery,
            beds = "4 Beds",
            baths = "3 Baths",
            size = "2100 sqft",
            rating = "4.8"
        )
    }
}

@Composable
fun FavoriteHouseCard(
    title: String,
    price: String,
    address: String,
    imageRes: Int,
    beds: String,
    baths: String,
    size: String,
    rating: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InfoIconText(R.drawable.ic_menu_mylocation, beds, size = 13)
                    InfoIconText(R.drawable.ic_menu_compass, baths, size = 13)
                    InfoIconText(R.drawable.ic_menu_crop, size, size = 13)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    price,
                    color = Color(0xFFEFB8C8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
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
