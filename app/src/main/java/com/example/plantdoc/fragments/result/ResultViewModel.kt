package com.example.plantdoc.fragments.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantdoc.data.PlantDocPreferencesRepository
import com.example.plantdoc.data.entities.disease.Disease
import com.example.plantdoc.data.entities.disease.DiseaseRepository
import com.example.plantdoc.data.entities.history.History
import com.example.plantdoc.data.entities.history.HistoryRepository
import com.example.plantdoc.data.entities.user.User
import com.example.plantdoc.data.entities.user.UserRepository
import com.example.plantdoc.data.network.PlantDocApiService
import com.example.plantdoc.utils.Constants
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
    private val userRepository: UserRepository,
    private val historyRepository: HistoryRepository,
    private val diseaseRepository: DiseaseRepository,
    private val apiService: PlantDocApiService,
    val preferencesRepository: PlantDocPreferencesRepository,
) : ViewModel() {
    var disease: Disease? = null
    private var _resultModel: MutableStateFlow<ResultModel> = MutableStateFlow(ResultModel())
    val resultModel: StateFlow<ResultModel> = _resultModel.asStateFlow()
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
            historyRepository.insert(
                history = history
            )
            try {
//                apiService.insertHistory(history = history)
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
}