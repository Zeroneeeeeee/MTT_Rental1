package com.example.mtt_rental.ui.manager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.model.Feedback
import com.example.mtt_rental.viewmodel.manager.ReplyViewModel
import com.example.mtt_rental.viewmodel.tenant.FeedbackViewModel

@Composable
fun FeedbackReplyScreen(
    feedbackId: String,
    viewModel: ReplyViewModel = viewModel()
) {
    val feedback by viewModel.feedback
    var replyText by remember { mutableStateOf("") }

    // Load feedback khi mở màn hình
    LaunchedEffect(feedbackId) {
        viewModel.loadFeedback(feedbackId)
    }

    if (feedback == null) {
        // Loading hoặc không có dữ liệu
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFEFB8C8))
        }
    } else {
        val fb = feedback!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- Hiển thị content ---
            Text(
                text = "Feedback from ${fb.idSender}:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF5F5F5),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = fb.content,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 15.sp
                )
            }

            if (fb.status == "replied") {
                // --- Nếu đã reply, chỉ hiển thị reply ---
                Text(
                    text = "Your Reply:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF4CAF50)
                )
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = fb.reply,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            } else {
                // --- Nếu chưa reply, hiển thị ô nhập ---
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("Your reply") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, false),
                    maxLines = 5
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updated = fb.copy(
                            reply = replyText,
                            status = "replied"
                        )
                        viewModel.updateFeedback(updated)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFB8C8))
                ) {
                    Text("Send Reply", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
