package com.example.myapplication.voicedatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface VoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoice(voiceItem: VoiceDatabaseItem)

    @Query("SELECT * FROM VoiceDatabaseItem ORDER BY name ASC")
    fun getVoices(): Flow<List<VoiceDatabaseItem>>

    @Query("SELECT * from VoiceDatabaseItem WHERE voice_id = :voice_id")
    fun getVoiceByIDNumber(voice_id: String) : Flow<VoiceDatabaseItem?>
}