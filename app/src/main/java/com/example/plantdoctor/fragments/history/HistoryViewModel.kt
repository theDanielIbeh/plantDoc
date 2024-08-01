package com.example.plantdoctor.fragments.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.plantdoctor.data.PlantDocPreferencesRepository
import com.example.plantdoctor.data.entities.disease.DiseaseRepository
import com.example.plantdoctor.data.entities.history.HistoryRepository
import com.example.plantdoctor.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val application: Application,
    private val diseaseRepository: DiseaseRepository,
    private val historyRepository: HistoryRepository,
    preferencesRepository:PlantDocPreferencesRepository
) : ViewModel() {

    val isLoggedIn = preferencesRepository.getPreference(
        Boolean::class.java,
        Constants.IS_LOGGED_IN
    )

    fun getTransactionsLive() =
        historyRepository.getAllHistoryPagingData(
            10,
        ).cachedIn(viewModelScope)


    fun getAllTransactionsByDateLive(date: String) =
        historyRepository.getAllHistoryByDatePagingData(
            10,
            date,
        ).cachedIn(viewModelScope)

    suspend fun getDisease(diseaseId: Int) =
        diseaseRepository.getDiseaseById(diseaseId = diseaseId)

}
