package com.example.myapplication

import android.provider.MediaStore.Audio
import com.example.myapplication.data.TextToVoiceQuery
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


interface UserInterface {

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


    companion object{
        private const val BASE_URL = "https://api.elevenlabs.io/v1/"

        fun create(): UserInterface {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(UserInterface::class.java)
    }

    }
}