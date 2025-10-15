package com.example.mtt_rental.viewmodel.tenant

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.model.Review
import com.example.mtt_rental.viewmodel.repo.ApartmentRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val reviewsRef = database.getReference("reviews")

    private val _apartmentList = mutableStateOf<List<Apartment>>(emptyList())
    val apartmentList: State<List<Apartment>> = _apartmentList

    private val _ratings = mutableStateOf<Map<String, Double>>(emptyMap())
    val ratings: State<Map<String, Double>> = _ratings

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

                // Tải rating trung bình cho từng apartment
                apartments.forEach { apt ->
                    loadAverageRating(apt.apartmentId){
                        _ratings.value = _ratings.value.toMutableMap().apply {
                            put(apt.apartmentId, it)
                        }
                    }
                }
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

    // --- Hàm gộp từ ReviewRepository ---
    fun loadAverageRating(apartmentId: String, onResult: (Double) -> Unit) {
        reviewsRef.child("user")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ratings = mutableListOf<Double>()
                    for (child in snapshot.children) {
                        val review = child.getValue(Review::class.java)
                        if (review != null && review.idApartment == apartmentId) {
                            ratings.add(review.rating?:0.0)
                        }
                    }

                    val avg = if (ratings.isNotEmpty()) {
                        ratings.average()
                    } else 0.0

                    onResult(avg)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(0.0)
                }
            })
    }
}
