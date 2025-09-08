package com.example.mtt_rental

sealed interface Screen {
    data object LoginScreen: Screen
    data object RegisterScreen: Screen
    data object OTPVerificationScreen : Screen
    data object HomeScreen: Screen
    data object MainScreen: Screen
    data object FavoriteScreen: Screen
    data object ProfileScreen: Screen

    data class DetailScreen(val id: String): Screen
    data object AddApartment: Screen

    // Manager screens
    data object ManagerMainScreen : Screen
    data object ManagerDashboardScreen : Screen
    data object ManagerManageScreen : Screen
    data object ManagerProfileScreen : Screen
    data class ManagerAddApartmentScreen(val updateId:String = "") : Screen
}