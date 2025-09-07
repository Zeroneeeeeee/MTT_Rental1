package com.example.mtt_rental.repo

import com.example.mtt_rental.model.Apartment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ApartmentDB {
    private val database = FirebaseDatabase.getInstance()
    private val apartmentRef = database.getReference("apartments")

    fun getDepartments(onResult: (List<Apartment>) -> Unit) {
        apartmentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val apartments = mutableListOf<Apartment>()
                for (apartmentSnapshot in snapshot.children) {
                    val apartment = apartmentSnapshot.getValue(Apartment::class.java)
                    if (apartment != null) apartments.add(apartment)
                }
                onResult(apartments)
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    fun getDepartmentById(
        id: String,
        onResult: (Apartment?) -> Unit,
        onError: (DatabaseError) -> Unit = {}
    ) {
        apartmentRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val apartment = snapshot.getValue(Apartment::class.java)
                onResult(apartment)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
    }
}