package com.example.mtt_rental.viewmodel.manager

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.Feedback
import com.example.mtt_rental.viewmodel.repo.FeedbackRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManagerDashboardViewModel : ViewModel() {

    private val repo = FeedbackRepository()

    // --- Dashboard số liệu ---
    private val _totalProperties = mutableStateOf(0)
    val totalProperties: State<Int> = _totalProperties

    private val _pendingIssues = mutableStateOf(0)
    val pendingIssues: State<Int> = _pendingIssues

    // --- Feedback ---
    private val _feedbacks = mutableStateOf<List<Feedback>>(emptyList())
    val feedbacks: State<List<Feedback>> = _feedbacks

    private val dbApartments = FirebaseDatabase.getInstance().getReference("apartments")
    private val dbFeedbacks = FirebaseDatabase.getInstance().getReference("feedbacks")

    // --- Lấy danh sách feedback của manager ---
    fun loadFeedbacks(managerId: String) {
        dbFeedbacks.orderByChild("idReceiver").equalTo(managerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Feedback>()
                    var pendingCount = 0
                    for (child in snapshot.children) {
                        val fb = child.getValue(Feedback::class.java)
                        fb?.let {
                            list.add(it)
                            if (it.status == "pending") pendingCount++
                        }
                    }
                    _feedbacks.value = list
                    _pendingIssues.value = pendingCount
                }

                override fun onCancelled(error: DatabaseError) {
                    _feedbacks.value = emptyList()
                    _pendingIssues.value = 0
                }
            })
    }

    // --- Lấy tổng số căn hộ của manager ---
    fun loadTotalProperties(managerId: String) {
        dbApartments.orderByChild("ownerId").equalTo(managerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _totalProperties.value = snapshot.childrenCount.toInt()
                }

                override fun onCancelled(error: DatabaseError) {
                    _totalProperties.value = 0
                }
            })
    }

    // Hàm load tổng hợp cho Dashboard
    fun loadDashboardData(managerId: String) {
        loadTotalProperties(managerId)
        loadFeedbacks(managerId)
    }
}

