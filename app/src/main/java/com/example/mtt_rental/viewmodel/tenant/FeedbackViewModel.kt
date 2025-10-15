package com.example.mtt_rental.viewmodel.tenant

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Feedback
import androidx.compose.runtime.State
import com.example.mtt_rental.viewmodel.repo.FeedbackRepository
import com.google.firebase.database.FirebaseDatabase

class FeedbackViewModel : ViewModel() {
    private val dbFeedback = FirebaseDatabase.getInstance().getReference("feedbacks")

    private val repo = FeedbackRepository()

    private val _feedbacks = mutableStateOf<List<Feedback>>(emptyList())
    val feedbacks: State<List<Feedback>> = _feedbacks

    fun sendFeedback(
        senderId: String,
        readerId: String,
        content: String,
        onComplete: (Boolean) -> Unit
    ) {
        if (readerId.isBlank()) {
            onComplete(false)
            return
        }

        val newRef = dbFeedback.push()
        val fb = Feedback(
            idFeedback = newRef.key ?: "",
            idSender = senderId,
            idReceiver = readerId,
            content = content,
        )
        newRef.setValue(fb)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun loadFeedbacksByReceiver(userId: String) {
        repo.getFeedbacksByReceiver(
            userId,
            onResult = { list ->
                _feedbacks.value = list
            },
            onError = { e ->
                println("Error loading feedbacks: ${e.message}")
            }
        )
    }

    fun loadFeedbacksBySender(userId: String) {
        repo.getFeedbacksBySender(
            userId,
            onResult = { list ->
                _feedbacks.value = list
            },
            onError = { e ->
                println("Error loading feedbacks: ${e.message}")
            }
        )
    }

    fun replyToFeedback(feedback: Feedback, currentUserId: String, replyContent: String, onComplete: (Boolean, String) -> Unit) {
        if (currentUserId != feedback.idReceiver) {
            onComplete(false, "Bạn không có quyền trả lời feedback này")
            return
        }

        dbFeedback.child(feedback.idFeedback).child("reply")
            .setValue(replyContent)
            .addOnSuccessListener { onComplete(true, "Trả lời thành công") }
            .addOnFailureListener { onComplete(false, "Lỗi khi gửi trả lời") }
    }
}
