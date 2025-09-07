package com.example.mtt_rental.ui.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mtt_rental.repo.UserRepo
import com.example.mtt_rental.model.Apartment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Preview(showBackground = true)
@Composable
fun ManagerManageScreen(
    toAddRenterScreen: () -> Unit = {},
    toEditScreen: (String) -> Unit = {},
    toDetailScreen: (String) -> Unit = {},
    onDeleteProperty: (String) -> Unit = {}
) {
    val firebaseRef =
        FirebaseDatabase.getInstance().getReference("apartments").orderByChild("ownerId")
            .equalTo(UserRepo.idUser)
    val apartmentList = remember { mutableStateListOf<Apartment>() }
    LaunchedEffect(Unit) {
        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                apartmentList.clear() // Clear existing list to avoid duplicates
                if (snapshot.exists()) {
                    for (apartmentSnapshot in snapshot.children) {
                        val empData = apartmentSnapshot.getValue(Apartment::class.java)
                        if (empData != null) {
                            apartmentList.add(empData)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error appropriately
            }

        })
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { toAddRenterScreen() },
                containerColor = Color(0xFFEFB8C8)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Property")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Property Management",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(apartmentList) { property ->
                    PropertyCard(
                        property = property,
                        toEditScreen = toEditScreen,
                        toDetailScreen = toDetailScreen,
                        onDeleteProperty = { apartmentId ->
                            // Call the delete function passed from parent
                            onDeleteProperty(apartmentId)
                            // Or implement direct Firebase deletion here:
                            // FirebaseDatabase.getInstance().getReference("apartments")
                            //     .child(apartmentId).removeValue()
                            // apartmentList.removeAll { it.apartmentId == apartmentId }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyCard(
    property: Apartment,
    toEditScreen: (String) -> Unit = {},
    toDetailScreen: (String) -> Unit = {},
    onDeleteProperty: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { toDetailScreen(property.apartmentId) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = Color(0xFFEFB8C8),
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = property.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = property.location,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Text(
                    text = "${property.price}/month",
                    color = Color(0xFF4CAF50),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Spacer(modifier = Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }

                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color(0xFFEFB8C8)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("View Details") },
                        onClick = {
                            expanded = false
                            toDetailScreen(property.apartmentId)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "View Details",
                                tint = Color(0xFFEFB8C8)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Manage") },
                        onClick = {
                            expanded = false
                            toDetailScreen(property.apartmentId)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "View Details",
                                tint = Color(0xFFEFB8C8)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expanded = false
                            toEditScreen(property.apartmentId)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFFEFB8C8)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        onClick = {
                            expanded = false
                            onDeleteProperty(property.apartmentId)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    )
                }
            }
        }
    }
}