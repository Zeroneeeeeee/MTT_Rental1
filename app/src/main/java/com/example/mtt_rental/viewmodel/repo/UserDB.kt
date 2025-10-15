package com.example.mtt_rental.viewmodel.repo

import com.example.mtt_rental.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object UserDB {
    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("users")

    fun getUsers(
        onResult: (List<User>) -> Unit,
        onError: (DatabaseError) -> Unit = {}
    ) {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) users.add(user)
                }
                onResult(users)
            }
            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
    }

    // Cập nhật danh sách yêu thích của user
    fun updateUserFavorites(
        userId: String,
        favoriteApartments: List<String>,
        onComplete: (Boolean) -> Unit = {}
    ) {
        userRef.child(userId).child("favoriteApartments").setValue(favoriteApartments)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Lấy danh sách yêu thích của user
    fun getUserFavorites(userId: String, onResult: (List<String>) -> Unit) {
        userRef.child(userId).child("favoriteApartments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorites = mutableListOf<String>()
                    if (snapshot.exists()) {
                        // Nếu favoriteApartments là array
                        for (favoriteSnapshot in snapshot.children) {
                            val apartmentId = favoriteSnapshot.getValue(String::class.java)
                            if (apartmentId != null) favorites.add(apartmentId)
                        }
                    }
                    onResult(favorites)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    // Thêm căn hộ vào danh sách yêu thích
    fun addApartmentToFavorites(
        userId: String,
        apartmentId: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        userRef.child(userId).child("favoriteApartments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentFavorites = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (favoriteSnapshot in snapshot.children) {
                            val id = favoriteSnapshot.getValue(String::class.java)
                            if (id != null) currentFavorites.add(id)
                        }
                    }

                    if (!currentFavorites.contains(apartmentId)) {
                        val updatedFavorites = currentFavorites + apartmentId
                        updateUserFavorites(userId, updatedFavorites, onComplete)
                    } else {
                        onComplete(true) // Đã có trong danh sách
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(false)
                }
            })
    }

    // Xóa căn hộ khỏi danh sách yêu thích
    fun removeApartmentFromFavorites(
        userId: String,
        apartmentId: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        userRef.child(userId).child("favoriteApartments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentFavorites = mutableListOf<String>()

                    if (snapshot.exists()) {
                        for (favoriteSnapshot in snapshot.children) {
                            val id = favoriteSnapshot.getValue(String::class.java)
                            if (id != null) currentFavorites.add(id)
                        }
                    }

                    val updatedFavorites = currentFavorites.filter { it != apartmentId }
                    updateUserFavorites(userId, updatedFavorites, onComplete)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(false)
                }
            })
    }
}