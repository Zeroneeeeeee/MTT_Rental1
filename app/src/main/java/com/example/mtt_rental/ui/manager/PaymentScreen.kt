package com.example.mtt_rental.ui.manager

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.viewmodel.manager.PaymentViewModel

@Composable
fun PaymentScreen(
    idApartment: String,
    idRoomType: String,
    viewModel: PaymentViewModel = viewModel()
) {
    val roomType by viewModel.roomType
    val services by viewModel.services
    val total by viewModel.total

    // Load data when entering the screen
    LaunchedEffect(idRoomType) {
        viewModel.loadRoomType(idApartment, idRoomType)
        viewModel.loadServices(idApartment, idRoomType)
        Log.d("PaymentScreen", "PaymentScreen: $roomType")
    }

    if (roomType == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading data...")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Rental Payment", style = MaterialTheme.typography.headlineSmall)
            Text("Room Price: ${roomType!!.price} VND")

            Spacer(Modifier.height(8.dp))
            Text("Services:", style = MaterialTheme.typography.titleMedium)

            services.forEach { service ->
                var input by remember { mutableStateOf("") }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(service.name, style = MaterialTheme.typography.bodyLarge)
                        Text("Unit Price: ${service.fee} VND/${service.unit}")
                    }

                    TextField(
                        value = input,
                        onValueChange = {
                            input = it
                            val amt = it.toIntOrNull() ?: 0
                            viewModel.updateAmount(service.idRoomService, amt)
                        },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("Quantity") }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Total: $total VND",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


