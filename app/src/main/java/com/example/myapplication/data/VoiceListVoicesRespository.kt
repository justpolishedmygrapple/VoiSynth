package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.ui.VoiceInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VoiceListVoicesRespository(
    private val service: VoiceInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun loadListOfVoices(): Result<VoiceResponse> = withContext(ioDispatcher){
        try{
            val response = service.getVoices()

            Log.d("response", response.toString())

            if(response.isSuccessful){
                Result.success(response.body()!!)

            }
            else{
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch(e:Exception){
            Result.failure(e)
        }
    }
}