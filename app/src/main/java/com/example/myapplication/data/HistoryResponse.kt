package com.example.myapplication.data

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class HistoryResponse(
    val history: List<HistoryItem>
) : Serializable