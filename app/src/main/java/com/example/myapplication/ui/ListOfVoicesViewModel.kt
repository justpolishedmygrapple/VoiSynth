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

    private val _loadingStatus = MutableLiveData<LoadingStatus>(LoadingStatus.SUCCESS)
//
    val loadingStatus : LiveData<LoadingStatus> = _loadingStatus


    fun loadListOfVoices(){

        _loadingStatus.value = LoadingStatus.LOADING
        viewModelScope.launch {
            val result = repository.loadListOfVoices()

            Log.d("resultzzz", result.toString())

            _voiceListResults.value = result.getOrNull()
            _loadingStatus.value = when(result.isSuccess){
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }


        }
    }

}

