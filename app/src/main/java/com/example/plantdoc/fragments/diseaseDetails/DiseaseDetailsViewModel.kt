package com.example.plantdoc.fragments.diseaseDetails

import androidx.lifecycle.ViewModel
import com.example.plantdoc.data.entities.disease.Disease
import com.example.plantdoc.data.entities.disease.DiseaseRepository
import com.example.plantdoc.data.entities.plant.Plant
import com.example.plantdoc.data.entities.plant.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiseaseDetailsViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val diseaseRepository: DiseaseRepository,
) : ViewModel() {
    lateinit var disease: Disease
}