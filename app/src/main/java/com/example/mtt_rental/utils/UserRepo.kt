package com.example.mtt_rental.utils

import androidx.compose.runtime.mutableStateListOf
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.viewmodel.repo.ApartmentDB
import com.example.mtt_rental.viewmodel.repo.UserDB

object UserRepo {
    var idUser: String = ""
    var profileName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var userType: String = ""
    var address: String? = null

    private val _favoriteApartmentIds = mutableStateListOf<String>()
    val favoriteApartmentIds: List<String> get() = _favoriteApartmentIds

    // Danh sách các đối tượng apartment yêu thích
    private val _favoriteApartments =
        mutableStateListOf<Apartment>()
    val favoriteApartments: List<Apartment> get() = _favoriteApartments

    fun updateUser(idUser:String, profileName:String, email:String, phoneNumber:String, userType:String, address:String?){
        this.idUser = idUser
        this.profileName = profileName
        this.email = email
        this.phoneNumber = phoneNumber
        this.userType = userType
        this.address = address
        loadUserFavorites()
    }

    fun clearUser(){
        this.idUser = ""
        this.profileName = ""
        this.email = ""
        this.phoneNumber = ""
        this.userType = ""
        this.address = ""
    }

    private fun loadUserFavorites() {
        if (idUser.isNotEmpty()) {
            UserDB.getUserFavorites(idUser) { favorites ->
                _favoriteApartmentIds.clear()
                _favoriteApartmentIds.addAll(favorites)
                refreshFavoriteApartments()
            }
        }
    }

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

    fun isFavorite(apartmentId: String): Boolean {
        return _favoriteApartmentIds.contains(apartmentId)
    }

    fun toggleFavorite(apartmentId: String) {
        if (isFavorite(apartmentId)) {
            removeFromFavorites(apartmentId)
        } else {
            addToFavorites(apartmentId)
        }
    }
}