package com.example.mtt_rental.ui.tenant.mainscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.mtt_rental.Screen
import com.example.mtt_rental.ui.tenant.DetailsScreen
import com.example.mtt_rental.ui.tenant.RentScreen

@Composable
fun UserScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val backStack = remember { mutableStateListOf<Screen>(Screen.HomeScreen) }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<Screen.HomeScreen> {
                        HomeScreen(toDetail = {
                            backStack.add(Screen.DetailScreen(it))
                        })
                    }
                    entry<Screen.ProfileScreen> {
                        ProfileScreen()
                    }
                    entry<Screen.FavoriteScreen> {
                        FavoriteScreen()
                    }
                    entry<Screen.DetailScreen> { (id) ->
                        DetailsScreen(
                            apartmentId = id,
                            toRentScreen = { backStack.add(Screen.RentScreen(it)) }
                        )
                    }
                    entry<Screen.RentScreen> { (id) ->
                        RentScreen(idApartment = id)
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
                    backStack.add(Screen.HomeScreen)
                    selectedTab = 0
                },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") }
            )
            NavigationBarItem(
                selected = selectedTab == 1,
                onClick = {
                    backStack.clear()
                    backStack.add(Screen.FavoriteScreen)
                    selectedTab = 1
                },
                icon = {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Favorite"
                    )
                },
                label = { Text("Favorite") }
            )
            NavigationBarItem(
                selected = selectedTab == 2,
                onClick = {
                    backStack.clear()
                    backStack.add(Screen.ProfileScreen)
                    selectedTab = 2
                },
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile") }
            )
        }
    }
}
