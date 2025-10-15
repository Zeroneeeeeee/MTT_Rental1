package com.example.mtt_rental.viewmodel.manager

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.dtmodel.ApartmentVM
import com.example.mtt_rental.dtmodel.RoomServiceVM
import com.example.mtt_rental.dtmodel.RoomTypeVM
import com.example.mtt_rental.dtmodel.RoomVM
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.model.Room
import com.example.mtt_rental.model.RoomService
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.utils.UserRepo
import com.google.firebase.database.FirebaseDatabase

class ManagerAddRentalViewModel : ViewModel() {

    private val _apartment = mutableStateOf<ApartmentVM?>(null)
    val apartment: State<ApartmentVM?> = _apartment

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _saveResult = mutableStateOf<SaveResult?>(null)
    val saveResult: State<SaveResult?> = _saveResult

    private val firebaseRef = FirebaseDatabase.getInstance().getReference("apartments")

    // Current apartmentId being edited
    private val _apartmentId = mutableStateOf<String?>(null)
    val apartmentId: State<String?> = _apartmentId

    /** ================== SAVE APARTMENT ================== */
    fun saveApartment(
        editApartmentId: String = "",
        location: String,
        title: String,
        image: String
    ) {
        val errors = mutableListOf<String>()
        if (location.isBlank()) errors.add("Location cannot be empty")
        if (title.isBlank()) errors.add("Title cannot be empty")

        if (errors.isNotEmpty()) {
            _saveResult.value = SaveResult.Error(errors.joinToString(", "))
            return
        }

        _isLoading.value = true
        try {
            val newApartmentId = if (editApartmentId == "") firebaseRef.push().key!! else editApartmentId

            val newApartment = Apartment(
                apartmentId = newApartmentId,
                title = title,
                location = location,
                image = image,
                ownerId = UserRepo.idUser,
            )

            firebaseRef.child(newApartmentId).setValue(newApartment)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _apartmentId.value = newApartmentId
                        _saveResult.value = SaveResult.Success("Apartment has been saved successfully")
                    } else {
                        _saveResult.value =
                            SaveResult.Error("Error saving apartment: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Error saving apartment: ${ex.message}")
                }
        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Error: ${e.message}")
        }
    }

    /** ================== SAVE ROOM TYPE ================== */
    fun saveRoomType(
        idRoomType: String = "",
        price: Long,
        area: Long,
        maxRenter: Long,
        description: String
    ) {

        val idApartment = _apartmentId.value ?: return run {
            _saveResult.value = SaveResult.Error("No apartmentId available to add room type")
        }

        _isLoading.value = true
        try {
            val newRoomTypeId = if (idRoomType == "") firebaseRef.push().key!! else idRoomType
            val roomType = RoomType(
                idRoomType = newRoomTypeId,
                price = price,
                area = area,
                maxRenter = maxRenter,
                description = description,
            )

            firebaseRef.child(idApartment)
                .child("roomTypes")
                .child(newRoomTypeId)
                .setValue(roomType)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _saveResult.value = SaveResult.Success("Room type added successfully")
                    } else {
                        _saveResult.value = SaveResult.Error("Error: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Error: ${ex.message}")
                }
        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Error: ${e.message}")
        }
    }

    /** ================== SAVE ROOM ================== */
    fun saveRoom(
        idRoomType: String,
        name: String,
        floor: Int
    ) {
        val idApartment = _apartmentId.value ?: return run {
            _saveResult.value = SaveResult.Error("No apartmentId available to add room")
        }

        _isLoading.value = true
        try {
            val roomId = firebaseRef.push().key!!
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
                        _saveResult.value = SaveResult.Success("Room added successfully")
                    } else {
                        _saveResult.value = SaveResult.Error("Error: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Error: ${ex.message}")
                }
        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Error: ${e.message}")
        }
    }

    /** ================== SAVE SERVICE ================== */
    fun saveService(
        idRoomType: String,
        name: String,
        unit: String,
        fee: Long
    ) {
        val idApartment = _apartmentId.value ?: return run {
            _saveResult.value = SaveResult.Error("No apartmentId available to add service")
        }

        _isLoading.value = true
        try {
            val serviceId = firebaseRef.push().key ?: name.replace(" ", "_")
            val service = RoomServiceVM(
                idRoomService = serviceId,
                name = name,
                unit = unit,
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
                        _saveResult.value = SaveResult.Success("Service added successfully")
                    } else {
                        _saveResult.value = SaveResult.Error("Error: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Error: ${ex.message}")
                }
        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Error: ${e.message}")
        }
    }

    /** ================== LOAD APARTMENT ================== */
    fun loadApartment(apartmentId: String) {
        _isLoading.value = true
        firebaseRef.child(apartmentId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val id = snapshot.child("apartmentId").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val location = snapshot.child("location").value.toString()
                    val ownerId = snapshot.child("ownerId").value.toString()
                    val image = snapshot.child("image").value.toString()

                    val roomTypeList = snapshot.child("roomTypes").children.map { rtSnap ->
                        val idRoomType = rtSnap.key ?: ""
                        val price = rtSnap.child("price").getValue(Long::class.java) ?: 0L
                        val area = rtSnap.child("area").getValue(Long::class.java) ?: 0L
                        val maxRenter = rtSnap.child("maxRenter").getValue(Long::class.java) ?: 0L
                        val description = rtSnap.child("description").value.toString()

                        val roomList = rtSnap.child("rooms").children.map { rSnap ->
                            RoomVM(
                                idRoom = rSnap.key ?: "",
                                idRoomType = idRoomType,
                                name = rSnap.child("name").value.toString(),
                                floor = rSnap.child("floor").getValue(Int::class.java) ?: 0,
                                tenants = emptyList()
                            )
                        }

                        val serviceList = rtSnap.child("services").children.map { sSnap ->
                            RoomServiceVM(
                                idRoomService = sSnap.key ?: "",
                                name = sSnap.child("name").value.toString(),
                                unit = sSnap.child("unit").value.toString(),
                                fee = sSnap.child("fee").getValue(Long::class.java) ?: 0L
                            )
                        }

                        RoomTypeVM(
                            idRoomType = idRoomType,
                            price = price,
                            area = area,
                            maxRenter = maxRenter,
                            description = description,
                            roomList = roomList,
                            roomServiceList = serviceList
                        )
                    }

                    _apartment.value = ApartmentVM(
                        apartmentId = id,
                        title = title,
                        location = location,
                        ownerId = ownerId,
                        image = image,
                        roomType = roomTypeList
                    )
                }
                _isLoading.value = false
            }
            .addOnFailureListener { ex ->
                _isLoading.value = false
                _error.value = "Error loading apartment: ${ex.message}"
            }
    }
    /** ================== DELETE ROOM TYPE ================== */
    fun deleteRoomType(idRoomType: String) {
        val idApartment = _apartmentId.value ?: return run {
            _saveResult.value = SaveResult.Error("No apartmentId available to delete room type")
        }

        _isLoading.value = true
        try {
            firebaseRef.child(idApartment)
                .child("roomTypes")
                .child(idRoomType)
                .removeValue()
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _saveResult.value = SaveResult.Success("Room type deleted successfully")
                    } else {
                        _saveResult.value = SaveResult.Error("Error deleting room type: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Error deleting room type: ${ex.message}")
                }
        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Error: ${e.message}")
        }
    }

    /** ================== DELETE ROOM ================== */
    fun deleteRoom(idRoomType: String, idRoom: String) {
        val idApartment = _apartmentId.value ?: return run {
            _saveResult.value = SaveResult.Error("No apartmentId available to delete room")
        }

        _isLoading.value = true
        try {
            firebaseRef.child(idApartment)
                .child("roomTypes")
                .child(idRoomType)
                .child("rooms")
                .child(idRoom)
                .removeValue()
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _saveResult.value = SaveResult.Success("Room deleted successfully")
                    } else {
                        _saveResult.value = SaveResult.Error("Error deleting room: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { ex ->
                    _isLoading.value = false
                    _saveResult.value = SaveResult.Error("Error deleting room: ${ex.message}")
                }
        } catch (e: Exception) {
            _isLoading.value = false
            _saveResult.value = SaveResult.Error("Error: ${e.message}")
        }
    }



    /** ================== CLEAR STATE ================== */
    fun clearSaveResult() {
        _saveResult.value = null
    }

    fun clearError() {
        _error.value = null
    }
}

/** ================== RESULT ================== */
sealed class SaveResult {
    data class Success(val message: String) : SaveResult()
    data class Error(val message: String) : SaveResult()
}
