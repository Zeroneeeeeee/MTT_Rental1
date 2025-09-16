package com.example.mtt_rental.ui.manager

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.dtmodel.RoomTypeVM
import com.example.mtt_rental.viewmodel.manager.ManagerAddRentalViewModel
import com.example.mtt_rental.viewmodel.manager.SaveResult


@Preview(showBackground = true)
@Composable
fun ManagerAddRentalScreen(
    editApartmentId: String = "",
    roomTypeList:List<RoomTypeVM> = emptyList(),
    toAddRoomScreen: () -> Unit = {},
    toManageScreen: () -> Unit = {},
    viewModel: ManagerAddRentalViewModel = viewModel()
) {

    val context = LocalContext.current
    var location by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading
    val saveResult by viewModel.saveResult

    // Handle save result
    LaunchedEffect(saveResult) {
        val result = saveResult
        when (result) {
            is SaveResult.Success -> {
                // If the success is from saving the apartment, proceed to save room types
                if (result.message == "Apartment đã được lưu thành công") {
                    roomTypeList.forEach { roomType ->
                        viewModel.saveRoomType(
                            name = roomType.idRoomType,
                            maxRenter = roomType.maxRenter,
                            price = roomType.price,
                            area = roomType.area,
                            description = roomType.description
                        )
                        roomType.roomList.forEach { room ->
                            viewModel.saveRoom(
                                idRoomType = roomType.idRoomType,
                                name = room.name,
                                floor = room.floor
                            )
                        }
                        roomType.roomServiceList.forEach { roomService ->
                            viewModel.saveService(
                                idRoomType = roomType.idRoomType,
                                name = roomService.name,
                                fee = roomService.fee,
                                param = roomService.unit
                            )
                        }
                    }
                    // After saving everything, navigate away
                    toManageScreen()
                }
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                viewModel.clearSaveResult()
            }

            is SaveResult.Error -> {
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
            }
            item {
                RoomTypeList(listRoomType = roomTypeList, onAddRoom = toAddRoomScreen)
            }
            item {
                Button(
                    onClick = {
                        // Only call saveApartment here, the LaunchedEffect will handle the rest
                        viewModel.saveApartment(
                            editApartmentId = editApartmentId,
                            location = location,
                            title = title,
                            image = image
                        )
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
                        Text("Add Rental")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomTypeList(listRoomType:List<RoomTypeVM> = emptyList(), onClick: () -> Unit = {}, onAddRoom: () -> Unit = {}) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listRoomType.forEach { roomType ->
            RoomTag(
                name = roomType.idRoomType,
                onClick = { onClick() }
            )
        }

        RoomTag(
            name = "+ Add Room",
            onClick = onAddRoom
        )

    }
}

@Composable
fun RoomTag(
    modifier: Modifier = Modifier,
    name: String = "Room name",
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(Color(0xFFBBDEFB)) // để dễ thấy ô
            .padding(8.dp)
    ) {
        Text(name, maxLines = 1) // cho phép wrap nhiều dòng
    }
}
