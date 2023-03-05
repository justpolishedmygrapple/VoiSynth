package com.example.myapplication

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


interface VoiceInterface {

    @GET("user")
    @Headers(
        "xi-api-key: $ELEVEN_LABS_API",
        "accept: application/json"
    )
    fun search() : Call<String>


    @GET("voices")
    @Headers(
        "xi-api-key: $ELEVEN_LABS_API",
        "accept: application/json"
    )
    fun getVoices(): Call<String>


    @POST("text-to-speech/{voiceID}/stream")
    @Headers(
        "xi-api-key: $ELEVEN_LABS_API",
        "accept: */*",
        "Content-Type: application/json",
    )
     fun generateVoiceAudio(@Path("voiceID") voiceID: String, @Body text: String): Call<ResponseBody>


     @GET("history")
     @Headers(
         "xi-api-key: $ELEVEN_LABS_API",
         "accept: application/json"
     )
     fun getHistory(): Call<String>


    companion object{
        private const val BASE_URL = "https://api.elevenlabs.io/v1/"

        fun create(): VoiceInterface {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(VoiceInterface::class.java)
    }

    }
}