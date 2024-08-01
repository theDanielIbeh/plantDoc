package com.example.plantdoctor.fragments.register

import androidx.lifecycle.ViewModel
import com.example.plantdoctor.data.entities.user.User
import com.example.plantdoctor.data.entities.user.UserRepository
import com.example.plantdoctor.data.network.PlantDocApiService
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
    }

    fun resetRegisterModel() {
        _registerModel.value = RegisterModel()
    }
}