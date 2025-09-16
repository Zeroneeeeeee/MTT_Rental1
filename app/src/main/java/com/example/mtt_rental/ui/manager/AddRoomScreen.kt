package com.example.mtt_rental.ui.manager

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.dtmodel.RoomServiceVM
import com.example.mtt_rental.dtmodel.RoomTypeVM
import com.example.mtt_rental.dtmodel.RoomVM
import com.example.mtt_rental.viewmodel.manager.AddRoomViewModel
import com.example.mtt_rental.viewmodel.manager.RoomSaveResult

@Preview(showBackground = true)
@Composable
fun Preview(modifier: Modifier = Modifier) {
    AddRoomScreen()
}

@Composable
fun AddRoomScreen(
    modifier: Modifier = Modifier,
    onAddRoomTypeEvent: (RoomTypeVM) -> Unit = {},
    viewModel: AddRoomViewModel = viewModel()
) {
    val context = LocalContext.current
    val roomList = remember { mutableStateListOf<RoomVM>() }
    // Dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var roomNumber by remember { mutableStateOf("") }
    var floor by remember { mutableIntStateOf(0) }

    // Form states
    var name by remember { mutableStateOf("") }
    var rental by remember { mutableStateOf("") }
    var electric by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }
    var service by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var maxPeople by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading
    val saveResult by viewModel.saveResult

    LaunchedEffect(saveResult) {
        val result = saveResult
        when (result) {
            is RoomSaveResult.Success -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                viewModel.clearSaveResult()
                // Reset dialog form if dialog was used
                showAddDialog = false
                roomNumber = ""
                floor = 0
            }

            is RoomSaveResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                viewModel.clearSaveResult()
            }

            null -> {
                // No result yet
            }
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                RoomList(listRoom = roomList,onAddRoom = { showAddDialog = true })
            }
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Room Name") },
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
            }
            item {
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
            item {
                Button(
                    onClick = {
                        val electric = RoomServiceVM(
                            idRoomService = "Electricity",
                            name = "Electricity",
                            fee = electric.toLong(),
                            unit = "kWh"
                        )
                        val water = RoomServiceVM(
                            idRoomService = "Water",
                            name = "Water",
                            fee = water.toLong(),
                            unit = "m3"
                        )
                        val serviceFee = RoomServiceVM(
                            idRoomService = "Service",
                            name = "Service",
                            fee = service.toLong(),
                            unit = "Month"
                        )
                        val serviceList = mutableListOf(electric, water, serviceFee)
                        val newRoomType = RoomTypeVM(
                            idRoomType = name.replace(" ", "_"),
                            price = rental.toLong(),
                            area = area.toLong(),
                            maxRenter = maxPeople.toInt(),
                            description = "",
                            roomList = roomList,
                            roomServiceList = serviceList
                        )
                        onAddRoomTypeEvent(newRoomType)

                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Add Room Type")
                    }
                }
            }
        }

        // Dialog with 2 text fields
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    roomNumber = ""
                    floor = 0
                },
                title = { Text("Add Room") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = roomNumber,
                            onValueChange = { roomNumber = it },
                            label = { Text("Room Number") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                        OutlinedTextField(
                            value = floor.toString(),
                            onValueChange = { floor = it.toIntOrNull() ?: 0 },
                            label = { Text("Floor") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            roomList.add(
                                RoomVM(
                                    name = roomNumber,
                                    floor = floor
                                )
                            )
                            showAddDialog = false
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Add")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddDialog = false
                        roomNumber = ""
                        floor = 0
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
@Composable
fun RoomList(listRoom:List<RoomVM> = emptyList(), onClick: () -> Unit = {}, onAddRoom: () -> Unit = {}) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listRoom.forEach { room ->
            RoomTag(
                name = room.name,
                onClick = { onClick() }
            )
        }

        RoomTag(
            name = "+ Add Room",
            onClick = onAddRoom
        )

    }
}