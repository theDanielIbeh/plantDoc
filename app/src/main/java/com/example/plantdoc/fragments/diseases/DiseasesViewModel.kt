package com.example.plantdoc.fragments.diseases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.plantdoc.data.entities.disease.Disease
import com.example.plantdoc.data.entities.disease.DiseaseRepository
import com.example.plantdoc.data.entities.plant.Plant
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiseasesViewModel @Inject constructor(
    private val diseaseRepository: DiseaseRepository,
) : ViewModel() {
    lateinit var plant: Plant
    private var _filterText: MutableLiveData<String> = MutableLiveData("%%")
    var filterText: String? = "%%"


    suspend fun getPlantDetails(diseaseId: Int): Disease? =
        diseaseRepository.getDiseaseById(diseaseId = diseaseId)

    internal fun getDiseases(): LiveData<PagingData<Disease>> = _filterText.switchMap {
        diseaseRepository.getDiseasesByPlantIdPagingData(
            10,
            it,
            plantId = plant.id
        ).cachedIn(viewModelScope)
    }

    fun search(searchQuery: String) {
        val query = "%$searchQuery%"
        _filterText.postValue(query)
    }
}