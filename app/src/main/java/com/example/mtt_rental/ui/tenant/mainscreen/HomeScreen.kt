package com.example.mtt_rental.ui.tenant.mainscreen

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.repo.UserRepo
import com.example.mtt_rental.viewmodel.tenant.HomeViewModel

@Preview(showBackground = true)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(), toDetail:(String) -> Unit ={}) {
    val apartmentList by viewModel.apartmentList
    val searchQuery by viewModel.searchQuery
    val filteredApartmentList by viewModel.filteredApartmentList
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(16.dp)
    ) {
        // Location Row
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Location", fontSize = 15.sp, color = Color.Gray)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFEFB8C8))
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEFB8C8))
            Text("San Jose, CA", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
        }
        Spacer(Modifier.height(16.dp))
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(18.dp),
            placeholder = {
                Text(
                    "Search apartment, location...",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFFEFB8C8)
                )
            },
            singleLine = true
        )

        Spacer(Modifier.height(28.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (searchQuery.isNotEmpty()) "Search Results" else "Recommended",
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
            Spacer(Modifier.weight(1f))
            if (searchQuery.isEmpty()) {
                Text(
                    "More",
                    color = Color(0xFFEFB8C8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            } else {
                Text(
                    "${filteredApartmentList.size} found",
                    color = Color(0xFFEFB8C8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
        Spacer(Modifier.height(18.dp))

        // Show loading or content
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFEFB8C8))
            }
        } else if (error != null) {
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
        } else {
            // Property List (filtered or all)
            PropertyRowList(filteredApartmentList, onClick = { toDetail(it) })
            Spacer(Modifier.height(18.dp))
            // Show nearby section only when not searching
            if (searchQuery.isEmpty()) {
                Text("Nearby your location", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Spacer(Modifier.height(12.dp))
                ColumnItem()
            }
        }
    }
}


@Composable
fun InfoIconText(iconRes: Int, text: String, size: Int = 16) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painterResource(iconRes),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(size.dp)
        )
        Spacer(Modifier.width(3.dp))
        Text(text, fontSize = size.sp, color = Color.DarkGray)
    }
}

@Composable
fun PropertyRowList(apartmentList: List<Apartment> = emptyList(), onClick: (String) -> Unit = {}){
    LazyRow() {
        items(apartmentList) {
            PropertyCard(apartment = it, onClick = { onClick(it.apartmentId) })
        }
    }
}

@Composable
fun PropertyCard(
    apartment: Apartment? = null,
    title: String = apartment?.title ?: "Title",
    cost: String = "${apartment?.price?.toString()}/month",
    location: String = apartment?.location ?: "Location",
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(end = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Box {
                Image(
                    painterResource(R.drawable.ic_menu_gallery),
                    contentDescription = null,
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Favorite heart icon
                    if (apartment != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    UserRepo.toggleFavorite(apartment.apartmentId)
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    if (UserRepo.isFavorite(apartment.apartmentId))
                                        Icons.Default.Favorite
                                    else
                                        Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (UserRepo.isFavorite(apartment.apartmentId))
                                        Color.Red
                                    else
                                        Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Rating
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
                                modifier = Modifier.size(16.dp)
                            )
                            Text("4.9", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    cost,
                    color = Color(0xFFEFB8C8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(location, fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ColumnItem(
    modifier: Modifier = Modifier,
    apartment: Apartment? = null, title: String = apartment?.title ?: "Modern Apartment",
    location: String = apartment?.location ?: "Downtown San Jose",
    cost: String = apartment?.price?.toString() ?: "$2500/month",
    rating: String = apartment?.rating?.toString() ?: "4.8",
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Image(
                    painterResource(R.drawable.ic_menu_gallery),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                // Favorite heart icon for ColumnItem
                if (apartment != null) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                            .clickable {
                                UserRepo.toggleFavorite(apartment.apartmentId)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (UserRepo.isFavorite(apartment.apartmentId))
                                    Icons.Default.Favorite
                                else
                                    Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (UserRepo.isFavorite(apartment.apartmentId))
                                    Color.Red
                                else
                                    Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(location, fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InfoIconText(R.drawable.ic_menu_mylocation, "4 Beds", size = 14)
                    InfoIconText(R.drawable.ic_menu_compass, "4 Baths", size = 14)
                    InfoIconText(R.drawable.ic_menu_crop, "2830 sqft", size = 14)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    cost,
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

@Composable
fun ColumnList(modifier: Modifier = Modifier) {

}
