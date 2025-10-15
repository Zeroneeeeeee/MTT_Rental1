package com.example.mtt_rental.viewmodel.manager

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.RoomService
import com.example.mtt_rental.model.RoomType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PaymentViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()

    private val _roomType = mutableStateOf<RoomType?>(null)
    val roomType: State<RoomType?> = _roomType

    private val _services = mutableStateOf<List<RoomService>>(emptyList())
    val services: State<List<RoomService>> = _services

    private val _amounts = mutableStateOf<Map<String, Int>>(emptyMap())
    val amounts: State<Map<String, Int>> = _amounts

    private val _total = mutableStateOf(0L)
    val total: State<Long> = _total

    /** ✅ Load RoomType theo apartmentId + roomTypeId */
    fun loadRoomType(apartmentId: String, roomTypeId: String) {
        val ref = database.getReference("apartments")
            .child(apartmentId)
            .child("roomTypes")
            .child(roomTypeId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val type = snapshot.getValue(RoomType::class.java)?.copy(
                        idRoomType = snapshot.key ?: ""
                    )
                    if (type != null) {
                        _roomType.value = type
                        recalcTotal()
                    } else {
                        Log.e("PaymentVM", "Mapping RoomType failed!")
                    }
                } else {
                    Log.e("PaymentVM", "RoomType with id=$roomTypeId not found!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PaymentVM", "loadRoomType cancelled: ${error.message}")
            }
        })
    }

    /** ✅ Load Services theo apartmentId + roomTypeId */
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
                    recalcTotal()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /** ✅ Cập nhật số lượng sử dụng của 1 service */
    fun updateAmount(serviceId: String, amount: Int) {
        val newMap = _amounts.value.toMutableMap()
        newMap[serviceId] = amount
        _amounts.value = newMap
        recalcTotal()
    }

    /** ✅ Tính tổng tiền */
    private fun recalcTotal() {
        val price = _roomType.value?.price ?: 0L
        val sumServices = _services.value.sumOf { s ->
            val amt = _amounts.value[s.idRoomService] ?: 0
            amt * s.fee
        }
        _total.value = price + sumServices
    }
}

