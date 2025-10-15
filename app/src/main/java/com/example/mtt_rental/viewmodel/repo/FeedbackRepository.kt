package com.example.mtt_rental.viewmodel.repo

import com.example.mtt_rental.model.Feedback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FeedbackRepository {

    private val database = FirebaseDatabase.getInstance().getReference("feedbacks")

    fun getFeedbacksByReceiver(
        receiverId: String,
        onResult: (List<Feedback>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        database.orderByChild("idReceiver").equalTo(receiverId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val feedbacks = snapshot.children.mapNotNull { it.getValue(Feedback::class.java) }
                    onResult(feedbacks)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            }
            )
    }
    fun getFeedbacksBySender(
        senderId: String,
        onResult: (List<Feedback>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        database.orderByChild("idSender").equalTo(senderId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val feedbacks = snapshot.children.mapNotNull { it.getValue(Feedback::class.java) }
                    onResult(feedbacks)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }
}
