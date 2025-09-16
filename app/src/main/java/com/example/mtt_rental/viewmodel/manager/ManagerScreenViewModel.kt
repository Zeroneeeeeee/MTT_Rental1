package com.example.mtt_rental.viewmodel.manager

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.dtmodel.RoomTypeVM
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.model.Room
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.utils.toRoomTypeVM
import com.example.mtt_rental.utils.toRoomVM
import com.google.firebase.database.FirebaseDatabase

class ManagerScreenViewModel : ViewModel() {
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



    fun loadRooms(idApartment: String, idRoomType: String) {
        _isLoading.value = true
        firebaseRef.child(idApartment).child("roomTypes").child(idRoomType).child("rooms")
            .get()
            .addOnSuccessListener { snapshot ->
                _isLoading.value = false
                val list = snapshot.children.mapNotNull {
                    it.getValue(Room::class.java)
                }
                _rooms.value = list
            }
            .addOnFailureListener { ex ->
                _isLoading.value = false
                _error.value = "Error loading rooms: ${ex.message}"
            }
    }
    fun loadRoomTypesAndRooms(
        idApartment: String,
        onLoaded: (List<RoomTypeVM>) -> Unit
    ) {
        _isLoading.value = true
        firebaseRef.child(idApartment).child("roomTypes")
            .get()
            .addOnSuccessListener { snapshot ->
                val roomTypeList = snapshot.children.mapNotNull { it.getValue(RoomType::class.java) }

                if (roomTypeList.isEmpty()) {
                    _isLoading.value = false
                    onLoaded(emptyList())
                    return@addOnSuccessListener
                }

                val resultList = mutableListOf<RoomTypeVM>()
                var loadedCount = 0

                roomTypeList.forEach { roomType ->
                    firebaseRef.child(idApartment)
                        .child("roomTypes")
                        .child(roomType.idRoomType)
                        .child("rooms")
                        .get()
                        .addOnSuccessListener { roomSnap ->
                            val roomList = roomSnap.children.mapNotNull { it.getValue(Room::class.java) }
                            val roomVMs = roomList.map { it.toRoomVM() }

                            resultList.add(roomType.toRoomTypeVM(roomVMs))

                            loadedCount++
                            if (loadedCount == roomTypeList.size) {
                                _isLoading.value = false
                                onLoaded(resultList)
                            }
                        }
                }
            }
            .addOnFailureListener { ex ->
                _isLoading.value = false
                _error.value = "Error loading room types and rooms: ${ex.message}"
            }
    }

}