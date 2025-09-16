package com.example.mtt_rental.repo

import com.example.mtt_rental.model.Apartment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ApartmentRepository {

    private val database = FirebaseDatabase.getInstance()
    private val apartmentRef = database.getReference("apartments")

    fun getAllApartments(
        onResult: (List<Apartment>) -> Unit,
        onError: (DatabaseError) -> Unit = {}
    ) {
        apartmentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val apartments = mutableListOf<Apartment>()
                if (snapshot.exists()) {
                    for (apartmentSnapshot in snapshot.children) {
                        val apartment = apartmentSnapshot.getValue(Apartment::class.java)
                        if (apartment != null) {
                            apartments.add(apartment)
                        }
                    }
                }
                onResult(apartments)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
    }

    fun getApartmentsByOwner(
        ownerId: String,
        onResult: (List<Apartment>) -> Unit,
        onError: (DatabaseError) -> Unit = {}
    ) {
        apartmentRef.orderByChild("ownerId").equalTo(ownerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val apartments = mutableListOf<Apartment>()
                    if (snapshot.exists()) {
                        for (apartmentSnapshot in snapshot.children) {
                            val apartment = apartmentSnapshot.getValue(Apartment::class.java)
                            if (apartment != null) {
                                apartments.add(apartment)
                            }
                        }
                    }
                    onResult(apartments)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error)
                }
            })
    }

    fun saveApartment(
        apartment: Apartment,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val apartmentId = if (apartment.apartmentId.isEmpty()) {
            apartmentRef.push().key!!
        } else {
            apartment.apartmentId
        }

        val updatedApartment = apartment.copy(apartmentId = apartmentId)

        apartmentRef.child(apartmentId).setValue(updatedApartment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }

    fun deleteApartment(
        apartmentId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        apartmentRef.child(apartmentId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }
}