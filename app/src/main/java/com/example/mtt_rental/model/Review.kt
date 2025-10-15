package com.example.mtt_rental.model

data class Review(
    val idReview: String? = "",
    val idUser: String? = "",
    val idApartment: String? = "",
    val comment: String? = "",
    val rating: Double? = 0.0
)
