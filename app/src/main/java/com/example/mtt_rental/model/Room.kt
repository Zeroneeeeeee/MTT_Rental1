package com.example.mtt_rental.model

data class Room(
    val idRoom:String,
    val idApartment:String,
    val area:Long,
    val maxRenter:Int,
    val description:String,
)