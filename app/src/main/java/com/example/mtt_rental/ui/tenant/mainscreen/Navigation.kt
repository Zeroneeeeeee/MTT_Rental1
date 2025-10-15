package com.example.mtt_rental.ui.tenant.mainscreen

import android.util.Log
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
import com.example.mtt_rental.ui.tenant.FeedbackListScreen
import com.example.mtt_rental.ui.tenant.FeedbackScreen
import com.example.mtt_rental.ui.tenant.RentScreen
import com.example.mtt_rental.ui.tenant.SendReviewScreen

@Composable
fun UserScreen(toLogin: () -> Unit = {}) {
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
                        ProfileScreen(
                            toReviewScreen = { backStack.add(Screen.ReviewScreen(it)) },
                            toFeedbackScreen = { backStack.add(Screen.FeedbackScreen(it)) },
                            toLogin = toLogin
                        )
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
                        RentScreen(idApartment = id, toHome = { backStack.add(Screen.HomeScreen) })
                    }
                    entry<Screen.ReviewScreen> { (apartmentId) ->
                        SendReviewScreen(apartmentId)
                    }
                    entry<Screen.FeedbackScreen> { (managerId) ->
                        Log.d("TAG", "UserScreen: $managerId")
                        FeedbackListScreen(onAddFeedback = {
                            backStack.add(
                                Screen.AddFeedbackScreen(
                                    managerId
                                )
                            )
                        })
                    }
                    entry<Screen.AddFeedbackScreen> { (receiverId) ->
                        FeedbackScreen(
                            managerId = receiverId,
                            toProfileScreen = {
                            backStack.clear()
                            backStack.add(Screen.ProfileScreen)
                        })
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
