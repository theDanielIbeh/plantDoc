package com.example.plantdoc.data.entities.history

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import javax.inject.Inject

interface HistoryRepository {
    suspend fun insert(history: History)
    suspend fun updateHistory(local: String, remote: String)
    suspend fun getHistory(): LiveData<List<History>>
    fun getAllHistoryPagingData(pageSize: Int): LiveData<PagingData<History>>
    fun getAllHistoryByDatePagingData(pageSize: Int, date: String): LiveData<PagingData<History>>
    suspend fun getHistoryById(historyId: Int): History?
}

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {
    override suspend fun insert(history: History) {
        historyDao.insert(history = history)
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

    override suspend fun getHistoryById(historyId: Int): History? =
        historyDao.getHistoryById(historyId = historyId)
}