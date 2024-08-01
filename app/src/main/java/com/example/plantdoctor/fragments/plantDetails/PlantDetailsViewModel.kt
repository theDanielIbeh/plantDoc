package com.example.plantdoctor.fragments.plantDetails

import androidx.lifecycle.ViewModel
import com.example.plantdoctor.data.entities.disease.DiseaseRepository
import com.example.plantdoctor.data.entities.plant.Plant
import com.example.plantdoctor.data.entities.plant.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlantDetailsViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val diseaseRepository: DiseaseRepository,
) : ViewModel() {
    lateinit var plant: Plant
}