package com.example.mtt_rental.viewmodel.tenant

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.model.Contract
import com.example.mtt_rental.model.Room
import com.example.mtt_rental.model.RoomService
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.utils.UserRepo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()

    // State lưu dữ liệu
    private val _apartment = mutableStateOf<Apartment?>(null)
    val apartment: State<Apartment?> = _apartment

    private val _roomType = mutableStateOf<RoomType?>(null)
    val roomType: State<RoomType?> = _roomType

    private val _room = mutableStateOf<Room?>(null)
    val room: State<Room?> = _room

    private val _services = mutableStateOf<List<RoomService>>(emptyList())
    val services: State<List<RoomService>> = _services

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _currentRenterCount = mutableStateOf(0)
    val currentRenterCount: State<Int> = _currentRenterCount

    private val _isUpdating = mutableStateOf(false)
    val isUpdating: State<Boolean> = _isUpdating

    private val _updateSuccess = mutableStateOf(false)
    val updateSuccess: State<Boolean> = _updateSuccess


    /**
     * Gọi khi load màn Profile -> tab Location
     * Lấy hợp đồng của user hiện tại
     */
    fun loadUserLocation(userId: String) {
        _loading.value = true
        _error.value = null

        val contractRef = db.getReference("contracts")
        contractRef.orderByChild("idUser").equalTo(userId)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var found = false
                    for (contractSnap in snapshot.children) {
                        val contract = contractSnap.getValue(Contract::class.java)
                        if (contract != null && contract.status == "Joined") {
                            findLocationFromContract(contract.idRoom)
                            found = true
                            break
                        }
                    }
                    if (!found) {
                        _loading.value = false
                        _error.value = "Không tìm thấy hợp đồng hợp lệ"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _loading.value = false
                    _error.value = error.message
                }
            })
    }

    /**
     * Từ idRoom -> tìm Room, RoomType, Apartment
     */
    private fun findLocationFromContract(roomId: String) {
        val apartmentsRef = db.getReference("apartments")
        apartmentsRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (aptSnap in snapshot.children) {
                    val apartment = aptSnap.getValue(Apartment::class.java)
                        ?.copy(apartmentId = aptSnap.key ?: "")

                    val roomTypesSnap = aptSnap.child("roomTypes")
                    for (rtSnap in roomTypesSnap.children) {
                        val roomType = rtSnap.getValue(RoomType::class.java)
                            ?.copy(
                                idRoomType = rtSnap.key ?: "",
                                idApartment = apartment?.apartmentId ?: ""
                            )

                        val roomsSnap = rtSnap.child("rooms")
                        for (roomSnap in roomsSnap.children) {
                            val room = roomSnap.getValue(Room::class.java)
                                ?.copy(
                                    idRoom = roomSnap.key ?: "",
                                    idRoomType = roomType?.idRoomType ?: ""
                                )

                            if (room?.idRoom == roomId) {
                                _room.value = room
                                _roomType.value = roomType
                                _apartment.value = apartment
                                // Đếm số người thực tế đang ở phòng này
                                countCurrentRenters(roomId)
                                // Lấy dịch vụ của roomType
                               if (roomType != null) loadRoomServices(roomType.idRoomType)
                                _loading.value = false
                                return
                            }
                        }
                    }
                }
                _loading.value = false
                _error.value = "Không tìm thấy phòng"
            }

            override fun onCancelled(error: DatabaseError) {
                _loading.value = false
                _error.value = error.message
            }
        })
    }

    // Đếm số contract có idRoom trùng và status == "Joined"
    private fun countCurrentRenters(roomId: String) {
        val contractRef = db.getReference("contracts")
        contractRef.orderByChild("idRoom").equalTo(roomId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    for (contractSnap in snapshot.children) {
                        val contract = contractSnap.getValue(Contract::class.java)
                        if (contract != null && contract.status == "Joined") {
                            count++
                        }
                    }
                    _currentRenterCount.value = count
                }
                override fun onCancelled(error: DatabaseError) {
                    _currentRenterCount.value = 0
                }
            })
    }

    // Lấy danh sách dịch vụ của roomType
    private fun loadRoomServices(idRoomType: String) {
        val servicesRef = db.getReference("roomServices")
        servicesRef.orderByChild("idRoomType").equalTo(idRoomType)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<RoomService>()
                    for (serviceSnap in snapshot.children) {
                        val service = serviceSnap.getValue(RoomService::class.java)
                        if (service != null) list.add(service)
                    }
                    _services.value = list
                }
                override fun onCancelled(error: DatabaseError) {
                    _services.value = emptyList()
                }
            })
    }
    fun updateUserProfile(
        idUser: String,
        profileName: String,
        email: String,
        phoneNumber: String
    ) {
        _isUpdating.value = true
        _updateSuccess.value = false
        _error.value = null

        val updates = mapOf(
            "profileName" to profileName,
            "email" to email,
            "phoneNumber" to phoneNumber
        )

        UserRepo.profileName = profileName
        UserRepo.email = email
        UserRepo.phoneNumber = phoneNumber

        db.getReference("users")
            .child(idUser)
            .updateChildren(updates)
            .addOnSuccessListener {
                _isUpdating.value = false
                _updateSuccess.value = true
            }
            .addOnFailureListener { e ->
                _isUpdating.value = false
                _error.value = e.message
            }
    }
}
