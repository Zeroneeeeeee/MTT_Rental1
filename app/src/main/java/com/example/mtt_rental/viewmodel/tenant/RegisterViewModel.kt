package com.example.mtt_rental.viewmodel.tenant

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.User
import com.example.mtt_rental.repo.UserDB

class RegisterViewModel : ViewModel() {

    private val _userList = mutableStateOf<List<User>>(emptyList())
    val userList: State<List<User>> = _userList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _validationResult = mutableStateOf<ValidationResult?>(null)
    val validationResult: State<ValidationResult?> = _validationResult

    init {
        loadUsers()
    }

    private fun loadUsers() {
        _isLoading.value = true
        UserDB.getUsers(
            onResult = { users ->
                _userList.value = users
                _isLoading.value = false
            },
            onError = { error ->
                _error.value = "Lỗi tải dữ liệu: ${error.message}"
                _isLoading.value = false
            }
        )
    }

    fun validateRegistration(
        username: String,
        password: String,
        email: String,
        mobile: String,
        role: String
    ) {
        val users = _userList.value
        val errors = mutableMapOf<String, String>()

        // Validation logic
        if (username.isBlank()) {
            errors["username"] = "Không được bỏ trống username"
        } else if (users.any { it.idUser == username }) {
            errors["username"] = "Username đã tồn tại!"
        }

        if (password.isBlank()) {
            errors["password"] = "Không được bỏ trống password"
        } else if (password.length < 8) {
            errors["password"] = "Mật khẩu phải ≥ 8 ký tự"
        }

        if (email.isBlank()) {
            errors["email"] = "Không được bỏ trống email"
        }

        if (mobile.isBlank()) {
            errors["mobile"] = "Không được bỏ trống số điện thoại"
        }

        if (errors.isEmpty()) {
            val newUser = User(
                idUser = username,
                password = password,
                email = email,
                phoneNumber = mobile,
                profileName = username,
                userType = role
            )
            _validationResult.value = ValidationResult.Success(newUser)
        } else {
            _validationResult.value = ValidationResult.Error(errors)
        }
    }

    fun clearValidationResult() {
        _validationResult.value = null
    }

    fun clearError() {
        _error.value = null
    }
}

sealed class ValidationResult {
    data class Success(val user: User) : ValidationResult()
    data class Error(val errors: Map<String, String>) : ValidationResult()
}