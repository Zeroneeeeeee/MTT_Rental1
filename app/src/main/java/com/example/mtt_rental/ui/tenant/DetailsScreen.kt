package com.example.mtt_rental.ui.tenant

import android.R
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.mtt_rental.model.Apartment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Preview(showBackground = true)
@Composable
fun DetailsScreen(apartmentId:String = "") {
    val database = FirebaseDatabase.getInstance()
    val apartmentRef = database.getReference("apartments")
    var apartmentDetail by remember { mutableStateOf(Apartment()) }
    LaunchedEffect(Unit){
        apartmentRef.child(apartmentId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Lấy dữ liệu dưới dạng class Apartment
                    val apartment = snapshot.getValue(Apartment::class.java)
                    if (apartment != null) {
                        // Làm gì đó với apartment
                        apartmentDetail = apartment
                        Log.d("Firebase", "Tên: ${apartment.title}")
                    } else {
                        Log.d("Firebase", "Không tìm thấy dữ liệu")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Lỗi: ${error.message}")
                }
            })
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
            Surface(shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
                IconButton(onClick = { /*back*/ }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFEFB8C8)
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Text("Details", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color.Gray)
            Spacer(Modifier.weight(1f))
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
        // Image
        Image(
            painterResource(R.drawable.ic_menu_gallery), contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp), contentScale = ContentScale.Crop
        )
        // Duplex Home Info Card
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(apartmentDetail.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.weight(1f))
                Text(
                    apartmentDetail.price.toString(),
                    color = Color(0xFFEFB8C8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(R.drawable.ic_menu_mylocation),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Text(apartmentDetail.location, fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.weight(1f))
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
                        Text(apartmentDetail.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(7.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoIconTextDetail(R.drawable.ic_menu_mylocation, "3 Beds", 15)
                InfoIconTextDetail(R.drawable.ic_menu_compass, "4 Baths", 15)
                InfoIconTextDetail(R.drawable.ic_menu_crop, "2450 sqft", 15)
            }
        }
        Divider(
            color = Color(0xFFEFB8C8),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 18.dp)
        )
        // Description section
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp)) {
            Text("Description", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                apartmentDetail.description,
                fontSize = 14.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Text(
                "Read More",
                color = Color(0xFFEFB8C8),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 2.dp, top = 3.dp)
            )
        }
        // Gallery section
        Column(modifier = Modifier.padding(start = 20.dp, top = 7.dp)) {
            Text("Gallery", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
            val galleryImgs = listOf(
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
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
        Spacer(Modifier.height(22.dp))
        // Rent Now button
        Button(
            onClick = {},
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
}

@Composable
fun InfoIconTextDetail(iconRes: Int, text: String, size: Int = 16) {
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
