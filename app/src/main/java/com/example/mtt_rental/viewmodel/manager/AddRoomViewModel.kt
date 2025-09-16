package com.example.mtt_rental.viewmodel.manager

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Room
import com.example.mtt_rental.model.RoomService
import com.example.mtt_rental.model.RoomType
import com.google.firebase.database.FirebaseDatabase

class AddRoomViewModel : ViewModel() {

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _saveResult = mutableStateOf<RoomSaveResult?>(null)
    val saveResult: State<RoomSaveResult?> = _saveResult

    private val firebaseRef = FirebaseDatabase.getInstance().getReference("apartments")

    fun saveRoomType(
        idApartment: String,
        name: String,
        maxRenter: Int,
        price: Long,
        area: Long,
        description: String
    ) {
        _isLoading.value = true

        try {
            // Dùng name làm idRoomType, cần replace ký tự không hợp lệ
            val roomTypeId = name.replace(" ", "_")

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
                .child(roomTypeId) // sử dụng name làm ID
                .setValue(roomType)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _saveResult.value = RoomSaveResult.Success("Thêm loại phòng thành công")
                    } else {
                        _saveResult.value = RoomSaveResult.Error("Lỗi: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = RoomSaveResult.Error("Lỗi: ${ex.message}")
                }

        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = RoomSaveResult.Error("Lỗi: ${e.message}")
        }
    }
    fun saveRoom(
        idApartment: String,
        idRoomType: String, // chính là name đã được chuẩn hóa
        name: String,
        floor: Int
    ) {
        _isLoading.value = true

        try {
            val roomId = name.replace(" ", "_") // dùng name làm ID cho room

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
                        _saveResult.value = RoomSaveResult.Success("Thêm phòng thành công")
                    } else {
                        _saveResult.value = RoomSaveResult.Error("Lỗi: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = RoomSaveResult.Error("Lỗi: ${ex.message}")
                }

        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = RoomSaveResult.Error("Lỗi: ${e.message}")
        }
    }

    fun saveRoomService(
        idApartment: String,
        idRoomType: String,
        name: String,
        unit: String,
        fee: Long
    ) {
        _isLoading.value = true

        try {
            // idRoomService có thể dùng name hoặc push key
            val idRoomService = name.replace(" ", "_")

            val service = RoomService(
                idRoomService = idRoomService,
                idRoomType = idRoomType,
                name = name,
                unit = unit,
                fee = fee
            )

            firebaseRef.child(idApartment)
                .child("roomTypes")
                .child(idRoomType)
                .child("roomServices")
                .child(idRoomService)
                .setValue(service)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _saveResult.value = RoomSaveResult.Success("Thêm dịch vụ thành công")
                    } else {
                        _saveResult.value = RoomSaveResult.Error("Lỗi: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = RoomSaveResult.Error("Lỗi: ${ex.message}")
                }

        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = RoomSaveResult.Error("Lỗi: ${e.message}")
        }
    }


    fun clearSaveResult() {
        _saveResult.value = null
    }

    fun clearError() {
        _error.value = null
    }
}

sealed class RoomSaveResult {
    data class Success(val message: String) : RoomSaveResult()
    data class Error(val message: String) : RoomSaveResult()
}