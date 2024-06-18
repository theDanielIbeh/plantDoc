package com.example.plantdoc.fragments.plants

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.plantdoc.data.entities.plant.Plant
import com.example.plantdoc.data.entities.plant.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlantsViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
) : ViewModel() {

    private var _filterText: MutableLiveData<String> = MutableLiveData("%%")
    var filterText: String? = "%%"


    suspend fun getPlantDetails(plantId: Int): Plant? =
        plantRepository.getPlantById(plantId = plantId)

    internal val plants: LiveData<PagingData<Plant>> = _filterText.switchMap {
        plantRepository.getAllPlantsPagingData(
            10,
            it,
        ).cachedIn(viewModelScope)
    }

    fun search(searchQuery: String) {
        val query = "%$searchQuery%"
        _filterText.postValue(query)
    }
}