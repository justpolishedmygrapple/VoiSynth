package com.example.myapplication.voicedatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class VoiceDatabaseItem(
    @PrimaryKey val voice_id: String,
    val name: String,
    val category: String
) : Serializable