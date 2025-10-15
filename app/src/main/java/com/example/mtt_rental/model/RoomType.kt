package com.example.mtt_rental.model

data class RoomType(
    val idRoomType: String = "",
    val idApartment: String = "",
    val maxRenter: Long = 0,
    val price: Long = 0,
    val area: Long = 0,
    val description: String = ""
)