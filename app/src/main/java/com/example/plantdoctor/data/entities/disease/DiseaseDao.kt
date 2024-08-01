package com.example.plantdoctor.data.entities.disease

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DiseaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(disease: Disease)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diseases: List<Disease>)

    @Query(
        "SELECT * FROM disease"
    )
    fun getAllDiseases(): LiveData<List<Disease>>

    @Query(
        "SELECT * FROM disease where plant_id = :plantId"
    )
    fun getDiseasesByPlantId(plantId: Int): LiveData<List<Disease>>

    @Query(
        "SELECT * FROM disease WHERE LOWER(name) LIKE :searchText AND plant_id = :plantId"
    )
    fun getDiseasesByPlantIdPagingData(searchText: String?,plantId: Int): PagingSource<Int, Disease>

    @Query(
        "SELECT * FROM disease where id = :diseaseId"
    )
    suspend fun getDiseaseById(diseaseId: Int): Disease?

    @Query(
        "SELECT * FROM disease where class_index = :idx"
    )
    suspend fun getDiseaseByClassIndex(idx: Int): Disease?
}