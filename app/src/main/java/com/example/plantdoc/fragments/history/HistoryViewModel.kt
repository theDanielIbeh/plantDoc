package com.example.plantdoc.fragments.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.plantdoc.data.entities.history.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val application: Application,
    private val historyRepository: HistoryRepository
) : ViewModel() {


    fun getTransactionsLive() =
        historyRepository.getAllHistoryPagingData(
            10,
        ).cachedIn(viewModelScope)


    fun getAllTransactionsByDateLive(date: String) =
        historyRepository.getAllHistoryByDatePagingData(
            10,
            date,
        ).cachedIn(viewModelScope)

}
