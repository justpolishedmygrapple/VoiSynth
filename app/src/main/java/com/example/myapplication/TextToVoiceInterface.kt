package com.example.myapplication

import com.example.myapplication.data.TextToVoiceQuery
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface TextToVoiceInterface {

    @POST("text-to-speech/{voiceID}/stream")
    @Headers(
        "xi-api-key: $ELEVEN_LABS_API",
        "accept: */*",
        "Content-Type: application/json",
    )

    suspend fun generateAudio(@Body text: TextToVoiceQuery) : Response<ResponseBody>
}