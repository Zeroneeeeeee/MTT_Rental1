package com.example.mtt_rental.viewmodel.manager

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.mtt_rental.model.Contract
import com.example.mtt_rental.model.Room
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.jvm.java

class RoomManagementViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()

    private val _rooms = mutableStateOf<List<Room>>(emptyList())
    val rooms: State<List<Room>> = _rooms

    private val _roomTypes = mutableStateOf<List<RoomType>>(emptyList())
    val roomTypes: State<List<RoomType>> = _roomTypes

    private val _joinedUsersByRoomId = mutableStateOf<Map<String, List<User>>>(emptyMap())
    val joinedUsersByRoomId: State<Map<String, List<User>>> = _joinedUsersByRoomId

    private val _pendingUsersByRoomId = mutableStateOf<Map<String, List<User>>>(emptyMap())
    val pendingUsersByRoomId: State<Map<String, List<User>>> = _pendingUsersByRoomId

    val contractRef = database.getReference("contracts")
    val userRef = database.getReference("users")

    fun loadRoomsByApartment(apartmentId: String) {
        val roomRef = database.getReference("apartments")
            .child(apartmentId)
            .child("roomTypes")

        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomList = mutableListOf<Room>()
                val roomTypeList = mutableListOf<RoomType>() // ✅

                for (rtSnap in snapshot.children) {
                    val idRoomType = rtSnap.key ?: ""
                    val price = rtSnap.child("price").getValue(Long::class.java) ?: 0L
                    val area = rtSnap.child("area").getValue(Long::class.java) ?: 0L
                    val maxRenter = rtSnap.child("maxRenter").getValue(Long::class.java) ?: 0L
                    val description = rtSnap.child("description").getValue(String::class.java) ?: ""

                    val roomType = RoomType(
                        idRoomType = idRoomType,
                        idApartment = apartmentId,
                        price = price,
                        area = area,
                        maxRenter = maxRenter,
                        description = description
                    )
                    roomTypeList.add(roomType)

                    val roomListSnap = rtSnap.child("rooms")
                    for (roomSnap in roomListSnap.children) {
                        val idRoom = roomSnap.key ?: ""
                        val name = roomSnap.child("name").getValue(String::class.java)
                        val floor = roomSnap.child("floor").getValue(Int::class.java)
                        if (name != null && floor != null) {
                            val room = Room(
                                idRoom = idRoom,
                                idRoomType = idRoomType,
                                name = name,
                                floor = floor
                            )
                            roomList.add(room)
                        }
                    }
                }
                _rooms.value = roomList
                _roomTypes.value = roomTypeList // ✅ cập nhật RoomTypes
            }

            override fun onCancelled(error: DatabaseError) {
                _rooms.value = emptyList()
                _roomTypes.value = emptyList()
            }
        })
    }

    fun loadJoinedUsersByRoomId(roomId: String) {
        contractRef.orderByChild("idRoom").equalTo(roomId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usersList = mutableListOf<User>()
                    val contracts = snapshot.children.mapNotNull { it.getValue(Contract::class.java) }
                    val joinedContracts = contracts.filter { it.status == "Joined" }

                    if (joinedContracts.isEmpty()) {
                        val newMap = _joinedUsersByRoomId.value.toMutableMap()
                        newMap[roomId] = emptyList()
                        _joinedUsersByRoomId.value = newMap
                        return
                    }

                    var loaded = 0
                    for (contract in joinedContracts) {
                        userRef.child(contract.idUser)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnap: DataSnapshot) {
                                    val user = userSnap.getValue(User::class.java)
                                    user?.let { usersList.add(it) }
                                    loaded++
                                    if (loaded == joinedContracts.size) {
                                        val newMap = _joinedUsersByRoomId.value.toMutableMap()
                                        newMap[roomId] = usersList
                                        _joinedUsersByRoomId.value = newMap
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    loaded++
                                    if (loaded == joinedContracts.size) {
                                        val newMap = _joinedUsersByRoomId.value.toMutableMap()
                                        newMap[roomId] = usersList
                                        _joinedUsersByRoomId.value = newMap
                                    }
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun loadPendingUsersByRoomId(roomId: String) {
        contractRef.orderByChild("idRoom").equalTo(roomId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usersList = mutableListOf<User>()
                    val contracts = snapshot.children.mapNotNull { it.getValue(Contract::class.java) }
                    val pendingContracts = contracts.filter { it.status == "Pending" }

                    if (pendingContracts.isEmpty()) {
                        val newMap = _pendingUsersByRoomId.value.toMutableMap()
                        newMap[roomId] = emptyList()
                        _pendingUsersByRoomId.value = newMap
                        return
                    }

                    var loaded = 0
                    for (contract in pendingContracts) {
                        userRef.child(contract.idUser)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnap: DataSnapshot) {
                                    val user = userSnap.getValue(User::class.java)
                                    user?.let { usersList.add(it) }
                                    loaded++
                                    if (loaded == pendingContracts.size) {
                                        val newMap = _pendingUsersByRoomId.value.toMutableMap()
                                        newMap[roomId] = usersList
                                        _pendingUsersByRoomId.value = newMap
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    loaded++
                                    if (loaded == pendingContracts.size) {
                                        val newMap = _pendingUsersByRoomId.value.toMutableMap()
                                        newMap[roomId] = usersList
                                        _pendingUsersByRoomId.value = newMap
                                    }
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun updateContractStatus(roomId: String, userId: String, newStatus: String) {
        contractRef.orderByChild("idRoom").equalTo(roomId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (contractSnap in snapshot.children) {
                        val contract = contractSnap.getValue(Contract::class.java)
                        if (contract?.idUser == userId && contract.status == "Pending") {
                            contractSnap.ref.child("status").setValue(newStatus)
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun removeUserFromRoom(roomId: String, userId: String) {
        contractRef.orderByChild("idRoom").equalTo(roomId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (contractSnap in snapshot.children) {
                        val contract = contractSnap.getValue(Contract::class.java)
                        if (contract?.idUser == userId && contract.status == "Joined") {
                            // ❌ Xóa contract này
                            contractSnap.ref.removeValue()

                            // ✅ Cập nhật lại danh sách joined users
                            loadJoinedUsersByRoomId(roomId)
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}
