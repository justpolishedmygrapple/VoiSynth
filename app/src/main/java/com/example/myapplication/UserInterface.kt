package com.example.myapplication

import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers


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