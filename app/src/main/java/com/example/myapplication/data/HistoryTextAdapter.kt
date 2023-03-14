package com.example.myapplication.data

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import okhttp3.OkHttpClient
import okhttp3.Request

class HistoryTextAdapter(private val onClick: (HistoryItem) -> Unit, private val onLongClick: (HistoryItem) -> Unit) : RecyclerView.Adapter<HistoryTextAdapter.ViewHolder>(){


    private var historyItemList = listOf<HistoryItem>()


    override fun getItemCount() = this.historyItemList.size

    fun addHistoryItem(newHistoryItemList: List<HistoryItem>?){
        historyItemList = newHistoryItemList ?: listOf()
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_result_item, parent, false)

        val vibrator = parent.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        view.setOnLongClickListener{it ->
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            Log.d("vibrate","vibrate")

            val client = OkHttpClient()




            true
        }

        return ViewHolder(view, onClick, onLongClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.historyItemList[position])
    }




    class ViewHolder(view: View, val onClick: (HistoryItem) -> Unit, val onLongClick: (HistoryItem) -> Unit): RecyclerView.ViewHolder(view){
        private val textFromHistoryItem: TextView = view.findViewById(R.id.tv_history_item_text)

        private lateinit var currentHistoryItem: HistoryItem

//        init {
//            itemView.setOnClickListener {
//                currentHistoryItem?.let(onClick)
//            }
//        }

        init {
            itemView.setOnLongClickListener{
                currentHistoryItem?.let(onLongClick)
                true
            }
        }


        fun bind(historyItem: HistoryItem){
            currentHistoryItem = historyItem
            textFromHistoryItem.text = currentHistoryItem.text

        }
    }


}