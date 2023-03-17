package com.example.myapplication.ui

import com.example.myapplication.ELEVEN_LABS_API
import com.example.myapplication.data.HistoryResponse
import com.example.myapplication.data.VoiceResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.io.File


interface VoiceInterface {




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
    @Streaming
    suspend fun generateVoiceAudio(@Path("voiceID") voiceID: String, @Body text: RequestBody): Response<ResponseBody>


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