package com.example.mtt_rental.dtmodel

data class RoomTypeVM(
    val idRoomType:String,
    val price: Long,
    val area:Long,
    val maxRenter:Int,
    val description:String,
    val roomList:List<RoomVM>,
    val roomServiceList:List<RoomServiceVM> = emptyList()
)