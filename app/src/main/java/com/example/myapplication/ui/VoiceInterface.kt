package com.example.myapplication.ui

import com.example.myapplication.ELEVEN_LABS_API
import com.example.myapplication.data.HistoryResponse
import com.example.myapplication.data.Voice
import com.example.myapplication.data.VoiceResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


interface VoiceInterface {

    @GET("user")
    @Headers(
        "xi-api-key: $ELEVEN_LABS_API",
        "accept: application/json"
    )
    suspend fun search() : Response<String>


    @GET("voices")
    @Headers(
        "xi-api-key: $ELEVEN_LABS_API",
        "accept: application/json"
    )
    suspend fun getVoices(): Response<VoiceResponse>


    @POST("text-to-speech/{voiceID}/stream")
    @Headers(
        "xi-api-key: $ELEVEN_LABS_API",
        "accept: */*",
        "Content-Type: application/json",
    )
     suspend fun generateVoiceAudio(@Path("voiceID") voiceID: String, @Body text: String): Call<ResponseBody>


     @GET("history")
     @Headers(
         "xi-api-key: $ELEVEN_LABS_API",
         "accept: application/json"
     )
     suspend fun getHistory(): Response<HistoryResponse>


    companion object{
        private const val BASE_URL = "https://api.elevenlabs.io/v1/"

        fun create(): VoiceInterface {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(VoiceInterface::class.java)
    }

    }
}