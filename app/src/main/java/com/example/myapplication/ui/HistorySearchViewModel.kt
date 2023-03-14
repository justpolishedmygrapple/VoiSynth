package com.example.myapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.HistoryItem
import com.example.myapplication.data.VoiceHistoryRepository
import kotlinx.coroutines.launch

class HistorySearchViewModel: ViewModel() {

    private val repository = VoiceHistoryRepository(VoiceInterface.create())

    private val _historySearchResults = MutableLiveData<List<HistoryItem>?>(null)

    val historySearchResults: LiveData<List<HistoryItem>?> = _historySearchResults

    fun loadHistorySearchResults(){
        viewModelScope.launch {
            val result = repository.loadHistorySearch()

            _historySearchResults.value = result.getOrNull()
        }
    }

}