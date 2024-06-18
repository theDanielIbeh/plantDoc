package com.example.plantdoc.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantdoc.data.DiseaseList
import com.example.plantdoc.data.entities.user.User
import com.example.plantdoc.data.entities.user.UserRepository
import com.example.plantdoc.data.PlantDocPreferencesRepository
import com.example.plantdoc.data.PlantList
import com.example.plantdoc.data.entities.disease.DiseaseRepository
import com.example.plantdoc.data.entities.plant.PlantRepository
import com.example.plantdoc.data.network.PlantDocApiService
import com.example.plantdoc.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginModel(
    var email: String = "",
    var password: String = "",
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val plantRepository: PlantRepository,
    private val diseaseRepository: DiseaseRepository,
    private val apiService: PlantDocApiService,
    val preferencesRepository: PlantDocPreferencesRepository,
) : ViewModel() {
    private var _loginModel: MutableStateFlow<LoginModel> = MutableStateFlow(LoginModel())
    val loginModel: StateFlow<LoginModel> = _loginModel.asStateFlow()

    suspend fun getUserDetails(email: String): User? {
        return userRepository.getUser(email = email)
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
//                val users = apiService.getUsers()
//                userRepository.insertUsers(users)
                // Handle the user data
            } catch (e: Exception) {
                // Handle the error
                e.printStackTrace()
            }
        }
    }

    fun resetLoginModel() {
        _loginModel.value = LoginModel()
    }
    suspend fun savePreferences(it: User) {
        preferencesRepository.savePreference(Constants.USER, it)
        preferencesRepository.savePreference(Constants.IS_LOGGED_IN, true)
    }

    suspend fun saveAdminPreferences() {
        preferencesRepository.savePreference(Constants.IS_ADMIN, true)
        preferencesRepository.savePreference(Constants.IS_LOGGED_IN, true)
    }

    fun insertData() {
        val plants = PlantList.list()
        val diseases = DiseaseList.list()
        plants.forEach { plant ->
            viewModelScope.launch {
                plantRepository.insert(plant = plant)
            }
        }
        diseases.forEach { disease ->
            viewModelScope.launch {
                diseaseRepository.insert(disease = disease)
            }
        }
    }
}