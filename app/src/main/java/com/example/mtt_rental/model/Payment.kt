package com.example.mtt_rental.model

data class Payment(
    val idPayment: String = "",
    val idContract: String = "",
    val idService: String = "",
    val usage: Double = 0.0,
    val timeStamp: Long = 0L
)
