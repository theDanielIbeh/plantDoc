package com.example.plantdoc.data.entities.disease

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import javax.inject.Inject

interface DiseaseRepository {
    suspend fun insert(disease: Disease)
    suspend fun insert(diseases: List<Disease>)
    suspend fun getAllDiseases(): LiveData<List<Disease>>
    suspend fun getDiseasesByPlantId(plantId: Int): LiveData<List<Disease>>
    fun getDiseasesByPlantIdPagingData(
        pageSize: Int,
        searchText: String?,
        plantId: Int
    ): LiveData<PagingData<Disease>>

    suspend fun getDiseaseById(diseaseId: Int): Disease?
    suspend fun getDiseaseByClassIndex(idx: Int): Disease?
}

class DiseaseRepositoryImpl @Inject constructor(
    private val diseaseDao: DiseaseDao
) : DiseaseRepository {
    override suspend fun insert(disease: Disease) {
        diseaseDao.insert(disease = disease)
    }

    override suspend fun insert(diseases: List<Disease>) {
        diseaseDao.insert(diseases = diseases)
    }

    override suspend fun getAllDiseases(): LiveData<List<Disease>> =
        diseaseDao.getAllDiseases()

    override suspend fun getDiseasesByPlantId(plantId: Int): LiveData<List<Disease>> =
        diseaseDao.getDiseasesByPlantId(plantId = plantId)

    override fun getDiseasesByPlantIdPagingData(
        pageSize: Int,
        searchText: String?,
        plantId: Int
    ): LiveData<PagingData<Disease>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            diseaseDao.getDiseasesByPlantIdPagingData(
                searchText = searchText,
                plantId = plantId
            )
        }
    ).liveData

    override suspend fun getDiseaseById(diseaseId: Int): Disease? =
        diseaseDao.getDiseaseById(diseaseId = diseaseId)

    override suspend fun getDiseaseByClassIndex(idx: Int): Disease? =
        diseaseDao.getDiseaseByClassIndex(idx = idx)
}