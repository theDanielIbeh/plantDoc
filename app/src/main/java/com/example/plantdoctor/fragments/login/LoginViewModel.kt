package com.example.plantdoctor.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantdoctor.data.DiseaseList
import com.example.plantdoctor.data.entities.user.User
import com.example.plantdoctor.data.entities.user.UserRepository
import com.example.plantdoctor.data.PlantDocPreferencesRepository
import com.example.plantdoctor.data.PlantList
import com.example.plantdoctor.data.WorkManagerRepository
import com.example.plantdoctor.data.entities.disease.DiseaseRepository
import com.example.plantdoctor.data.entities.plant.PlantRepository
import com.example.plantdoctor.utils.Constants
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
    val preferencesRepository: PlantDocPreferencesRepository,
    private val workManagerRepository: WorkManagerRepository
) : ViewModel() {
    private var _loginModel: MutableStateFlow<LoginModel> = MutableStateFlow(LoginModel())
    val loginModel: StateFlow<LoginModel> = _loginModel.asStateFlow()

    val isLoggedIn = preferencesRepository.getPreference(
        Boolean::class.java,
        Constants.IS_LOGGED_IN
    )

    suspend fun getUserDetails(email: String): User? {
        return userRepository.getUser(email = email)
    }

    suspend fun login(email: String, password: String) {
        userRepository.login(
            email = email,
            password = password
        )
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

    fun downloadData() {
        workManagerRepository.download()
    }
}