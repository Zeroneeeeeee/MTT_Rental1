package com.example.mtt_rental.ui.manager

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.mtt_rental.Screen
import com.example.mtt_rental.dtmodel.RoomTypeVM
import com.example.mtt_rental.ui.tenant.DetailsScreen
import com.example.mtt_rental.viewmodel.manager.ManagerScreenViewModel

@Preview(showBackground = true)
@Composable
fun ManagerScreen(viewModel: ManagerScreenViewModel = viewModel(), toLogin: () -> Unit = {}){
    var selectedTab by remember { mutableIntStateOf(0) }
    val roomTypeList = remember { mutableStateListOf<RoomTypeVM>() }
    val backStack = remember { mutableStateListOf<Screen>(Screen.ManagerDashboardScreen) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<Screen.ManagerDashboardScreen> {
                        ManagerDashboardScreen(onFeedbackClick = {
                            backStack.add(
                                Screen.ReplyScreen(
                                    it
                                )
                            )
                        })
                    }
                    entry<Screen.ManagerManageScreen> {
                        ManagerManageScreen(
                            toAddRenterScreen = { backStack.add(Screen.ManagerAddApartmentScreen("")) },
                            toEditScreen = { apartmentId ->
                                Log.d("ManagerManageScreen", apartmentId)
                                viewModel.loadRoomTypesAndRooms(apartmentId) { list ->
                                    roomTypeList.clear()
                                    roomTypeList.addAll(list)
                                    backStack.add(Screen.ManagerAddApartmentScreen(apartmentId))
                                }
                            },
                            toRoomManagementScreen = {
                                backStack.add(Screen.RoomManageScreen(it))
                            },
                            toDetailScreen = { apartmentId ->
                                backStack.add(Screen.DetailScreen(apartmentId))
                            }
                        )
                    }
                    entry<Screen.ManagerProfileScreen> {
                        ManagerProfileScreen(toLogin = toLogin)
                    }
                    entry<Screen.ManagerAddApartmentScreen> { screen ->
                        ManagerAddRentalScreen(
                            roomTypeList = roomTypeList,
                            editApartmentId = screen.updateId,
                            toManageScreen = {
                                backStack.clear()
                                backStack.add(Screen.ManagerManageScreen)
                                roomTypeList.clear()
                            },
                            toAddRoomScreen = { backStack.add(Screen.AddRoomScreen(screen.updateId)) },
                            toEditRoomScreen = {  apartmentId, roomTypeId ->
                                backStack.add(Screen.EditRoomScreen(apartmentId, roomTypeId))
                            }
                        )
                    }
                    entry<Screen.DetailScreen> { (id) ->
                        DetailsScreen(id)
                    }
                    entry<Screen.AddRoomScreen> { (id) ->
                        AddRoomScreen(
                            onAddRoomTypeEvent = {
                                roomTypeList.add(it)
                                backStack.add(Screen.ManagerAddApartmentScreen(id))//
                            }
                        )
                    }
                    entry<Screen.RoomManageScreen> { (id) ->
                        RoomManagementScreen(
                            apartmentId = id,
                            toPaymentScreen = { apartmentId, roomId ->
                                backStack.add(Screen.PaymentScreen(apartmentId, roomId))
                            })
                    }
                    entry<Screen.PaymentScreen> { (apartmentId, roomTypeId) ->
                        PaymentScreen(idApartment = apartmentId, idRoomType = roomTypeId)
                    }
                    entry<Screen.ReplyScreen> { (feedbackId) ->
                        FeedbackReplyScreen(feedbackId)
                    }
                    entry<Screen.EditRoomScreen> { (apartmentId, roomTypeId) ->
                        AddRoomScreen(idRoomType = roomTypeId, idApartment = apartmentId)
                    }
                }
            )
        }

        NavigationBar(
            containerColor = Color(0xFFEFB8C8)
        ) {
            NavigationBarItem(
                selected = selectedTab == 0,
                onClick = {
                    backStack.clear()
                    backStack.add(Screen.ManagerDashboardScreen)
                    selectedTab = 0
                },
                icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                label = { Text("Dashboard") }
            )

            NavigationBarItem(
                selected = selectedTab == 1,
                onClick = {
                    backStack.clear()
                    backStack.add(Screen.ManagerManageScreen)
                    selectedTab = 1
                },
                icon = { Icon(Icons.Default.Star, contentDescription = "Manage") },
                label = { Text("Manage") }
            )

            NavigationBarItem(
                selected = selectedTab == 2,
                onClick = {
                    backStack.clear()
                    backStack.add(Screen.ManagerProfileScreen)
                    selectedTab = 2
                },
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile") }
            )
        }
    }
}