package com.example.plantdoc.data.entities.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.plantdoc.data.network.PlantDocApiService
import javax.inject.Inject

interface HistoryRepository {
    suspend fun insert(history: History)
    suspend fun updateHistory(local: String, remote: String)
    suspend fun getHistory(): LiveData<List<History>>
    fun getAllHistoryPagingData(pageSize: Int): LiveData<PagingData<History>>
    fun getAllHistoryByDatePagingData(pageSize: Int, date: String): LiveData<PagingData<History>>
}

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao,
    private val apiService: PlantDocApiService
) : HistoryRepository {
    override suspend fun insert(history: History) {
        historyDao.insert(history = history)
        val response = apiService.insertHistory(history = history)
        Log.d("Initial", response.body().toString())
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                Log.d("Insert History", "Success")
                Log.d("Insert History", responseBody.toString())
                // Handle the history data
            } ?: run {
                throw Exception("Response body is null")
            }
        } else {
            when (response.code()) {
                else -> {
                    throw Exception("Unsuccessful response: ${response.code()} - ${response.message()}")
                }
            }
        }
    }

    override suspend fun updateHistory(local: String, remote: String) {
        historyDao.updateHistory(local = local, remote = remote)
    }

    override suspend fun getHistory(): LiveData<List<History>> =
        historyDao.getHistory()

    override fun getAllHistoryPagingData(pageSize: Int): LiveData<PagingData<History>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            historyDao.getAllHistoryPagingData()
        }
    ).liveData

    override fun getAllHistoryByDatePagingData(
        pageSize: Int,
        date: String
    ): LiveData<PagingData<History>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            historyDao.getAllHistoryByDatePagingData(date)
        }
    ).liveData
}