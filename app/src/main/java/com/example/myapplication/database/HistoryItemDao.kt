package com.example.myapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(histItem: HistoryDatabaseItem)

    @Query("SELECT * FROM HistoryDatabaseItem ORDER BY date_unix DESC")
    fun getHistory(): Flow<List<HistoryDatabaseItem>>
}