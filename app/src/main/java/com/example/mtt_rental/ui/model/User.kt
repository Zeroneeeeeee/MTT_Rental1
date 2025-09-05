package com.example.mtt_rental.ui.model

data class User(
    val idUser: String = "",
    val password: String = "",
    val profileName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val userType: String = "",
    val address: String? = null,   // dấu ? nghĩa là có thể null
    val favoriteApartments: List<String> = emptyList() // Danh sách ID các căn hộ yêu thích
)
