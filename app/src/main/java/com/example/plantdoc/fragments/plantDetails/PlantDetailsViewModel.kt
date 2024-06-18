package com.example.plantdoc.fragments.plantDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantdoc.data.entities.disease.DiseaseRepository
import com.example.plantdoc.data.entities.plant.Plant
import com.example.plantdoc.data.entities.plant.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantDetailsViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val diseaseRepository: DiseaseRepository,
) : ViewModel() {
    lateinit var plant: Plant
}