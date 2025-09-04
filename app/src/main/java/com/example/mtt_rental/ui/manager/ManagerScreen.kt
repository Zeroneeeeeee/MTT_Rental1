package com.example.mtt_rental.ui.manager

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
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.mtt_rental.Screen
import com.example.mtt_rental.ui.tenant.DetailsScreen
import java.util.Map.entry

@Preview(showBackground = true)
@Composable
fun ManagerScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val backStack = remember { mutableStateListOf<Screen>(Screen.ManagerDashboardScreen) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<Screen.ManagerDashboardScreen> {
                        ManagerDashboardScreen()
                    }
                    entry<Screen.ManagerManageScreen> {
                        ManagerManageScreen(
                            toAddRenterScreen = { backStack.add(Screen.ManagerAddApartmentScreen("")) },
                            toEditScreen = { apartmentId ->
                                backStack.add(Screen.ManagerAddApartmentScreen(apartmentId))
                            },
                            toDetailScreen = { apartmentId ->
                                backStack.add(Screen.DetailScreen(apartmentId))
                            }
                        )
                    }
                    entry<Screen.ManagerProfileScreen> {
                        ManagerProfileScreen()
                    }
                    entry<Screen.ManagerAddApartmentScreen> {(id) ->
                        ManagerAddRentalScreen(id)
                    }
                    entry<Screen.DetailScreen> {(id) ->
                        DetailsScreen(id)
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