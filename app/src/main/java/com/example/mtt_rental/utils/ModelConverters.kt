package com.example.mtt_rental.utils

import com.example.mtt_rental.model.Room
import com.example.mtt_rental.model.RoomType
import com.example.mtt_rental.model.Apartment
import com.example.mtt_rental.dtmodel.RoomVM
import com.example.mtt_rental.dtmodel.RoomTypeVM
import com.example.mtt_rental.dtmodel.ApartmentVM
import com.example.mtt_rental.dtmodel.RoomServiceVM

// Extension functions for Room conversions
fun Room.toRoomVM(): RoomVM {
    return RoomVM(
        idRoom = this.idRoom,
        name = this.name,
        floor = this.floor,
        idRoomType = this.idRoomType
    )
}

fun RoomVM.toRoom(idRoomType: String = ""): Room {
    return Room(
        idRoom = this.idRoom,
        idRoomType = idRoomType,
        name = this.name,
        floor = this.floor
    )
}

// Extension functions for RoomType conversions
fun RoomType.toRoomTypeVM(
    rooms: List<RoomVM>,
    services: List<RoomServiceVM> = emptyList()
): RoomTypeVM {
    return RoomTypeVM(
        idRoomType = this.idRoomType,
        price = this.price,
        area = this.area,
        maxRenter = this.maxRenter,
        description = this.description,
        roomList = rooms,
        roomServiceList = services
    )
}

fun RoomTypeVM.toRoomType(idApartment: String = ""): RoomType {
    return RoomType(
        idRoomType = this.idRoomType,
        idApartment = idApartment,
        price = this.price,
        area = this.area,
        maxRenter = this.maxRenter,
        description = this.description
    )
}

// Extension functions for Apartment conversions
fun Apartment.toApartmentVM(roomTypeList: List<RoomTypeVM> = emptyList()): ApartmentVM {
    return ApartmentVM(
        apartmentId = this.apartmentId,
        title = this.title,
        location = this.location,
        ownerId = this.ownerId,
        image = this.image,
        roomType = roomTypeList
    )
}

fun ApartmentVM.toApartment(
    description: String = "",
    rating: Double = 0.0
): Apartment {
    return Apartment(
        apartmentId = this.apartmentId,
        title = this.title,
        description = description,
        location = this.location,
        ownerId = this.ownerId,
        image = this.image
    )
}

// Utility functions for converting lists
fun List<Room>.toRoomVMList(): List<RoomVM> {
    return this.map { it.toRoomVM() }
}

fun List<RoomVM>.toRoomList(idRoomType: String = ""): List<Room> {
    return this.map { it.toRoom(idRoomType) }
}

fun List<RoomType>.toRoomTypeVMList(roomListMap: Map<String, List<RoomVM>> = emptyMap()): List<RoomTypeVM> {
    return this.map { roomType ->
        val roomList = roomListMap[roomType.idRoomType] ?: emptyList()
        roomType.toRoomTypeVM(roomList)
    }
}

fun List<RoomTypeVM>.toRoomTypeList(): List<RoomType> {
    return this.map { it.toRoomType() }
}

fun List<Apartment>.toApartmentVMList(roomTypeMap: Map<String, List<RoomTypeVM>> = emptyMap()): List<ApartmentVM> {
    return this.map { apartment ->
        val roomTypeList = roomTypeMap[apartment.apartmentId] ?: emptyList()
        apartment.toApartmentVM(roomTypeList)
    }
}

fun List<ApartmentVM>.toApartmentList(): List<Apartment> {
    return this.map { it.toApartment() }
}
//
//// Advanced conversion functions
//object ModelConverter {
//
//    /**
//     * Convert ApartmentVM with nested structure to separate models
//     * Returns Triple of (Apartment, List<RoomType>, List<Room>)
//     */
//    fun apartmentVMToModels(apartmentVM: ApartmentVM): Triple<Apartment, List<RoomType>, List<Room>> {
//        val apartment = apartmentVM.toApartment()
//        val roomTypes = mutableListOf<RoomType>()
//        val rooms = mutableListOf<Room>()
//
//        apartmentVM.roomType.forEach { roomTypeVM ->
//            roomTypes.add(roomTypeVM.toRoomType())
//            roomTypeVM.roomList.forEach { roomVM ->
//                rooms.add(roomVM.toRoom(roomTypeVM.idRoomType))
//            }
//        }
//
//        return Triple(apartment, roomTypes, rooms)
//    }
//
//    /**
//     * Convert separate models to ApartmentVM with nested structure
//     */
//    fun modelsToApartmentVM(
//        apartment: Apartment,
//        roomTypes: List<RoomType>,
//        rooms: List<Room>
//    ): ApartmentVM {
//        // Group rooms by roomType
//        val roomsByRoomType = rooms.groupBy { it.idRoomType }
//
//        // Convert roomTypes with their associated rooms
//        val roomTypeVMs = roomTypes
//            .filter { it.idApartment == apartment.apartmentId }
//            .map { roomType ->
//                val roomVMs = roomsByRoomType[roomType.idRoomType]?.toRoomVMList() ?: emptyList()
//                roomType.toRoomTypeVM(roomVMs)
//            }
//
//        return apartment.toApartmentVM(roomTypeVMs)
//    }
//
//    /**
//     * Get all rooms from ApartmentVM flattened
//     */
//    fun getAllRoomsFromApartmentVM(apartmentVM: ApartmentVM): List<RoomVM> {
//        return apartmentVM.roomType.flatMap { it.roomList }
//    }
//
//    /**
//     * Get total room count from ApartmentVM
//     */
//    fun getTotalRoomCount(apartmentVM: ApartmentVM): Int {
//        return apartmentVM.roomType.sumOf { it.roomList.size }
//    }
//
//    /**
//     * Get room type by ID from ApartmentVM
//     */
//    fun getRoomTypeById(apartmentVM: ApartmentVM, roomTypeId: String): RoomTypeVM? {
//        return apartmentVM.roomType.find { it.idRoomType == roomTypeId }
//    }
//
//    /**
//     * Get room by ID from ApartmentVM
//     */
//    fun getRoomById(apartmentVM: ApartmentVM, roomId: String): RoomVM? {
//        return apartmentVM.roomType
//            .flatMap { it.roomList }
//            .find { it.idRoom == roomId }
//    }
//
//    /**
//     * Add room to specific room type in ApartmentVM
//     */
//    fun addRoomToApartmentVM(
//        apartmentVM: ApartmentVM,
//        roomTypeId: String,
//        roomVM: RoomVM
//    ): ApartmentVM {
//        val updatedRoomTypes = apartmentVM.roomType.map { roomType ->
//            if (roomType.idRoomType == roomTypeId) {
//                roomType.copy(roomList = roomType.roomList + roomVM)
//            } else {
//                roomType
//            }
//        }
//        return apartmentVM.copy(roomType = updatedRoomTypes)
//    }
//
//    /**
//     * Remove room from ApartmentVM
//     */
//    fun removeRoomFromApartmentVM(apartmentVM: ApartmentVM, roomId: String): ApartmentVM {
//        val updatedRoomTypes = apartmentVM.roomType.map { roomType ->
//            roomType.copy(roomList = roomType.roomList.filter { it.idRoom != roomId })
//        }
//        return apartmentVM.copy(roomType = updatedRoomTypes)
//    }
//
//    /**
//     * Calculate total area of apartment from room types
//     */
//    fun calculateTotalArea(apartmentVM: ApartmentVM): Long {
//        return apartmentVM.roomType.sumOf { it.area * it.roomList.size }
//    }
//
//    /**
//     * Calculate average price of apartment
//     */
//    fun calculateAveragePrice(apartmentVM: ApartmentVM): Double {
//        val roomTypes = apartmentVM.roomType
//        if (roomTypes.isEmpty()) return 0.0
//
//        return roomTypes.map { it.price }.average()
//    }
//}