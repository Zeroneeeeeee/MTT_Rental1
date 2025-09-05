package com.example.mtt_rental.repo

import androidx.compose.runtime.mutableStateListOf

object UserRepo {
    var idUser: String = ""
    var profileName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var userType: String = ""
    var address: String? = null

    // Danh sách yêu thích local cho hiệu suất
    private val _favoriteApartmentIds = mutableStateListOf<String>()
    val favoriteApartmentIds: List<String> get() = _favoriteApartmentIds

    // Danh sách các đối tượng apartment yêu thích
    private val _favoriteApartments =
        mutableStateListOf<com.example.mtt_rental.ui.model.Apartment>()
    val favoriteApartments: List<com.example.mtt_rental.ui.model.Apartment> get() = _favoriteApartments

    fun updateUser(idUser:String, profileName:String, email:String, phoneNumber:String, userType:String, address:String?){
        this.idUser = idUser
        this.profileName = profileName
        this.email = email
        this.phoneNumber = phoneNumber
        this.userType = userType
        this.address = address

        // Load favorites từ Firebase khi user login
        loadUserFavorites()
    }

    // Load danh sách yêu thích từ Firebase
    private fun loadUserFavorites() {
        if (idUser.isNotEmpty()) {
            UserDB.getUserFavorites(idUser) { favorites ->
                _favoriteApartmentIds.clear()
                _favoriteApartmentIds.addAll(favorites)
                refreshFavoriteApartments()
            }
        }
    }

    // Refresh danh sách apartment objects từ IDs
    fun refreshFavoriteApartments() {
        if (_favoriteApartmentIds.isNotEmpty()) {
            ApartmentDB.getDepartments { allApartments ->
                val favorites = allApartments.filter { apartment ->
                    _favoriteApartmentIds.contains(apartment.apartmentId)
                }
                _favoriteApartments.clear()
                _favoriteApartments.addAll(favorites)
            }
        } else {
            _favoriteApartments.clear()
        }
    }

    // Thêm căn hộ vào danh sách yêu thích
    fun addToFavorites(apartmentId: String) {
        if (idUser.isNotEmpty() && !_favoriteApartmentIds.contains(apartmentId)) {
            _favoriteApartmentIds.add(apartmentId)
            UserDB.addApartmentToFavorites(idUser, apartmentId) { success ->
                if (success) {
                    refreshFavoriteApartments()
                }
            }
        }
    }

    // Xóa căn hộ khỏi danh sách yêu thích
    fun removeFromFavorites(apartmentId: String) {
        if (idUser.isNotEmpty()) {
            _favoriteApartmentIds.remove(apartmentId)
            UserDB.removeApartmentFromFavorites(idUser, apartmentId) { success ->
                if (success) {
                    refreshFavoriteApartments()
                }
            }
        }
    }

    // Kiểm tra căn hộ có trong danh sách yêu thích không
    fun isFavorite(apartmentId: String): Boolean {
        return _favoriteApartmentIds.contains(apartmentId)
    }

    // Toggle trạng thái yêu thích
    fun toggleFavorite(apartmentId: String) {
        if (isFavorite(apartmentId)) {
            removeFromFavorites(apartmentId)
        } else {
            addToFavorites(apartmentId)
        }
    }
}
