package com.example.mtt_rental.viewmodel.tenant

import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Review
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class ReviewViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().getReference("reviews")

    fun sendReview(userId: String, apartmentId: String, comment: String, rating: Double, onComplete: (Boolean) -> Unit) {
        val userReview = dbRef.child(userId).child(apartmentId)
        val reviewId = UUID.randomUUID().toString()
        val review = Review(
            idReview = reviewId,
            idUser = userId,
            idApartment = apartmentId,
            comment = comment,
            rating = rating
        )

        userReview.setValue(review)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
