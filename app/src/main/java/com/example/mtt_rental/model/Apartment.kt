package com.example.mtt_rental.model

data class Apartment(
    val apartmentId: String= "",
    val title:String = "",
    val description:String = "",
    val location:String = "",
    val ownerId:String = "",
    val price:Int=0,
    val image:String = "",
    val maxRenter: Int=0,
    val area: Double = 0.0,
    val rating: Double = 0.0,
)