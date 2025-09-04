package com.example.mtt_rental.ui.tenant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight

@Preview(showBackground = true)
@Composable
fun ChatScreen() {
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("Xin chào! Bạn cần thuê gì?", isMe = false),
                ChatMessage("Mình có thể giúp gì cho bạn?", isMe = false),
                ChatMessage("Cho mình hỏi căn hộ còn không?", isMe = true),
                ChatMessage("Căn hộ đó còn nhé!", isMe = false)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(8.dp)
    ) {
        Text("Chat", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEFB8C8))
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            MessagesList(messages = messages)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Nhập tin nhắn...") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (message.text.isNotBlank()) {
                        messages = listOf(ChatMessage(message.text, isMe = true)) + messages
                        message = TextFieldValue("")
                    }
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFB8C8)),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Text("Gửi", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MessagesList(messages: List<ChatMessage>) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 8.dp)) {
        for (msg in messages) {
            ChatBubble(msg)
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = if (message.isMe) 14.dp else 0.dp,
                topEnd = if (message.isMe) 0.dp else 14.dp,
                bottomEnd = 14.dp, bottomStart = 14.dp
            ),
            color = if (message.isMe) Color(0xFFEFB8C8) else Color.White,
            tonalElevation = if (message.isMe) 2.dp else 1.dp
        ) {
            Text(
                message.text,
                modifier = Modifier.padding(12.dp),
                fontSize = 16.sp,
                color = if (message.isMe) Color.White else Color.Black
            )
        }
    }
}

// Model chat message
class ChatMessage(val text: String, val isMe: Boolean)
