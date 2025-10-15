package com.example.mtt_rental.viewmodel.tenant

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.model.Contract
import com.example.mtt_rental.model.Review
import com.example.mtt_rental.model.RoomService
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.viewmodel.repo.ApartmentDB
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApartmentDetailsViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val contractsRef = FirebaseDatabase.getInstance().getReference("contracts")
    private val reviewsRef = FirebaseDatabase.getInstance().getReference("reviews")

    private val _apartment = mutableStateOf(Apartment())
    val apartment: State<Apartment> = _apartment

    private val _services = mutableStateOf<List<RoomService>>(emptyList())
    val services: State<List<RoomService>> = _services

    private val _roomTypes = mutableStateOf<List<RoomType>>(emptyList())
    val roomTypes: State<List<RoomType>> = _roomTypes

    private val _review = mutableStateOf<List<Review>>(emptyList())
    val reviews: State<List<Review>> = _review

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun loadApartmentById(apartmentId: String) {
        _isLoading.value = true
        _error.value = null

        ApartmentDB.getDepartmentById(
            id = apartmentId,
            onResult = { apartment ->
                _isLoading.value = false
                if (apartment != null) {
                    _apartment.value = apartment
                } else {
                    _error.value = "Không tìm thấy dữ liệu apartment"
                }
            },
            onError = { error ->
                _isLoading.value = false
                _error.value = "Lỗi: ${error.message}"
            }
        )
    }

    fun loadRoomTypes(apartmentId: String) {
        _isLoading.value = true
        _error.value = null

        ApartmentDB.getRoomTypesByApartmentId(
            id = apartmentId,
            onResult = { list ->
                _isLoading.value = false
                _roomTypes.value = list ?: emptyList()
            },
            onError = { error ->
                _isLoading.value = false
                _error.value = "Lỗi: ${error.message}"
            }
        )
    }

    fun loadServices(apartmentId: String, roomTypeId: String) {
        database.getReference("apartments")
            .child(apartmentId)
            .child("roomTypes")
            .child(roomTypeId)
            .child("services")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull {
                        it.getValue(RoomService::class.java)?.copy(
                            idRoomService = it.key ?: ""
                        )
                    }
                    _services.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    _error.value = "Lỗi load services: ${error.message}"
                }
            })
    }

    fun createContract(
        idUser: String,
        idRoom: String,
        status: String = "None",
        startTime: Long = System.currentTimeMillis(),
        endTime: Long = 0L,
        onResult: (Boolean, String) -> Unit
    ) {
        _isLoading.value = true
        _error.value = null

        val idContract = "${idUser}_${idRoom}"

        val contract = Contract(
            idContract = idContract,
            idUser = idUser,
            idRoom = idRoom,
            status = status,
            startTime = startTime,
            endTime = endTime
        )

        contractsRef.child(idContract).setValue(contract)
            .addOnSuccessListener {
                _isLoading.value = false
                onResult(true, "Tạo contract thành công")
            }
            .addOnFailureListener { ex ->
                _isLoading.value = false
                _error.value = "Lỗi khi tạo contract: ${ex.message}"
                onResult(false, "Lỗi khi tạo contract: ${ex.message}")
            }
    }
    fun loadReviewsByApartment(apartmentId: String) {
        val ref = reviewsRef.child("user")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviewList = mutableListOf<Review>()
                for (child in snapshot.children) {
                    val review = child.getValue(Review::class.java)
                    if (review != null && review.idApartment == apartmentId) {
                        reviewList.add(review)
                    }
                }
                _review.value = reviewList
            }

            override fun onCancelled(error: DatabaseError) {
                // Nếu cần có thể log hoặc hiển thị lỗi
                _review.value = emptyList()
            }
        })
    }
    fun calculateAverageRating(apartmentId: String, onResult: (Double) -> Unit) {
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