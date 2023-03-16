package com.example.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [HistoryDatabaseItem::class], version=1)
abstract class HistoryDatabase: RoomDatabase() {

    abstract fun historyItemDao(): HistoryItemDao

    companion object {
        @Volatile private var instance: HistoryDatabase? = null

        private fun buildDataBase(context: Context) =
            Room.databaseBuilder(
                context,
                HistoryDatabase::class.java,
                "history_item_db").build()


        fun getInstance(context: Context): HistoryDatabase {
            return instance?: synchronized(this){
                instance ?: buildDataBase(context).also{
                    instance = it
                }
            }
        }

    }

}