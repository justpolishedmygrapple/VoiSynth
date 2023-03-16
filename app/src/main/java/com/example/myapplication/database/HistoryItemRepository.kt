package com.example.myapplication.database

class HistoryItemRepository ( private val dao: HistoryItemDao) {

    suspend fun insertHistory(history: HistoryDatabaseItem) =
        dao.insert(history)


    fun getHistory() = dao.getHistory()

}