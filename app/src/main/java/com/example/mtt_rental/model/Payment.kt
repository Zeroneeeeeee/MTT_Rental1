package com.example.mtt_rental.model

data class Payment(
    val idPayment: String = "",
    val idContract: String = "",
    val total:Int = 0,
    val status: String = "",
    val timeStamp: Long = 0L
)
