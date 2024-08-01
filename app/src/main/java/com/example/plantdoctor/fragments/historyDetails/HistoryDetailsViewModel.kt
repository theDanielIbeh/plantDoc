package com.example.plantdoctor.fragments.historyDetails

import androidx.lifecycle.ViewModel
import com.example.plantdoctor.data.entities.disease.Disease
import com.example.plantdoctor.data.entities.disease.DiseaseRepository
import com.example.plantdoctor.data.entities.history.History
import com.example.plantdoctor.data.entities.history.HistoryRepository
import com.example.plantdoctor.data.entities.plant.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryDetailsViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val diseaseRepository: DiseaseRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    lateinit var history: History
    var disease: Disease? = null

    suspend fun getDisease(diseaseId: Int) =
        diseaseRepository.getDiseaseById(diseaseId = diseaseId)
}