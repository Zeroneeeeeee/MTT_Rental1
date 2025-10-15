package com.example.mtt_rental.model

data class Feedback(
    val idFeedback: String = "",
    val idSender: String = "",   // idUser gửi feedback
    val idReceiver: String = "", // idUser nhận feedback (Manager hoặc User)
    val content: String = "",
    val reply: String = "",
    val timeStamp: Long = System.currentTimeMillis(),
    val status: String = "pending" // pending, seen, resolved...
)
