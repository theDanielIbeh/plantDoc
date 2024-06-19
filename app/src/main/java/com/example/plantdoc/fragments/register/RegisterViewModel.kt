package com.example.plantdoc.fragments.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.plantdoc.data.entities.user.User
import com.example.plantdoc.data.entities.user.UserRepository
import com.example.plantdoc.data.network.PlantDocApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterModel(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val apiService: PlantDocApiService,
) : ViewModel() {
    private var _registerModel: MutableStateFlow<RegisterModel> = MutableStateFlow(RegisterModel())
    val registerModel: StateFlow<RegisterModel> = _registerModel.asStateFlow()

    suspend fun getUserDetails(email: String): User? {
        return userRepository.getUser(email = email)
    }

    suspend fun insertUser() {
            val (
                firstName,
                lastName,
                email,
                password
            ) = registerModel.value
            val user = User(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
            userRepository.insert(
                user = user
            )
    }

    fun resetRegisterModel() {
        _registerModel.value = RegisterModel()
    }
}