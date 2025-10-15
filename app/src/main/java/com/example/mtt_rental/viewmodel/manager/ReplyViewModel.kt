package com.example.mtt_rental.viewmodel.manager

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import com.example.mtt_rental.model.Feedback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReplyViewModel: ViewModel() {
    private val db = FirebaseDatabase.getInstance().getReference("feedbacks")

    private val _feedback = mutableStateOf<Feedback?>(null)
    val feedback: State<Feedback?> = _feedback

    // Load feedback theo id
    fun loadFeedback(feedbackId: String) {
        db.child(feedbackId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fb = snapshot.getValue(Feedback::class.java)
                    _feedback.value = fb
                }

                override fun onCancelled(error: DatabaseError) {
                    _feedback.value = null
                }
            })
    }

    // Cập nhật feedback (reply + status)
    fun updateFeedback(feedback: Feedback) {
        db.child(feedback.idFeedback)
            .setValue(feedback)
            .addOnSuccessListener {
                Log.d("FeedbackVM", "Feedback updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("FeedbackVM", "Error updating feedback", e)
            }
    }
}