package com.example.mtt_rental.viewmodel.tenant

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.User
import com.example.mtt_rental.viewmodel.repo.UserDB
import com.example.mtt_rental.utils.UserRepo

class LoginViewModel : ViewModel() {

    private val _userList = mutableStateOf<List<User>>(emptyList())
    val userList: State<List<User>> = _userList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _loginResult = mutableStateOf<LoginResult?>(null)
    val loginResult: State<LoginResult?> = _loginResult

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

    fun login(username: String, password: String) {
        val users = _userList.value

        when {
            username.isBlank() -> {
                _loginResult.value = LoginResult.Error("Không được bỏ trống username")
            }

            users.all { it.idUser != username } -> {
                _loginResult.value = LoginResult.Error("Username không tồn tại!")
            }

            password.isBlank() -> {
                _loginResult.value = LoginResult.Error("Không được bỏ trống password")
            }

            users.any { it.idUser == username } && users.first { it.idUser == username }.password != password -> {
                _loginResult.value = LoginResult.Error("Sai password!")
            }

            else -> {
                val user = users.first { it.idUser == username }
                UserRepo.updateUser(
                    user.idUser,
                    user.profileName,
                    user.email,
                    user.phoneNumber,
                    user.userType,
                    user.address
                )
                _loginResult.value = LoginResult.Success(user)
            }
        }
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }

    fun clearError() {
        _error.value = null
    }
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}