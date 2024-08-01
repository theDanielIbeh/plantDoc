package com.example.plantdoctor.data.entities.history

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: History)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: List<History>)

    @Query(
        "UPDATE history SET remote_url = :remote WHERE local_url= :local"
    )
    suspend fun updateHistory(local: String, remote: String)

    @Query(
        "SELECT * FROM history ORDER BY date DESC"
    )
    fun getHistory(): LiveData<List<History>>

    @Query(
        "SELECT * FROM history ORDER BY date DESC"
    )
    fun getAllHistoryPagingData(): PagingSource<Int, History>

    @Query(
        "SELECT * FROM history WHERE STRFTIME('%Y-%m-%d', date) =:date ORDER BY date DESC"
    )
    fun getAllHistoryByDatePagingData(date: String): PagingSource<Int, History>
}