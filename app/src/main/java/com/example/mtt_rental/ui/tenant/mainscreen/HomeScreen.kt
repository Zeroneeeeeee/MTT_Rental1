package com.example.mtt_rental.ui.tenant.mainscreen

import android.R
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import com.example.mtt_rental.ui.model.Apartment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Preview(showBackground = true)
@Composable
fun HomeScreen() {
    val firebaseRef = FirebaseDatabase.getInstance().getReference("apartments")
    val apartmentList = remember { mutableStateListOf<Apartment>() }
    LaunchedEffect(Unit){
        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (apartmentSnapshot in snapshot.children) {
                        val empData = apartmentSnapshot.getValue(Apartment::class.java)
                        apartmentList.add(empData!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
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
        Spacer(Modifier.height(22.dp))
        // Category Tabs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CategoryIconWithLabel("Apartment")
            CategoryIconWithLabel("Condo")
            CategoryIconWithLabel("House")
            CategoryIconWithLabel("Flat")
            CategoryIconWithLabel("Dept")
        }
        Spacer(Modifier.height(28.dp))
        // Recommended Title
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Recommended", fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Spacer(Modifier.weight(1f))
            Text("More", color = Color(0xFFEFB8C8), fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Spacer(Modifier.height(18.dp))
        // Duplex Home Card
        PropertyRowList(apartmentList)
        Spacer(Modifier.height(18.dp))
        // Nearby Title
        Text("Nearby your location", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        Spacer(Modifier.height(12.dp))
        // Nearby Card
        ColumnItem()
    }
}

@Composable
fun CategoryIconWithLabel(label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = Color(0xFFF6EAF1),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Default.Home, contentDescription = label, tint = Color(0xFFEFB8C8))
            }
        }
        Text(label, fontSize = 11.sp, color = Color.Gray)
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
fun PropertyRowList(apartmentList: List<Apartment> = emptyList()){
    LazyRow(){
        items(apartmentList){
            PropertyCard(it.title,it.price.toString(),it.location)
        }
    }
}

@Composable
fun PropertyCard(
    title:String= "Title",
    cost:String = "Cost/Time",
    location:String = "Location"
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                        .padding(10.dp)
                ) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.weight(1f))
                    Text(
                        cost,
                        color = Color(0xFFEFB8C8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(location, fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoIconText(R.drawable.ic_menu_mylocation, "3 Beds")
                    InfoIconText(R.drawable.ic_menu_compass, "4 Baths")
                    InfoIconText(R.drawable.ic_menu_crop, "2450 sqft")
                }
            }
        }
    }
}

@Composable
fun ColumnItem(
    modifier: Modifier = Modifier,
    title: String = "",
    location: String ="",
    cost: String="",
    rating: String="",
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
            Image(
                painterResource(R.drawable.ic_menu_gallery),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
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
fun ColumnList(modifier: Modifier = Modifier,) {
    
}
