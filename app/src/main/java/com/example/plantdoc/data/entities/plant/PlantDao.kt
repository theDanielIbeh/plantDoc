package com.example.plantdoc.data.entities.plant

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: Plant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plants: List<Plant>)

    @Query(
        "SELECT * FROM plant"
    )
    fun getAllPlants(): LiveData<List<Plant>>

    @Query(
        "SELECT * FROM plant WHERE LOWER(name) LIKE LOWER(:searchText)"
    )
    fun getAllPlantsPagingData(searchText: String?): PagingSource<Int, Plant>

    @Query(
        "SELECT * FROM plant where id = :plantId"
    )
    suspend fun getPlantById(plantId: Int): Plant?
}