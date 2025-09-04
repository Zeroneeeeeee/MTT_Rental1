package com.example.mtt_rental.ui.model

data class Contract(
    val idContract: String = "",
    val idUser: String = "",
    val idApartment: String = "",
    val startTime: Long = 0L,   // lưu millis (timestamp)
    val endTime: Long = 0L
)

