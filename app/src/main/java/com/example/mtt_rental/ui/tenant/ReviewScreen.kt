package com.example.mtt_rental.ui.tenant

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.utils.UserRepo
import com.example.mtt_rental.viewmodel.tenant.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendReviewScreen(
    apartmentId: String,
    reviewViewModel: ReviewViewModel = viewModel(),
    onReviewSent: () -> Unit = {}
) {
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0.0) }
    var isSending by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Review", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Comment") },
            modifier = Modifier.fillMaxWidth()
        )

        // Rating nhập bằng Slider (0 -> 5)
        Text("Rating: ${rating.toInt()} / 5")
        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toDouble() },
            valueRange = 0f..5f,
            steps = 4
        )

        Button(
            onClick = {
                isSending = true
                reviewViewModel.sendReview(UserRepo.idUser, apartmentId, comment, rating) { success ->
                    isSending = false
                    if (success) {
                        message = "Send review successfully"
                        onReviewSent()
                    } else {
                        message = "Please try again"
                    }
                }
            },
            enabled = !isSending,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSending) "Sending..." else "Send Review")
        }

        if (message.isNotEmpty()) {
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
