package com.example.myapplication.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class HistoryTextAdapter : RecyclerView.Adapter<HistoryTextAdapter.ViewHolder>(){

    private var historyItemList = listOf<HistoryItem>()

    override fun getItemCount() = this.historyItemList.size

    fun addHistoryItem(newHistoryItemList: List<HistoryItem>?){
        historyItemList = newHistoryItemList ?: listOf()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.historyItemList[position])
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val textFromHistoryItem: TextView = view.findViewById(R.id.tv_history_item_text)

        private lateinit var currentHistoryItem: HistoryItem


        fun bind(historyItem: HistoryItem){
            currentHistoryItem = historyItem
            textFromHistoryItem.text = currentHistoryItem.text
        }
    }


}