package com.example.mtt_rental.viewmodel.tenant

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import com.example.mtt_rental.dtmodel.RoomTypeVM
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.model.Contract
import com.example.mtt_rental.model.Room
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.utils.toRoomTypeVM
import com.example.mtt_rental.utils.toRoomVM
import com.google.firebase.database.FirebaseDatabase

class RentViewModel : ViewModel() {

    // ✅ Tham chiếu tới node "contracts" trong Firebase
    private val contractsRef = FirebaseDatabase.getInstance().getReference("contracts")
    private val firebaseRef = FirebaseDatabase.getInstance().getReference("apartments")

    private val _apartment = mutableStateOf(Apartment())
    val apartment: State<Apartment> = _apartment

    private val _roomTypes = mutableStateOf<List<RoomType>>(emptyList())
    val roomTypes: State<List<RoomType>> = _roomTypes

    private val _rooms = mutableStateOf<List<Room>>(emptyList())
    val rooms: State<List<Room>> = _rooms

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // ... loadApartmentById, loadRoomTypes như cũ

    fun createContract(
        idUser: String,
        idRoom: String,
        status: String = "Pending",
        startTime: Long = System.currentTimeMillis(),
        endTime: Long = 0L,
        onResult: (Boolean, String) -> Unit ={_,_ ->}
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

    fun loadRoomTypes(idApartment: String) {
        _isLoading.value = true
        firebaseRef.child(idApartment).child("roomTypes")
            .get()
            .addOnSuccessListener { snapshot ->
                _isLoading.value = false
                val list = snapshot.children.mapNotNull {
                    it.getValue(RoomType::class.java)
                }
                _roomTypes.value = list
            }
            .addOnFailureListener { ex ->
                _isLoading.value = false
                _error.value = "Error loading room types: ${ex.message}"
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