package com.example.myapplication.voicedatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [VoiceDatabaseItem::class], version=1)
abstract class VoiceDatabase: RoomDatabase() {

    abstract fun VoiceDao(): VoiceDao

    companion object {
        @Volatile private var instance: VoiceDatabase? = null

        private fun buildDataBase(context: Context) =
            Room.databaseBuilder(
                context,
                VoiceDatabase::class.java,
                "voice_item_db").build()


        fun getInstance(context: Context): VoiceDatabase {
            return instance ?: synchronized(this){
                instance ?: buildDataBase(context).also{
                    instance = it
                }
            }
        }

    }


}