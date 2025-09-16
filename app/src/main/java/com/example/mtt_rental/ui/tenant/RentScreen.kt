package com.example.mtt_rental.ui.tenant

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.dtmodel.RoomTypeVM
import com.example.mtt_rental.dtmodel.RoomVM
import com.example.mtt_rental.repo.UserDB
import com.example.mtt_rental.repo.UserRepo
import com.example.mtt_rental.utils.toRoomTypeVMList
import com.example.mtt_rental.utils.toRoomVMList
import com.example.mtt_rental.viewmodel.tenant.RentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RentScreen(
    viewModel: RentViewModel = viewModel(),
    idApartment:String=""
) {
    var items by remember { mutableStateOf(emptyList<RoomTypeVM>()) }
    var selectedItem by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Log.d("RentScreen", "RentScreen: Loading room types for apartment $idApartment")
        viewModel.loadRoomTypesAndRooms(idApartment){items = it}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chọn phòng") }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (selectedItem != null) {
                        println("Bạn đã chọn: $selectedItem")
                    } else {
                        println("Chưa chọn phòng nào")
                    }
                    viewModel.createContract(
                        idUser = UserRepo.idUser,
                        idRoom = selectedItem?:"",
                        status = "Pending",
                        startTime = System.currentTimeMillis(),
                        endTime = 0L
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Xác nhận lựa chọn")
            }
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item{
                RoomTypeList(
                    list = items,
                    roomClick = { id ->
                        selectedItem = id
                    },
                    isRoomSelected = { it == selectedItem }
                )
            }
        }

    }
}

@Composable
fun RoomTypeList(
    list: List<RoomTypeVM>,
    roomClick: (String) -> Unit = {},
    isRoomSelected: (String) -> Boolean = { false }
) {
    Column {
        list.forEach { roomType ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Loại phòng: ${roomType.idRoomType}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
                RoomList(
                    roomList = roomType.roomList,
                    itemClick = roomClick,
                    isSelected = isRoomSelected
                )
                Log.d("RoomTypeList", "RoomTypeList: ${roomType.roomList}")
            }
        }
    }
}


@Composable
private fun RoomList(
    modifier: Modifier = Modifier,
    roomList: List<RoomVM> = emptyList(),
    itemClick: (String) -> Unit = {},
    isSelected: (String) -> Boolean = { false }
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(roomList) { item ->
            Card(
                modifier = Modifier
                    .width(200.dp) // 👈 mỗi card có độ rộng vừa phải
                    .clickable { itemClick(item.idRoom) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected(item.idRoom)) Color(0xFFB3E5FC) else Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item.name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}