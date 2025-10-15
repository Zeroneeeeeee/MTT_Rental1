package com.example.mtt_rental.ui.manager

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mtt_rental.dtmodel.RoomTypeVM
import com.example.mtt_rental.viewmodel.manager.ManagerAddRentalViewModel
import com.example.mtt_rental.viewmodel.manager.SaveResult

@Composable
fun ManagerAddRentalScreen(
    editApartmentId: String = "",
    roomTypeList: List<RoomTypeVM> = emptyList(),
    toAddRoomScreen: () -> Unit = {},
    toEditRoomScreen: (String,String) -> Unit = {_,_ ->},
    toManageScreen: () -> Unit = {},
    viewModel: ManagerAddRentalViewModel = viewModel()
) {
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) } // nhiều ảnh

    val isLoading by viewModel.isLoading
    val saveResult by viewModel.saveResult

    // Gallery multiple picker
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris = uris
    }

    // Handle save result
    LaunchedEffect(saveResult) {
        val result = saveResult
        when (result) {
            is SaveResult.Success -> {
                if (result.message == "Apartment has been saved successfully") {
                    roomTypeList.forEach { roomType ->
                        Log.d("ManagerAddRentalVM", "saveRoomType")
                        viewModel.saveRoomType(
                            idRoomType = roomType.idRoomType,
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
                                unit = roomService.unit
                            )
                        }
                    }
                    toManageScreen()
                }
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                viewModel.clearSaveResult()
            }
            is SaveResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                viewModel.clearSaveResult()
            }
            null -> {}
        }
    }


    Surface(color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            item {
                Text("Add Rental", fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))

                // Location
                Text("Location", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text("Title", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Multiple Images
                Text("Images", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { pickImagesLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick Images from Gallery")
                }

                // Show image previews
                if (imageUris.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(imageUris.size) { index ->
                            AsyncImage(
                                model = imageUris[index],
                                contentDescription = "Apartment Image",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Room type
            item {
                Text("Room Type", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                RoomTypeList(listRoomType = roomTypeList, onAddRoom = toAddRoomScreen, onClick = {toEditRoomScreen(editApartmentId,it)})//
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Save button
            item {
                Button(
                    onClick = {
                        // Lưu danh sách ảnh dưới dạng chuỗi URI ngăn cách nhau
                        viewModel.saveApartment(
                            editApartmentId = editApartmentId,
                            location = location,
                            title = title,
                            image = imageUris.joinToString(",") { it.toString() }
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
fun RoomTypeList(listRoomType:List<RoomTypeVM> = emptyList(), onClick: (String) -> Unit = {}, onAddRoom: () -> Unit = {}) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listRoomType.forEach { roomType ->
            RoomTag(
                name = roomType.idRoomType,
                onClick = { onClick(roomType.idRoomType) }
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
