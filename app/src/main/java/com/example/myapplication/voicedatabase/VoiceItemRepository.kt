package com.example.myapplication.voicedatabase

class VoiceItemRepository (private val dao: VoiceDao) {

    suspend fun insertVoice(voice: VoiceDatabaseItem) =
        dao.insertVoice(voice)

    fun getVoices() = dao.getVoices()

    fun searchVoice(voice_id: String) = dao.getVoiceByIDNumber(voice_id)
}