package com.example.plantdoctor.data.entities.plant

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import javax.inject.Inject

interface PlantRepository {
    suspend fun insert(plant: Plant)
    suspend fun insert(plants: List<Plant>)
    fun getAllPlants(): LiveData<List<Plant>>
    fun getAllPlantsPagingData(
        pageSize: Int,
        searchText: String?
    ): LiveData<PagingData<Plant>>

    suspend fun getPlantById(plantId: Int): Plant?
}

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao
) : PlantRepository {
    override suspend fun insert(plant: Plant) {
        plantDao.insert(plant = plant)
    }

    override suspend fun insert(plants: List<Plant>) {
        plantDao.insert(plants = plants)
    }

    override fun getAllPlants(): LiveData<List<Plant>> =
        plantDao.getAllPlants()

    override fun getAllPlantsPagingData(
        pageSize: Int,
        searchText: String?
    ): LiveData<PagingData<Plant>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            plantDao.getAllPlantsPagingData(searchText = searchText)
        }
    ).liveData

    override suspend fun getPlantById(plantId: Int): Plant? =
        plantDao.getPlantById(plantId = plantId)
}