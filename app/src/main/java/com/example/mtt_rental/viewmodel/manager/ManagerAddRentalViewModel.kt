package com.example.mtt_rental.viewmodel.manager

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.model.Room
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.model.Service
import com.example.mtt_rental.repo.UserRepo
import com.google.firebase.database.FirebaseDatabase

class ManagerAddRentalViewModel : ViewModel() {
    private val _apartment = mutableStateOf<Apartment?>(null)
    val apartment: State<Apartment?> = _apartment

    private val _roomTypes = mutableStateOf<List<RoomType>>(emptyList())
    val roomTypes: State<List<RoomType>> = _roomTypes

    private val _rooms = mutableStateOf<List<Room>>(emptyList())
    val rooms: State<List<Room>> = _rooms

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _saveResult = mutableStateOf<SaveResult?>(null)
    val saveResult: State<SaveResult?> = _saveResult

    private val firebaseRef = FirebaseDatabase.getInstance().getReference("apartments")

    // apartmentId chung để tái sử dụng
    private val _apartmentId = mutableStateOf<String?>(null)
    val apartmentId: State<String?> = _apartmentId

    fun saveApartment(
        editApartmentId: String = "",
        location: String,
        title: String,
        image: String,
    ) {
        val errors = mutableListOf<String>()

        if (location.isBlank()) errors.add("Location không được để trống")
        if (title.isBlank()) errors.add("Title không được để trống")

        if (errors.isNotEmpty()) {
            _saveResult.value = SaveResult.Error(errors.joinToString(", "))
            return
        }

        _isLoading.value = true

        try {
            val newApartmentId =
                if (editApartmentId == "") firebaseRef.push().key!! else editApartmentId
            Log.d("ManagerAddRentalViewModel", "saveApartment: $newApartmentId")
            val newApartment = Apartment(
                apartmentId = newApartmentId,
                title = title,
                description = "",
                location = location,
                image = image,
                ownerId = UserRepo.idUser
            )

            firebaseRef.child(newApartmentId).setValue(newApartment)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        // Lưu lại id để dùng cho saveRoomType, saveRoom...
                        _apartmentId.value = newApartmentId
                        _saveResult.value =
                            SaveResult.Success("Apartment đã được lưu thành công")
                    } else {
                        _saveResult.value =
                            SaveResult.Error("Lỗi lưu apartment: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { exception ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Lỗi lưu apartment: ${exception.message}")
                }
        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Lỗi: ${e.message}")
        }
    }

    fun saveRoomType(
        id: String = "",
        name: String,
        maxRenter: Int,
        price: Long,
        area: Long,
        description: String
    ) {
        val idApartment = _apartmentId.value ?: return run {
            _saveResult.value = SaveResult.Error("Chưa có apartmentId để thêm room type")
        }

        _isLoading.value = true
        try {
            val roomTypeId = if (id == "") name.replace(" ", "_") else id
            val roomType = RoomType(
                idRoomType = roomTypeId,
                idApartment = idApartment,
                maxRenter = maxRenter,
                price = price,
                area = area,
                description = description
            )

            firebaseRef.child(idApartment)
                .child("roomTypes")
                .child(roomTypeId)
                .setValue(roomType)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _saveResult.value = SaveResult.Success("Thêm loại phòng thành công")
                    } else {
                        _saveResult.value = SaveResult.Error("Lỗi: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Lỗi: ${ex.message}")
                }

        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Lỗi: ${e.message}")
        }
    }

    fun saveRoom(
        id: String="",
        idRoomType: String,
        name: String,
        floor: Int
    ) {
        val idApartment = _apartmentId.value ?: return run {
            _saveResult.value = SaveResult.Error("Chưa có apartmentId để thêm room")
        }

        _isLoading.value = true
        try {
            val roomId = if(id == "") firebaseRef.push().key ?: name.replace(" ", "_") else id
            val room = Room(
                idRoom = roomId,
                idRoomType = idRoomType,
                name = name,
                floor = floor
            )

            firebaseRef.child(idApartment)
                .child("roomTypes")
                .child(idRoomType)
                .child("rooms")
                .child(roomId)
                .setValue(room)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _saveResult.value = SaveResult.Success("Thêm phòng thành công")
                    } else {
                        _saveResult.value = SaveResult.Error("Lỗi: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Lỗi: ${ex.message}")
                }

        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Lỗi: ${e.message}")
        }
    }

    fun saveService(
        idRoomType: String,
        name: String,
        param: String,
        fee: Long
    ) {
        val idApartment = _apartmentId.value ?: return run {
            _saveResult.value = SaveResult.Error("Chưa có apartmentId để thêm room")
        }
        _isLoading.value = true
        try {
            // Tạo id service (có thể dùng push key hoặc name)
            val serviceId = firebaseRef.push().key ?: name.replace(" ", "_")

            val service = Service(
                idService = serviceId,
                name = name,
                param = param,
                fee = fee
            )

            firebaseRef.child(idApartment)
                .child("roomTypes")
                .child(idRoomType)
                .child("services")
                .child(serviceId)
                .setValue(service)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _saveResult.value = SaveResult.Success("Thêm dịch vụ thành công")
                    } else {
                        _saveResult.value = SaveResult.Error("Lỗi: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Lỗi: ${ex.message}")
                }

        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Lỗi: ${e.message}")
        }
    }

    fun clearSaveResult() {
        _saveResult.value = null
    }

    fun clearError() {
        _error.value = null
    }
}


sealed class SaveResult {
    data class Success(val message: String) : SaveResult()
    data class Error(val message: String) : SaveResult()
}