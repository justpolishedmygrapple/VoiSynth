package com.example.myapplication.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import kotlinx.coroutines.launch

class ListOfVoicesViewModel: ViewModel() {

    private val repository = VoiceListVoicesRespository(VoiceInterface.create())

    private val _voiceListResults = MutableLiveData<VoiceResponse>(null)

    val voiceListResults: LiveData<VoiceResponse> = _voiceListResults


    fun loadHistorySearchResults(){
        viewModelScope.launch {
            val result = repository.loadListOfVoices()

            Log.d("resultzzz", result.toString())

            _voiceListResults.value = result.getOrNull()
        }
    }

}

