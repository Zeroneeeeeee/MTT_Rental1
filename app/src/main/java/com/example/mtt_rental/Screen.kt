package com.example.mtt_rental

import com.example.mtt_rental.dtmodel.RoomTypeVM

sealed interface Screen {
    data object LoginScreen : Screen
    data object RegisterScreen : Screen
    data object OTPVerificationScreen : Screen
    data object HomeScreen : Screen
    data object MainScreen : Screen
    data object FavoriteScreen : Screen
    data object ProfileScreen : Screen
    data class RentScreen(val id: String) : Screen

    data class DetailScreen(val id: String) : Screen

    // Manager screens
    data object ManagerMainScreen : Screen
    data object ManagerDashboardScreen : Screen
    data object ManagerManageScreen : Screen
    data object ManagerProfileScreen : Screen
    data class ManagerAddApartmentScreen(
        val updateId: String = ""
    ) : Screen

    data class AddRoomScreen(val id: String) : Screen
}