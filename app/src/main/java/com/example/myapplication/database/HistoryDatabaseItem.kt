package com.example.myapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class HistoryDatabaseItem(
    @PrimaryKey val history_item_id: String,
    val voice_name: String,
    val text: String,
    val date_unix: Int
) : Serializable