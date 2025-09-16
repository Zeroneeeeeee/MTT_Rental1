package com.example.mtt_rental.viewmodel.tenant

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.model.Contract
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.repo.ApartmentDB
import com.google.firebase.database.FirebaseDatabase

class ApartmentDetailsViewModel : ViewModel() {
    private val contractsRef = FirebaseDatabase.getInstance().getReference("contracts")

    private val _apartment = mutableStateOf(Apartment())
    val apartment: State<Apartment> = _apartment

    private val _roomTypes = mutableStateOf<List<RoomType>>(emptyList())
    val roomTypes: State<List<RoomType>> = _roomTypes

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

        // ✅ Dùng lại contractsRef
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
}