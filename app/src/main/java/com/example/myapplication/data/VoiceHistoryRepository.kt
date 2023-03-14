package com.example.myapplication.data

import com.example.myapplication.ui.VoiceInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VoiceHistoryRepository(
    private val service: VoiceInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {


    suspend fun loadHistorySearch(): Result<List<HistoryItem>> = withContext(ioDispatcher) {

        try{
            val response = service.getHistory()

            if(response.isSuccessful){
                Result.success(response.body()?.history ?: listOf())
            }
            else{
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch(e: Exception){
            Result.failure(e)
        }

    }

}