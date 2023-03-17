package com.example.myapplication.data

import com.squareup.moshi.JsonClass
import java.io.Serializable


@JsonClass(generateAdapter = true)
data class Voice(
    val voice_id: String,
    val name: String,
    val category: String
) : Serializable

