package com.example.plantdoc.fragments.register

import androidx.lifecycle.ViewModel
import com.example.plantdoc.data.entities.user.User
import com.example.plantdoc.data.entities.user.UserRepository
import com.example.plantdoc.data.network.PlantDocApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        try {
            apiService.insertUser(user = user)
            // Handle the user data
        } catch (e: Exception) {
            // Handle the error
            e.printStackTrace()
        }
    }

    fun resetRegisterModel() {
        _registerModel.value = RegisterModel()
    }
}