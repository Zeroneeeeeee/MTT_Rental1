package com.example.mtt_rental

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mtt_rental.ui.manager.PaymentScreen
import com.example.mtt_rental.ui.tenant.AppScreen
import com.example.mtt_rental.ui.tenant.SendReviewScreen
import com.example.mtt_rental.ui.tenant.mainscreen.UserScreen
import com.example.mtt_rental.ui.theme.MTT_RentalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MTT_RentalTheme {
                AppScreen()
                //RegisterScreen()
               // PreviewRoomManagement()
                //SendReviewScreen()
            }
        }
    }
}
