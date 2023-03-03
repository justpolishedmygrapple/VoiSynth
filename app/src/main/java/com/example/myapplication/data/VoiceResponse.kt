package com.example.myapplication.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoiceResponse(val voices: List<Voice>)