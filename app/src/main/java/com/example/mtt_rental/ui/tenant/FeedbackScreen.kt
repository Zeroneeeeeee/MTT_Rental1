package com.example.mtt_rental.ui.tenant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.model.User
import com.example.mtt_rental.utils.UserRepo
import com.example.mtt_rental.viewmodel.tenant.FeedbackViewModel

@Composable
fun FeedbackScreen(
    managerId: String, // passed from outside
    viewModel: FeedbackViewModel = viewModel(),
    toProfileScreen:() -> Unit
) {
    var feedbackText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Send Feedback", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth().height(150.dp)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (feedbackText.isNotBlank()) {
                    viewModel.sendFeedback(UserRepo.idUser, managerId, feedbackText) { success ->
                        message = if (success) "Sent successfully" else "Failed to send"
                        if (success) feedbackText = ""
                    }
                    toProfileScreen()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Send")
        }

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                message,
                color = if (message.contains("successfully")) Color.Green else Color.Red
            )
        }
    }
}
