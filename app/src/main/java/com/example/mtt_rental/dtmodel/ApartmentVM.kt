package com.example.mtt_rental.dtmodel

data class ApartmentVM(
    val apartmentId: String= "",
    val title:String = "",
    val location:String = "",
    val ownerId:String = "",
    val image:String = "",
    val roomType: List<RoomTypeVM>
)