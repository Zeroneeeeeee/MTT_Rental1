package com.example.mtt_rental.repo

import com.example.mtt_rental.ui.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object UserDB {
    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("users")

    fun getUsers(onResult: (List<User>) -> Unit) {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) users.add(user)
                }
                onResult(users)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}