package com.example.myapplication.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HistoryDBViewModel(application: Application): AndroidViewModel(application) {

    private val repository = HistoryItemRepository(HistoryDatabase.getInstance(application).historyItemDao())

    val LatestHistoryItems = repository.getHistory().asLiveData()

    fun addHistoryItem(history: HistoryDatabaseItem){
        viewModelScope.launch {
            repository.insertHistory(history)
        }
    }
}