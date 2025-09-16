package com.example.mtt_rental.repo

import com.example.mtt_rental.model.RoomType
import com.google.firebase.database.FirebaseDatabase

object RoomRepository {

    private val database = FirebaseDatabase.getInstance()
    private val roomRef = database.getReference("rooms")

    fun saveRoom(
        room: RoomType,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        roomRef.child(room.idRoomType).setValue(room)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }
}