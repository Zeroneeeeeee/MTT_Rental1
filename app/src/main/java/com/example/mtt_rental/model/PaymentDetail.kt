package com.example.mtt_rental.model

data class PaymentDetail(
    val idPayment : String ="",
    val idRoomService: String="",
    val idPaymentDetail: String="",
    val amount:Int = 0
)
