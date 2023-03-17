package com.example.myapplication.data.voicedatabase

class VoiceItemRepository (private val dao: VoiceDao) {

    suspend fun insertVoice(voice: VoiceDatabaseItem) =
        dao.insertVoice(voice)

    fun getVoices() = dao.getVoices()
}