package com.example.mtt_rental.ui.manager

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.dtmodel.RoomVM
import com.example.mtt_rental.model.User
import com.example.mtt_rental.utils.toRoomVM
import com.example.mtt_rental.viewmodel.manager.RoomManagementViewModel

@Composable
fun RoomManagementScreen(
    apartmentId: String,
    viewModel: RoomManagementViewModel = viewModel(),
    toPaymentScreen: (String, String) -> Unit = { _, _ -> }
) {
    LaunchedEffect(apartmentId) {
        viewModel.loadRoomsByApartment(apartmentId)
    }
    val rooms = viewModel.rooms.value
    var expandedRoomId by remember { mutableStateOf<String?>(null) }

    if (rooms.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading rooms...")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(rooms) { room ->
                Log.d("RoomManagementScreen", "Room: ${room.name}")
                RoomItem(
                    apartmentId = apartmentId,
                    room = room.toRoomVM(),
                    isExpanded = expandedRoomId == room.idRoom,
                    onExpand = {
                        expandedRoomId = if (expandedRoomId == room.idRoom) null else room.idRoom
                    },
                    toPaymentScreen = toPaymentScreen
                )
            }
        }
    }
}

@Composable
fun RoomItem(
    apartmentId: String,
    room: RoomVM,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    viewModel: RoomManagementViewModel = viewModel(),
    toPaymentScreen: (String, String) -> Unit = { _, _ -> }
) {
    // Load joined users when expanded
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            viewModel.loadJoinedUsersByRoomId(room.idRoom)
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onExpand() }
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text("Room: ${room.name}", style = MaterialTheme.typography.titleMedium)

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tenants = viewModel.joinedUsersByRoomId.value[room.idRoom] ?: emptyList()
                tenants.forEach { tenant ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👤 ${tenant.profileName} (${tenant.idUser})")

                        Button(
                            onClick = {
                                viewModel.removeUserFromRoom(room.idRoom, tenant.idUser)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Delete")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Button(onClick = { showDialog = true }) { Text("Add tenant") }
                        Button(onClick = { toPaymentScreen(apartmentId, room.idRoomType) }) { Text("Payment") }
                    }
                }
            }
        }
    }

    if (showDialog) {
        PendingUserDialog(
            roomId = room.idRoom,
            viewModel = viewModel,
            onDismiss = { showDialog = false },
            onUserConfirmed = { selectedUser ->
                viewModel.updateContractStatus(room.idRoom, selectedUser.idUser, "Joined")
                viewModel.loadJoinedUsersByRoomId(room.idRoom) // refresh joined list
                showDialog = false
            }
        )
    }
}

@Composable
fun PendingUserDialog(
    roomId: String,
    viewModel: RoomManagementViewModel,
    onDismiss: () -> Unit,
    onUserConfirmed: (User) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf<String?>(null) }

    // ✅ Load pending users from ViewModel
    LaunchedEffect(roomId) {
        viewModel.loadPendingUsersByRoomId(roomId)
    }

    val pendingUsers = viewModel.pendingUsersByRoomId.value[roomId] ?: emptyList()

    val filteredUsers = pendingUsers.filter { user ->
        user.profileName.contains(searchQuery, ignoreCase = true) ||
                user.idUser.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedUserId?.let { id ->
                        pendingUsers.find { it.idUser == id }?.let { user ->
                            onUserConfirmed(user)
                        }
                    }
                },
                enabled = selectedUserId != null
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Close")
            }
        },
        text = {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name or username...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    items(filteredUsers) { user ->
                        val isSelected = user.idUser == selectedUserId
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else Color.Transparent
                                )
                                .clickable { selectedUserId = user.idUser }
                                .padding(12.dp)
                        ) {
                            Text(
                                "👤 ${user.profileName}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "ID: ${user.idUser}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    )
}
