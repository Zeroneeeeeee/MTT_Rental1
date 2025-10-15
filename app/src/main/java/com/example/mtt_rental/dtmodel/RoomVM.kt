package com.example.mtt_rental.dtmodel

import com.example.mtt_rental.model.User

data class RoomVM(
    val idRoom: String = "",
    val idRoomType: String = "",
    val name: String = "",
    val floor: Int = 0,
    val tenants: List<User> = emptyList()
)
