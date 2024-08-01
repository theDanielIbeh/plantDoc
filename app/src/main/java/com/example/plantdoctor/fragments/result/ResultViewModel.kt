package com.example.plantdoctor.fragments.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantdoctor.data.PlantDocPreferencesRepository
import com.example.plantdoctor.data.entities.disease.Disease
import com.example.plantdoctor.data.entities.disease.DiseaseRepository
import com.example.plantdoctor.data.entities.history.History
import com.example.plantdoctor.data.entities.history.HistoryRepository
import com.example.plantdoctor.data.entities.plant.Plant
import com.example.plantdoctor.data.entities.plant.PlantRepository
import com.example.plantdoctor.data.entities.user.User
import com.example.plantdoctor.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ResultModel(
    var predictedClass: Int = -1,
    var localUrl: String = "",
    var remoteUrl: String = "",
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val diseaseRepository: DiseaseRepository,
    private val plantRepository: PlantRepository,
    val preferencesRepository: PlantDocPreferencesRepository,
) : ViewModel() {
    var disease: Disease? = null
    var plant: Plant? = null
    private var _resultModel: MutableStateFlow<ResultModel> = MutableStateFlow(ResultModel())
    val resultModel: StateFlow<ResultModel> = _resultModel.asStateFlow()
    val isLoggedIn = preferencesRepository.getPreference(
        Boolean::class.java,
        Constants.IS_LOGGED_IN
    )
    val loggedInUser = preferencesRepository.getPreference(
        User::class.java,
        Constants.USER
    )

    fun insertHistory(userId: Int) {
        val formatter = SimpleDateFormat(Constants.DATE_FORMAT_SPREAD, Locale.getDefault())
        val currentDate = Date()

        val (
            predictedClass,
            localUrl,
            remoteUrl,
        ) = resultModel.value
        val history = History(
            userId = userId,
            predictedClassId = predictedClass,
            localUrl = localUrl,
            remoteUrl = remoteUrl,
            date = formatter.format(currentDate),
        )
        viewModelScope.launch {
            try {
                historyRepository.insert(
                    history = history
                )
                // Handle the user data
            } catch (e: Exception) {
                // Handle the error
                e.printStackTrace()
            }
        }
    }

    fun resetResultModel() {
        _resultModel.value = ResultModel()
    }

    fun updateHistory(remoteUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                historyRepository.updateHistory(
                    resultModel.value.localUrl,
                    remote = remoteUrl
                )
            }
        }
    }

    suspend fun getDiseaseByIndex(idx: Int): Disease? =
        diseaseRepository.getDiseaseByClassIndex(idx)

    suspend fun getPlantById(id: Int): Plant? =
        plantRepository.getPlantById(id)
}