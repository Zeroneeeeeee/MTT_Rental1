package com.example.mtt_rental.ui.model

data class Service(
    val idService: String = "",
    val name: String = "",
    val param: String = "",   // đơn vị sử dụng, ví dụ "kWh", "m³", "GB"
    val fee: Double = 0.0     // đơn giá (có thể số lẻ)
)
