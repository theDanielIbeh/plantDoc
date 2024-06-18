package com.example.plantdoc.fragments.historyDetails

import androidx.lifecycle.ViewModel
import com.example.plantdoc.data.entities.disease.DiseaseRepository
import com.example.plantdoc.data.entities.history.History
import com.example.plantdoc.data.entities.history.HistoryRepository
import com.example.plantdoc.data.entities.plant.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryDetailsViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val diseaseRepository: DiseaseRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    lateinit var history: History
}