package com.example.mtt_rental.viewmodel.tenant

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.repo.ApartmentRepository

class HomeViewModel : ViewModel() {

    private val _apartmentList = mutableStateOf<List<Apartment>>(emptyList())
    val apartmentList: State<List<Apartment>> = _apartmentList

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _filteredApartmentList = mutableStateOf<List<Apartment>>(emptyList())
    val filteredApartmentList: State<List<Apartment>> = _filteredApartmentList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        loadApartments()
    }

    private fun loadApartments() {
        _isLoading.value = true
        ApartmentRepository.getAllApartments(
            onResult = { apartments ->
                _apartmentList.value = apartments
                updateFilteredList()
                _isLoading.value = false
            },
            onError = { error ->
                _error.value = "Lỗi tải dữ liệu: ${error.message}"
                _isLoading.value = false
            }
        )
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredList()
    }

    private fun updateFilteredList() {
        val query = _searchQuery.value
        val apartments = _apartmentList.value

        _filteredApartmentList.value = if (query.isEmpty()) {
            apartments
        } else {
            apartments.filter { apartment ->
                apartment.title.contains(query, ignoreCase = true) ||
                        apartment.location.contains(query, ignoreCase = true) ||
                        apartment.description.contains(query, ignoreCase = true)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}