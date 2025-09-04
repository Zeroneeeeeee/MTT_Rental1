package com.example.mtt_rental.ui.manager

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import com.example.mtt_rental.repo.UserRepo
import com.example.mtt_rental.ui.model.Apartment
import com.google.firebase.database.FirebaseDatabase


@Preview(showBackground = true)
@Composable
fun ManagerAddRentalScreen(editApartmentId: String = "") {
    val firebaseRef = FirebaseDatabase.getInstance().getReference("apartments")
    val editApartment = firebaseRef.child(editApartmentId)
    Log.d("Check" , editApartmentId)
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var rental by remember { mutableStateOf("") }
    var electric by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var maxPeople by remember { mutableStateOf("") }
    var benefit1 by remember { mutableStateOf("") }
    var benefit2 by remember { mutableStateOf("") }

    Surface(color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = image,
                    onValueChange = { image = it },
                    label = { Text("Image") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Area") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = maxPeople,
                    onValueChange = { maxPeople = it },
                    label = { Text("Max people") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = benefit1,
                    onValueChange = { benefit1 = it },
                    label = { Text("Benefit 1") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = benefit2,
                    onValueChange = { benefit2 = it },
                    label = { Text("Benefit 2") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
            item{
                Text("Cost")
                OutlinedTextField(
                    value = rental,
                    onValueChange = { rental = it },
                    label = { Text("Rental") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = electric,
                    onValueChange = { electric = it },
                    label = { Text("Electric") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = water,
                    onValueChange = { water = it },
                    label = { Text("Water") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = service,
                    onValueChange = { service = it },
                    label = { Text("Service") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item{
                Button(
                    onClick = {
                        val apartmentId =
                            if (editApartmentId == "") firebaseRef.push().key!! else editApartmentId
                        val newApartment = Apartment(
                            apartmentId = apartmentId,
                            title = title,
                            description = "",
                            location = location,
                            price = 0,
                            image = "",
                            maxRenter = 4,
                            ownerId = UserRepo.idUser
                        )
                        firebaseRef.child(apartmentId).setValue(newApartment)
                            .addOnCompleteListener {
                                Toast.makeText(
                                    context,
                                    "Apartment added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Failed to add apartment",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Rental")
                }
            }
        }
    }
}
