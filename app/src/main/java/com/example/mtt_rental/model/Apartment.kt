package com.example.mtt_rental.model

data class Apartment(
    val apartmentId: String= "",
    val title:String = "",
    val description:String = "",
    val location:String = "",
    val ownerId:String = "",
    val image:String = "",
    val rating: Double = 0.0,
)