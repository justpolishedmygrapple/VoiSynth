package com.example.myapplication.data.voicedatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class VoiceDBViewModel(application: Application): AndroidViewModel(application) {

    private val repository = VoiceItemRepository(VoiceDatabase.getInstance(application).VoiceDao())

    val allVoices = repository.getVoices().asLiveData()

    fun addVoice(voice: VoiceDatabaseItem){
        viewModelScope.launch {
            repository.insertVoice(voice)
        }
    }

}