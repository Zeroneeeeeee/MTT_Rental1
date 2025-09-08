package com.example.mtt_rental.ui.tenant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.mtt_rental.Screen
import com.example.mtt_rental.model.User
import com.example.mtt_rental.ui.manager.ManagerAddRentalScreen
import com.example.mtt_rental.ui.manager.ManagerScreen
import com.example.mtt_rental.ui.tenant.mainscreen.UserScreen

@Composable
fun AppScreen() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.LoginScreen) }
    var pendingUserData: User? = null
    var pendingPhoneNumber: String = ""

    NavDisplay(
        backStack = backStack,
        onBack = {backStack.removeLastOrNull()},
        entryProvider = entryProvider{
            entry(Screen.LoginScreen) {
                LoginScreen(
                    toRegister = {backStack.add(Screen.RegisterScreen)},
                    toUserScreen = {
                        backStack.clear()
                        backStack.add(Screen.MainScreen)
                    },
                    toManagerScreen = {
                        backStack.clear()
                        backStack.add(Screen.ManagerMainScreen)
                    }
                )
            }
            entry(Screen.RegisterScreen){
                RegisterScreen(
                    toLogin = {
                        backStack.clear()
                        backStack.add(Screen.LoginScreen)
                    },
                    toOTPVerification = { phoneNumber, userData ->
                        pendingPhoneNumber = phoneNumber
                        pendingUserData = userData
                        backStack.add(Screen.OTPVerificationScreen)
                    }
                )
            }
            entry(Screen.OTPVerificationScreen) {
                OTPVerificationScreen(
                    phoneNumber = pendingPhoneNumber,
                    userData = pendingUserData ?: User("", "", "", "", "", ""),
                    onVerificationSuccess = {
                        backStack.clear()
                        backStack.add(Screen.LoginScreen)
                    },
                    onBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry(Screen.MainScreen){
                UserScreen()
            }
            entry(Screen.ManagerMainScreen) {
                ManagerScreen()
            }
            entry(Screen.AddApartment){
                ManagerAddRentalScreen()
            }
        }
    )
}