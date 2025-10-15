package com.example.mtt_rental.viewmodel.manager

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.utils.UserRepo
import com.example.mtt_rental.viewmodel.repo.ApartmentRepository

class ManagerManageViewModel : ViewModel() {

    private val _apartmentList = mutableStateOf<List<Apartment>>(emptyList())
    val apartmentList: State<List<Apartment>> = _apartmentList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _deleteResult = mutableStateOf<DeleteResult?>(null)
    val deleteResult: State<DeleteResult?> = _deleteResult

    init {
        loadManagerApartments()
    }

    private fun loadManagerApartments() {
        _isLoading.value = true
        ApartmentRepository.getApartmentsByOwner(
            ownerId = UserRepo.idUser,
            onResult = { apartments ->
                _apartmentList.value = apartments
                _isLoading.value = false
            },
            onError = { error ->
                _error.value = "Lỗi tải dữ liệu: ${error.message}"
                _isLoading.value = false
            }
        )
    }

    fun deleteApartment(apartmentId: String) {
        _isLoading.value = true
        ApartmentRepository.deleteApartment(
            apartmentId = apartmentId,
            onSuccess = {
                _deleteResult.value = DeleteResult.Success("Xóa apartment thành công")
                _isLoading.value = false
            },
            onError = { exception ->
                _deleteResult.value = DeleteResult.Error("Lỗi xóa apartment: ${exception.message}")
                _isLoading.value = false
            }
        )
    }

    fun clearDeleteResult() {
        _deleteResult.value = null
    }

    fun clearError() {
        _error.value = null
    }
}

sealed class DeleteResult {
    data class Success(val message: String) : DeleteResult()
    data class Error(val message: String) : DeleteResult()
}