package com.example.mtt_rental.model

data class Contract(
    val idContract: String = "",
    val idUser: String = "",
    val idRoom: String = "",
    val status:String = "",
    val startTime: Long = 0L,   // lưu millis (timestamp)
    val endTime: Long = 0L
)