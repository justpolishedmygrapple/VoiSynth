package com.example.myapplication.data

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)

data class HistoryItem(
    val history_item_id: String,
    val voice_id: String,
    val text: String,
    val date_unix: Int,
    @Transient
    var url: Uri? = null
) : Serializable
